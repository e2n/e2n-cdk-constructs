package de.e2n.cdk.stacks;

import java.util.Map;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.customresources.*;
import software.constructs.Construct;

/**
 * CDK stack to block public sharing of AWS Systems Manager (SSM) documents.
 * <p>
 * This recommended security best practice should be enabled in all AWS accounts.
 * For more information:
 * <ul>
 *   <li><a href="https://docs.aws.amazon.com/securityhub/latest/userguide/ssm-controls.html#ssm-7">AWS Security Hub SSM-7</a></li>
 *   <li><a href="https://docs.aws.amazon.com/systems-manager/latest/userguide/documents-ssm-sharing.html#block-public-access">Block public access to SSM documents</a></li>
 * </ul>
 * <p>
 * Since CloudFormation does not directly support this setting, a custom resource is used to set the service setting
 * "/ssm/documents/console/public-sharing-permission" to "Disable". This prevents public sharing of SSM documents.
 * See also: <a href="https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_UpdateServiceSetting.html">API_UpdateServiceSetting</a>
 */
public class SSMDocumentsBlockPublicSharingStack extends Stack {

    /**
     * Creates a new stack instance that blocks public sharing of SSM documents.
     *
     * @param scope  The parent construct scope.
     * @param id     The unique stack ID.
     * @param props  The stack properties.
     * @param region The AWS Region in which to apply the setting.
     */
    public SSMDocumentsBlockPublicSharingStack(Construct scope, String id, StackProps props, String region) {
        super(scope, id, props);

        // Custom resource to set the service setting that blocks public sharing
        var call = AwsSdkCall.builder()
                .service("SSM")
                .region(region)
                .action("updateServiceSetting")
                .parameters(Map.of(
                        "SettingId", "/ssm/documents/console/public-sharing-permission",
                        "SettingValue", "Disable"))
                .physicalResourceId(PhysicalResourceId.of("SSMDocumentsBlockPublicSharing"))
                .build();

        var policy = AwsCustomResourcePolicy.fromSdkCalls(SdkCallsPolicyOptions.builder()
                .resources(AwsCustomResourcePolicy.ANY_RESOURCE)
                .build());

        AwsCustomResource.Builder.create(this, id)
                .onCreate(call)
                .onUpdate(call)
                .policy(policy)
                // See https://github.com/aws/aws-cdk/issues/30067
                .installLatestAwsSdk(false)
                .build();
    }
}
