package vn.edu.epu.quanlyhoso.dashboard.dto;

import java.util.Map;

public record DashboardResponse(String scope, long accounts, long activeAccounts, long projects,
                Map<String, Long> projectsByStatus, long papers, long pendingPapers, long councils) {
}
