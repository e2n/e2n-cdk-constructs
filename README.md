# e2n-cdk-constructs

A Java library of reusable [AWS CDK](https://aws.amazon.com/cdk/) constructs and stacks for AWS infrastructure projects.

[![Java CI with Maven](https://github.com/E2N-development/e2n-cdk-constructs/actions/workflows/maven.yml/badge.svg)](https://github.com/E2N-development/e2n-cdk-constructs/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/de.e2n/cdk-constructs)](https://central.sonatype.com/artifact/de.e2n/cdk-constructs)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Prerequisites

- Java 21
- Maven
- AWS CDK v2

## Installation

### Maven

```xml
<dependency>
    <groupId>de.e2n</groupId>
    <artifactId>cdk-constructs</artifactId>
    <version>1.1.1</version>
</dependency>
```

## Contents

The library is organized into three packages:

| Package | Description |
|---|---|
| `de.e2n.cdk.constructs` | Reusable CDK constructs for individual AWS resource groups |
| `de.e2n.cdk.stacks` | Complete CDK stacks for common application scenarios |
| `de.e2n.cdk.model` | Configuration models and interfaces |

---

## Constructs

### Compute & Messaging

| Construct | Description |
|---|---|
| `Lambda` | Lambda function with an IAM role, versioned deployment, and optional alias |
| `ScheduledLambda` | Lambda triggered by one or more EventBridge schedule rules (`Schedule → Lambda`) |
| `ConsumerQueue` | SQS queue with an optional dead-letter queue, connected to a consumer Lambda as an event source (`Queue → Consumer`) |
| `ScheduledProducerConsumerQueue` | Complete producer-consumer pattern — the producer Lambda automatically receives `QUEUE_URL` as an environment variable (`Schedule → Producer → Queue → Consumer`) |
| `BatchJob` | AWS Batch job on Fargate triggered by an EventBridge schedule; builds the image from a local directory |
| `EcsService` | Helper construct for ECS Fargate task definitions (ARM64/Linux), including an execution role |

### Networking & Certificates

| Construct | Description |
|---|---|
| `ACMCertificate` | ACM SSL certificate with automatic DNS validation via Route 53 |
| `ExistingDomainName` | Reference to an existing Route 53 domain |
| `VpcPeeringRequester` | Creates a VPC peering connection (requester side) |
| `VpcPeeringAccepter` | Accepts a peering request using a cross-account IAM role (accepter side) |
| `VpcPeeringRouting` | Configures route table entries on both sides |

### SSM Parameter

| Construct | Description |
|---|---|
| `SSMParameter` | Value object for the parameter name, reader role, and Region |
| `SharedSSMParameter` | SSM parameter with cross-account read access via RAM |
| `SSMParameterReader` | Custom resource for reading SSM parameters across Regions and accounts |
| `SSMParameterCrossAccountWriter` | Writes SSM parameters to another account |
| `SSMParameterRAMWriter` | Shares SSM parameters through AWS Resource Access Manager |

### Monitoring & Dashboards

| Construct | Description |
|---|---|
| `ApplicationELBCloudwatchDashboard` | CloudWatch dashboard for an Application Load Balancer |
| `CognitoCloudwatchDashboard` | CloudWatch dashboard for a Cognito user pool |
| `CognitoServiceQuotaCloudwatchDashboard` | CloudWatch dashboard for Cognito service quotas |
| `CognitoServiceQuotaAlarms` | CloudWatch alarms for Cognito service quota limits |

### IAM

| Construct | Description |
|---|---|
| `Policy` | Helper construct for creating an IAM policy |

### Storage

| Construct | Description |
|---|---|
| `S3Bucket` | Configurable S3 bucket with secure defaults (encryption, versioning, public access, lifecycle rules, CORS, static website hosting, and access logs) |

---

## Stacks

### Infrastruktur

| Stack | Description |
|---|---|
| `VpcStack` | VPC with a configurable CIDR block and optional number of NAT gateways |
| `EcsStack` | ECS cluster (Fargate) with capacity providers and Container Insights enabled |
| `RedisStack` | ElastiCache Redis 7 in private subnets, including a security group and Secrets Manager secret |
| `KeyStack` | KMS key with an alias |
| `CertificateStack` | ACM certificate with optional subject alternative names (SANs) |
| `RepositoryStack` | ECR repository encrypted with KMS and configurable lifecycle rules |

---

### CI/CD

| Stack | Description |
|---|---|
| `QuarkusBackendContinuousIntegrationStack` | CodePipeline: CodeCommit → CodeBuild (Quarkus/Docker Build) → ECR |
| `ReactFrontendContinuousIntegrationStack` | CodePipeline: CodeCommit → CodeBuild (React/Docker Build) → ECR |
| `EcsContainerContinuousDeliveryStack` | CodePipeline: ECR → ECS service update (continuous delivery for containers) |
| `CloudfrontContinuousDeliveryStack` | CodePipeline: ECR → S3 + CloudFront invalidation (continuous delivery for frontend) |
| `GithubActionRoleStack` | IAM OIDC provider and role for GitHub Actions with ECR access |

---

### Networking & Landing Zone

| Stack | Description |
|---|---|
| `VpcPeeringRequesterStack` | VPC peering request, including an SSM parameter for the peering connection ID |
| `VpcPeeringAccepterStack` | Accepts peering requests from another account |
| `VpcPeeringRoutingStack` | Configures routing tables on both sides |
| `NameServerRecordStack` | Route 53 NS record in a parent hosted zone |
| `LandingZoneEventBusStack` | Event bus with a cross-account access policy and CloudWatch log group |

---

### Monitoring & Security

| Stack | Description |
|---|---|
| `InspectorFindingsAlarmStack` | EventBridge rule for critical AWS Inspector findings → SNS |
| `SSMDocumentsBlockPublicSharingStack` | Prevents public sharing of SSM documents |
| `SharedSSMParametersReaderStack` | Reads SSM parameters shared from other accounts via RAM |
| `PipelinesTriggerStack` | Triggers multiple CodePipelines through a central event |

---

## Development

### Build

```bash
mvn package
```

### Publishing

The library is automatically published to [Maven Central](https://central.sonatype.com/artifact/de.e2n/cdk-constructs) via GitHub Actions whenever a commit is pushed to the `main` branch.

## License

[MIT](LICENSE) © E2N GmbH
