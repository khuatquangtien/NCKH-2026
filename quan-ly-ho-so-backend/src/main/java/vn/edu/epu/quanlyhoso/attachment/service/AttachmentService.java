package vn.edu.epu.quanlyhoso.attachment.service;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.epu.quanlyhoso.attachment.dto.response.AttachmentResponse;
import vn.edu.epu.quanlyhoso.attachment.entity.Attachment;
import vn.edu.epu.quanlyhoso.attachment.entity.AttachmentType;
import vn.edu.epu.quanlyhoso.attachment.repository.AttachmentRepository;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final Path uploadPath;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            @Value("${app.upload-dir:uploads}") String uploadDir) {
        this.attachmentRepository = attachmentRepository;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        createUploadDirectory();
    }

    @Transactional
    public AttachmentResponse uploadFile(MultipartFile file, AttachmentType fileType, Long projectId, Long paperId) {
        validateFile(file);
        validateOwner(projectId, paperId);

        String originalFileName = cleanFileName(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path targetPath = uploadPath.resolve(storedFileName).normalize();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot save file to server", exception);
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(originalFileName);
        attachment.setFilePath(targetPath.toString());
        attachment.setFileType(fileType);
        attachment.setProjectId(projectId);
        attachment.setPaperId(paperId);

        return toResponse(attachmentRepository.save(attachment));
    }

    public Resource loadFile(Long attachmentId) {
        Attachment attachment = findAttachmentById(attachmentId);

        try {
            Path filePath = Paths.get(attachment.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new EntityNotFoundException("File does not exist on server");
            }

            return resource;
        } catch (MalformedURLException exception) {
            throw new IllegalStateException("Invalid file path", exception);
        }
    }

    public Attachment findAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + attachmentId));
    }

    public List<Attachment> findByProjectId(Long projectId) {
        return attachmentRepository.findByProjectId(projectId);
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create upload directory", exception);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
    }

    private void validateOwner(Long projectId, Long paperId) {
        if (projectId == null && paperId == null) {
            throw new IllegalArgumentException("Project id or paper id is required");
        }
    }

    private String cleanFileName(String fileName) {
        return Paths.get(fileName).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private AttachmentResponse toResponse(Attachment attachment) {
        AttachmentResponse response = new AttachmentResponse();
        response.setId(attachment.getId());
        response.setFileName(attachment.getFileName());
        response.setFileType(attachment.getFileType());
        response.setProjectId(attachment.getProjectId());
        response.setPaperId(attachment.getPaperId());
        response.setUploadedAt(attachment.getUploadedAt());
        return response;
    }
}
