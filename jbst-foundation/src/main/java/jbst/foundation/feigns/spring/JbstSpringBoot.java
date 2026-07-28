package jbst.foundation.feigns.spring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import feign.RequestLine;
import feign.RetryableException;
import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.maven.JbstMavenDetails;
import jbst.foundation.domain.tuples.Tuple2;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Status;

import java.util.ArrayList;

import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@AllArgsConstructor
public abstract class JbstSpringBoot {

    // Classes: Definitions
    public interface SpringBootDefinition {
        @RequestLine("GET /actuator/info")
        SpringBootActuatorInfo info();

        @RequestLine("GET /actuator/health")
        SpringBootActuatorHealth health();
    }

    // Classes: Responses
    /**
     * @param git               spring-based
     * @param activeProfiles    spring-framework: String[]
     * @param activeProfile     jbst: BaseInfoResource
     * @param maven             jbst: BaseInfoResource
     */
    public record SpringBootActuatorInfo(
            @JsonInclude(JsonInclude.Include.NON_NULL) SpringBootActuatorInfoGit git,
            @JsonInclude(JsonInclude.Include.NON_NULL) ArrayList<String> activeProfiles,
            @JsonInclude(JsonInclude.Include.NON_NULL) String activeProfile,
            @JsonInclude(JsonInclude.Include.NON_NULL) JbstMavenDetails maven
    ) {

        public static SpringBootActuatorInfo fixed() {
            return new SpringBootActuatorInfo(
                    SpringBootActuatorInfoGit.fixed(),
                    null,
                    "dev",
                    JbstMavenDetails.fixed()
            );
        }

        public static SpringBootActuatorInfo dash() {
            return new SpringBootActuatorInfo(
                    SpringBootActuatorInfoGit.dash(),
                    null,
                    JbstConstants.Symbols.DASH,
                    JbstMavenDetails.dash()
            );
        }

        public static SpringBootActuatorInfo offline() {
            return dash();
        }

        @JsonIgnore
        public String getProfileOrDash() {
            if (nonNull(this.activeProfile)) {
                return this.activeProfile;
            } else if (!isEmpty(this.activeProfiles)) {
                return this.activeProfiles.getFirst();
            } else {
                return JbstConstants.Symbols.DASH;
            }
        }

        @JsonIgnore
        public boolean isProfileDash() {
            return JbstConstants.Symbols.DASH.equals(this.getProfileOrDash());
        }

        @JsonIgnore
        public Version getMavenVersionOrDash() {
            if (nonNull(this.maven)) {
                return this.maven.version();
            } else {
                return Version.dash();
            }
        }

        @SuppressWarnings("unused")
        @JsonIgnore
        public SpringBootActuatorInfoGit getGitOrDash() {
            if (nonNull(this.git)) {
                return this.git;
            } else {
                return SpringBootActuatorInfoGit.dash();
            }
        }

        public record SpringBootActuatorInfoCommit(
                @JsonProperty("id") String id,
                @JsonProperty("time") String time
        ) {

            public static SpringBootActuatorInfoCommit fixed() {
                return new SpringBootActuatorInfoCommit(
                        "1234567",
                        "01.01.2024 15:00:00"
                );
            }

            public static SpringBootActuatorInfoCommit dash() {
                return new SpringBootActuatorInfoCommit(
                        JbstConstants.Symbols.DASH,
                        JbstConstants.Symbols.DASH
                );
            }
        }

        public record SpringBootActuatorInfoGit(
                @JsonProperty("commit") SpringBootActuatorInfoCommit commit,
                @JsonProperty("branch") String branch
        ) {

            public static SpringBootActuatorInfoGit fixed() {
                return new SpringBootActuatorInfoGit(
                        SpringBootActuatorInfoCommit.fixed(),
                        "dev"
                );
            }

            public static SpringBootActuatorInfoGit dash() {
                return new SpringBootActuatorInfoGit(
                        SpringBootActuatorInfoCommit.dash(),
                        JbstConstants.Symbols.DASH
                );
            }
        }
    }

    public record SpringBootActuatorHealth(
            @JsonInclude(JsonInclude.Include.NON_NULL) Status status
    ) {

        public static SpringBootActuatorHealth fixed() {
            return new SpringBootActuatorHealth(
                    Status.UP
            );
        }

        public static SpringBootActuatorHealth unknown() {
            return new SpringBootActuatorHealth(
                    Status.UNKNOWN
            );
        }

        public static SpringBootActuatorHealth offline() {
            return unknown();
        }
    }

    protected final SpringBootDefinition definition;

    public abstract ServerName getServerName();

    @SuppressWarnings("unused")
    public final boolean isAlive() {
        var info = this.info();
        return nonNull(info) && !SpringBootActuatorInfo.offline().equals(info);
    }

    public final SpringBootActuatorInfo info() {
        try {
            return this.definition.info();
        } catch (RetryableException ex) {
            LOGGER.error(JbstConstants.Logs.SERVER_OFFLINE, this.getServerName(), ex.getMessage());
            return SpringBootActuatorInfo.offline();
        }
    }

    public final SpringBootActuatorHealth health() {
        try {
            return this.definition.health();
        } catch (RetryableException ex) {
            LOGGER.error(JbstConstants.Logs.SERVER_OFFLINE, this.getServerName(), ex.getMessage());
            return SpringBootActuatorHealth.offline();
        }
    }

    @SuppressWarnings("unused")
    public final Tuple2<ServerName, SpringBootActuatorInfo> serverNameInfo() {
        return new Tuple2<>(this.getServerName(), this.info());
    }

    @SuppressWarnings("unused")
    public final Tuple2<ServerName, SpringBootActuatorHealth> serverNameHealth() {
        return new Tuple2<>(this.getServerName(), this.health());
    }
}
