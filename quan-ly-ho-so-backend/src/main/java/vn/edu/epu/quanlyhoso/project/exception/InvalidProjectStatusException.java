package vn.edu.epu.quanlyhoso.project.exception;

import vn.edu.epu.quanlyhoso.common.exception.BusinessException;

public class InvalidProjectStatusException extends BusinessException {

    public InvalidProjectStatusException(String message) {
        super(message);
    }
}
