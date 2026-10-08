resource "aws_s3_bucket" "alb_logs" {
  bucket        = "${local.name_prefix}-alb-logs-${data.aws_caller_identity.current.account_id}"
  force_destroy = !local.is_production
  tags          = { Name = "${local.name_prefix}-alb-logs", Purpose = "ALB access logs" }
}

resource "aws_s3_bucket_lifecycle_configuration" "alb_logs" {
  bucket = aws_s3_bucket.alb_logs.id

  rule {
    id     = "expire-old-logs"
    status = "Enabled"
    filter { prefix = "" }
    expiration { days = local.is_production ? 90 : 7 }
  }
}

resource "aws_s3_bucket_public_access_block" "alb_logs" {
  bucket                  = aws_s3_bucket.alb_logs.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "alb_logs" {
  bucket = aws_s3_bucket.alb_logs.id
  rule {
    apply_server_side_encryption_by_default { sse_algorithm = "AES256" }
  }
}

resource "aws_s3_bucket_policy" "alb_logs" {
  bucket = aws_s3_bucket.alb_logs.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "ALBLogDelivery"
      Effect    = "Allow"
      Principal = { AWS = "arn:aws:iam::718504428378:root" }
      Action    = "s3:PutObject"
      Resource  = "${aws_s3_bucket.alb_logs.arn}/${local.name_prefix}/alb-access-logs/*"
    }]
  })
}

resource "aws_s3_bucket" "app_uploads" {
  bucket        = "${local.name_prefix}-uploads-${data.aws_caller_identity.current.account_id}"
  force_destroy = !local.is_production
  tags          = { Name = "${local.name_prefix}-uploads", Purpose = "User uploaded content" }
}

resource "aws_s3_bucket_public_access_block" "app_uploads" {
  bucket                  = aws_s3_bucket.app_uploads.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "app_uploads" {
  bucket = aws_s3_bucket.app_uploads.id
  rule {
    apply_server_side_encryption_by_default { sse_algorithm = "AES256" }
  }
}

resource "aws_s3_bucket_versioning" "app_uploads" {
  bucket = aws_s3_bucket.app_uploads.id
  versioning_configuration {
    status = local.is_production ? "Enabled" : "Disabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "app_uploads" {
  bucket = aws_s3_bucket.app_uploads.id

  rule {
    id     = "move-old-uploads-to-ia"
    status = local.is_production ? "Enabled" : "Disabled"
    filter { prefix = "profile-pictures/" }
    transition {
      days          = 90
      storage_class = "STANDARD_IA"
    }
  }
}