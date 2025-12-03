package jbst.foundation.domain.cryptography;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.cryptography.JbstHashing.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class JbstHashingTest {

    private static Stream<Arguments> hashingAlgorithmsTest() {
        return Stream.of(
                Arguments.of(
                        "value1",
                        "q#Hv%8w5Mzs@q7tj",
                        "906f237da6cc946842fac59e37be7876f04dc51f3644eff8bcd208a64267a2f5",
                        "2652b4edca3650b317445e330c14f392a913b19474c2e2ecc6f5156a8f231f64961e17ba73ab24db2a5c1cc8084a4c36",
                        "c562c5d5a619e65e84594228eb3d5b422869131b0c2ef4d79cb20d6b3e8f75128918c91dcc1b9d3ffab64942dbb4dd3bcecb3aaeaa78dc7ab82d56ca9afa7660"
                ),
                Arguments.of(
                        "value2",
                        "xAXykr&qMv^NV63#",
                        "248d10e1c5a9836e7f9d53e4a2adbe8ebb651e455811a2bd5abca4295b7da326",
                        "7a2989bb1fd3debccaca922689c10cc261693f79f709be6c4231b1a1beefd0e1e3a35358a91fe3cbdfdaf0b65bd1d975",
                        "7566106b66f824737465dccdf0abf78d89a8fbe499359d6c8dcfba32acc0b11bb840e14aeb919af2bdbc9d9f437b3a1214bc661fd75e8bee1cff468bc1b65cf1"
                ),
                Arguments.of(
                        "recvWindow=5000&timestamp=1651087973504",
                        "DN535ilfcz7yRf1jbBW2A7ITT3wJFz4poxdcnMMxbxicX0k7xt9jEnzwSGgTd8aZ",
                        "06a938638039bff0af70982ccba91f5e6e28f009778845d291bb270fac19e405",
                        "6704006f84d14724faa24a63daf54983e3d9cf10cefafed653dc69b3c161d9fecdde238fcdd94ee704d50302fd3555f5",
                        "8daca76a3bd187470cfbe29e509fc8e82b37cefe1bfd520413fd39226655073c12e317400a83b9bd76757f6464ca42e743e7a250d6a0614cec15611e7727d26b"
                ),
                Arguments.of(
                        "9d8217b9f70148f29016f0fa5a5f7495",
                        "c6b84aca7a6940e8ad38c6c3937e32ac2a82d9d79fe34d828c494659254dd25c",
                        "f477573db653e44999c56c481d62d77fda811d64b94b5658a40f93c7bc32432a",
                        "fb7523a45014141e49bbf6affddd36ff2aa24f265982b7c51c9dadff11fb5e461b09b38493e6d5a2bb934ecbc55f792f",
                        "f56b316bb47a8903408320f0df268fdba149da05509cfcc06a33f026d901218d01781fa2cc2cf5253729465c1ce5b923ffcee690a26635d8ec7c56a36c8f022d"
                ),
                Arguments.of(
                        "93F88D362C5D478AA3EDF9DD9BEC165E",
                        "9bb4cf6adfe34413b8cbd387e920516cEECE56D4FC3A420D9B9DBF4AE24A3C5A",
                        "06c35571b1b65c74c982e3be4691fe2cfcf336a82b7dc814c7090d238fc2d6ef",
                        "2a5c3b19d36cbdba6c25a34b38645afd661a6bf3fc3172c497cbf8f6a8135a45786b751f2cf56f63bfa415c29d76cc04",
                        "938e68ec3135a9ac5848c82a0833e8f9e813d90728539697fb1ae5ffa9c6a09a73ffa3968242d5d88c67aff9fab7ba9b71c38c60e7a558fbd9a631940806c0e9"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("hashingAlgorithmsTest")
    void hashingAlgorithmsTest(String value, String hashingKey, String expected256, String expected384, String expected512) {
        // Act
        var actual256 = hmacSha256(value, hashingKey);
        var actual384 = hmacSha384(value, hashingKey);
        var actual512 = hmacSha512(value, hashingKey);

        // Assert
        assertThat(actual256).isEqualTo(expected256);
        assertThat(actual384).isEqualTo(expected384);
        assertThat(actual512).isEqualTo(expected512);
    }

    @Test
    void shaByAlgorithmExceptionTest() {
        // Act
        var throwable = catchThrowable(() -> shaByAlgorithm("value", "hashingKey", "HmacSHA0"));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Hashing Failure. Value: `value`. Key: `hashingKey`. Algorithm: `HmacSHA0`. Exception: `NoSuchAlgorithmException`");
    }
}
