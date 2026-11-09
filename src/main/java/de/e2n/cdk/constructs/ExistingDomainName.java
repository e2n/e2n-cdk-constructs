package de.e2n.cdk.constructs;

import software.amazon.awscdk.services.apigateway.DomainName;
import software.amazon.awscdk.services.apigateway.DomainNameAttributes;
import software.amazon.awscdk.services.apigateway.IDomainName;
import software.constructs.Construct;

/**
 * Enables referencing an existing API Gateway domain name resource using AWS CDK.
 */
public class ExistingDomainName extends Construct {

    private final IDomainName iDomainName;

    /**
     * Creates a new construct using AWS CDK that represents and references an existing
     * API Gateway domain name.
     * @param scope The parent of this construct, usually an {@code App} or {@code Stage}
     *              (always of type {@code Construct}).
     * @param id The construct ID.
     * @param domainName The domain name (e.g. {@code myapi.de}) to reference in API Gateway.
     * @param aliasHostedZoneId The hosted zone ID of the domain name resource.
     * @param aliasTarget The API Gateway domain name
     *                    (e.g. {@code abc1234.execute-api.eu-central-1.amazonaws.com}).
     */
    public ExistingDomainName(
            final Construct scope, final String id, String domainName, String aliasHostedZoneId, String aliasTarget) {
        super(scope, id);

        this.iDomainName = DomainName.fromDomainNameAttributes(
                this,
                id,
                DomainNameAttributes.builder()
                        .domainName(domainName)
                        .domainNameAliasHostedZoneId(aliasHostedZoneId)
                        .domainNameAliasTarget(aliasTarget)
                        .build());
    }

    public IDomainName getiDomainName() {
        return iDomainName;
    }
}
