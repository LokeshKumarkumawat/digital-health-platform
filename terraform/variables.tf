variable "aws_region" {
  type        = string
  description = "AWS region"
  default     = "ap-south-1"
}

variable "project_name" {
  type        = string
  description = "Project name used in all resource names"
  default     = "digital-health-platform"

  validation {
    condition     = can(regex("^[a-z0-9-]+$", var.project_name))
    error_message = "Must be lowercase alphanumeric with hyphens only."
  }
}

variable "environment" {
  type        = string
  description = "Deployment environment: dev | staging | prod"
  default     = "dev"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Must be one of: dev, staging, prod."
  }
}

variable "domain_name" {
  type        = string
  description = "Root domain. Backend serves api.{domain_name}"
  default     = "devmatters.in"
}

variable "enable_dns" {
  type        = bool
  description = "Enable Route53 + ACM. Set true after hosted zone exists."
  default     = false
}

variable "enable_cloudfront" {
  type        = bool
  description = "Enable CloudFront ALB secret validation. Set true after frontend deployed."
  default     = false
}

variable "ecr_image_uri" {
  type        = string
  description = "Full ECR image URI"
  default     = "placeholder/image:latest"
}

variable "ecs_task_cpu" {
  type        = string
  description = "Fargate task CPU units"
  default     = "256"
}

variable "ecs_task_memory" {
  type        = string
  description = "Fargate task memory in MiB"
  default     = "512"
}

variable "ecs_desired_count" {
  type        = number
  description = "Desired ECS task count"
  default     = 1
}

variable "rds_instance_class" {
  type        = string
  description = "RDS instance type"
  default     = "db.t4g.micro"
}

variable "rds_master_username" {
  type        = string
  description = "PostgreSQL master username"
  default     = "dhp_admin"
}

variable "rds_master_password" {
  type        = string
  description = "PostgreSQL master password"
  sensitive   = true
}

variable "rds_allocated_storage" {
  type        = number
  description = "Initial RDS storage in GB"
  default     = 20
}

variable "rds_db_name" {
  type        = string
  description = "Initial database name"
  default     = "dhp_db"
}

variable "alert_email" {
  type        = string
  description = "Email for CloudWatch SNS alarm notifications"
}

variable "log_retention_days" {
  type        = number
  description = "CloudWatch log retention in days"
  default     = 7
}