package vn.edu.epu.quanlyhoso.lecturer.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.common.exception.ProfileIncompleteException;
import vn.edu.epu.quanlyhoso.common.security.CurrentUserService;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;

@Service
@RequiredArgsConstructor
public class CurrentLecturerService {

    private final CurrentUserService currentUserService;
    private final LecturerRepository lecturerRepository;

    public Lecturer getCurrentLecturerOrThrow() {
        Account account = currentUserService.getCurrentAccount();
        return lecturerRepository.findByAccountId(account.getId()).orElseThrow(ProfileIncompleteException::new);
    }
}
