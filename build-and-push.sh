#!/bin/bash

# ==============================================================================
# build-and-push.sh — Docker Build & ECR Push Script
# ==============================================================================
# Usage:
#   ./build-and-push.sh                    # Uses git SHA as version
#   ./build-and-push.sh v1.0.5             # Uses explicit version tag
#   ./build-and-push.sh v1.0.5 dev         # Explicit version + environment
#
# Requirements:
#   - AWS CLI configured (aws configure OR IAM role via EC2/GitHub Actions)
#   - Docker running locally
#   - ECR repository already created (via Terraform or manually)
#
# Matches Terraform variables:
#   var.aws_region   = "ap-south-1"
#   var.project_name = "digital-health-platform"
#   var.environment  = "dev" | "staging" | "prod"
# ==============================================================================

set -euo pipefail  # Exit on error | undefined vars | pipe failures
# -e  → exit immediately if any command fails
# -u  → treat unset variables as errors
# -o pipefail → catch errors inside pipes (e.g. cmd1 | cmd2)

# ─── 1. CONFIGURATION ─────────────────────────────────────────────────────────
# Must match var.aws_region in your Terraform variables.tf
AWS_REGION="ap-south-1"

# Must match var.project_name in your Terraform variables.tf
APP_NAME="digital-health-platform"

# Environment: dev | staging | prod
# Matches var.environment → drives naming like "digital-health-platform-dev"
ENVIRONMENT=${2:-"dev"}

# Validate environment input
if [[ ! "${ENVIRONMENT}" =~ ^(dev|staging|prod)$ ]]; then
    echo "❌ Error: ENVIRONMENT must be 'dev', 'staging', or 'prod'" >&2
    echo "   Usage: $0 [version] [environment]" >&2
    exit 1
fi

# Enable Docker BuildKit for layer caching and parallel stage builds
export DOCKER_BUILDKIT=1

echo ""
echo "══════════════════════════════════════════════════════"
echo "   ${APP_NAME} — Docker Build & ECR Push"
echo "══════════════════════════════════════════════════════"

# ─── 2. RESOLVE AWS ACCOUNT IDENTITY ──────────────────────────────────────────
echo ""
echo "▶ Resolving AWS Account Identity..."

if ! AWS_ACCOUNT_ID=$(aws sts get-caller-identity \
    --query Account \
    --output text \
    --region "${AWS_REGION}" 2>/dev/null); then
    echo "❌ Error: AWS CLI is not authenticated." >&2
    echo "   Run 'aws configure' locally OR ensure IAM role is attached in CI/CD." >&2
    exit 1
fi

echo "   ✅ AWS Account ID : ${AWS_ACCOUNT_ID}"
echo "   ✅ Region         : ${AWS_REGION}"
echo "   ✅ Environment    : ${ENVIRONMENT}"

# ─── 3. DERIVE ECR REGISTRY & REPO ────────────────────────────────────────────
# ECR Registry: {account}.dkr.ecr.{region}.amazonaws.com
ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# ECR Repository name — matches what Terraform creates
# Pattern: {project_name}  (ECR repos are account-global, no env prefix needed)
ECR_REPO="${ECR_REGISTRY}/${APP_NAME}"

# ─── 4. VERSIONING STRATEGY ───────────────────────────────────────────────────
# Priority: CLI arg → git short SHA → "latest" fallback
VERSION=${1:-$(git rev-parse --short HEAD 2>/dev/null || echo "latest")}

# ISO-8601 UTC timestamp for build metadata labels
BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ')

# Full image URI — this is what goes into Terraform:
# ecr_image_uri = "{account}.dkr.ecr.ap-south-1.amazonaws.com/digital-health-platform:v1.0.5"
FULL_IMAGE_URI="${ECR_REPO}:${VERSION}"

