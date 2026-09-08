package vn.edu.epu.quanlyhoso.project.exception;

import vn.edu.epu.quanlyhoso.common.exception.BusinessException;

public class DuplicateSubmissionException extends BusinessException {

    public DuplicateSubmissionException(String message) {
        super(message);
    }
}
