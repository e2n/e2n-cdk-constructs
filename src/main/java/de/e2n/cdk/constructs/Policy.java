package de.e2n.cdk.constructs;

import software.amazon.awscdk.services.iam.PolicyDocument;

/**
 * An IAM policy consists of a name and a document containing the policy statements.
 */
public class Policy {

    private final String name;
    private final PolicyDocument document;

    /**
     * @param name The policy name.
     * @param document The {@link PolicyDocument} for the policy.
     */
    public Policy(String name, PolicyDocument document) {
        this.name = name;
        this.document = document;
    }

    /**
     *
     * @return The policy name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The {@link PolicyDocument} for the policy.
     */
    public PolicyDocument getDocument() {
        return document;
    }
}
