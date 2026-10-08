locals {
  env_octet = {
    prod    = 1
    staging = 2
    dev     = 3
  }

  octet = local.env_octet[var.environment]

  vpc_cidr = "10.${local.octet}.0.0/16"

  # ALB requires 2 subnets in 2 different AZs (AWS hard requirement)
  # RDS subnet group requires 2 subnets in 2 different AZs (AWS hard requirement)
  # Dev uses 2 AZs minimum to satisfy AWS requirements
  public_subnets   = ["10.${local.octet}.1.0/24", "10.${local.octet}.2.0/24"]
  private_subnets  = ["10.${local.octet}.10.0/24", "10.${local.octet}.11.0/24"]
  database_subnets = ["10.${local.octet}.20.0/24", "10.${local.octet}.21.0/24"]

  # Minimum 2 AZs required by ALB and RDS — even in dev
  availability_zones = var.environment == "prod" ? (
  ["ap-south-1a", "ap-south-1b", "ap-south-1c"]
  ) : ["ap-south-1a", "ap-south-1b"]

  name_prefix    = "${var.project_name}-${var.environment}"
  secrets_prefix = "${var.environment}/${var.project_name}"
  is_production  = var.environment == "prod"
}