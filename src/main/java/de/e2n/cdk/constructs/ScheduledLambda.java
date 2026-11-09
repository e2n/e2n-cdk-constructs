package de.e2n.cdk.constructs;

import de.e2n.cdk.model.ILambda;
import java.util.Arrays;
import java.util.List;
import software.amazon.awscdk.services.events.Rule;
import software.amazon.awscdk.services.events.Schedule;
import software.amazon.awscdk.services.events.targets.LambdaFunction;
import software.constructs.Construct;

/**
 * This class uses AWS CDK to create a Lambda triggered by one or more schedules.
 *
 * The CDK construct consists of the following AWS resources:
 * - An AWS Lambda function with an IAM role, version, and alias
 * - An EventBridge schedule rule
 */
public class ScheduledLambda extends Construct {

    private final ILambda lambda;

    public ScheduledLambda(final Construct scope, final String id, ILambda lambda, Schedule schedule) {
        this(scope, id, lambda, List.of(schedule));
    }

    public ScheduledLambda(final Construct scope, final String id, ILambda lambda, List<Schedule> schedules) {
        super(scope, id);

        this.lambda = lambda;

        for (int i = 0; i < schedules.size(); i++) {
            String ruleId = "Rule" + i;
            Rule rule = Rule.Builder.create(this, ruleId)
                    .schedule(schedules.get(i))
                    .targets(Arrays.asList(new LambdaFunction(lambda.getAlias())))
                    .build();
        }
    }

    public ILambda getLambda() {
        return lambda;
    }
}