echo ""
echo "──────────────────────────────────────────────────────"
echo "   Build Specification:"
echo "   Application  : ${APP_NAME}"
echo "   Environment  : ${ENVIRONMENT}"
echo "   AWS Region   : ${AWS_REGION}"
echo "   ECR Registry : ${ECR_REGISTRY}"
echo "   ECR Repo     : ${ECR_REPO}"
echo "   Version Tag  : ${VERSION}"
echo "   Full URI     : ${FULL_IMAGE_URI}"
echo "   Build Date   : ${BUILD_DATE}"
echo "──────────────────────────────────────────────────────"
echo ""

# ─── 5. VERIFY ECR REPOSITORY EXISTS ──────────────────────────────────────────
# Fail fast if repo doesn't exist — prevents pushing to wrong registry
echo "▶ Verifying ECR repository exists..."

if ! aws ecr describe-repositories \
    --repository-names "${APP_NAME}" \
    --region "${AWS_REGION}" \
    --query 'repositories[0].repositoryName' \
    --output text > /dev/null 2>&1; then
    echo "❌ Error: ECR repository '${APP_NAME}' does not exist in ${AWS_REGION}." >&2
    echo "   Create it first:" >&2
    echo "   aws ecr create-repository --repository-name ${APP_NAME} --region ${AWS_REGION}" >&2
    echo "   OR run Terraform to provision infrastructure first." >&2
    exit 1
fi

echo "   ✅ ECR repository verified: ${APP_NAME}"

# ─── 6. ECR AUTHENTICATION ────────────────────────────────────────────────────
echo ""
echo "▶ Authenticating Docker with AWS ECR..."

aws ecr get-login-password \
    --region "${AWS_REGION}" | \
    docker login \
        --username AWS \
        --password-stdin \
        "${ECR_REGISTRY}"

echo "   ✅ Docker authenticated with ECR"

# ─── 7. DOCKER BUILD ──────────────────────────────────────────────────────────
# Platform: linux/amd64 — matches ECS task definition:
#   runtime_platform { cpu_architecture = "X86_64" }
#
# Cache strategy:
#   --cache-from → pulls cached layers from ECR:latest (speeds up CI builds)
#   BUILDKIT_INLINE_CACHE=1 → embeds cache metadata INTO the image
#                             so next build can USE this image as cache source
echo ""
echo "▶ Building Docker image [linux/amd64]..."

docker build \
    --platform linux/amd64 \
    --build-arg BUILD_DATE="${BUILD_DATE}" \
    --build-arg VCS_REF="${VERSION}" \
    --build-arg ENVIRONMENT="${ENVIRONMENT}" \
    --build-arg BUILDKIT_INLINE_CACHE=1 \
    --cache-from "type=registry,ref=${ECR_REPO}:latest" \
    --label "org.opencontainers.image.created=${BUILD_DATE}" \
    --label "org.opencontainers.image.revision=${VERSION}" \
    --label "org.opencontainers.image.title=${APP_NAME}" \
    --label "org.opencontainers.image.source=https://github.com/your-org/${APP_NAME}" \
    -t "${APP_NAME}:${VERSION}" \
    -f Dockerfile \
    .

echo "   ✅ Docker build completed"

# ─── 8. TAG IMAGES ────────────────────────────────────────────────────────────
# Two tags pushed to ECR:
#
# 1. Immutable version tag  (e.g. :v1.0.5 or :abc1234)
#    → Used in Terraform ecr_image_uri — pinned, never changes
#    → Used by ECS task definition for reproducible deployments
#
# 2. Rolling :latest tag
#    → Used as --cache-from source in next build (speeds up CI)
#    → NOT used by ECS directly (use versioned tag for predictability)
echo ""
echo "▶ Tagging images..."

# Immutable versioned tag → goes into Terraform ecr_image_uri
docker tag "${APP_NAME}:${VERSION}" "${ECR_REPO}:${VERSION}"
echo "   ✅ Tagged: ${ECR_REPO}:${VERSION}"

