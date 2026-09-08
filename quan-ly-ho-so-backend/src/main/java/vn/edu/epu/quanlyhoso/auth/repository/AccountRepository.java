package vn.edu.epu.quanlyhoso.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.epu.quanlyhoso.auth.entity.Account;

import java.util.List;
import java.util.Optional;
import vn.edu.epu.quanlyhoso.auth.entity.*;


@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    //Tìm hồ sơ dựa vào username
    Optional<Account> findByUsername(String username);
    // xoá tài khoản
    void deleteByid(Integer id);
    
}