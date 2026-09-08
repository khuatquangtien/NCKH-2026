package vn.edu.epu.quanlyhoso.auth.dto.response;

import vn.edu.epu.quanlyhoso.auth.entity.Role;

public class AccountResponse {
	private	Integer id;
	
	private String username;

	private String email;

	private String rolename;

	private Boolean isActive;

	

	public AccountResponse(Integer id2, String username2, String role, Boolean isActive2, String email2) {
		// TODO Auto-generated constructor stub
		this.id =id2;
		this.username = username2;
		this.rolename = role;
		this.isActive = isActive2;
		this.email = email2;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
}
