import { useEffect } from "react";
import { Form, Input, Button, Card, Typography, message } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import authApi from "../api/authApi.js";
import { useAuth } from "../context/AuthContext.jsx";
const { Title } = Typography;

export default function LoginPage() {
  const navigate = useNavigate();
  const { setUser } = useAuth();
  // Nếu đã đăng nhập, tự động chuyển hướng vào dashboard
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      navigate("/dashboard", { replace: true });
    }
  }, [navigate]);

  // Hàm này tự động chạy khi người dùng bấm Đăng nhập và ĐÃ VƯỢT QUA các điều kiện kiểm tra (validation)
  const onFinish = async (values) => {
    try {
      const response = await authApi.login({
        username: values.username,
        password: values.password,
      });
      // setUser(response);
      // console.log("Đăng nhập thành công:", user.role);
      // console.log("du lieu backend tra ve :", response);

      if (response && response.token) {
        localStorage.setItem("token", response.token);
        localStorage.setItem("Role", response.role);
        message.success("Đăng nhập thành công!");
        setUser({
          username: values.username,
          role: {
            name: response.role,
          },
        });
        navigate("/dashboard"); // Chuyển hướng đến trang dashboard sau khi đăng nhập thành công
      }
    } catch (error) {
      console.error("Lỗi đăng nhập:", error);
      message.error(
        "Đăng nhập thất bại! Vui lòng kiểm tra lại tài khoản và mật khẩu.",
      );
    }
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
        background: "#f0f2f5", // Màu nền xám nhạt chuyên nghiệp
      }}
    >
      <Card style={{ width: 400, boxShadow: "0 4px 12px rgba(0,0,0,0.1)" }}>
        <div style={{ textAlign: "center", marginBottom: 24 }}>
          <Title level={3} style={{ margin: 0 }}>
            Hệ Thống Quản Lý
          </Title>
          <p style={{ color: "gray", marginTop: 8 }}>
            Vui lòng đăng nhập để tiếp tục
          </p>
        </div>

        <Form name="login" onFinish={onFinish} layout="vertical">
          <Form.Item
            name="username"
            rules={[{ required: true, message: "Vui lòng nhập tài khoản!" }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="Tài khoản"
              size="large"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: "Vui lòng nhập mật khẩu!" }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Mật khẩu"
              size="large"
            />
          </Form.Item>

          <Form.Item style={{ marginTop: 32 }}>
            <Button type="primary" htmlType="submit" size="large" block>
              Đăng Nhập
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
