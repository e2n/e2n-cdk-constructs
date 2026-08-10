package de.e2n.cdk.constructs;

import de.e2n.cdk.model.S3BucketConfig;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

/**
 * Diese Klasse erzeugt mittels AWS CDK einen konfigurierbaren AWS S3-Bucket.
 * <p>
 * Die Konfiguration erfolgt über {@link S3BucketConfig}, welche standardmäßig einen möglichst sicher
 * konfigurierten Bucket erzeugt (Verschlüsselung, Blockierung öffentlichen Zugriffs, verpflichtendes SSL/TLS).
 * <p>
 * Das CDK-Konstrukt besteht aus den folgenden AWS Ressourcen:
 * - ein AWS S3-Bucket
 */
public class S3Bucket extends Construct {

    private final Bucket bucket;

    public S3Bucket(final Construct scope,
                    final String id,
                    final S3BucketConfig config) {
        super(scope, id);

        Bucket.Builder bucketBuilder = Bucket.Builder.create(this, "Bucket")
                .versioned(config.isVersioned())
                .encryption(config.getEncryption())
                .blockPublicAccess(config.getBlockPublicAccess())
                .publicReadAccess(config.isPublicReadAccess())
                .removalPolicy(config.getRemovalPolicy())
                .autoDeleteObjects(config.isAutoDeleteObjects())
                .enforceSsl(config.isEnforceSSL())
                .lifecycleRules(config.getLifecycleRules())
                .cors(config.getCorsRules())
                .eventBridgeEnabled(config.isEventBridgeEnabled());

        if (config.getBucketName() != null) {
            bucketBuilder.bucketName(config.getBucketName());
        }

        if (config.getEncryptionKey() != null) {
            bucketBuilder.encryptionKey(config.getEncryptionKey());
        }

        if (config.getWebsiteIndexDocument() != null) {
            bucketBuilder.websiteIndexDocument(config.getWebsiteIndexDocument());
        }

        if (config.getWebsiteErrorDocument() != null) {
            bucketBuilder.websiteErrorDocument(config.getWebsiteErrorDocument());
        }

        if (config.getServerAccessLogsBucket() != null) {
            bucketBuilder.serverAccessLogsBucket(config.getServerAccessLogsBucket());
        }

        if (config.getServerAccessLogsPrefix() != null) {
            bucketBuilder.serverAccessLogsPrefix(config.getServerAccessLogsPrefix());
        }

        bucket = bucketBuilder.build();
    }

    public Bucket getBucket() {
        return bucket;
    }

}
