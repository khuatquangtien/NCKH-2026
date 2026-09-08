package vn.edu.epu.quanlyhoso.project.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class CreateProjectRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    private String objective;

    private String expectedProduct;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal estimatedBudget;

    @NotBlank
    @Size(max = 100)
    private String faculty;

    private List<Integer> memberIds;

    public CreateProjectRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getExpectedProduct() {
        return expectedProduct;
    }

    public void setExpectedProduct(String expectedProduct) {
        this.expectedProduct = expectedProduct;
    }

    public BigDecimal getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(BigDecimal estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public List<Integer> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Integer> memberIds) {
        this.memberIds = memberIds;
    }
}
