resource "aws_db_parameter_group" "main" {
  name        = "${local.name_prefix}-pg17-params"
  family      = "postgres17"
  description = "PostgreSQL 17 parameters for ${local.name_prefix}"

  parameter {
    name         = "rds.force_ssl"
    value        = "1"
    apply_method = "pending-reboot"
  }

  parameter {
    name         = "log_min_duration_statement"
    value        = var.environment == "dev" ? "1000" : "500"
    apply_method = "immediate"
  }

  parameter {
    name         = "shared_preload_libraries"
    value        = "pg_stat_statements"
    apply_method = "pending-reboot"
  }

  parameter {
    name         = "pg_stat_statements.track"
    value        = "all"
    apply_method = "pending-reboot"
  }

  tags = { Name = "${local.name_prefix}-pg17-params" }

  lifecycle { create_before_destroy = true }
}

resource "aws_db_instance" "main" {
  identifier           = "${local.name_prefix}-postgres"
  engine               = "postgres"
  engine_version       = "17.5"    # ← ONLY THIS LINE CHANGED (17.2 → 17.5)
  instance_class       = var.rds_instance_class
  parameter_group_name = aws_db_parameter_group.main.name

  allocated_storage     = var.rds_allocated_storage
  max_allocated_storage = local.is_production ? 100 : 50
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.rds_db_name
  username = var.rds_master_username
  password = var.rds_master_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false
  port                   = 5432
  multi_az               = local.is_production

  backup_retention_period = local.is_production ? 7 : 1
  backup_window           = "03:00-04:00"
  maintenance_window      = "Mon:04:00-Mon:05:00"

  deletion_protection       = local.is_production
  skip_final_snapshot       = !local.is_production
  final_snapshot_identifier = local.is_production ? "${local.name_prefix}-final-snapshot" : null
  copy_tags_to_snapshot     = true

  performance_insights_enabled          = local.is_production
  performance_insights_retention_period = local.is_production ? 7 : null
  monitoring_interval                   = local.is_production ? 60 : 0
  enabled_cloudwatch_logs_exports       = ["postgresql", "upgrade"]
  auto_minor_version_upgrade            = true

  tags = { Name = "${local.name_prefix}-postgres" }

  lifecycle { ignore_changes = [password] }

  depends_on = [aws_db_subnet_group.main]
}

resource "aws_secretsmanager_secret" "database" {
  name                    = "${local.secrets_prefix}/database"
  description             = "RDS credentials for ${local.name_prefix}"
  recovery_window_in_days = local.is_production ? 30 : 0
  tags                    = { Name = "${local.name_prefix}-database-secret" }
}

resource "aws_secretsmanager_secret_version" "database" {
  secret_id = aws_secretsmanager_secret.database.id

  secret_string = jsonencode({
    SPRING_DATASOURCE_URL      = "jdbc:postgresql://${aws_db_instance.main.address}:${aws_db_instance.main.port}/${var.rds_db_name}?sslmode=require&reWriteBatchedInserts=true&prepareThreshold=3"
    SPRING_DATASOURCE_USERNAME = var.rds_master_username
    SPRING_DATASOURCE_PASSWORD = var.rds_master_password
    host                       = aws_db_instance.main.address
    port                       = tostring(aws_db_instance.main.port)
    dbname                     = var.rds_db_name
    username                   = var.rds_master_username
    password                   = var.rds_master_password
  })

  lifecycle { ignore_changes = [secret_string] }
}

resource "aws_secretsmanager_secret" "infra" {
  name                    = "${local.secrets_prefix}/infra"
  description             = "Redis and OpenTelemetry endpoints for ${local.name_prefix}"
  recovery_window_in_days = local.is_production ? 30 : 0
  tags                    = { Name = "${local.name_prefix}-infra-secret" }
}

resource "aws_secretsmanager_secret_version" "infra" {
  secret_id = aws_secretsmanager_secret.infra.id

  secret_string = jsonencode({
    SPRING_DATA_REDIS_HOST                                = "localhost"
    MANAGEMENT_OTLP_METRICS_EXPORT_URL                    = "http://localhost:4318/v1/metrics"
    MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT = "http://localhost:4318/v1/traces"
    MANAGEMENT_OPENTELEMETRY_LOGGING_EXPORT_OTLP_ENDPOINT = "http://localhost:4318/v1/logs"
  })

  lifecycle { ignore_changes = [secret_string] }
}

resource "aws_secretsmanager_secret" "app_secrets" {
  name                    = "${local.secrets_prefix}/app-secrets"
  description             = "Third-party credentials for ${local.name_prefix}"
  recovery_window_in_days = local.is_production ? 30 : 0
  tags                    = { Name = "${local.name_prefix}-app-secrets" }
}

resource "aws_secretsmanager_secret_version" "app_secrets" {
  secret_id = aws_secretsmanager_secret.app_secrets.id

  secret_string = jsonencode({
    GOOGLE_CLIENT_ID      = "REPLACE_ME"
    GOOGLE_CLIENT_SECRET  = "REPLACE_ME"
    MAIL_USERNAME         = "REPLACE_ME"
    MAIL_PASSWORD         = "REPLACE_ME"
    STRIPE_API_KEY        = "REPLACE_ME"
    STRIPE_WEBHOOK_SECRET = "REPLACE_ME"
    JWT_SECRET            = "REPLACE_ME_WITH_64_CHAR_MIN_SECRET"
  })

  lifecycle { ignore_changes = [secret_string] }
}