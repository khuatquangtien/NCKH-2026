package vn.edu.epu.quanlyhoso.project.mapper;

import org.springframework.stereotype.Component;

import vn.edu.epu.quanlyhoso.project.dto.response.BudgetResponse;
import vn.edu.epu.quanlyhoso.project.entity.Project;

@Component
public class BudgetMapper {

    public BudgetResponse toResponse(Project project) {
        BudgetResponse response = new BudgetResponse();
        response.setProjectId(project.getId());
        response.setProjectTitle(project.getTitle());
        response.setEstimatedBudget(project.getEstimatedBudget());
        response.setApprovedBudget(project.getApprovedBudget());
        return response;
    }
}
