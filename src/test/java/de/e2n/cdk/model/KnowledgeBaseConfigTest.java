package de.e2n.cdk.model;

import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase.BedrockEmbeddingModelConfigurationProperty;
import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase.EmbeddingModelConfigurationProperty;
import software.amazon.awscdk.services.bedrock.CfnKnowledgeBase.ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty;

import static org.junit.jupiter.api.Assertions.*;

public class KnowledgeBaseConfigTest {

    @Test
    void defaultsCheck() {
        KnowledgeBaseConfig config = KnowledgeBaseConfig.Builder.create()
                .name("my-knowledgebase")
                .build();

        assertNull(config.getDescription());
        assertNull(config.getRole());
        assertNull(config.getEmbeddingModelArn());
        assertNull(config.getEmbeddingModelConfiguration());
        assertNull(config.getServerSideEncryptionConfiguration());
    }

    @Test
    void nameFehltWirftException() {
        assertThrows(IllegalArgumentException.class, () -> KnowledgeBaseConfig.Builder.create()
                .embeddingModelArn("arn:aws:bedrock:eu-central-1::foundation-model/amazon.titan-embed-text-v2:0")
                .build());
    }

    @Test
    void builderUebernimmtGesetzteWerte() {
        EmbeddingModelConfigurationProperty embeddingModelConfiguration = EmbeddingModelConfigurationProperty.builder()
                .bedrockEmbeddingModelConfiguration(BedrockEmbeddingModelConfigurationProperty.builder()
                        .dimensions(1024)
                        .embeddingDataType("FLOAT32")
                        .build())
                .build();
        ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration =
                ManagedKnowledgeBaseServerSideEncryptionConfigurationProperty.builder()
                        .kmsKeyArn("arn:aws:kms:eu-central-1:123456789012:key/my-key")
                        .build();

        KnowledgeBaseConfig config = KnowledgeBaseConfig.Builder.create()
                .name("my-knowledgebase")
                .description("Managed Knowledge Base")
                .embeddingModelArn("arn:aws:bedrock:eu-central-1::foundation-model/amazon.titan-embed-text-v2:0")
                .embeddingModelConfiguration(embeddingModelConfiguration)
                .serverSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .build();

        assertEquals("my-knowledgebase", config.getName());
        assertEquals("Managed Knowledge Base", config.getDescription());
        assertEquals("arn:aws:bedrock:eu-central-1::foundation-model/amazon.titan-embed-text-v2:0", config.getEmbeddingModelArn());
        assertEquals(embeddingModelConfiguration, config.getEmbeddingModelConfiguration());
        assertEquals(serverSideEncryptionConfiguration, config.getServerSideEncryptionConfiguration());
    }

}
