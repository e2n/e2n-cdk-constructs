package de.e2n.cdk.model;

import java.util.List;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.kms.IKey;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.amazon.awscdk.services.s3.CorsRule;
import software.amazon.awscdk.services.s3.IBucket;
import software.amazon.awscdk.services.s3.LifecycleRule;

/**
 * Configuration for an AWS S3 bucket. Initialized using the {@link Builder}.
 * <p>
 * By default, the bucket is configured securely with SSE-S3 encryption, all public access blocked,
 * SSL/TLS enforced, and {@link RemovalPolicy#RETAIN}.
 */
public class S3BucketConfig {

    private final String bucketName;
    private final boolean versioned;
    private final BucketEncryption encryption;
    private final IKey encryptionKey;
    private final BlockPublicAccess blockPublicAccess;
    private final boolean publicReadAccess;
    private final RemovalPolicy removalPolicy;
    private final boolean autoDeleteObjects;
    private final boolean enforceSSL;
    private final List<LifecycleRule> lifecycleRules;
    private final List<CorsRule> corsRules;
    private final String websiteIndexDocument;
    private final String websiteErrorDocument;
    private final IBucket serverAccessLogsBucket;
    private final String serverAccessLogsPrefix;
    private final boolean eventBridgeEnabled;

    public S3BucketConfig(
            String bucketName,
            boolean versioned,
            BucketEncryption encryption,
            IKey encryptionKey,
            BlockPublicAccess blockPublicAccess,
            boolean publicReadAccess,
            RemovalPolicy removalPolicy,
            boolean autoDeleteObjects,
            boolean enforceSSL,
            List<LifecycleRule> lifecycleRules,
            List<CorsRule> corsRules,
            String websiteIndexDocument,
            String websiteErrorDocument,
            IBucket serverAccessLogsBucket,
            String serverAccessLogsPrefix,
            boolean eventBridgeEnabled) {
        this.bucketName = bucketName;
        this.versioned = versioned;
        this.encryption = encryption;
        this.encryptionKey = encryptionKey;
        this.blockPublicAccess = blockPublicAccess;
        this.publicReadAccess = publicReadAccess;
        this.removalPolicy = removalPolicy;
        this.autoDeleteObjects = autoDeleteObjects;
        this.enforceSSL = enforceSSL;
        this.lifecycleRules = lifecycleRules;
        this.corsRules = corsRules;
        this.websiteIndexDocument = websiteIndexDocument;
        this.websiteErrorDocument = websiteErrorDocument;
        this.serverAccessLogsBucket = serverAccessLogsBucket;
        this.serverAccessLogsPrefix = serverAccessLogsPrefix;
        this.eventBridgeEnabled = eventBridgeEnabled;
    }

    public String getBucketName() {
        return bucketName;
    }

    public boolean isVersioned() {
        return versioned;
    }

    public BucketEncryption getEncryption() {
        return encryption;
    }

    public IKey getEncryptionKey() {
        return encryptionKey;
    }

    public BlockPublicAccess getBlockPublicAccess() {
        return blockPublicAccess;
    }

    public boolean isPublicReadAccess() {
        return publicReadAccess;
    }

    public RemovalPolicy getRemovalPolicy() {
        return removalPolicy;
    }

    public boolean isAutoDeleteObjects() {
        return autoDeleteObjects;
    }

    public boolean isEnforceSSL() {
        return enforceSSL;
    }

    public List<LifecycleRule> getLifecycleRules() {
        return lifecycleRules;
    }

    public List<CorsRule> getCorsRules() {
        return corsRules;
    }

    public String getWebsiteIndexDocument() {
        return websiteIndexDocument;
    }

    public String getWebsiteErrorDocument() {
        return websiteErrorDocument;
    }

    public IBucket getServerAccessLogsBucket() {
        return serverAccessLogsBucket;
    }

    public String getServerAccessLogsPrefix() {
        return serverAccessLogsPrefix;
    }

    public boolean isEventBridgeEnabled() {
        return eventBridgeEnabled;
    }

    /**
     * {@link S3BucketConfig} Builder.
     */
    public static class Builder {

        private String bucketName;
        private boolean versioned = false;
        private BucketEncryption encryption = BucketEncryption.S3_MANAGED;
        private IKey encryptionKey;
        private BlockPublicAccess blockPublicAccess = BlockPublicAccess.BLOCK_ALL;
        private boolean publicReadAccess = false;
        private RemovalPolicy removalPolicy = RemovalPolicy.RETAIN;
        private boolean autoDeleteObjects = false;
        private boolean enforceSSL = true;
        private List<LifecycleRule> lifecycleRules = List.of();
        private List<CorsRule> corsRules = List.of();
        private String websiteIndexDocument;
        private String websiteErrorDocument;
        private IBucket serverAccessLogsBucket;
        private String serverAccessLogsPrefix;
        private boolean eventBridgeEnabled = false;

        private Builder() {}

        /**
         * @return {@link Builder} a new builder.
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * @param bucketName The bucket name. If not set, CloudFormation generates a unique name. Default: {@code null}
         * @return {@link Builder}
         */
        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        /**
         * @param versioned Enables versioning for all objects in the bucket. Default: {@code false}
         * @return {@link Builder}
         */
        public Builder versioned(boolean versioned) {
            this.versioned = versioned;
            return this;
        }

        /**
         * The encryption type for objects stored in the bucket.
         * <p>
         * To use a custom KMS key, {@link #encryptionKey(IKey)} must also be set.
         * Default: {@link BucketEncryption#S3_MANAGED}
         * @param encryption The encryption type.
         * @return {@link Builder}
         */
        public Builder encryption(BucketEncryption encryption) {
            this.encryption = encryption;
            return this;
        }

