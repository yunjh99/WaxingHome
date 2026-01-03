package com.example.waxingweb.file.repository;

import com.example.waxingweb.file.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
}
