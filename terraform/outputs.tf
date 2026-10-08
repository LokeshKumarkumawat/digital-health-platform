output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "vpc_cidr" {
  description = "VPC CIDR block"
  value       = aws_vpc.main.cidr_block
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "Private subnet IDs"
  value       = aws_subnet.private[*].id
}

output "database_subnet_ids" {
  description = "Database subnet IDs"
  value       = aws_subnet.database[*].id
}

output "alb_dns_name" {
  description = "ALB DNS — copy to frontend tfvars"
  value       = aws_lb.main.dns_name
}

output "alb_zone_id" {
  description = "ALB zone ID — copy to frontend tfvars"
  value       = aws_lb.main.zone_id
}

output "acm_certificate_arn" {
  description = "ACM certificate ARN (empty when enable_dns=false)"
  value       = var.enable_dns ? aws_acm_certificate.alb[0].arn : "Not created — set enable_dns=true"
}

output "ecs_cluster_name" {
  description = "GitHub Secret: ECS_CLUSTER_NAME"
  value       = aws_ecs_cluster.main.name
}

output "ecs_service_name" {
  description = "GitHub Secret: ECS_SERVICE_NAME"
  value       = aws_ecs_service.app.name
}

output "ecs_task_definition_arn" {
  description = "Latest task definition ARN"
  value       = aws_ecs_task_definition.app.arn
}

output "rds_endpoint" {
  description = "RDS endpoint"
  value       = aws_db_instance.main.endpoint
  sensitive   = true
}

output "rds_hostname" {
  description = "RDS hostname"
  value       = aws_db_instance.main.address
  sensitive   = true
}

output "rds_port" {
  description = "RDS port"
  value       = aws_db_instance.main.port
}

output "database_secret_arn" {
  description = "Secrets Manager ARN for DB credentials"
  value       = aws_secretsmanager_secret.database.arn
}

output "ecr_repository_url" {
  description = "ECR repository URL"
  value       = aws_ecr_repository.app.repository_url
}

output "app_uploads_bucket" {
  description = "S3 uploads bucket name"
  value       = aws_s3_bucket.app_uploads.bucket
}

output "sns_alerts_topic_arn" {
  description = "SNS alerts topic ARN"
  value       = aws_sns_topic.alerts.arn
}

output "deployment_summary" {
  description = "Key deployment values"
  value       = <<-EOT
    ════════════════════════════════════════════════════
    ${upper(var.environment)} — ${var.project_name}
    ════════════════════════════════════════════════════
    VPC CIDR  : ${aws_vpc.main.cidr_block}
    ALB DNS   : ${aws_lb.main.dns_name}
    ALB Zone  : ${aws_lb.main.zone_id}
    ECS       : ${aws_ecs_cluster.main.name} / ${aws_ecs_service.app.name}
    ECR       : ${aws_ecr_repository.app.repository_url}
    DNS       : ${var.enable_dns}
    CloudFront: ${var.enable_cloudfront}
    ════════════════════════════════════════════════════
    Frontend tfvars:
      alb_dns_name = "${aws_lb.main.dns_name}"
      alb_zone_id  = "${aws_lb.main.zone_id}"
    ════════════════════════════════════════════════════
    GitHub Secrets:
      ECS_CLUSTER_NAME = ${aws_ecs_cluster.main.name}
      ECS_SERVICE_NAME = ${aws_ecs_service.app.name}
    ════════════════════════════════════════════════════
  EOT
}