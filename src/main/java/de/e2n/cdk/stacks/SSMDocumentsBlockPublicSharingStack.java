package de.e2n.cdk.stacks;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.customresources.*;
import software.constructs.Construct;

import java.util.Map;

/**
 * CDK Stack zum Blockieren der öffentlichen Freigabe von AWS Systems Manager (SSM) Dokumenten.
 * <p>
 * Diese Maßnahme stellt eine empfohlene Sicherheits-Best-Practice dar und sollte in allen AWS Accounts aktiviert werden.
 * Weitere Informationen:
 * <ul>
 *   <li><a href="https://docs.aws.amazon.com/securityhub/latest/userguide/ssm-controls.html#ssm-7">AWS Security Hub SSM-7</a></li>
 *   <li><a href="https://docs.aws.amazon.com/systems-manager/latest/userguide/documents-ssm-sharing.html#block-public-access">Block public access to SSM documents</a></li>
 * </ul>
 * <p>
 * Da CloudFormation diese Einstellung nicht direkt unterstützt, wird ein Custom Resource verwendet, um die Service-Einstellung
 * "/ssm/documents/console/public-sharing-permission" auf "Disable" zu setzen. Dies verhindert die öffentliche Freigabe von SSM Dokumenten.
 * Siehe auch: <a href="https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_UpdateServiceSetting.html">API_UpdateServiceSetting</a>
 */
public class SSMDocumentsBlockPublicSharingStack extends Stack {

    /**
     * Erstellt eine neue Instanz des Stacks, der die öffentliche Freigabe von SSM Dokumenten blockiert.
     *
     * @param scope  Der übergeordnete Construct-Scope
     * @param id     Die eindeutige Stack-ID
     * @param props  Stack-Eigenschaften
     * @param region Die AWS-Region, in der die Einstellung vorgenommen werden soll
     */
    public SSMDocumentsBlockPublicSharingStack(Construct scope, String id, StackProps props, String region) {
        super(scope, id, props);

        // Custom Resource zum Setzen der Service-Einstellung für das Blockieren der öffentlichen Freigabe
        var call = AwsSdkCall.builder()
                .service("SSM")
                .region(region)
                .action("updateServiceSetting")
                .parameters(Map.of(
                        "SettingId", "/ssm/documents/console/public-sharing-permission",
                        "SettingValue", "Disable"
                ))
                .physicalResourceId(PhysicalResourceId.of("SSMDocumentsBlockPublicSharing"))
                .build();


        var policy = AwsCustomResourcePolicy.fromSdkCalls(
                SdkCallsPolicyOptions.builder()
                        .resources(AwsCustomResourcePolicy.ANY_RESOURCE)
                        .build());

        AwsCustomResource.Builder.create(this, id)
                .onCreate(call)
                .onUpdate(call)
                .policy(policy)
                // Siehe: https://github.com/aws/aws-cdk/issues/30067
                .installLatestAwsSdk(false)
                .build();
    }

}
