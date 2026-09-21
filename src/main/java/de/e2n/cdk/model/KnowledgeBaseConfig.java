package de.e2n.cdk.model;

import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase.EmbeddingModelConfigurationProperty;
import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase.ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty;
import software.amazon.awscdk.services.iam.IRole;

/**
 * The configuration of a Managed Bedrock Knowledge Base. Initialized via {@link Builder}.
 * <p>
 * Represents the AWS resource {@code AWS::Bedrock::KnowledgeBase} for storage type {@code MANAGED} —
 * AWS manages the vector store itself, so no custom storage configuration (OpenSearch, etc.) needs to be provided.
 */
public class KnowledgeBaseConfig {

    private final String name;
    private final String description;
    private final IRole role;
    private final String embeddingModelArn;
    private final EmbeddingModelConfigurationProperty embeddingModelConfiguration;
    private final ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration;

    public KnowledgeBaseConfig(String name,
                               String description,
                               IRole role,
                               String embeddingModelArn,
                               EmbeddingModelConfigurationProperty embeddingModelConfiguration,
                               ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration) {
        this.name = name;
        this.description = description;
        this.role = role;
        this.embeddingModelArn = embeddingModelArn;
        this.embeddingModelConfiguration = embeddingModelConfiguration;
        this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public IRole getRole() {
        return role;
    }

    public String getEmbeddingModelArn() {
        return embeddingModelArn;
    }

    public EmbeddingModelConfigurationProperty getEmbeddingModelConfiguration() {
        return embeddingModelConfiguration;
    }

    public ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty getServerSideEncryptionConfiguration() {
        return serverSideEncryptionConfiguration;
    }

    /**
     * Builder for {@link KnowledgeBaseConfig}.
     */
    public static class Builder {

        private String name;
        private String description;
        private IRole role;
        private String embeddingModelArn;
        private EmbeddingModelConfigurationProperty embeddingModelConfiguration;
        private ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration;

        private Builder() {
        }

        /**
         * @return {@link Builder} a new builder.
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * @param name The name of the knowledge base.
         * @return {@link Builder}
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @param description A description of the knowledge base. Default: {@code null}
         * @return {@link Builder}
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * The IAM role that Bedrock assumes when accessing the embedding model and the data sources.
         * <p>
         * If not set, the associated construct creates a default role. Default: {@code null}
         * @param role The service role for the knowledge base.
         * @return {@link Builder}
         */
        public Builder role(IRole role) {
            this.role = role;
            return this;
        }

        /**
         * The ARN of the embedding model that Bedrock uses to generate vectors for the managed knowledge base,
         * e.g. {@code arn:aws:bedrock:<region>::foundation-model/amazon.titan-embed-text-v2:0}.
         * <p>
         * Default: {@code null} - AWS Bedrock's free managed embedding model is used.
         * @param embeddingModelArn The ARN of the embedding model.
         * @return {@link Builder}
         */
        public Builder embeddingModelArn(String embeddingModelArn) {
            this.embeddingModelArn = embeddingModelArn;
            return this;
        }

        /**
         * Settings for the embedding model, e.g. vector dimension or data type of the embeddings
         * (for models that support it, e.g. Titan Text Embeddings v2).
         * <p>
         * Default: {@code null} (model default)
         * @param embeddingModelConfiguration The embedding model configuration.
         * @return {@link Builder}
         */
        public Builder embeddingModelConfiguration(EmbeddingModelConfigurationProperty embeddingModelConfiguration) {
            this.embeddingModelConfiguration = embeddingModelConfiguration;
            return this;
        }

        /**
         * Encryption of the Bedrock-managed vector store with a custom KMS key instead of the
         * AWS-managed default encryption. Default: {@code null} (AWS-managed encryption)
         * @param serverSideEncryptionConfiguration The encryption configuration of the vector store.
         * @return {@link Builder}
         */
        public Builder serverSideEncryptionConfiguration(ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration) {
            this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
            return this;
        }

        /**
         * @return {@link KnowledgeBaseConfig}
         * @throws IllegalArgumentException if {@link #name(String)} or {@link #embeddingModelArn(String)} is missing.
         */
        public KnowledgeBaseConfig build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(
                        "name is required, otherwise the AWS::Bedrock::KnowledgeBase resource cannot be named.");
            }

            return new KnowledgeBaseConfig(
                    name,
                    description,
                    role,
                    embeddingModelArn,
                    embeddingModelConfiguration,
                    serverSideEncryptionConfiguration
            );
        }

    }

}
