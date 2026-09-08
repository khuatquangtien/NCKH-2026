package vn.edu.epu.quanlyhoso.account.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.admin.dto.request.UpdateAccountRequest;
import vn.edu.epu.quanlyhoso.auth.dto.response.AccountResponse;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.auth.entity.Role;
import vn.edu.epu.quanlyhoso.auth.repository.AccountRepository;
import vn.edu.epu.quanlyhoso.auth.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class AccountService {
	
	private final AccountRepository accountRepository;
	private final RoleRepository roleRepository;
	
    @Transactional
    public void deleteById(Integer id) {
    	accountRepository.deleteByid(id);
    	System.out.print("du lieu da chạy toi day");
    }
    
    
    public List<AccountResponse> GetAllAccount(){
    	List<AccountResponse> result = new ArrayList<AccountResponse>();
    	List<Account> accounts = accountRepository.findAll();
    	for(Account account : accounts) {
    		String rolename = "No role";
    		if(account.getRole() != null) {
    			rolename = account.getRole().getRoleName();  
    		}
    		AccountResponse accountDto = new AccountResponse(account.getId(), account.getUsername(),rolename,account.getIsActive(),account.getEmail());
    		result.add(accountDto);
    		
    	}
    	return result; 
    }
    
    // chức năng sửa tất cả của admin
    public void updateAccount(Integer id, UpdateAccountRequest request) {
        // 1. Tìm account, không thấy thì ném lỗi lập tức để Controller biết
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản có ID: " + id));
        
        // 2. Gán dữ liệu mới
        account.setEmail(request.getEmail());
        account.setIsActive(request.getIsActive());

        // 3. Tìm và gán Role mới
        if (request.getRoleId() != null) {
            Role roleNew = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền có ID: " + request.getRoleId()));
            account.setRole(roleNew);
        }

        // 4. BẮT BUỘC PHẢI CÓ dòng này để lưu xuống DB
        accountRepository.save(account);
        System.out.println("Đã chạy lệnh SAVE xuống database thành công cho ID: " + id);
    }

}
