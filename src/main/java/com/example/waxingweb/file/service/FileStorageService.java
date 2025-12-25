package com.example.waxingweb.file.service;

import com.example.waxingweb.file.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public UploadFile store(MultipartFile file, String domainDir) {
        if (file == null || file.isEmpty()) return null;

        String originalName = file.getOriginalFilename();

        // 확장자
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        // 저장 파일명(UUID)
        String storedName = UUID.randomUUID() + ext;

        // ✅ DB에 저장할 상대 경로 (key prefix)
        // 예: event/2025-12-26
        String dateDir = LocalDate.now().toString();
        String relativePath = domainDir + "/" + dateDir;

        // ✅ 실제 저장될 물리 경로
        // 예: C:/data/waxingweb/uploads/event/2025-12-26
        Path saveDir = Paths.get(uploadDir, domainDir, dateDir);
        Path fullPath = saveDir.resolve(storedName);

        try {
            Files.createDirectories(saveDir);
            file.transferTo(fullPath.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장 실패", e);
        }

        return UploadFile.of(
                originalName,
                storedName,
                relativePath,                 // ✅ 절대경로 대신 상대경로 저장
                file.getSize(),
                file.getContentType()
        );
    }

    public Path resolvePhysicalPath(UploadFile f) {
        // ✅ 실제 파일 조합은 여기서만
        return Paths.get(uploadDir, f.getPath(), f.getStoredName());
    }

    public void delete(UploadFile f) {
        if (f == null) return;

        try {
            Files.deleteIfExists(resolvePhysicalPath(f));
        } catch (IOException e) {
            throw new IllegalStateException("파일 삭제 실패", e);
        }
    }
}

