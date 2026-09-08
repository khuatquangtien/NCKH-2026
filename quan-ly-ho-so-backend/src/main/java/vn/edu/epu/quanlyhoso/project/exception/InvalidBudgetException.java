package vn.edu.epu.quanlyhoso.project.exception;

import vn.edu.epu.quanlyhoso.common.exception.BusinessException;

public class InvalidBudgetException extends BusinessException {

    public InvalidBudgetException(String message) {
        super(message);
    }
}
