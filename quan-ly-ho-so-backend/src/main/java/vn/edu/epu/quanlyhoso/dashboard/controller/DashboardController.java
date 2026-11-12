package vn.edu.epu.quanlyhoso.dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.common.security.CurrentUserService;
import vn.edu.epu.quanlyhoso.dashboard.dto.DashboardResponse;
import vn.edu.epu.quanlyhoso.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    private final CurrentUserService current;

    @GetMapping
    public DashboardResponse get() {
        return dashboardService.getDashboard(current.getCurrentAccount());
       
    }

}
