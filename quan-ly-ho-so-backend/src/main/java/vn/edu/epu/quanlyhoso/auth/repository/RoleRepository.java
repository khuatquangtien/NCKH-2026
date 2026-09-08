package vn.edu.epu.quanlyhoso.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.epu.quanlyhoso.auth.entity.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    //java tự mình điền vào hàm này
}