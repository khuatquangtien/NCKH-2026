package vn.edu.epu.quanlyhoso.council.dto.request;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class CreateCouncilRequest {

    @NotBlank(message = "Council name is required")
    private String name;

    @Valid
    @NotEmpty(message = "Council must have members")
    private List<CouncilMemberRequest> members;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<CouncilMemberRequest> getMembers() { return members; }
    public void setMembers(List<CouncilMemberRequest> members) { this.members = members; }
}
