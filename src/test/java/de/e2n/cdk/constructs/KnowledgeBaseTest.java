package de.e2n.cdk.constructs;

import de.e2n.cdk.model.KnowledgeBaseConfig;
import de.e2n.cdk.model.S3DataSourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.assertions.Match;
import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.IBucket;

import java.util.List;
import java.util.Map;

public class KnowledgeBaseTest {

    private Stack stack;

    @BeforeEach
    void setUp() {
        stack = new Stack(new App(), "TestStack");
    }

    @Test
    void defaultKBTest() {
        IBucket bucket = new Bucket(stack, "S3TestBucket");

        ManagedKnowledgeBase managedKnowledgeBase = new ManagedKnowledgeBase(
                stack,
                "TestManagedKnowledgeBase",
                KnowledgeBaseConfig.Builder.create()
                        .name("TestKnowledgeBase")
                        .embeddingModelArn("arn:aws:bedrock:eu-central-1::foundation-model/amazon.titan-embed-text-v2:0")
                        .build(),
                S3DataSourceConfig.Builder.create()
                        .name("TestDataSource")
                        .bucket(bucket)
                        .build());

        Template template = Template.fromStack(stack);

        template.hasResourceProperties("AWS::Bedrock::KnowledgeBase", Match.objectLike(Map.of(
                "Name", "TestKnowledgeBase",
                "KnowledgeBaseConfiguration", Match.objectLike(Map.of(
                        "Type", "MANAGED",
                        "ManagedKnowledgeBaseConfiguration", Match.objectLike(Map.of(
                                "EmbeddingModelArn", "arn:aws:bedrock:eu-central-1::foundation-model/amazon.titan-embed-text-v2:0"
                        ))
                ))
        )));

        template.hasResourceProperties("AWS::Bedrock::DataSource", Match.objectLike(Map.of(
                "Name", "TestDataSource",
                "DataSourceConfiguration", Match.objectLike(Map.of(
                        "Type", "S3", "S3Configuration", Match.objectLike(Map.of(
                                "BucketArn", Match.anyValue())))))));

        template.hasResourceProperties("AWS::IAM::Role", Match.objectLike(Map.of(
                "AssumeRolePolicyDocument", Map.of(
                        "Statement", Match.arrayWith(List.of(Match.objectLike(Map.of(
                                "Principal", Map.of("Service", "bedrock.amazonaws.com")))))))));
        template.hasResourceProperties("AWS::IAM::Policy", Match.objectLike(Map.of(
                "PolicyDocument", Match.objectLike(Map.of(
                        "Statement", Match.arrayWith(List.of(Match.objectLike(Map.of(
                                "Action", "bedrock:InvokeModel"
                        )), Match.objectLike(Map.of(
                                "Action", Match.arrayWith(List.of("s3:GetObject*"))
                        ))))
                ))
        )));
    }
}
