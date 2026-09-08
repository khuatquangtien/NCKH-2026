package vn.edu.epu.quanlyhoso.admin.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.auth.entity.Role;
import vn.edu.epu.quanlyhoso.auth.repository.AccountRepository;
import vn.edu.epu.quanlyhoso.auth.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    public String assignRole(String username, Integer newRoleId) {
        // 1. Tìm nhân viên cần thăng chức/giáng chức
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + username));

        // 2. Lấy cái thẻ quyền hạn mới từ DB
        Role newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền này!"));

        // 3. Gắn thẻ mới cho nó và lưu lại
        account.setRole(newRole);
        accountRepository.save(account);

        return "Cấp quyền " + newRole.getRoleName() + " cho tài khoản " + username + " thành công!";
    }

}
