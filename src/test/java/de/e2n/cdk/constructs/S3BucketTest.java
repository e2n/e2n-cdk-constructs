package de.e2n.cdk.constructs;

import de.e2n.cdk.model.S3BucketConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.assertions.Match;
import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.services.kms.Key;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.amazon.awscdk.services.s3.CorsRule;
import software.amazon.awscdk.services.s3.HttpMethods;
import software.amazon.awscdk.services.s3.LifecycleRule;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class S3BucketTest {

    private Stack stack;

    @BeforeEach
    void setUp() {
        stack = new Stack(new App(), "TestStack");
    }

    @Test
    void defaultBucketIstVerschluesseltUndPrivatUndWirdBeiLoeschungBehalten() {
        S3Bucket s3Bucket = new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create().build());
        assertNotNull(s3Bucket.getBucket());

        Template template = Template.fromStack(stack);

        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
                "BucketEncryption", Map.of(
                        "ServerSideEncryptionConfiguration", List.of(Map.of(
                                "ServerSideEncryptionByDefault", Map.of("SSEAlgorithm", "AES256")))),
                "PublicAccessBlockConfiguration", Map.of(
                        "BlockPublicAcls", true,
                        "BlockPublicPolicy", true,
                        "IgnorePublicAcls", true,
                        "RestrictPublicBuckets", true)
        )));

        template.hasResource("AWS::S3::Bucket", Match.objectLike(Map.of(
                "DeletionPolicy", "Retain",
                "UpdateReplacePolicy", "Retain"
        )));

        template.hasResourceProperties("AWS::S3::BucketPolicy", Match.objectLike(Map.of(
                "PolicyDocument", Map.of(
                        "Statement", Match.arrayWith(List.of(Match.objectLike(Map.of(
                                "Effect", "Deny",
                                "Principal", Map.of("AWS", "*"),
                                "Condition", Map.of("Bool", Map.of("aws:SecureTransport", "false"))
                        ))))
                )
        )));
    }

    @Test
    void bucketNameWirdGesetzt() {
        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .bucketName("my-custom-bucket")
                .build());

        Template template = Template.fromStack(stack);
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
                "BucketName", "my-custom-bucket"
        )));
    }

    @Test
    void versionierungWirdAktiviert() {
        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .versioned(true)
                .build());

        Template template = Template.fromStack(stack);
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
                "VersioningConfiguration", Map.of("Status", "Enabled")
        )));
    }

    @Test
    void kmsVerschluesselungMitEigenemKeyWirdGesetzt() {
        Key key = Key.Builder.create(stack, "Key").build();

        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .encryption(BucketEncryption.KMS)
                .encryptionKey(key)
                .build());

        Template template = Template.fromStack(stack);
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
                "BucketEncryption", Map.of(
                        "ServerSideEncryptionConfiguration", List.of(Match.objectLike(Map.of(
                                "ServerSideEncryptionByDefault", Match.objectLike(Map.of("SSEAlgorithm", "aws:kms"))))))
        )));
    }

    @Test
    void publicReadAccessErzeugtOeffentlicheBucketPolicy() {
        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .publicReadAccess(true)
                .blockPublicAccess(BlockPublicAccess.BLOCK_ACLS_ONLY)
                .build());

        Template template = Template.fromStack(stack);
        template.hasResourceProperties("AWS::S3::BucketPolicy", Match.objectLike(Map.of(
                "PolicyDocument", Map.of(
                        "Statement", Match.arrayWith(List.of(Match.objectLike(Map.of(
                                "Effect", "Allow",
                                "Principal", Map.of("AWS", "*"),
                                "Action", "s3:GetObject"
                        ))))
                )
        )));
    }

    @Test
    void autoDeleteObjectsErzeugtCustomResource() {
        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .build());

        Template template = Template.fromStack(stack);
        template.resourceCountIs("Custom::S3AutoDeleteObjects", 1);
        template.hasResource("AWS::S3::Bucket", Match.objectLike(Map.of(
                "DeletionPolicy", "Delete"
        )));
    }

    @Test
    void lifecycleRulesWerdenUebernommen() {
        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .lifecycleRules(List.of(new LifecycleRule.Builder()
                        .id("expire-old-objects")
                        .enabled(true)
                        .expiration(Duration.days(30))
                        .build()))
                .build());

        Template template = Template.fromStack(stack);
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
                "LifecycleConfiguration", Map.of(
                        "Rules", List.of(Match.objectLike(Map.of(
                                "Id", "expire-old-objects",
                                "Status", "Enabled",
                                "ExpirationInDays", 30
                        ))))
        )));
    }

    @Test
    void websiteHostingWirdKonfiguriert() {
        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .websiteIndexDocument("index.html")
                .websiteErrorDocument("error.html")
                .build());

        Template template = Template.fromStack(stack);
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
                "WebsiteConfiguration", Map.of(
                        "IndexDocument", "index.html",
                        "ErrorDocument", "error.html")
        )));
    }

    @Test
    void corsRulesWerdenUebernommen() {
        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .corsRules(List.of(new CorsRule.Builder()
                        .allowedMethods(List.of(HttpMethods.GET))
                        .allowedOrigins(List.of("https://example.com"))
                        .build()))
                .build());

        Template template = Template.fromStack(stack);
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
                "CorsConfiguration", Map.of(
                        "CorsRules", List.of(Match.objectLike(Map.of(
                                "AllowedMethods", List.of("GET"),
                                "AllowedOrigins", List.of("https://example.com")
                        ))))
        )));
    }

    @Test
    void eventBridgeWirdAktiviert() {
        new S3Bucket(stack, "TestBucket", S3BucketConfig.Builder.create()
                .eventBridgeEnabled(true)
                .build());

        Template template = Template.fromStack(stack);
        template.resourceCountIs("Custom::S3BucketNotifications", 1);
        template.hasResourceProperties("Custom::S3BucketNotifications", Match.objectLike(Map.of(
                "NotificationConfiguration", Map.of("EventBridgeConfiguration", Map.of())
        )));
    }

}
