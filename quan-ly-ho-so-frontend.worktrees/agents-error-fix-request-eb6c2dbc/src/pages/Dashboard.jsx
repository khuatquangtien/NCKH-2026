import React, { useEffect } from "react";
import { useNavigate, Outlet } from "react-router-dom";
import { Table, Layout, Menu, Button, Typography, theme } from "antd";
import {
  HomeOutlined,
  UserOutlined,
  SettingOutlined,
  LogoutOutlined,
} from "@ant-design/icons";
import axiosInstance from "../api/axiosInstance";

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

export default function Dashboard() {
  const navigate = useNavigate();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  // 1. Kiểm tra bảo mật: Nếu chưa đăng nhập (không có token) thì back về trang Login
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login"); // Thay "/login" bằng route trang đăng nhập của bạn nếu khác
    }
  }, [navigate]);

  // 2. Xử lý sự kiện Đăng xuất
  const handleLogout = () => {
    localStorage.removeItem("token"); // Xóa vé đăng nhập
    navigate("/login"); // Quay về trang Login
  };

  return (
    <Layout style={{ minHeight: "100vh" }}>
      {/* Cột Menu bên trái */}
      <Sider breakpoint="lg" collapsedWidth="0">
        <div
          style={{
            height: 32,
            margin: 16,
            background: "rgba(255, 255, 255, 0.2)",
            borderRadius: 6,
          }}
        />
        <Menu
          theme="dark"
          mode="inline"
          defaultSelectedKeys={["1"]}
          onClick={({ key }) => {
            if (key === "1") navigate("/dashboard");
            if (key === "2") navigate("/dashboard/accounts"); // Chuyển sang trang quản lý tài khoản
          }}
          items={[
            { key: "1", icon: <HomeOutlined />, label: "Trang chủ" },
            { key: "2", icon: <UserOutlined />, label: "Quản lý tài khoản" },
            { key: "3", icon: <SettingOutlined />, label: "Cài đặt" },
          ]}
        />
      </Sider>

      {/* Khu vực nội dung bên phải */}
      <Layout>
        {/* Thanh tiêu đề (Header) */}
        <Header
          style={{
            padding: "0 24px",
            background: colorBgContainer,
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            backgroundColor: "#00182e",
          }}
        >
          <Title level={4} style={{ margin: 0, color: "#ffffff" }}>
            Hệ thống Quản lý Hồ sơ
          </Title>
          <Button
            type="primary"
            icon={<LogoutOutlined />}
            onClick={handleLogout}
          >
            Đăng xuất
          </Button>
        </Header>

        {/* Nội dung chính (Content) */}
        <Content style={{ margin: "24px 16px 0" }}>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}
