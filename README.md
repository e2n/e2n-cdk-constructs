# e2n-cdk-constructs

Eine Java-Bibliothek mit wiederverwendbaren [AWS CDK](https://aws.amazon.com/cdk/) Constructs und Stacks für den Einsatz in AWS-Infrastrukturprojekten.

[![Java CI with Maven](https://github.com/E2N-development/e2n-cdk-constructs/actions/workflows/maven.yml/badge.svg)](https://github.com/E2N-development/e2n-cdk-constructs/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/de.e2n/cdk-constructs)](https://central.sonatype.com/artifact/de.e2n/cdk-constructs)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Voraussetzungen

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

## Inhalt

Die Bibliothek gliedert sich in drei Pakete:

| Paket | Beschreibung |
|---|---|
| `de.e2n.cdk.constructs` | Wiederverwendbare CDK-Constructs für einzelne AWS-Ressourcengruppen |
| `de.e2n.cdk.stacks` | Vollständige CDK-Stacks für typische Anwendungsszenarien |
| `de.e2n.cdk.model` | Konfigurationsmodelle und Interfaces |

---

## Constructs

### Compute & Messaging

| Construct | Beschreibung |
|---|---|
| `Lambda` | Lambda Function mit IAM Role, versioniertem Deployment und optionalem Alias |
| `ScheduledLambda` | Lambda getriggert durch eine oder mehrere EventBridge-Schedule-Regeln (`Schedule → Lambda`) |
| `ConsumerQueue` | SQS-Queue mit optionaler Dead-Letter-Queue, verbunden als Event-Source mit einer Consumer-Lambda (`Queue → Consumer`) |
| `ScheduledProducerConsumerQueue` | Vollständiges Producer-Consumer-Pattern – Producer-Lambda erhält `QUEUE_URL` automatisch als Umgebungsvariable (`Schedule → Producer → Queue → Consumer`) |
| `BatchJob` | Per EventBridge-Schedule getriggerter AWS Batch Job auf Fargate-Basis; Image-Build aus lokalem Verzeichnis |
| `EcsService` | Hilfskonstrukt für ECS Fargate Task Definitions (ARM64/Linux) inkl. Execution Role |

### Netzwerk & Zertifikate

| Construct | Beschreibung |
|---|---|
| `ACMCertificate` | ACM-SSL-Zertifikat mit automatischer DNS-Validierung via Route 53 |
| `ExistingDomainName` | Referenz auf eine bereits existierende Route 53 Domain |
| `VpcPeeringRequester` | Erstellt eine VPC-Peering-Verbindung (Requester-Seite) |
| `VpcPeeringAccepter` | Akzeptiert eine Peering-Anfrage mit Cross-Account IAM Role (Accepter-Seite) |
| `VpcPeeringRouting` | Konfiguriert Route-Table-Einträge beider Seiten |

### SSM Parameter

| Construct | Beschreibung |
|---|---|
| `SSMParameter` | Wertobjekt für Parameter-Name, Reader-Role und Region |
| `SharedSSMParameter` | SSM-Parameter mit Cross-Account-Lesezugriff via RAM |
| `SSMParameterReader` | Custom Resource zum Lesen von SSM-Parametern cross-region/cross-account |
| `SSMParameterCrossAccountWriter` | Schreibt SSM-Parameter in ein fremdes Konto |
| `SSMParameterRAMWriter` | Teilt SSM-Parameter über AWS Resource Access Manager |

### Monitoring & Dashboards

| Construct | Beschreibung |
|---|---|
| `ApplicationELBCloudwatchDashboard` | CloudWatch-Dashboard für einen Application Load Balancer |
| `CognitoCloudwatchDashboard` | CloudWatch-Dashboard für einen Cognito User Pool |
| `CognitoServiceQuotaCloudwatchDashboard` | CloudWatch-Dashboard für Cognito Service Quotas |
| `CognitoServiceQuotaAlarms` | CloudWatch-Alarme für Cognito Service Quota Limits |

### IAM

| Construct | Beschreibung |
|---|---|
| `Policy` | Hilfskonstrukt zum Erstellen einer IAM Policy |

### Storage

| Construct | Beschreibung |
|---|---|
| `S3Bucket` | Konfigurierbarer S3-Bucket (Verschlüsselung, Versionierung, Public Access, Lifecycle-Regeln, CORS, Static Website Hosting, Access Logs) mit sicheren Defaults |

---

## Stacks

### Infrastruktur

| Stack | Beschreibung |
|---|---|
| `VpcStack` | VPC mit konfigurierbarem CIDR-Block und optionaler NAT-Gateway-Anzahl |
| `EcsStack` | ECS-Cluster (Fargate) mit aktivierten Capacity Providers und Container Insights |
| `RedisStack` | ElastiCache Redis 7 in privaten Subnetzen, inkl. Security Group und Secrets Manager Secret |
| `KeyStack` | KMS-Schlüssel mit Alias |
| `CertificateStack` | ACM-Zertifikat mit optionalen Subject Alternative Names (SAN) |
| `RepositoryStack` | ECR-Repository (KMS-verschlüsselt) mit konfigurierbaren Lifecycle-Regeln |

---

### CI/CD

| Stack | Beschreibung |
|---|---|
| `QuarkusBackendContinuousIntegrationStack` | CodePipeline: CodeCommit → CodeBuild (Quarkus/Docker Build) → ECR |
| `ReactFrontendContinuousIntegrationStack` | CodePipeline: CodeCommit → CodeBuild (React/Docker Build) → ECR |
| `EcsContainerContinuousDeliveryStack` | CodePipeline: ECR → ECS-Service-Update (Continuous Delivery für Container) |
| `CloudfrontContinuousDeliveryStack` | CodePipeline: ECR → S3 + CloudFront Invalidierung (Continuous Delivery für Frontend) |
| `GithubActionRoleStack` | IAM OIDC-Provider und Role für GitHub Actions mit ECR-Zugriff |

---

### Networking & Landing Zone

| Stack | Beschreibung |
|---|---|
| `VpcPeeringRequesterStack` | VPC-Peering-Anfrage inkl. SSM-Parameter für die Peering-Connection-ID |
| `VpcPeeringAccepterStack` | Akzeptiert Peering-Anfragen aus einem anderen Konto |
| `VpcPeeringRoutingStack` | Konfiguriert Routing-Tabellen beider Seiten |
| `NameServerRecordStack` | Route 53 NS-Record in einer übergeordneten Hosted Zone |
| `LandingZoneEventBusStack` | EventBus mit Cross-Account-Zugriffsrichtlinie und CloudWatch-Log-Gruppe |

---

### Monitoring & Sicherheit

| Stack | Beschreibung |
|---|---|
| `InspectorFindingsAlarmStack` | EventBridge-Regel für kritische AWS Inspector Findings → SNS |
| `SSMDocumentsBlockPublicSharingStack` | Verhindert das öffentliche Teilen von SSM-Dokumenten |
| `SharedSSMParametersReaderStack` | Liest geteilte SSM-Parameter aus anderen Konten via RAM |
| `PipelinesTriggerStack` | Triggert mehrere CodePipelines über ein zentrales Event |

---

## Entwicklung

### Build

```bash
mvn package
```

### Veröffentlichung

Die Bibliothek wird automatisch via GitHub Actions auf [Maven Central](https://central.sonatype.com/artifact/de.e2n/cdk-constructs) veröffentlicht, sobald ein Push auf den `main`-Branch erfolgt.

## Lizenz

[MIT](LICENSE) © E2N GmbH
