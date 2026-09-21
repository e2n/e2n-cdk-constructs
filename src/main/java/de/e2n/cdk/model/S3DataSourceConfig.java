package de.e2n.cdk.model;

import software.amazon.awscdk.services.bedrock.CfnDataSource.ServerSideEncryptionConfigurationProperty;
import software.amazon.awscdk.services.bedrock.CfnDataSource.VectorIngestionConfigurationProperty;
import software.amazon.awscdk.services.s3.IBucket;

import java.util.List;

/**
 * The configuration of an S3 data source for a Bedrock Knowledge Base. Initialized via {@link Builder}.
 * <p>
 * Represents the AWS resource {@code AWS::Bedrock::DataSource} for data source type {@code S3}. A
 * knowledge base needs at least one data source to ingest and index documents.
 */
public class S3DataSourceConfig {

    private final String name;
    private final IBucket bucket;
    private final List<String> inclusionPrefixes;
    private final String description;
    private final ServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration;
    private final VectorIngestionConfigurationProperty vectorIngestionConfiguration;

    public S3DataSourceConfig(String name,
                              IBucket bucket,
                              List<String> inclusionPrefixes,
                              String description,
                              ServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration,
                              VectorIngestionConfigurationProperty vectorIngestionConfiguration) {
        this.name = name;
        this.bucket = bucket;
        this.inclusionPrefixes = inclusionPrefixes;
        this.description = description;
        this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
        this.vectorIngestionConfiguration = vectorIngestionConfiguration;
    }

    public String getName() {
        return name;
    }

    public IBucket getBucket() {
        return bucket;
    }

    public List<String> getInclusionPrefixes() {
        return inclusionPrefixes;
    }

    public String getDescription() {
        return description;
    }

    public ServerSideEncryptionConfigurationProperty getServerSideEncryptionConfiguration() {
        return serverSideEncryptionConfiguration;
    }

    public VectorIngestionConfigurationProperty getVectorIngestionConfiguration() {
        return vectorIngestionConfiguration;
    }

    /**
     * Builder for {@link S3DataSourceConfig}.
     */
    public static class Builder {

        private String name;
        private IBucket bucket;
        private List<String> inclusionPrefixes;
        private String description;
        private ServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration;
        private VectorIngestionConfigurationProperty vectorIngestionConfiguration;

        private Builder() {
        }

        /**
         * @return {@link Builder} a new builder.
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * @param name The name of the data source. Used as the name of the {@code AWS::Bedrock::DataSource} resource.
         * @return {@link Builder}
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @param bucket The S3 bucket from which the knowledge base ingests its documents.
         * @return {@link Builder}
         * Default: {@code null} The default S3 bucket would be created.
         */
        public Builder bucket(IBucket bucket) {
            this.bucket = bucket;
            return this;
        }

        /**
         * Restricts ingestion to objects below the specified S3 prefixes.
         * <p>
         * CloudFormation currently allows a maximum of one entry here. Default: {@code null} (entire bucket
         * is ingested)
         * @param inclusionPrefixes The S3 prefixes to which ingestion should be restricted.
         * @return {@link Builder}
         */
        public Builder inclusionPrefixes(List<String> inclusionPrefixes) {
            this.inclusionPrefixes = inclusionPrefixes;
            return this;
        }

        /**
         * @param description A description of the data source. Default: {@code null}
         * @return {@link Builder}
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Encryption of the data source metadata with a custom KMS key instead of the AWS-managed
         * default encryption. Default: {@code null} (AWS-managed encryption)
         * @param serverSideEncryptionConfiguration The encryption configuration of the data source.
         * @return {@link Builder}
         */
        public Builder serverSideEncryptionConfiguration(ServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration) {
            this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
            return this;
        }

        /**
         * Controls how ingested documents are processed before generating embeddings: chunking
         * strategy, parsing, context enrichment, and optional custom transformation via Lambda.
         * <p>
         * Default: {@code null} (Bedrock default chunking)
         * @param vectorIngestionConfiguration The ingestion configuration of the data source.
         * @return {@link Builder}
         */
        public Builder vectorIngestionConfiguration(VectorIngestionConfigurationProperty vectorIngestionConfiguration) {
            this.vectorIngestionConfiguration = vectorIngestionConfiguration;
            return this;
        }

        /**
         * @return {@link S3DataSourceConfig}
         * @throws IllegalArgumentException if {@link #name(String)} is missing, or
         *                                   {@link #inclusionPrefixes(List)} contains more than one element.
         */
        public S3DataSourceConfig build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(
                        "name is required, otherwise the AWS::Bedrock::DataSource resource cannot be named.");
            }
            if (inclusionPrefixes != null && inclusionPrefixes.size() > 1) {
                throw new IllegalArgumentException(
                        "inclusionPrefixes must not contain more than one entry according to the CloudFormation schema of AWS::Bedrock::DataSource.");
            }

            return new S3DataSourceConfig(
                    name,
                    bucket,
                    inclusionPrefixes,
                    description,
                    serverSideEncryptionConfiguration,
                    vectorIngestionConfiguration
            );
        }
    }
}
