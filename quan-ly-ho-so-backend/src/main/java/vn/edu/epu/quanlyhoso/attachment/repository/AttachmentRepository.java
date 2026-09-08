package vn.edu.epu.quanlyhoso.attachment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.attachment.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByProjectId(Long projectId);
    List<Attachment> findByPaperId(Long paperId);
}