        /**
         * @param encryptionKey The KMS key to use for encrypting objects in the bucket.
         *                      Requires {@link #encryption(BucketEncryption)} = {@link BucketEncryption#KMS}.
         *                      Default: {@code null}
         * @return {@link Builder}
         */
        public Builder encryptionKey(IKey encryptionKey) {
            this.encryptionKey = encryptionKey;
            return this;
        }

        /**
         * @param blockPublicAccess Configuration for the types of public access to block for the bucket.
         *                          Default: {@link BlockPublicAccess#BLOCK_ALL}
         * @return {@link Builder}
         */
        public Builder blockPublicAccess(BlockPublicAccess blockPublicAccess) {
            this.blockPublicAccess = blockPublicAccess;
            return this;
        }

        /**
         * @param publicReadAccess Grants public read access to the bucket objects (AWS:*).
         *                         Requires a sufficiently permissive {@link #blockPublicAccess(BlockPublicAccess)}
         *                         configuration. Default: {@code false}
         * @return {@link Builder}
         */
        public Builder publicReadAccess(boolean publicReadAccess) {
            this.publicReadAccess = publicReadAccess;
            return this;
        }

        /**
         * @param removalPolicy The policy to apply when the associated stack is deleted.
         *                       Default: {@link RemovalPolicy#RETAIN}
         * @return {@link Builder}
         */
        public Builder removalPolicy(RemovalPolicy removalPolicy) {
            this.removalPolicy = removalPolicy;
            return this;
        }

        /**
         * @param autoDeleteObjects Automatically deletes all objects when the bucket is deleted.
         *                          Requires {@link #removalPolicy(RemovalPolicy)} = {@link RemovalPolicy#DESTROY}.
         *                          Default: {@code false}
         * @return {@link Builder}
         */
        public Builder autoDeleteObjects(boolean autoDeleteObjects) {
            this.autoDeleteObjects = autoDeleteObjects;
            return this;
        }

        /**
         * @param enforceSSL Adds a bucket policy requiring all requests to use SSL/TLS. Default: {@code true}
         * @return {@link Builder}
         */
        public Builder enforceSSL(boolean enforceSSL) {
            this.enforceSSL = enforceSSL;
            return this;
        }

        /**
         * @param lifecycleRules Rules for object lifecycles in the bucket, e.g. automatic transitions to other
         *                       storage classes or deletion of old objects. Default: empty list
         * @return {@link Builder}
         */
        public Builder lifecycleRules(List<LifecycleRule> lifecycleRules) {
            this.lifecycleRules = lifecycleRules;
            return this;
        }

        /**
         * @param corsRules Cross-Origin Resource Sharing (CORS) rules for the bucket. Default: empty list
         * @return {@link Builder}
         */
        public Builder corsRules(List<CorsRule> corsRules) {
            this.corsRules = corsRules;
            return this;
        }

        /**
         * Enables static website hosting for the bucket. Must be set together with
         * {@link #websiteErrorDocument(String)}.
         * @param websiteIndexDocument The name of the index document (e.g. {@code index.html}).
         * @return {@link Builder}
         */
        public Builder websiteIndexDocument(String websiteIndexDocument) {
            this.websiteIndexDocument = websiteIndexDocument;
            return this;
        }

        /**
         * @param websiteErrorDocument The name of the error document (e.g. {@code error.html}) for static website hosting.
         * @return {@link Builder}
         */
        public Builder websiteErrorDocument(String websiteErrorDocument) {
            this.websiteErrorDocument = websiteErrorDocument;
            return this;
        }

        /**
         * @param serverAccessLogsBucket The bucket where server access logs for this bucket are written.
         *                                Default: {@code null} (no access logs)
         * @return {@link Builder}
         */
        public Builder serverAccessLogsBucket(IBucket serverAccessLogsBucket) {
            this.serverAccessLogsBucket = serverAccessLogsBucket;
            return this;
        }

        /**
         * @param serverAccessLogsPrefix The prefix used to store server access logs in the
         *                                {@link #serverAccessLogsBucket(IBucket)}.
         * @return {@link Builder}
         */
        public Builder serverAccessLogsPrefix(String serverAccessLogsPrefix) {
            this.serverAccessLogsPrefix = serverAccessLogsPrefix;
            return this;
        }

        /**
         * @param eventBridgeEnabled Sends all bucket events (e.g. object created/deleted) to EventBridge.
         *                           Default: {@code false}
         * @return {@link Builder}
         */
        public Builder eventBridgeEnabled(boolean eventBridgeEnabled) {
            this.eventBridgeEnabled = eventBridgeEnabled;
            return this;
        }

        /**
         * @return {@link S3BucketConfig}
         * @throws IllegalArgumentException if {@link #autoDeleteObjects(boolean)} is enabled without setting
         *                                  {@link #removalPolicy(RemovalPolicy)} to {@link RemovalPolicy#DESTROY}.
         */
        public S3BucketConfig build() {
            if (autoDeleteObjects && removalPolicy != RemovalPolicy.DESTROY) {
                throw new IllegalArgumentException(
                        "autoDeleteObjects requires removalPolicy=DESTROY; otherwise, objects cannot be automatically removed when the bucket is deleted.");
            }

            return new S3BucketConfig(
                    bucketName,
                    versioned,
                    encryption,
                    encryptionKey,
                    blockPublicAccess,
                    publicReadAccess,
                    removalPolicy,
                    autoDeleteObjects,
                    enforceSSL,
                    lifecycleRules,
                    corsRules,
                    websiteIndexDocument,
                    websiteErrorDocument,
                    serverAccessLogsBucket,
                    serverAccessLogsPrefix,
                    eventBridgeEnabled);
        }
    }
}
