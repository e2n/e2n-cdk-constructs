package de.e2n.cdk.constructs;

import de.e2n.cdk.model.KnowledgeBaseConfig;
import de.e2n.cdk.model.S3DataSourceConfig;
import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;

import java.util.List;

public class ManagedKnowledgeBase extends Construct {

    private CfnKnowledgeBase knowledgeBase;

    public ManagedKnowledgeBase(final Construct scope,
                                final String id,
                                final KnowledgeBaseConfig knowledgeBaseConfig,
                                final S3DataSourceConfig s3DataSourceConfig) {
        super(scope, id);

        IRole role;
        if (knowledgeBaseConfig.getRole() == null) {
            Role defaultRole = Role.Builder.create(this, "DefaultKnowledgeBaseRole")
                    .assumedBy(new ServicePrincipal("bedrock.amazonaws.com"))
                    .build();
            defaultRole.addToPolicy(PolicyStatement.Builder.create()
                    .actions(List.of("bedrock:InvokeModel"))
                    .resources(List.of(knowledgeBaseConfig.getEmbeddingModelArn()))
                    .build());
            role = defaultRole;
        } else {
            role = knowledgeBaseConfig.getRole();
        }

        var managedKnowledgeBaseConfig = CfnKnowledgeBase.ManagedKnowledgeBaseConfigurationProperty.builder()
                .embeddingModelArn(knowledgeBaseConfig.getEmbeddingModelArn())
                .embeddingModelConfiguration(knowledgeBaseConfig.getEmbeddingModelConfiguration())
                .serverSideEncryptionConfiguration(knowledgeBaseConfig.getServerSideEncryptionConfiguration())
                .build();

        var knowledgeBaseConfiguration = CfnKnowledgeBase.KnowledgeBaseConfigurationProperty.builder()
                .type("MANAGED")
                .managedKnowledgeBaseConfiguration(managedKnowledgeBaseConfig)
                .build();

        var knowledgeBase = CfnKnowledgeBase.Builder.create(this, "ManagedKnowledgeBase")
                .name(knowledgeBaseConfig.getName())
                .description(knowledgeBaseConfig.getDescription())
                .roleArn(role.getRoleArn())
                .knowledgeBaseConfiguration(knowledgeBaseConfiguration)
                .build();

    }
}
