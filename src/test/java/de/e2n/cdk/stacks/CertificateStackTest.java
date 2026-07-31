package de.e2n.cdk.stacks;

import de.e2n.cdk.constructs.Policy;
import io.github.cdklabs.cdknag.AwsSolutionsChecks;
import io.github.cdklabs.cdknag.IApplyRule;
import io.github.cdklabs.cdknag.NagPackProps;
import io.github.cdklabs.cdknag.NagReportFormat;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Aspects;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.assertions.Annotations;
import software.amazon.awscdk.assertions.Match;
import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.IHostedZone;

import java.util.List;
import java.util.Map;

class CertificateStackTest {

    @Test
    void test() {
        var app = new App();

        Aspects.of(app).add(new
                AwsSolutionsChecks(NagPackProps.builder().verbose(true).build()));

        var hostedZoneStack = new Stack(app, "HostedZoneStack");
        var props = StackProps.builder().build();
        var hostedZone = HostedZone.Builder.create(hostedZoneStack, "HostedZoneTest")
                .zoneName("example.com")
                .build();

        var certificateStack = new CertificateStack(app, "CertificateStackTest", props, "example.com", hostedZone);

        final Template template = Template.fromStack(certificateStack);
        System.out.println(template.toJSON());
        app.synth();

        var annotations = Annotations.fromStack(certificateStack);
        annotations.hasNoError("*", Match.anyValue());
    }

}