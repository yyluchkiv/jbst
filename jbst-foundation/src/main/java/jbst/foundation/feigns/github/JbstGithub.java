package jbst.foundation.feigns.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.domain.constants.JbstConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@SuppressWarnings("unused")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstGithub {

    // Classes: Definitions
    public interface GithubDefinition {
        @RequestLine("GET /repos/{owner}/{repo}/contents/{path}")
        @Headers(
                {
                        "Authorization: token {token}",
                        "Content-Type: " + MediaType.APPLICATION_JSON_VALUE
                }
        )
        GithubRepoContentsResponse getContents(
                @Param("token") String token,
                @Param("owner") String owner,
                @Param("repo") String repo,
                @Param("path") String path
        );
    }

    // Classes: Requests
    public record GithubRepoContentsRequest(String token, String owner, String repo, String content) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GithubRepoContentsResponse(@JsonProperty("download_url") String downloadUrl) {

    }

    // Definitions
    private final GithubDefinition definition = Feign.builder()
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(JbstGithub.GithubDefinition.class, "https://api.github.com");

    public final GithubRepoContentsResponse getContents(GithubRepoContentsRequest request) {
        try {
            return this.definition.getContents(
                    request.token(),
                    request.owner(),
                    request.repo(),
                    request.content()
            );
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_RETRY, "GitHub", ex.getMessage());
            throw new IllegalArgumentException(ex);
        }
    }
}
