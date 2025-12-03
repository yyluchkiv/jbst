package jbst.foundation.domain.http.cache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jbst.foundation.domain.annotations.JbstDevelopmentOnly;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StreamUtils;

import java.io.*;

import static java.nio.charset.Charset.defaultCharset;
import static jbst.foundation.domain.random.JbstRandom.randomString;

@JbstDevelopmentOnly
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    public static class CachedBodyServletInputStream extends ServletInputStream {

        private final InputStream cachedBodyInputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            try {
                return this.cachedBodyInputStream.available() == 0;
            } catch (IOException ex) {
                return false;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return this.cachedBodyInputStream.read();
        }
    }

    public record CachedPayload(@NotNull String value) {

        @JsonCreator
        public static CachedPayload of(String value) {
            return new CachedPayload(value);
        }

        public static CachedPayload hardcoded() {
            return of("{}");
        }

        public static CachedPayload random() {
            return of(randomString());
        }

        @JsonValue
        @NotNull
        @Override
        public String toString() {
            return this.value;
        }
    }

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        var requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        var byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }

    public final CachedPayload getCachedPayload() {
        return new CachedPayload(new String(this.cachedBody, defaultCharset()));
    }
}
