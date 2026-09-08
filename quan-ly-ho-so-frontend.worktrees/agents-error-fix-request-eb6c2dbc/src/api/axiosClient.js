import axios from "axios";

// Khởi tạo một instance của axios với URL mặc định trỏ xuống Backend Spring Boot
const axiosClient = axios.create({
  baseURL: "http://localhost:8080/api", // Thay đổi port nếu backend của bạn chạy port khác
  headers: {
    "Content-Type": "application/json",
  },
});

// ĐÁNH CHẶN REQUEST: Trước khi gửi đi, tự động nhét Token vào Header
axiosClient.interceptors.request.use(
  (config) => {
    // Giả sử bạn lưu token trong localStorage sau khi login thành công
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// ĐÁNH CHẶN RESPONSE: Xử lý lỗi chung (ví dụ: Token hết hạn -> Bắt đăng nhập lại)
axiosClient.interceptors.response.use(
  (response) => {
    return response.data; // Chỉ lấy phần data, bỏ qua các thông tin rườm rà của axios
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // Xử lý logic khi token hết hạn hoặc không có quyền (ví dụ: redirect về trang Login)
      console.error("Unauthorized! Cần đăng nhập lại.");
    }
    return Promise.reject(error);
  },
);

export default axiosClient;
