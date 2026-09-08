import { Navigate, Outlet } from "react-router-dom";

// Nhận cả 2 biến user và loading từ file App truyền xuống
const ProtectedRoute = ({ user, loading }) => {
  // 1. Nếu đang tải dữ liệu từ API, hiển thị màn hình chờ (hoặc Antd Spin)
  if (loading) {
    return <div>Đang tải dữ liệu...</div>; // Bạn có thể thay bằng <Spin /> của Antd
  }

  // 2. Kiểm tra quyền ADMIN
  const isAdmin = user?.role?.name === "ADMIN";

  // 3. Nếu đúng Admin thì cho vào (Outlet), ngược lại đá về trang dashboard
  return isAdmin ? <Outlet /> : <Navigate to="/dashboard" replace />;
};

export default ProtectedRoute;
