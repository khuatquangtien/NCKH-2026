package vn.edu.epu.quanlyhoso.project.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UpdateProjectBudgetRequest {

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal approvedBudget;

    public UpdateProjectBudgetRequest() {
    }

    public BigDecimal getApprovedBudget() {
        return approvedBudget;
    }

    public void setApprovedBudget(BigDecimal approvedBudget) {
        this.approvedBudget = approvedBudget;
    }
}
