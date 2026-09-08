package vn.edu.epu.quanlyhoso.admin.dto.request;

public class UpdateAccountRequest {
	private String email;      // Sửa email
    private Integer roleId;    // Sửa quyền
    private Boolean isActive;
    
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	} 
}
