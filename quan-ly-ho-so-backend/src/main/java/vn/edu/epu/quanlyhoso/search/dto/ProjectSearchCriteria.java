package vn.edu.epu.quanlyhoso.search.dto;

import java.math.BigDecimal;

import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;

public class ProjectSearchCriteria {

    private String keyword;

    private ProjectStatus status;

    private String faculty;

    private BigDecimal minEstimatedBudget;

    private BigDecimal maxEstimatedBudget;

    private BigDecimal minApprovedBudget;

    private BigDecimal maxApprovedBudget;

    public ProjectSearchCriteria() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public BigDecimal getMinEstimatedBudget() {
        return minEstimatedBudget;
    }

    public void setMinEstimatedBudget(BigDecimal minEstimatedBudget) {
        this.minEstimatedBudget = minEstimatedBudget;
    }

    public BigDecimal getMaxEstimatedBudget() {
        return maxEstimatedBudget;
    }

    public void setMaxEstimatedBudget(BigDecimal maxEstimatedBudget) {
        this.maxEstimatedBudget = maxEstimatedBudget;
    }

    public BigDecimal getMinApprovedBudget() {
        return minApprovedBudget;
    }

    public void setMinApprovedBudget(BigDecimal minApprovedBudget) {
        this.minApprovedBudget = minApprovedBudget;
    }

    public BigDecimal getMaxApprovedBudget() {
        return maxApprovedBudget;
    }

    public void setMaxApprovedBudget(BigDecimal maxApprovedBudget) {
        this.maxApprovedBudget = maxApprovedBudget;
    }
}
