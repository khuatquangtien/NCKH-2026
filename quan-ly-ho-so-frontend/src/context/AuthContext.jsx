import { createContext, useState, useEffect, useContext } from "react";
// Import axiosClient mà bạn đã cấu hình sẵn
import axiosClient from "../api/axiosInstance";

// 1. Khởi tạo Context
export const AuthContext = createContext();

// 2. Tạo Provider để bọc ứng dụng
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // Lưu thông tin người dùng
  const [loading, setLoading] = useState(true); // Trạng thái chờ gọi API
  // Hàm gọi API lấy thông tin người dùng đang đăng nhập
  const fetchUserProfile = async (role) => {
    // console.log("role: ", role);
    try {
      if (role === "ADMIN") {
        const adminUser = {
          username: "Administrator",
          role: {
            name: "ADMIN",
          },
        };
        setUser(adminUser);
        setLoading(false);
        return;
      }
      // LƯU Ý: Thay '/profile/me' bằng đúng đường dẫn API lấy profile của backend bạn
      const response = await axiosClient.get("/profile/me");
      // console.log("response: ", response);
      // Tùy thuộc vào cách axiosClient của bạn trả về data, có thể chỉ là `response` hoặc `response.data`
      setUser(response);
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
    const storedRole = localStorage.getItem("Role");
    // console.log(" ", role);
    if (token) {
      fetchUserProfile(storedRole);
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
