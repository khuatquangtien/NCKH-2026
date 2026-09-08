package vn.edu.epu.quanlyhoso.common.utils;

import java.time.Year;

import vn.edu.epu.quanlyhoso.paper.entity.Paper;

/**
 * Class ValidationUtils - chứa các hàm static để kiểm tra dữ liệu đầu vào
 * Được sử dụng để validate các object trước khi lưu vào database
 */
public class ValidationUtils {

    /**
     * Kiểm tra một chuỗi có hợp lệ không (khác null và không bị rỗng)
     */
    public static boolean isValidString(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Kiểm tra năm xuất bản có hợp lệ không (phải lớn hơn 1900 và không vượt quá năm hiện tại)
     */
    public static boolean isValidPublicationYear(int year) {
        return year > 1900 && year <= Year.now().getValue();
    }

    /**
     * Kiểm tra tất cả dữ liệu của bài báo có hợp lệ không
     */
    public static boolean isValidPaper(Paper paper) {
        if (paper == null) {
            return false;
        }
        
        return isValidString(paper.getTitle()) &&
               isValidString(paper.getJournalName()) &&
               isValidString(paper.getIssnIsbn()) &&
               isValidPublicationYear(paper.getPublicationYear());
    }
}
