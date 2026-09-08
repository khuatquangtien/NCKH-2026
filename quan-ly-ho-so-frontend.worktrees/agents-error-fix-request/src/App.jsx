import { Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import Dashboard from "./pages/Dashboard";
import AccountManager from "./pages/AccountManager"; // Import trang vừa tạo

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      {/* Cấu hình Dashboard làm Route cha */}
      <Route path="/dashboard" element={<Dashboard />}>
        {/* Trang con mặc định hiện ra khi vào /dashboard */}
        <Route index element={<h2>Chào mừng bạn đến với Trang chủ! 🎉</h2>} />

        {/* Khi vào đường dẫn /dashboard/accounts thì hiện bảng quản lý */}
        <Route path="accounts" element={<AccountManager />} />
      </Route>

      <Route path="/" element={<LoginPage />} />
    </Routes>
  );
  
}
export default App;
