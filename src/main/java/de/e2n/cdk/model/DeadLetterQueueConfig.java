package de.e2n.cdk.model;

import software.amazon.awscdk.Duration;

/**
 * Configuration for an AWS SQS dead-letter queue. Initialized using the {@link Builder}.
 */
public class DeadLetterQueueConfig {

    private final boolean enabledDeadLetterQueue;
    private final int maximumReceives;
    private final Duration receiveMessageWaitTime;
    private final Duration retentionPeriod;

    public DeadLetterQueueConfig(
            boolean enabledDeadLetterQueue,
            int maximumReceives,
            Duration receiveMessageWaitTime,
            Duration retentionPeriod) {
        this.enabledDeadLetterQueue = enabledDeadLetterQueue;
        this.maximumReceives = maximumReceives;
        this.receiveMessageWaitTime = receiveMessageWaitTime;
        this.retentionPeriod = retentionPeriod;
    }

    public boolean isEnabledDeadLetterQueue() {
        return enabledDeadLetterQueue;
    }

    public int getMaximumReceives() {
        return maximumReceives;
    }

    public Duration getReceiveMessageWaitTime() {
        return receiveMessageWaitTime;
    }

    public Duration getRetentionPeriod() {
        return retentionPeriod;
    }

    /**
     * {@link DeadLetterQueueConfig} Builder.
     */
    public static class Builder {

        private boolean enabled = true;
        private int maximumReceives = 3;
        private Duration receiveMessageWaitTime = Duration.seconds(20);
        private Duration retentionPeriod = Duration.days(14);

        private Builder() {}

        /**
         * Disables the dead-letter queue.
         * @return {@link DeadLetterQueueConfig.Builder}
         */
        public Builder disabled() {
            this.enabled = false;
            return this;
        }

        /**
         * @param maximumReceives The maximum number of receives before a message is sent to the DLQ.
         *                        If a message's ReceiveCount exceeds this limit for a queue,
         *                        Amazon SQS moves the message to the associated DLQ (with its original message ID).
         * @return {@link DeadLetterQueueConfig.Builder}
         */
        public Builder maximumReceives(int maximumReceives) {
            final int max = 1000;
            final int min = 1;
            this.maximumReceives = validateRange(maximumReceives, min, max);
            return this;
        }

        /**
         * @param receiveMessageWaitTime The maximum time that polling waits for messages to arrive.
         *                               The minimum is zero and the maximum is 20 seconds.
         * @return {@link DeadLetterQueueConfig.Builder}
         */
        public Builder receiveMessageWaitTime(Duration receiveMessageWaitTime) {
            final Duration max = Duration.seconds(20);
            final Duration min = Duration.seconds(0);
            this.receiveMessageWaitTime = validateRange(receiveMessageWaitTime, min, max);
            return this;
        }

        /**
         * @param retentionPeriod The period for which Amazon SQS retains messages unless they are deleted.
         *                        Amazon SQS automatically deletes messages that remain in a queue beyond the maximum retention period.
         *                        The default retention period is 4 days. It can range from 60 seconds to 1,209,600 seconds (14 days).
         * @return {@link DeadLetterQueueConfig.Builder}
         */
        public Builder retentionPeriod(Duration retentionPeriod) {
            final Duration max = Duration.days(14);
            final Duration min = Duration.minutes(1);
            this.receiveMessageWaitTime = validateRange(retentionPeriod, min, max);
            return this;
        }

        private int validateRange(int value, int min, int max) {
            if (value > max) return max;
            if (value < min) return min;
            return value;
        }

        private Duration validateRange(Duration value, Duration min, Duration max) {
            if (value.toSeconds().intValue() > max.toSeconds().intValue()) return max;
            if (value.toSeconds().intValue() < min.toSeconds().intValue()) return min;
            return value;
        }

        /**
         * @return {@link DeadLetterQueueConfig.Builder} a new builder.
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * @return {@link DeadLetterQueueConfig}
         */
        public DeadLetterQueueConfig build() {
            return new DeadLetterQueueConfig(enabled, maximumReceives, receiveMessageWaitTime, retentionPeriod);
        }
    }
}
