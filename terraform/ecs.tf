# ==============================================================================
# ecs.tf — ECS Cluster, Task Definition & Service
# ==============================================================================
# COST OPTIMIZATION (dev):
#   - cpu: 256 (0.25 vCPU) + memory: 512 MB → ~$6.50/month on Fargate
#   - desired_count: 1 (no HA overhead)
#   - Container Insights: disabled (saves ~$0.35/1000 metrics)
#
# Scale up for prod in prod.tfvars:
#   ecs_task_cpu    = "1024"
#   ecs_task_memory = "2048"
#   ecs_desired_count = 3
# ==============================================================================

# ─── 1. ECS CLUSTER ───────────────────────────────────────────────────────────
resource "aws_ecs_cluster" "main" {
  name = "${local.name_prefix}-cluster"

  setting {
    name  = "containerInsights"
    value = local.is_production ? "enabled" : "disabled"
    # dev: disabled  → saves CloudWatch metrics cost
    # prod: enabled  → full container-level CPU/memory monitoring
  }

  tags = {
    Name = "${local.name_prefix}-cluster"
  }
}

# ─── 2. CLOUDWATCH LOG GROUP ───────────────────────────────────────────────────
resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/${local.name_prefix}"
  retention_in_days = var.log_retention_days

  tags = {
    Name = "${local.name_prefix}-ecs-logs"
  }
}

# ─── 3. ECS TASK DEFINITION ───────────────────────────────────────────────────
resource "aws_ecs_task_definition" "app" {
  family                   = local.name_prefix
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.ecs_task_cpu
  memory                   = var.ecs_task_memory
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

  container_definitions = jsonencode([
    {
      name      = var.project_name
      image     = var.ecr_image_uri
      essential = true

      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
          name          = "http"
        }
      ]

      # Non-sensitive environment variables only
      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = var.environment },
        { name = "SERVER_PORT", value = "8080" },
        { name = "AWS_REGION", value = var.aws_region },
        { name = "LOG_PATH", value = "/tmp" },
        {
          name  = "JAVA_OPTS"
          value = "-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -XX:InitialRAMPercentage=40.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+ExitOnOutOfMemoryError"
        }
      ]

      # All sensitive values come from Secrets Manager
      # Format: secret_arn:json_key::
      secrets = [
        # Database
        {
          name      = "SPRING_DATASOURCE_URL"
          valueFrom = "${aws_secretsmanager_secret.database.arn}:SPRING_DATASOURCE_URL::"
        },
        {
          name      = "SPRING_DATASOURCE_USERNAME"
          valueFrom = "${aws_secretsmanager_secret.database.arn}:SPRING_DATASOURCE_USERNAME::"
        },
        {
          name      = "SPRING_DATASOURCE_PASSWORD"
          valueFrom = "${aws_secretsmanager_secret.database.arn}:SPRING_DATASOURCE_PASSWORD::"
        },
        # Infrastructure
        {
          name      = "SPRING_DATA_REDIS_HOST"
          valueFrom = "${aws_secretsmanager_secret.infra.arn}:SPRING_DATA_REDIS_HOST::"
        },
        {
          name      = "MANAGEMENT_OTLP_METRICS_EXPORT_URL"
          valueFrom = "${aws_secretsmanager_secret.infra.arn}:MANAGEMENT_OTLP_METRICS_EXPORT_URL::"
        },
        {
          name      = "MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT"
          valueFrom = "${aws_secretsmanager_secret.infra.arn}:MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT::"
        },
        {
          name      = "MANAGEMENT_OPENTELEMETRY_LOGGING_EXPORT_OTLP_ENDPOINT"
          valueFrom = "${aws_secretsmanager_secret.infra.arn}:MANAGEMENT_OPENTELEMETRY_LOGGING_EXPORT_OTLP_ENDPOINT::"
        },
        # Application secrets
        {
          name      = "GOOGLE_CLIENT_ID"
          valueFrom = "${aws_secretsmanager_secret.app_secrets.arn}:GOOGLE_CLIENT_ID::"
        },
        {
          name      = "GOOGLE_CLIENT_SECRET"
          valueFrom = "${aws_secretsmanager_secret.app_secrets.arn}:GOOGLE_CLIENT_SECRET::"
        },
        {
          name      = "MAIL_USERNAME"
          valueFrom = "${aws_secretsmanager_secret.app_secrets.arn}:MAIL_USERNAME::"
        },
        {
          name      = "MAIL_PASSWORD"
          valueFrom = "${aws_secretsmanager_secret.app_secrets.arn}:MAIL_PASSWORD::"
        },
        {
          name      = "STRIPE_API_KEY"
          valueFrom = "${aws_secretsmanager_secret.app_secrets.arn}:STRIPE_API_KEY::"
        },
        {
          name      = "STRIPE_WEBHOOK_SECRET"
          valueFrom = "${aws_secretsmanager_secret.app_secrets.arn}:STRIPE_WEBHOOK_SECRET::"
        },
        {
          name      = "JWT_SECRET"
          valueFrom = "${aws_secretsmanager_secret.app_secrets.arn}:JWT_SECRET::"
        }
      ]

      healthCheck = {
        command     = ["CMD-SHELL", "wget -q --spider http://localhost:8080/actuator/health/liveness || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 90
      }

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ecs.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }

      user       = "1001:1001"
      privileged = false

      linuxParameters = {
        initProcessEnabled = true
        capabilities = {
          drop = ["ALL"]
          add  = []
        }
      }

      ulimits = [
        { name = "nofile", softLimit = 65536, hardLimit = 65536 }
      ]
    }
  ])

  tags = {
    Name = "${local.name_prefix}-task-def"
  }
}

# ─── 4. ECS SERVICE ───────────────────────────────────────────────────────────
resource "aws_ecs_service" "app" {
  name            = local.name_prefix
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = var.ecs_desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = aws_subnet.private[*].id
    security_groups  = [aws_security_group.app.id]
    assign_public_ip = false # Tasks in private subnet — no public IP needed
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.app.arn
    container_name   = var.project_name
    container_port   = 8080
  }

  deployment_minimum_healthy_percent = 100
  deployment_maximum_percent         = 200
  health_check_grace_period_seconds  = 120

  enable_execute_command = !local.is_production # ECS Exec: dev only for debugging

  deployment_circuit_breaker {
    enable   = true
    rollback = true # Auto-rollback on failed deployment
  }

  propagate_tags = "SERVICE"

  tags = {
    Name = "${local.name_prefix}-service"
  }

  lifecycle {
    ignore_changes = [ desired_count]   # task_definition,
  }

  depends_on = [
    aws_lb_listener.https,
    aws_iam_role_policy_attachment.ecs_execution_policy,
  ]
}