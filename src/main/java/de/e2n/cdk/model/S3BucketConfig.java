package de.e2n.cdk.model;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.kms.IKey;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.amazon.awscdk.services.s3.CorsRule;
import software.amazon.awscdk.services.s3.IBucket;
import software.amazon.awscdk.services.s3.LifecycleRule;

import java.util.List;

/**
 * Die Konfiguration eines AWS S3-Buckets. Wird per {@link Builder} initialisiert.
 * <p>
 * Standardmäßig wird ein möglichst sicher konfigurierter Bucket erzeugt: SSE-S3-Verschlüsselung,
 * Blockierung jeglichen öffentlichen Zugriffs, verpflichtendes SSL/TLS und {@link RemovalPolicy#RETAIN}.
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

    public S3BucketConfig(String bucketName,
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

        private Builder() {
        }

        /**
         * @return {@link Builder} ein neuer Builder.
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * @param bucketName Der Name des Buckets. Wenn nicht gesetzt, generiert CloudFormation einen
         *                    eindeutigen Namen. Default: {@code null}
         * @return {@link Builder}
         */
        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        /**
         * @param versioned Aktiviert Versionierung für alle Objekte im Bucket. Default: {@code false}
         * @return {@link Builder}
         */
        public Builder versioned(boolean versioned) {
            this.versioned = versioned;
            return this;
        }

        /**
         * Die Art der Verschlüsselung der im Bucket abgelegten Objekte.
         * <p>
         * Um einen eigenen KMS-Key zu verwenden, muss zusätzlich {@link #encryptionKey(IKey)} gesetzt werden.
         * Default: {@link BucketEncryption#S3_MANAGED}
         * @param encryption Die Art der Verschlüsselung.
         * @return {@link Builder}
         */
        public Builder encryption(BucketEncryption encryption) {
            this.encryption = encryption;
            return this;
        }

        /**
         * @param encryptionKey Der KMS-Key, mit dem die Objekte im Bucket verschlüsselt werden sollen.
         *                      Erfordert {@link #encryption(BucketEncryption)} = {@link BucketEncryption#KMS}.
         *                      Default: {@code null}
         * @return {@link Builder}
         */
        public Builder encryptionKey(IKey encryptionKey) {
            this.encryptionKey = encryptionKey;
            return this;
        }

        /**
         * @param blockPublicAccess Konfiguration, welche Arten von öffentlichem Zugriff auf den Bucket blockiert
         *                          werden sollen. Default: {@link BlockPublicAccess#BLOCK_ALL}
         * @return {@link Builder}
         */
        public Builder blockPublicAccess(BlockPublicAccess blockPublicAccess) {
            this.blockPublicAccess = blockPublicAccess;
            return this;
        }

        /**
         * @param publicReadAccess Gewährt jedem (AWS:*) öffentlichen Lesezugriff auf die Objekte im Bucket.
         *                         Erfordert eine entsprechend permissive {@link #blockPublicAccess(BlockPublicAccess)}
         *                         Konfiguration. Default: {@code false}
         * @return {@link Builder}
         */
        public Builder publicReadAccess(boolean publicReadAccess) {
            this.publicReadAccess = publicReadAccess;
            return this;
        }

        /**
         * @param removalPolicy Die Policy, die beim Löschen des zugehörigen Stacks angewendet wird.
         *                       Default: {@link RemovalPolicy#RETAIN}
         * @return {@link Builder}
         */
        public Builder removalPolicy(RemovalPolicy removalPolicy) {
            this.removalPolicy = removalPolicy;
            return this;
        }

        /**
         * @param autoDeleteObjects Löscht beim Löschen des Buckets automatisch alle enthaltenen Objekte.
         *                          Erfordert {@link #removalPolicy(RemovalPolicy)} = {@link RemovalPolicy#DESTROY}.
         *                          Default: {@code false}
         * @return {@link Builder}
         */
        public Builder autoDeleteObjects(boolean autoDeleteObjects) {
            this.autoDeleteObjects = autoDeleteObjects;
            return this;
        }

        /**
         * @param enforceSSL Erzwingt per Bucket-Policy, dass Requests auf den Bucket ausschließlich über
         *                   SSL/TLS erfolgen dürfen. Default: {@code true}
         * @return {@link Builder}
         */
        public Builder enforceSSL(boolean enforceSSL) {
            this.enforceSSL = enforceSSL;
            return this;
        }

        /**
         * @param lifecycleRules Regeln für den Lebenszyklus der Objekte im Bucket, z.B. für automatische
         *                       Übergänge in andere Storage-Klassen oder das Löschen alter Objekte.
         *                       Default: leere Liste
         * @return {@link Builder}
         */
        public Builder lifecycleRules(List<LifecycleRule> lifecycleRules) {
            this.lifecycleRules = lifecycleRules;
            return this;
        }

        /**
         * @param corsRules Cross-Origin Resource Sharing (CORS) Regeln für den Bucket. Default: leere Liste
         * @return {@link Builder}
         */
        public Builder corsRules(List<CorsRule> corsRules) {
            this.corsRules = corsRules;
            return this;
        }

        /**
         * Aktiviert Static Website Hosting für den Bucket. Muss zusammen mit
         * {@link #websiteErrorDocument(String)} gesetzt werden.
         * @param websiteIndexDocument Der Name des Index-Dokuments (z.B. {@code index.html}).
         * @return {@link Builder}
         */
        public Builder websiteIndexDocument(String websiteIndexDocument) {
            this.websiteIndexDocument = websiteIndexDocument;
            return this;
        }

        /**
         * @param websiteErrorDocument Der Name des Error-Dokuments (z.B. {@code error.html}) für das
         *                              Static Website Hosting.
         * @return {@link Builder}
         */
        public Builder websiteErrorDocument(String websiteErrorDocument) {
            this.websiteErrorDocument = websiteErrorDocument;
            return this;
        }

        /**
         * @param serverAccessLogsBucket Der Bucket, in den Server-Access-Logs für diesen Bucket geschrieben
         *                                werden sollen. Default: {@code null} (keine Access-Logs)
         * @return {@link Builder}
         */
        public Builder serverAccessLogsBucket(IBucket serverAccessLogsBucket) {
            this.serverAccessLogsBucket = serverAccessLogsBucket;
            return this;
        }

        /**
         * @param serverAccessLogsPrefix Das Prefix, mit dem die Server-Access-Logs im
         *                                {@link #serverAccessLogsBucket(IBucket)} abgelegt werden.
         * @return {@link Builder}
         */
        public Builder serverAccessLogsPrefix(String serverAccessLogsPrefix) {
            this.serverAccessLogsPrefix = serverAccessLogsPrefix;
            return this;
        }

        /**
         * @param eventBridgeEnabled Leitet alle Events des Buckets (z.B. Objekt erstellt/gelöscht) an
         *                           EventBridge weiter. Default: {@code false}
         * @return {@link Builder}
         */
        public Builder eventBridgeEnabled(boolean eventBridgeEnabled) {
            this.eventBridgeEnabled = eventBridgeEnabled;
            return this;
        }

        /**
         * @return {@link S3BucketConfig}
         * @throws IllegalArgumentException wenn {@link #autoDeleteObjects(boolean)} aktiviert ist, ohne dass
         *                                   {@link #removalPolicy(RemovalPolicy)} auf {@link RemovalPolicy#DESTROY}
         *                                   gesetzt wurde.
         */
        public S3BucketConfig build() {
            if (autoDeleteObjects && removalPolicy != RemovalPolicy.DESTROY) {
                throw new IllegalArgumentException(
                        "autoDeleteObjects erfordert removalPolicy=DESTROY, da sonst die Objekte beim Löschen des Buckets nicht automatisch entfernt werden können.");
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
                    eventBridgeEnabled
            );
        }

    }

}
