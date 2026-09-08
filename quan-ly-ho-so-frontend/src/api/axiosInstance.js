import axios from "axios";

// 1. Tạo một bản sao (instance) của Axios với cấu hình mặc định
const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api", // Địa chỉ cổng Backend Spring Boot của bạn
  headers: {
    "Content-Type": "application/json",
  },
});
// 2. Chế tạo "Interceptor" (Bộ chặn) - Tự động dán Token trước khi gửi đi
axiosInstance.interceptors.request.use(
  (config) => {
    // Mở két sắt lấy thẻ token ra
    const token = localStorage.getItem("token");

    // Nếu trong két sắt có token, tự động dán vào header Authorization
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    // Xử lý nếu quá trình chuẩn bị gửi gói tin bị lỗi
    return Promise.reject(error);
  },
);

// 3. (Tùy chọn thêm) Bộ đánh chặn phản hồi - Tự động đá ra ngoài nếu Token hết hạn (Lỗi 401)
axiosInstance.interceptors.response.use(
  (response) => response.data, // Nếu gọi API thành công, chỉ lấy phần data trả về
  (error) => {
    if (error.response && error.response.status === 401) {
      // Nếu Backend trả về lỗi 401 (Thẻ hết hạn hoặc giả mạo)
      localStorage.removeItem("token"); // Xóa token rác đi
      window.location.href = "/login"; // Đá người dùng về trang đăng nhập
    }
    return Promise.reject(error);
  },
);

export default axiosInstance;
