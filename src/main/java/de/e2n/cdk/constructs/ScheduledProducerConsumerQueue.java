package de.e2n.cdk.constructs;

import de.e2n.cdk.model.ILambda;
import de.e2n.cdk.model.QueueConfig;
import java.util.List;
import software.amazon.awscdk.services.events.Schedule;
import software.constructs.Construct;

/**
 * This class uses AWS CDK to create a scheduled producer Lambda, an SQS queue,
 * and a consumer Lambda.
 * The producer Lambda can send messages to the SQS queue.
 * The consumer Lambda is triggered by the queue and processes the messages.
 *
 * Schedule --> Producer --> Queue --> Consumer
 *
 * The CDK construct consists of the following AWS resources:
 * - Two AWS Lambda functions with IAM roles, versions, and aliases (producer and consumer)
 * - An EventBridge schedule rule
 * - An SQS queue and dead-letter queue
 */
public class ScheduledProducerConsumerQueue extends Construct {

    private final ScheduledLambda producer;
    private final ConsumerQueue consumerQueue;

    public ScheduledProducerConsumerQueue(
            final Construct scope,
            final String id,
            Schedule schedule,
            ILambda producer,
            ILambda consumer,
            QueueConfig queueConfig) {
        this(scope, id, List.of(schedule), producer, consumer, queueConfig);
    }

    public ScheduledProducerConsumerQueue(
            final Construct scope,
            final String id,
            List<Schedule> schedules,
            ILambda producer,
            ILambda consumer,
            QueueConfig queueConfig) {
        super(scope, id);

        this.producer = new ScheduledLambda(this, "Producer", producer, schedules);
        consumerQueue = new ConsumerQueue(this, "ConsumerQueue", consumer, queueConfig);
        consumerQueue.getQueue().grantSendMessages(this.producer.getLambda().getAlias());
        this.producer
                .getLambda()
                .getFunction()
                .addEnvironment("QUEUE_URL", consumerQueue.getQueue().getQueueUrl());
    }

    public ScheduledLambda getProducer() {
        return producer;
    }

    public ConsumerQueue getConsumerQueue() {
        return consumerQueue;
    }
}
