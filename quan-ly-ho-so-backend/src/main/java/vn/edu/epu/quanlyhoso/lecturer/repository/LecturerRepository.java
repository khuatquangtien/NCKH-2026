package vn.edu.epu.quanlyhoso.lecturer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;

public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {
    //Tìm hồ sơ dựa vào Account
    Optional<Lecturer> findByAccount(Account account);
    
    
    Optional<Lecturer> findByAccountId(Integer accountId);
    
    
    List<Lecturer> findByFaculty(String faculty);
}