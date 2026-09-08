// src/api/adminApi.js
import axiosInstance from "./axiosInstance";

const adminApi = {
  // Lấy danh sách tất cả tài khoản (cho trang Quản lý tài khoản)
  getAllAccounts: () => {
    // Backend API: @GetMapping("/accounts")
    return axiosInstance.get("/accounts/users");
  },

  assignRole: (data) => {
    // Đổi .post thành .put để khớp với @PutMapping dưới Backend
    return axiosInstance.put("/admin/assign-role", data);
  },

  // Cập nhật thông tin một tài khoản (dùng trong Modal chỉnh sửa)
  updateAccount: (userId, data) => {
    // Backend API: @PutMapping("/admin/accounts/{id}")
    return axiosInstance.put(`/accounts/user/${userId}`, data);
  },

  deleteAccount: (userId) => {
    // Backend API: @DeleteMapping("/accounts/{id}")
    return axiosInstance.delete(`/accounts/${userId}`);
  },
};

export default adminApi;
