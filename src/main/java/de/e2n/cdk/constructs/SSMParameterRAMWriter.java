package de.e2n.cdk.constructs;

import java.util.List;
import software.amazon.awscdk.services.ram.CfnResourceShare;
import software.amazon.awscdk.services.ssm.ParameterTier;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

public class SSMParameterRAMWriter extends Construct {

    /**
     * Creates an SSM parameter shared through AWS RAM with the organization. No cross-account reader role is required.
     * https://docs.aws.amazon.com/systems-manager/latest/userguide/parameter-store-shared-parameters.html
     */
    public SSMParameterRAMWriter(
            Construct scope, String id, String parameterName, String parameterValue, String resourceSharePrincipal) {
        super(scope, id);

        // its not possible to share refs or resources cross stack + cross account. We do it with SSM and a custom
        // resource.
        var ssmParameter = StringParameter.Builder.create(this, "SsmParameter")
                .parameterName(parameterName)
                .tier(ParameterTier.ADVANCED)
                .description("Ssm Parameter created with CDK in " + id)
                .stringValue(parameterValue)
                .build();

        CfnResourceShare cfnResourceShare = CfnResourceShare.Builder.create(this, "ResourceShare")
                // RAM does not accept slashes (which are common and valid in SSM parameter names),
                // so replace them with hyphens.
                .name(parameterName.replace("/", "-"))
                .allowExternalPrincipals(false)
                .principals(List.of(resourceSharePrincipal))
                .resourceArns(List.of(ssmParameter.getParameterArn()))
                .build();
    }
}
