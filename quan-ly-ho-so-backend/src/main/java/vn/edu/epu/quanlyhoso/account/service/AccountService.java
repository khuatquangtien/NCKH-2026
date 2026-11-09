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
    }

    public List<AccountResponse> getAllAccounts() {
        List<AccountResponse> result = new ArrayList<>();
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            String rolename = "No role";
            if (account.getRole() != null) {
                rolename = account.getRole().getRoleName();
            }
            AccountResponse accountDto = new AccountResponse(
                    account.getId(),
                    account.getUsername(),
                    rolename,
                    account.getIsActive(),
                    account.getEmail());
            result.add(accountDto);
        }
        return result;
    }

    public List<AccountResponse> GetAllAccount() {
        return getAllAccounts();
    }

    @Transactional
    public void updateAccount(Integer id, UpdateAccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản có ID: " + id));

        account.setEmail(request.getEmail());
        account.setIsActive(request.getIsActive());

        if (request.getRoleId() != null) {
            Role roleNew = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền có ID: " + request.getRoleId()));
            account.setRole(roleNew);
        }

        accountRepository.save(account);
    }
}
