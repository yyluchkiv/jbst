package jbst.ops.server.resources;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import jbst.ops.server.domain.storage.AccessCode;
import jbst.ops.server.domain.storage.SupportedFormat;
import jbst.ops.server.exceptions.SlackRuntimeException;
import jbst.ops.server.services.StorageService;
import jbst.ops.server.utils.MessagesUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorageResource {

    // Service
    private final StorageService storageService;
    // Utilities
    private final MessagesUtils messagesUtils;

    @GetMapping("/{format}")
    @ResponseStatus(HttpStatus.OK)
    public void pdf(
            @PathVariable(value = "format") String format,
            @RequestParam(value = "accessCode") AccessCode accessCode,
            HttpServletResponse response
    ) throws IOException {
        if (SupportedFormat.contains(format)) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + accessCode + "." + format);
            IOUtils.copy(this.storageService.getStream(accessCode), response.getOutputStream());
            response.flushBuffer();
        } else {
            throw new SlackRuntimeException(this.messagesUtils.getUnexpectedWarning());
        }
    }
}
