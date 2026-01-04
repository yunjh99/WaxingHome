package com.example.waxingweb.file.controller;

import com.example.waxingweb.file.domain.UploadFile;
import com.example.waxingweb.file.repository.UploadFileRepository;
import com.example.waxingweb.file.service.FileStorageService;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Path;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final UploadFileRepository uploadFileRepository;
    private final FileStorageService fileStorageService;

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Resource> serve(@PathVariable Long id) {
        UploadFile f = uploadFileRepository.findById(id).orElseThrow();
        Path path = fileStorageService.resolvePhysicalPath(f);

        Resource resource = new FileSystemResource(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, f.getContentType() != null ? f.getContentType() : "application/octet-stream")
                .body(resource);
    }
}
