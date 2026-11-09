package vn.edu.epu.quanlyhoso.common.exception;

public class ProfileIncompleteException extends RuntimeException {

    public ProfileIncompleteException() {
        super("Vui lòng hoàn thiện hồ sơ cá nhân trước khi thực hiện chức năng này ");
    }
}
