package de.e2n.cdk.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.bedrock.CfnDataSource.ChunkingConfigurationProperty;
import software.amazon.awscdk.services.bedrock.CfnDataSource.ServerSideEncryptionConfigurationProperty;
import software.amazon.awscdk.services.bedrock.CfnDataSource.VectorIngestionConfigurationProperty;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.IBucket;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3DataSourceConfigTest {

    private IBucket bucket;

    @BeforeEach
    void setUp() {
        Stack stack = new Stack(new App(), "TestStack");
        bucket = Bucket.Builder.create(stack, "TestBucket").build();
    }

    @Test
    void defaultsCheck() {
        S3DataSourceConfig config = S3DataSourceConfig.Builder.create()
                .name("my-datasource")
                .bucket(bucket)
                .build();

        assertNull(config.getInclusionPrefixes());
        assertNull(config.getDescription());
        assertNull(config.getServerSideEncryptionConfiguration());
        assertNull(config.getVectorIngestionConfiguration());
    }

    @Test
    void builderUebernimmtGesetzteWerte() {
        ServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration =
                ServerSideEncryptionConfigurationProperty.builder()
                        .kmsKeyArn("arn:aws:kms:eu-central-1:123456789012:key/my-key")
                        .build();
        VectorIngestionConfigurationProperty vectorIngestionConfiguration = VectorIngestionConfigurationProperty.builder()
                .chunkingConfiguration(ChunkingConfigurationProperty.builder()
                        .chunkingStrategy("NONE")
                        .build())
                .build();

        S3DataSourceConfig config = S3DataSourceConfig.Builder.create()
                .name("my-datasource")
                .bucket(bucket)
                .inclusionPrefixes(List.of("docs/"))
                .description("Testdaten")
                .serverSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .vectorIngestionConfiguration(vectorIngestionConfiguration)
                .build();

        assertEquals("my-datasource", config.getName());
        assertEquals(bucket, config.getBucket());
        assertEquals(List.of("docs/"), config.getInclusionPrefixes());
        assertEquals("Testdaten", config.getDescription());
        assertEquals(serverSideEncryptionConfiguration, config.getServerSideEncryptionConfiguration());
        assertEquals(vectorIngestionConfiguration, config.getVectorIngestionConfiguration());
    }

    @Test
    void nameFehltWirftException() {
        assertThrows(IllegalArgumentException.class, () -> S3DataSourceConfig.Builder.create()
                .bucket(bucket)
                .build());
    }

    @Test
    void bucketOptionalWennNichtGesetzt() {
        S3DataSourceConfig config = S3DataSourceConfig.Builder.create()
                .name("my-datasource")
                .build();

        assertNull(config.getBucket());
    }

    @Test
    void mehrAlsEinInclusionPrefixWirftException() {
        assertThrows(IllegalArgumentException.class, () -> S3DataSourceConfig.Builder.create()
                .name("my-datasource")
                .bucket(bucket)
                .inclusionPrefixes(List.of("docs/", "faq/"))
                .build());
    }

    @Test
    void genauEinInclusionPrefixIstErlaubt() {
        S3DataSourceConfig config = S3DataSourceConfig.Builder.create()
                .name("my-datasource")
                .bucket(bucket)
                .inclusionPrefixes(List.of("docs/"))
                .build();

        assertEquals(List.of("docs/"), config.getInclusionPrefixes());
    }
}
