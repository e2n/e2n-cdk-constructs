package de.e2n.cdk.model;

import de.e2n.cdk.utils.SortedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Die Konfiguration einer AWS Code Build BuildSpec: {@link software.amazon.awscdk.services.codebuild.BuildSpec}
 * Konfigurierbar über den {@link Builder}.
 * https://docs.aws.amazon.com/codebuild/latest/userguide/build-spec-ref.html
 *
 * @see <a href="https://docs.aws.amazon.com/codebuild/latest/userguide/build-spec-ref.html">A Build specification reference for CodeBuild</a>
 * @see <a href="https://docs.aws.amazon.com/codebuild/latest/userguide/build-spec-ref.html#build-spec-ref-example">Example BuildSpec</a>
 */
public final class BuildSpec {

    private BuildSpec() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String version;
        private Map<String, String> runtimeVersions = SortedMap.of();
        private List<String> installCommands = new ArrayList<>();
        private List<String> buildCommands = new ArrayList<>();
        private List<String> cachePaths = new ArrayList<>();

        /**
         * @param version Die Version der Buildspec.
         * @return {@link Builder}
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * @param runtimeVersions Die Runtime-Versionen für die Install-Phase.
         * @return {@link Builder}
         */
        public Builder runtimeVersions(Map<String, String> runtimeVersions) {
            this.runtimeVersions = runtimeVersions;
            return this;
        }

        /**
         * @param installCommands Die Commands, die während der Install-Phase ausgeführt werden.
         * @return {@link Builder}
         */
        public Builder installCommands(List<String> installCommands) {
            this.installCommands = new ArrayList<>(installCommands);
            return this;
        }

        /**
         * @param buildCommands Die Commands, die während der Build-Phase ausgeführt werden.
         * @return {@link Builder}
         */
        public Builder buildCommands(List<String> buildCommands) {
            this.buildCommands = new ArrayList<>(buildCommands);
            return this;
        }

        /**
         * @param cachePaths Die Pfade, die von CodeBuild gecached werden.
         * @return {@link Builder}
         */
        public Builder cachePaths(List<String> cachePaths) {
            this.cachePaths = new ArrayList<>(cachePaths);
            return this;
        }

        /**
         * Erstellt die AWS CodeBuild {@link software.amazon.awscdk.services.codebuild.BuildSpec}.
         *
         * @return {@link software.amazon.awscdk.services.codebuild.BuildSpec}
         */
        public software.amazon.awscdk.services.codebuild.BuildSpec build() {
            return software.amazon.awscdk.services.codebuild.BuildSpec.fromObject(
                    SortedMap.of(
                            "version", version,
                            "phases", SortedMap.of(
                                    "install", SortedMap.of(
                                            "runtime-versions", runtimeVersions,
                                            "commands", installCommands
                                    ),
                                    "build", SortedMap.of(
                                            "commands", buildCommands
                                    )
                            ),
                            "cache", SortedMap.of(
                                    "paths", cachePaths
                            )
                    )
            );
        }
    }
}
