package de.e2n.cdk.model;

import software.amazon.awscdk.services.bedrock.CfnDataSource.ServerSideEncryptionConfigurationProperty;
import software.amazon.awscdk.services.bedrock.CfnDataSource.VectorIngestionConfigurationProperty;
import software.amazon.awscdk.services.s3.IBucket;

import java.util.List;

/**
 * Die Konfiguration einer S3-Datenquelle für eine Bedrock Knowledge Base. Wird per {@link Builder} initialisiert.
 * <p>
 * Bildet die AWS-Ressource {@code AWS::Bedrock::DataSource} für den Datenquellentyp {@code S3} ab. Eine
 * Knowledge Base benötigt mindestens eine Datenquelle, um Dokumente einzulesen und zu indizieren.
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
     * {@link S3DataSourceConfig} Builder.
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
         * @return {@link Builder} ein neuer Builder.
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * @param name Der Name der Datenquelle. Wird als Name der {@code AWS::Bedrock::DataSource}-Ressource verwendet.
         * @return {@link Builder}
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @param bucket Der S3-Bucket, aus dem die Knowledge Base ihre Dokumente einliest.
         * @return {@link Builder}
         */
        public Builder bucket(IBucket bucket) {
            this.bucket = bucket;
            return this;
        }

        /**
         * Beschränkt das Einlesen auf Objekte unterhalb der angegebenen S3-Präfixe.
         * <p>
         * CloudFormation erlaubt hier aktuell maximal einen Eintrag. Default: {@code null} (gesamter Bucket
         * wird eingelesen)
         * @param inclusionPrefixes Die S3-Präfixe, auf die das Einlesen beschränkt werden soll.
         * @return {@link Builder}
         */
        public Builder inclusionPrefixes(List<String> inclusionPrefixes) {
            this.inclusionPrefixes = inclusionPrefixes;
            return this;
        }

        /**
         * @param description Eine Beschreibung der Datenquelle. Default: {@code null}
         * @return {@link Builder}
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Verschlüsselung der Datenquellen-Metadaten mit einem eigenen KMS-Key statt der AWS-verwalteten
         * Standardverschlüsselung. Default: {@code null} (AWS-verwaltete Verschlüsselung)
         * @param serverSideEncryptionConfiguration Die Verschlüsselungskonfiguration der Datenquelle.
         * @return {@link Builder}
         */
        public Builder serverSideEncryptionConfiguration(ServerSideEncryptionConfigurationProperty serverSideEncryptionConfiguration) {
            this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
            return this;
        }

        /**
         * Steuert, wie eingelesene Dokumente vor dem Erzeugen der Embeddings aufbereitet werden: Chunking-
         * Strategie, Parsing, Kontext-Anreicherung und optionale eigene Transformation per Lambda.
         * <p>
         * Default: {@code null} (Bedrock-Standard-Chunking)
         * @param vectorIngestionConfiguration Die Ingestion-Konfiguration der Datenquelle.
         * @return {@link Builder}
         */
        public Builder vectorIngestionConfiguration(VectorIngestionConfigurationProperty vectorIngestionConfiguration) {
            this.vectorIngestionConfiguration = vectorIngestionConfiguration;
            return this;
        }

        /**
         * @return {@link S3DataSourceConfig}
         * @throws IllegalArgumentException wenn {@link #name(String)} oder {@link #bucket(IBucket)} fehlt, oder
         *                                   {@link #inclusionPrefixes(List)} mehr als ein Element enthält.
         */
        public S3DataSourceConfig build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(
                        "name ist erforderlich, da die AWS::Bedrock::DataSource-Ressource sonst nicht benannt werden kann.");
            }
            if (bucket == null) {
                throw new IllegalArgumentException(
                        "bucket ist erforderlich, da die Datenquelle ohne S3-Bucket keine Dokumente einlesen kann.");
            }
            if (inclusionPrefixes != null && inclusionPrefixes.size() > 1) {
                throw new IllegalArgumentException(
                        "inclusionPrefixes darf laut CloudFormation-Schema von AWS::Bedrock::DataSource maximal einen Eintrag enthalten.");
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
