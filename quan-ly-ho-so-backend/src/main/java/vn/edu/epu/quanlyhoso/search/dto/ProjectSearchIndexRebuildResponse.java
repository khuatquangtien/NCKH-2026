package vn.edu.epu.quanlyhoso.search.dto;

import java.time.LocalDateTime;

public class ProjectSearchIndexRebuildResponse {

    private int indexedCount;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private boolean success;

    private String message;

    public ProjectSearchIndexRebuildResponse() {
    }

    public int getIndexedCount() {
        return indexedCount;
    }

    public void setIndexedCount(int indexedCount) {
        this.indexedCount = indexedCount;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
