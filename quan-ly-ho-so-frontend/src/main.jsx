import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom"; // Bọc ứng dụng
import App from "./App.jsx";
import "./styles.css";

// 1. Thêm dòng import AuthProvider từ file Context bạn vừa tạo
import { AuthProvider } from "./context/AuthContext.jsx";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <BrowserRouter>
      {/* 2. Bọc AuthProvider ôm lấy component App */}
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>,
);
