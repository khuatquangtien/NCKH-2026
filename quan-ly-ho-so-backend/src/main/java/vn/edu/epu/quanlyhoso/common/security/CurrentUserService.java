package vn.edu.epu.quanlyhoso.common.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import vn.edu.epu.quanlyhoso.auth.entity.Account;

@Service
public class CurrentUserService {

    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Account account)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated account not found");
        }
        return account;
    }
}
