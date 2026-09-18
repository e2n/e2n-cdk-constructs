package de.e2n.cdk.constructs;

import de.e2n.cdk.model.KnowledgeBaseConfig;
import de.e2n.cdk.model.S3DataSourceConfig;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.bedrock.CfnDataSource;
import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase;
import software.amazon.awscdk.services.iam.*;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

/**
 * Diese Klasse erzeugt mittels AWS CDK eine konfigurierbare AWS Bedrock Managed Knowledge Base
 * mit einem S3-Bucket als Data Source.
 * <p>
 * Die Konfiguration der Knowledge Base erfolgt über {@link KnowledgeBaseConfig}, die Konfiguration
 * der Data Source über {@link S3DataSourceConfig}. Sofern in {@link KnowledgeBaseConfig} keine
 * eigene {@link IRole} angegeben wird, erzeugt das Konstrukt automatisch eine Standard-Rolle mit den
 * benötigten Berechtigungen zum Aufruf des konfigurierten Embedding-Modells.
 * <p>
 * Das CDK-Konstrukt besteht aus den folgenden AWS Ressourcen:
 * - eine AWS Bedrock Knowledge Base ({@link CfnKnowledgeBase})
 * - eine AWS Bedrock Data Source ({@link CfnDataSource}) mit S3 als Datenquelle
 * - optional eine IAM-Rolle ({@link Role}), falls keine eigene Rolle übergeben wurde
 */
public class ManagedKnowledgeBase extends Construct {

    private final CfnKnowledgeBase knowledgeBase;
    private final CfnDataSource dataSource;

    public ManagedKnowledgeBase(final Construct scope,
                                final String id,
                                final KnowledgeBaseConfig knowledgeBaseConfig,
                                final S3DataSourceConfig s3DataSourceConfig) {
        super(scope, id);

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
            s3DataSourceConfig.getBucket().grantRead(role);
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

        var s3Config = CfnDataSource.S3DataSourceConfigurationProperty.builder()
                .bucketArn(s3DataSourceConfig.getBucket().getBucketArn())
                .inclusionPrefixes(s3DataSourceConfig.getInclusionPrefixes())
                .build();

        var dataSourceConfig = CfnDataSource.DataSourceConfigurationProperty.builder()
                .type("S3")
                .s3Configuration(s3Config)
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
