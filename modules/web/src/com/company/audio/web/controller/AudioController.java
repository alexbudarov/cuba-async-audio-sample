package com.company.audio.web.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@RestController
@RequestMapping("/audio/v1")
public class AudioController {

    @GetMapping("/audio")
    //@Async
    public ResponseEntity<StreamingResponseBody> downloadAudio(@RequestParam("name") String fileName) throws IOException {
        int fileSize;
        try (InputStream inputStream = getInputStream(fileName)) {
            ByteArrayOutputStream fullFile = new ByteArrayOutputStream();
            fileSize = IOUtils.copy(inputStream, fullFile);
        }

        InputStream inputStream = getInputStream(fileName);
        StreamingResponseBody responseBody = outputStream -> FileCopyUtils.copy(inputStream, outputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .header("Content-Type", "audio/wav")
                .contentLength(fileSize)
                .header("accept-ranges", "bytes")
                .body(responseBody);
    }

    private InputStream getInputStream(String fileName) {
        InputStream inputStream = getClass().getResourceAsStream("/com/company/audio/web/audio/" + fileName);
        Objects.requireNonNull(inputStream);
        return inputStream;
    }
}
