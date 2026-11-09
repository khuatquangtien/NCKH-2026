import { createContext, useState, useEffect, useContext } from "react";
// Import axiosClient mà bạn đã cấu hình sẵn
import authApi from "../api/authApi";

// 1. Khởi tạo Context
export const AuthContext = createContext();

// 2. Tạo Provider để bọc ứng dụng
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // Lưu thông tin người dùng
  const [loading, setLoading] = useState(true); // Trạng thái chờ gọi API
  // Hàm gọi API lấy thông tin người dùng đang đăng nhập
  const fetchUserProfile = async () => {
    // console.log("role: ", role);
    try {
      const response = await authApi.getMe();
      // console.log("response: ", response);
      // Tùy thuộc vào cách axiosClient của bạn trả về data, có thể chỉ là `response` hoặc `response.data`
      setUser({ ...response, id: response.lecturerId, role: { name: response.role } });
      // console.log("Fetched user profile:", response);
    } catch (error) {
      console.error("Lỗi xác thực hoặc chưa đăng nhập:", error);
      setUser(null);
    } finally {
      setLoading(false);
      // console.log("Loading: ", loading);
    }
  };

  // Tự động chạy một lần khi người dùng mở trang web
  useEffect(() => {
    const token = localStorage.getItem("token");
    // console.log(" ", role);
    if (token) {
      fetchUserProfile();
    } else {
      setLoading(false);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, setUser, loading, fetchUserProfile }}>
      {/* Nếu đang loading API thì có thể hiển thị màn hình trắng hoặc spinner, 
                nhưng để đơn giản ta cứ render children ra */}
      {children}
    </AuthContext.Provider>
  );
};
export const useAuth = () => useContext(AuthContext);
