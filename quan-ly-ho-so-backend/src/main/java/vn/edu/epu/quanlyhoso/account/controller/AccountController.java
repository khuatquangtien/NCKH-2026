package vn.edu.epu.quanlyhoso.account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.account.service.AccountService;
import vn.edu.epu.quanlyhoso.admin.dto.request.UpdateAccountRequest;
import vn.edu.epu.quanlyhoso.auth.dto.response.AccountResponse;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
	
    private  final AccountService accountService;
    
    
  //xoa tài khoản ( admin)
    @DeleteMapping("/{id}")
    public void DeleteAccount(@PathVariable Integer id) {
    	accountService.deleteById(id);
    }
    // lấy tài khoản tất cả người dùng
    @GetMapping("/users")
    public List<AccountResponse> GetAllAccount(){
    	return accountService.GetAllAccount();
    }
    
    // sửa tất cả 
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Integer id, @RequestBody UpdateAccountRequest request  ){
    	try {
			accountService.updateAccount(id, request);
			return ResponseEntity.ok("Đã cập nhật tài khoản");
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.badRequest().body(e.getMessage());
		}
    }	
}					
