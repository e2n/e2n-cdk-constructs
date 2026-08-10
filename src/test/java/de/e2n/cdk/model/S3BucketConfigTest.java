package de.e2n.cdk.model;

import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.BucketEncryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class S3BucketConfigTest {

    @Test
    void defaultsSindSicherKonfiguriert() {
        S3BucketConfig config = S3BucketConfig.Builder.create().build();

        assertNull(config.getBucketName());
        assertFalse(config.isVersioned());
        assertEquals(BucketEncryption.S3_MANAGED, config.getEncryption());
        assertNull(config.getEncryptionKey());
        assertEquals(BlockPublicAccess.BLOCK_ALL, config.getBlockPublicAccess());
        assertFalse(config.isPublicReadAccess());
        assertEquals(RemovalPolicy.RETAIN, config.getRemovalPolicy());
        assertFalse(config.isAutoDeleteObjects());
        assertTrue(config.isEnforceSSL());
        assertTrue(config.getLifecycleRules().isEmpty());
        assertTrue(config.getCorsRules().isEmpty());
        assertNull(config.getWebsiteIndexDocument());
        assertNull(config.getWebsiteErrorDocument());
        assertNull(config.getServerAccessLogsBucket());
        assertNull(config.getServerAccessLogsPrefix());
        assertFalse(config.isEventBridgeEnabled());
    }

    @Test
    void builderUebernimmtGesetzteWerte() {
        S3BucketConfig config = S3BucketConfig.Builder.create()
                .bucketName("my-bucket")
                .versioned(true)
                .encryption(BucketEncryption.KMS)
                .publicReadAccess(true)
                .blockPublicAccess(BlockPublicAccess.BLOCK_ACLS_ONLY)
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .enforceSSL(false)
                .eventBridgeEnabled(true)
                .websiteIndexDocument("index.html")
                .websiteErrorDocument("error.html")
                .serverAccessLogsPrefix("logs/")
                .build();

        assertEquals("my-bucket", config.getBucketName());
        assertTrue(config.isVersioned());
        assertEquals(BucketEncryption.KMS, config.getEncryption());
        assertTrue(config.isPublicReadAccess());
        assertEquals(BlockPublicAccess.BLOCK_ACLS_ONLY, config.getBlockPublicAccess());
        assertEquals(RemovalPolicy.DESTROY, config.getRemovalPolicy());
        assertTrue(config.isAutoDeleteObjects());
        assertFalse(config.isEnforceSSL());
        assertTrue(config.isEventBridgeEnabled());
        assertEquals("index.html", config.getWebsiteIndexDocument());
        assertEquals("error.html", config.getWebsiteErrorDocument());
        assertEquals("logs/", config.getServerAccessLogsPrefix());
    }

    @Test
    void autoDeleteObjectsOhneRemovalPolicyDestroyWirftException() {
        assertThrows(IllegalArgumentException.class, () -> S3BucketConfig.Builder.create()
                .autoDeleteObjects(true)
                .build());
    }

    @Test
    void autoDeleteObjectsMitRemovalPolicyDestroyIstErlaubt() {
        S3BucketConfig config = S3BucketConfig.Builder.create()
                .autoDeleteObjects(true)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        assertTrue(config.isAutoDeleteObjects());
        assertEquals(RemovalPolicy.DESTROY, config.getRemovalPolicy());
    }

}
