package vn.edu.epu.quanlyhoso.attachment.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.epu.quanlyhoso.attachment.dto.response.AttachmentResponse;
import vn.edu.epu.quanlyhoso.attachment.entity.Attachment;
import vn.edu.epu.quanlyhoso.attachment.entity.AttachmentType;
import vn.edu.epu.quanlyhoso.attachment.service.AttachmentService;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LECTURER')")  // ← THÊM: ADMIN hoặc LECTURER
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") AttachmentType fileType,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "paperId", required = false) Long paperId) {
        AttachmentResponse response = attachmentService.uploadFile(file, fileType, projectId, paperId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{attachmentId}")  // ← KHÔNG cần phân quyền
    public ResponseEntity<Resource> viewFile(@PathVariable Long attachmentId) {
        Attachment attachment = attachmentService.findAttachmentById(attachmentId);
        Resource resource = attachmentService.loadFile(attachmentId);

        return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(resource);
    }

    @GetMapping("/{attachmentId}/download")  // ← KHÔNG cần phân quyền
    public ResponseEntity<Resource> downloadFile(@PathVariable Long attachmentId) {
        Attachment attachment = attachmentService.findAttachmentById(attachmentId);
        Resource resource = attachmentService.loadFile(attachmentId);

        return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(resource);
    }

    @GetMapping("/projects/{projectId}")  // ← KHÔNG cần phân quyền
    public ResponseEntity<List<Attachment>> findByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(attachmentService.findByProjectId(projectId));
    }
}