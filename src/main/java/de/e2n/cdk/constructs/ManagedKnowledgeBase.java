package de.e2n.cdk.constructs;

import de.e2n.cdk.model.KnowledgeBaseConfig;
import de.e2n.cdk.model.S3BucketConfig;
import de.e2n.cdk.model.S3DataSourceConfig;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.bedrock.CfnDataSource;
import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.s3.IBucket;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class creates a configurable AWS Bedrock Managed Knowledge Base with an S3 bucket
 * as data source using AWS CDK.
 * <p>
 * The Knowledge Base configuration is done via {@link KnowledgeBaseConfig}, and the data source
 * configuration via {@link S3DataSourceConfig}. If no custom bucket is specified in {@link S3DataSourceConfig},
 * the construct automatically creates a default S3 bucket.
 * If no custom {@link IRole} is specified in
 * {@link KnowledgeBaseConfig}, the construct automatically creates a default role with the
 * permissions required to invoke the configured embedding model.
 * <p>
 * The CDK construct consists of the following AWS resources:
 * - an AWS Bedrock Knowledge Base ({@link CfnKnowledgeBase})
 * - an AWS Bedrock Data Source ({@link CfnDataSource}) using S3 as data source
 * - optionally an IAM role ({@link Role}), if no custom role was provided
 */
public class ManagedKnowledgeBase extends Construct {

    private final CfnKnowledgeBase knowledgeBase;
    private final CfnDataSource dataSource;

    public ManagedKnowledgeBase(final Construct scope,
                                final String id,
                                final KnowledgeBaseConfig knowledgeBaseConfig,
                                final S3DataSourceConfig s3DataSourceConfig) {
        super(scope, id);

        IBucket bucket;
        if (s3DataSourceConfig.getBucket() == null) {
            bucket = new S3Bucket(this, "KnowledgeBaseS3Bucket", S3BucketConfig.Builder.create().build()).getBucket();
        } else {
            bucket = s3DataSourceConfig.getBucket();
        }

        IRole role;
        if (knowledgeBaseConfig.getRole() == null) {
            Role defaultRole = Role.Builder.create(this, "DefaultKnowledgeBaseRole")
                    .assumedBy(new ServicePrincipal("bedrock.amazonaws.com", ServicePrincipalOpts.builder()
                            .conditions(Map.of("StringEquals", Map.of("aws:SourceAccount", Stack.of(this).getAccount())))
                            .build()))
                    .build();

            if (knowledgeBaseConfig.getEmbeddingModelArn() != null) {
                defaultRole.addToPolicy(PolicyStatement.Builder.create()
                        .actions(List.of("bedrock:InvokeModel"))
                        .resources(List.of(knowledgeBaseConfig.getEmbeddingModelArn()))
                        .build());
            }
            role = defaultRole;
            bucket.grantRead(role);
        } else {
            role = knowledgeBaseConfig.getRole();
        }

        var managedKnowledgeBaseConfig = CfnKnowledgeBase.ManagedKnowledgeBaseConfigurationProperty.builder()
                .embeddingModelType(knowledgeBaseConfig.getEmbeddingModelArn() == null ? "MANAGED" : "CUSTOM")
                .embeddingModelArn(knowledgeBaseConfig.getEmbeddingModelArn())
                .embeddingModelConfiguration(knowledgeBaseConfig.getEmbeddingModelConfiguration())
                .serverSideEncryptionConfiguration(knowledgeBaseConfig.getServerSideEncryptionConfiguration())
                .build();

        var knowledgeBaseConfiguration = CfnKnowledgeBase.KnowledgeBaseConfigurationProperty.builder()
                .type("MANAGED")
                .managedKnowledgeBaseConfiguration(managedKnowledgeBaseConfig)
                .build();

        knowledgeBase = CfnKnowledgeBase.Builder.create(this, "ManagedKnowledgeBase")
                .name(knowledgeBaseConfig.getName())
                .description(knowledgeBaseConfig.getDescription())
                .roleArn(role.getRoleArn())
                .knowledgeBaseConfiguration(knowledgeBaseConfiguration)
                .build();

        Map<String, Object> connectionConfiguration = Map.of(
                "bucketName", bucket.getBucketName(),
                "bucketOwnerAccountId", Stack.of(this).getAccount()
        );

        Map<String, Object> connectorParameters = new HashMap<>();
        connectorParameters.put("type", "S3");
        connectorParameters.put("version", "1");
        connectorParameters.put("connectionConfiguration", connectionConfiguration);
        if (s3DataSourceConfig.getInclusionPrefixes() != null) {
            connectorParameters.put("filterConfiguration", Map.of(
                    "inclusionPrefixes", s3DataSourceConfig.getInclusionPrefixes()
            ));
        }

        var managedConnectorConfig = CfnDataSource.ManagedKnowledgeBaseConnectorConfigurationProperty.builder()
                .connectorParameters(connectorParameters)
                .build();

        var dataSourceConfig = CfnDataSource.DataSourceConfigurationProperty.builder()
                .type("MANAGED_KNOWLEDGE_BASE_CONNECTOR")
                .managedKnowledgeBaseConnectorConfiguration(managedConnectorConfig)
                .build();

        dataSource = CfnDataSource.Builder.create(this, "KnowledgeBaseDataSource")
                .name(s3DataSourceConfig.getName())
                .knowledgeBaseId(knowledgeBase.getAttrKnowledgeBaseId())
                .description(s3DataSourceConfig.getDescription())
                .dataSourceConfiguration(dataSourceConfig)
                .vectorIngestionConfiguration(s3DataSourceConfig.getVectorIngestionConfiguration())
                .serverSideEncryptionConfiguration(s3DataSourceConfig.getServerSideEncryptionConfiguration())
                .build();
    }

    public CfnKnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public CfnDataSource getDataSource() {
        return dataSource;
    }
}
