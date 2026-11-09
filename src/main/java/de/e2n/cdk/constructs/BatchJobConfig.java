package de.e2n.cdk.constructs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.batch.EcsContainerDefinitionProps;
import software.amazon.awscdk.services.batch.OrderedComputeEnvironment;
import software.amazon.awscdk.services.events.Schedule;
import software.amazon.awscdk.services.iam.PolicyDocument;

/**
 * Configuration for an AWS Batch job running on Fargate.
 * Initialized using {@link BatchJobConfig.Builder}.
 */
public class BatchJobConfig {

    private final List<String> jobCommand;
    private final String dockerfilePath;
    private final Map<String, PolicyDocument> inlinePolicies;
    private final List<Schedule> schedules;
    private final Duration timeout;
    private final int vCpus;
    private final int memoryLimitMiB;
    private final List<OrderedComputeEnvironment> orderedComputeEnvironments;

    public BatchJobConfig(
            List<String> jobCommand,
            String dockerfilePath,
            Map<String, PolicyDocument> inlinePolicies,
            List<Schedule> schedules,
            Duration timeout,
            int vCpus,
            int memoryLimitMiB,
            List<OrderedComputeEnvironment> orderedComputeEnvironments) {
        this.jobCommand = jobCommand;
        this.dockerfilePath = dockerfilePath;
        this.inlinePolicies = inlinePolicies;
        this.schedules = schedules;
        this.timeout = timeout;
        this.vCpus = vCpus;
        this.memoryLimitMiB = memoryLimitMiB;
        this.orderedComputeEnvironments = orderedComputeEnvironments;
    }

    public List<String> getJobCommand() {
        return jobCommand;
    }

    public String getDockerfilePath() {
        return dockerfilePath;
    }

    public Map<String, PolicyDocument> getInlinePolicies() {
        return inlinePolicies;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public int getvCpus() {
        return vCpus;
    }

    public int getMemoryLimitMiB() {
        return memoryLimitMiB;
    }

    public List<OrderedComputeEnvironment> getOrderedComputeEnvironments() {
        return orderedComputeEnvironments;
    }

    /**
     * {@link BatchJobConfig} Builder.
     */
    public static class Builder {

        private List<String> jobCommand;
        private String dockerfilePath;
        private final Map<String, PolicyDocument> inlinePolicies = new LinkedHashMap<>();
        private final List<Schedule> schedules = new ArrayList<>();
        private Duration timeout = Duration.minutes(15);
        private int vCpus = 1;
        private int memoryLimitMiB = 2048;
        private List<OrderedComputeEnvironment> orderedComputeEnvironments = new ArrayList<>();

        private Builder() {}

        /**
         *
         * @return {@link BatchJobConfig.Builder} a new builder.
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * The list of arguments for the command to run in the job container,
         * e.g. {@code List.of("java", "-jar", "~/foobar-1.0.jar")}.
         * @param jobCommand The list of arguments for the job command.
         * @return {@link BatchJobConfig.Builder}
         */
        public BatchJobConfig.Builder setJobCommand(List<String> jobCommand) {
            this.jobCommand = jobCommand;
            return this;
        }

        /**
         * The path to the Dockerfile for the container image to run as a job.
         * @param dockerfilePath The path to the Dockerfile.
         * @return {@link BatchJobConfig.Builder}
         */
        public BatchJobConfig.Builder setDockerfilePath(String dockerfilePath) {
            this.dockerfilePath = dockerfilePath;
            return this;
        }

        /**
         * @param policy An IAM policy to set as an inline policy on the job's IAM role.
         * @return {@link BatchJobConfig.Builder}
         */
        public BatchJobConfig.Builder inlinePolicy(Policy policy) {
            this.inlinePolicies.put(policy.getName(), policy.getDocument());
            return this;
        }

        /**
         * @param schedule A {@link Schedule} (cron job) that triggers the job at the specified time.
         * @return {@link BatchJobConfig.Builder}
         */
        public BatchJobConfig.Builder schedule(Schedule schedule) {
            this.schedules.add(schedule);
            return this;
        }

        /**
         * The job timeout.
         * @param timeout The timeout as a duration.
         * @return {@link BatchJobConfig.Builder}
         */
        public BatchJobConfig.Builder setTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets the value of {@link EcsContainerDefinitionProps#getCpu()}
         * <p>
         * Default: 1408
         * <p>
         * @param vCpus The number of vCPUs reserved for the container.
         *              Each vCPU is equivalent to
         *              1,024 CPU shares. You must specify at least one vCPU for EC2 and 0.25 for Fargate.
         * @return {@link BatchJobConfig.Builder}
         */
        public BatchJobConfig.Builder setvCpus(int vCpus) {
            this.vCpus = vCpus;
            return this;
        }

        /**
         * (experimental) The hard limit (in MiB) of memory to present to the container.
         * <p>
         * If your container attempts to exceed
         * the memory specified here, the container is killed. You must specify at least 4 MiB of memory for EC2 and 512 MiB for Fargate.
         * </>
         * <p>
         * Default: 1024
         * <p>
         * @param memoryLimitMiB The limit in MiB of memory to present to the container.
         * @return {@link BatchJobConfig.Builder}
         */
        public BatchJobConfig.Builder setMemoryLimitMiB(int memoryLimitMiB) {
            this.memoryLimitMiB = memoryLimitMiB;
            return this;
        }

        /**
         * Sets the ordered compute environments for the job queue.
         * <p>
         * Default: empty list
         * </p>
         *
         * @param orderedComputeEnvironments The ordered compute environments.
         * @return {@link BatchJobConfig.Builder}
         */
        public BatchJobConfig.Builder setOrderedComputeEnvironments(
                List<OrderedComputeEnvironment> orderedComputeEnvironments) {
            this.orderedComputeEnvironments = orderedComputeEnvironments;
            return this;
        }

        /**
         *
         * @return {@link BatchJobConfig}
         */
        public BatchJobConfig build() {
            return new BatchJobConfig(
                    jobCommand,
                    dockerfilePath,
                    inlinePolicies,
                    schedules,
                    timeout,
                    vCpus,
                    memoryLimitMiB,
                    orderedComputeEnvironments);
        }
    }
}
