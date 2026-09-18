package de.e2n.cdk.model;

import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase.EmbeddingModelConfigurationProperty;
import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase.ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty;
import software.amazon.awscdk.services.iam.IRole;

/**
 * Die Konfiguration einer Managed Bedrock Knowledge Base. Wird per {@link Builder} initialisiert.
 * <p>
 * Bildet die AWS-Ressource {@code AWS::Bedrock::KnowledgeBase} für den Storage-Typ {@code MANAGED} ab —
 * AWS verwaltet dabei den Vector-Store selbst, es muss keine eigene Storage-Konfiguration (OpenSearch, usw.) angegeben werden.
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
     * {@link KnowledgeBaseConfig} Builder.
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
         * @return {@link Builder} ein neuer Builder.
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * @param name Der Name der Knowledge Base.
         * @return {@link Builder}
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @param description Eine Beschreibung der Knowledge Base. Default: {@code null}
         * @return {@link Builder}
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Die IAM-Role, die Bedrock beim Zugriff auf das Embedding-Modell und die Datenquellen annimmt.
         * <p>
         * Wenn nicht gesetzt, erstellt der zugehörige Construct eine Default-Role. Default: {@code null}
         * @param role Die Service-Role für die Knowledge Base.
         * @return {@link Builder}
         */
        public Builder role(IRole role) {
            this.role = role;
            return this;
        }

        /**
         * Die ARN des Embedding-Modells, mit dem Bedrock Vektoren für die Managed Knowledge Base erzeugt,
         * z.B. {@code arn:aws:bedrock:<region>::foundation-model/amazon.titan-embed-text-v2:0}.
         * <p>
         * Default: {@code null} - wird kostenlose gemanagte Embedding Modell von AWS Bedrock eingesetzt.
         * @param embeddingModelArn Die ARN des Embedding-Modells.
         * @return {@link Builder}
         */
        public Builder embeddingModelArn(String embeddingModelArn) {
            this.embeddingModelArn = embeddingModelArn;
            return this;
        }

        /**
         * Einstellungen für das Embedding-Modell, z.B. Vektordimension oder Datentyp der Embeddings
         * (bei Modellen, die das unterstützen, z.B. Titan Text Embeddings v2).
         * <p>
         * Default: {@code null} (Modell-Standard)
         * @param embeddingModelConfiguration Die Embedding-Modell-Konfiguration.
         * @return {@link Builder}
         */
        public Builder embeddingModelConfiguration(EmbeddingModelConfigurationProperty embeddingModelConfiguration) {
            this.embeddingModelConfiguration = embeddingModelConfiguration;
            return this;
        }

        /**
         * Verschlüsselung des von Bedrock verwalteten Vector-Stores mit einem eigenen KMS-Key statt der
         * AWS-verwalteten Standardverschlüsselung. Default: {@code null} (AWS-verwaltete Verschlüsselung)
         * @param serverSideEncryptionConfiguration Die Verschlüsselungskonfiguration des Vector-Stores.
         * @return {@link Builder}
         */
        public Builder serverSideEncryptionConfiguration(ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration) {
            this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
            return this;
        }

        /**
         * @return {@link KnowledgeBaseConfig}
         * @throws IllegalArgumentException wenn {@link #name(String)} oder {@link #embeddingModelArn(String)} fehlt.
         */
        public KnowledgeBaseConfig build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(
                        "name ist erforderlich, da die AWS::Bedrock::KnowledgeBase-Ressource sonst nicht benannt werden kann.");
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
