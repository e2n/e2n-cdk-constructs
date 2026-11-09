package de.e2n.cdk.stacks;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecr.LifecycleRule;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecr.RepositoryEncryption;
import software.amazon.awscdk.services.ecr.TagStatus;
import software.constructs.Construct;

public class RepositoryStack extends Stack {

    private final Repository repository;

    public RepositoryStack(Construct scope, String id, StackProps props, String name) {
        this(scope, id, props, name, List.of());
    }

    public RepositoryStack(
            Construct scope, String id, StackProps props, String name, List<LifecycleRule> lifecycleRules) {
        super(scope, id, props);

        repository = Repository.Builder.create(this, "Repository")
                .repositoryName(name)
                .encryption(RepositoryEncryption.KMS)
                .removalPolicy(RemovalPolicy.DESTROY)
                .lifecycleRules(lifecycleRules)
                .build();
    }

    public Repository getRepository() {
        return repository;
    }

    /**
     * Creates a list of lifecycle rules that never delete images with the specified tags and delete all other images,
     * except for the {@code maxImagesToKeep} newest images.
     * @param imageTagsToKeep Tags of images that should never be deleted.
     * @param maxImagesToKeep Number of newest images to keep.
     * @return A list of lifecycle rules.
     */
    public static List<LifecycleRule> buildLifecycleRules(List<String> imageTagsToKeep, int maxImagesToKeep) {
        List<LifecycleRule> rules = new ArrayList<>();
        int i = 1;
        for (String imageTag : imageTagsToKeep) {
            rules.add(LifecycleRule.builder()
                    .rulePriority(i++)
                    .tagStatus(TagStatus.TAGGED)
                    .tagPrefixList(List.of(imageTag))
                    .maxImageCount(1)
                    .description("Keep image with tag " + imageTag)
                    .build());
        }

        rules.add(LifecycleRule.builder()
                .rulePriority(i)
                .tagStatus(TagStatus.ANY)
                .maxImageCount(maxImagesToKeep)
                .description("Keep " + maxImagesToKeep + " images total and delete all older images")
                .build());

        return rules;
    }
}