# Rolling latest → for build cache only
docker tag "${APP_NAME}:${VERSION}" "${ECR_REPO}:latest"
echo "   ✅ Tagged: ${ECR_REPO}:latest"

# Environment-specific tag → useful for identifying which env an image was built for
docker tag "${APP_NAME}:${VERSION}" "${ECR_REPO}:${ENVIRONMENT}-latest"
echo "   ✅ Tagged: ${ECR_REPO}:${ENVIRONMENT}-latest"

# ─── 9. PUSH TO ECR ───────────────────────────────────────────────────────────
echo ""
echo "▶ Pushing images to ECR..."

# Push versioned tag first (most important)
echo "   Pushing :${VERSION}..."
docker push "${ECR_REPO}:${VERSION}"
echo "   ✅ Pushed: ${ECR_REPO}:${VERSION}"

# Push latest (for cache)
echo "   Pushing :latest..."
docker push "${ECR_REPO}:latest"
echo "   ✅ Pushed: ${ECR_REPO}:latest"

# Push environment tag
echo "   Pushing :${ENVIRONMENT}-latest..."
docker push "${ECR_REPO}:${ENVIRONMENT}-latest"
echo "   ✅ Pushed: ${ECR_REPO}:${ENVIRONMENT}-latest"

# ─── 10. CLEANUP LOCAL IMAGES (Optional — saves disk space in CI) ─────────────
# Uncomment if running in CI where disk space is limited
# echo ""
# echo "▶ Cleaning up local images..."
# docker rmi "${APP_NAME}:${VERSION}" || true
# docker rmi "${ECR_REPO}:${VERSION}" || true
# docker rmi "${ECR_REPO}:latest"     || true

# ─── 11. TERRAFORM UPDATE HINT ────────────────────────────────────────────────
echo ""
echo "══════════════════════════════════════════════════════"
echo "   ✅ Build & Push Complete!"
echo "══════════════════════════════════════════════════════"
echo ""
echo "   Pushed Image URI:"
echo "   ${FULL_IMAGE_URI}"
echo ""
echo "   Next Steps:"
echo "   ──────────────────────────────────────────────────"
echo "   1. Update your Terraform tfvars:"
echo "      ecr_image_uri = \"${FULL_IMAGE_URI}\""
echo ""
echo "   File: environments/${ENVIRONMENT}/terraform.tfvars"
echo ""
echo "   2. Deploy infrastructure:"
echo "      terraform apply -var-file=\"environments/${ENVIRONMENT}/terraform.tfvars\""
echo ""
echo "   3. OR update ECS service directly (if infra already exists):"
echo "      aws ecs update-service \\"
echo "          --cluster digital-health-platform-${ENVIRONMENT}-cluster \\"
echo "          --service digital-health-platform-${ENVIRONMENT} \\"
echo "          --force-new-deployment \\"
echo "          --region ${AWS_REGION}"
echo "══════════════════════════════════════════════════════"

# ─── 12. CI/CD OUTPUT (GitHub Actions) ────────────────────────────────────────
# Writes outputs to GitHub Actions workspace if running in CI
# These become available as outputs for downstream workflow steps:
#   ${{ steps.build.outputs.IMAGE_URI }}
#   ${{ steps.build.outputs.VERSION }}
#   ${{ steps.build.outputs.ENVIRONMENT }}
if [ -n "${GITHUB_OUTPUT:-}" ] && [ -w "${GITHUB_OUTPUT}" ]; then
    echo ""
    echo "▶ Writing GitHub Actions outputs..."
    {
        echo "IMAGE_URI=${FULL_IMAGE_URI}"
        echo "ECR_REGISTRY=${ECR_REGISTRY}"
        echo "VERSION=${VERSION}"
        echo "ENVIRONMENT=${ENVIRONMENT}"
        echo "APP_NAME=${APP_NAME}"
    } >> "${GITHUB_OUTPUT}"
    echo "   ✅ GitHub Actions outputs written"
fi