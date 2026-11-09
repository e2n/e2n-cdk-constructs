package de.e2n.cdk.constructs;

import de.e2n.cdk.model.DefaultLambdaConfig;
import de.e2n.cdk.model.ILambda;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

/**
 * This class uses AWS CDK to create an AWS Lambda function.
 *
 * The CDK construct consists of the following AWS resources:
 * - An AWS Lambda function with an IAM role and version
 */
public class Lambda extends Construct implements ILambda {

    private final Function function;
    private final Version version;
    private final Alias alias;

    public Lambda(final Construct scope, final String id, DefaultLambdaConfig lambdaConfig) {
        super(scope, id);

        final LocalDateTime now = LocalDateTime.now();

        Role role;
        if (lambdaConfig.getRole() == null) {
            // Create a default role if none was provided
            role = Role.Builder.create(this, "DefaultRole")
                    .managedPolicies(Arrays.asList(ManagedPolicy.fromManagedPolicyArn(
                            this,
                            "AWSLambdaBasicExecutionRole",
                            "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole")))
                    .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
                    .build();
        } else {
            role = lambdaConfig.getRole();
        }

        var functionBuilder = Function.Builder.create(this, "Function")
                .role(role)
                .code(lambdaConfig.getLambdaCode())
                .handler(lambdaConfig.getHandlerPath())
                /* Update the Lambda description each time to force creation of a new version.
                 * Otherwise, deployment with unchanged Lambda code would fail with
                 * `A version for this Lambda function exists ( 13 ). Modify the function to create a new version.`
                 * See https://github.com/aws/aws-cdk/issues/5334
                 */
                .description(now.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .architecture(lambdaConfig.getArchitecture())
                .runtime(
                        lambdaConfig.getHandlerPath().equals(Handler.FROM_IMAGE)
                                ? Runtime.FROM_IMAGE
                                : lambdaConfig.getRuntime())
                .timeout(lambdaConfig.getTimeout())
                .memorySize(lambdaConfig.getMemorySize())
                .ephemeralStorageSize(lambdaConfig.getEphemeralStorageSize())
                .reservedConcurrentExecutions(lambdaConfig.getReservedConcurrentExecutions());

        if (lambdaConfig.getVpc() != null) {
            functionBuilder.vpc(lambdaConfig.getVpc());
        }

        if (lambdaConfig.getSubnetSelection() != null) {
            functionBuilder.vpcSubnets(lambdaConfig.getSubnetSelection());
        }

        if (lambdaConfig.getSecurityGroups() != null) {
            functionBuilder.securityGroups(lambdaConfig.getSecurityGroups());
        }

        if (lambdaConfig.getEnvironment() != null) {
            functionBuilder.environment(lambdaConfig.getEnvironment());
        }

        function = functionBuilder.build();

        version = Version.Builder.create(this, "Version" + now)
                .lambda(function)
                .removalPolicy(RemovalPolicy.RETAIN)
                .description(
                        now.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " Version.")
                .build();

        if (lambdaConfig.getAlias() != null) {
            alias = Alias.Builder.create(this, "Alias")
                    .aliasName(lambdaConfig.getAlias())
                    .version(version)
                    .build();
        } else {
            alias = null;
        }
    }

    public Function getFunction() {
        return function;
    }

    public Version getVersion() {
        return version;
    }

    public Alias getAlias() {
        return alias;
    }
}
