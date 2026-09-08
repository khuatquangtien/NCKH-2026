package vn.edu.epu.quanlyhoso.paper.entity;

import java.sql.Timestamp;

/**
 * Lớp thực thể Paper - đại diện cho một bài báo khoa học
 * Chứa các thuộc tính tương ứng với các cột trong bảng papers của Database
 */
public class Paper {
    
    private int id;
    private String title;
    private String journalName;
    private int publicationYear;
    private String issnIsbn;
    private JournalRanking journalRanking;
    private PaperStatus status;
    private int creatorId;
    private Timestamp createdAt;

    /**
     * Constructor mặc định không tham số
     */
    public Paper() {
    }

    /**
     * Constructor đầy đủ tham số
     */
    public Paper(int id, String title, String journalName, int publicationYear,
                 String issnIsbn, JournalRanking journalRanking, PaperStatus status,
                 int creatorId, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.journalName = journalName;
        this.publicationYear = publicationYear;
        this.issnIsbn = issnIsbn;
        this.journalRanking = journalRanking;
        this.status = status;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
    }

    // Getter và Setter cho id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter và Setter cho title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter và Setter cho journalName
    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    // Getter và Setter cho publicationYear
    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    // Getter và Setter cho issnIsbn
    public String getIssnIsbn() {
        return issnIsbn;
    }

    public void setIssnIsbn(String issnIsbn) {
        this.issnIsbn = issnIsbn;
    }

    // Getter và Setter cho journalRanking
    public JournalRanking getJournalRanking() {
        return journalRanking;
    }

    public void setJournalRanking(JournalRanking journalRanking) {
        this.journalRanking = journalRanking;
    }

    // Getter và Setter cho status
    public PaperStatus getStatus() {
        return status;
    }

    public void setStatus(PaperStatus status) {
        this.status = status;
    }

    // Getter và Setter cho creatorId
    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    // Getter và Setter cho createdAt
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
