import { useEffect, useState } from "react";
import { Form, Input, Button, Card, Typography, message } from "antd";
import { UserOutlined, LockOutlined, MailOutlined } from "@ant-design/icons";
import { Link, useNavigate } from "react-router-dom";
import authApi from "../api/authApi.js";

const { Title, Text } = Typography;

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);

  // Nếu đã đăng nhập thì không cần đăng ký nữa
  useEffect(() => {
    if (localStorage.getItem("token")) {
      navigate("/dashboard", { replace: true });
    }
  }, [navigate]);

  const onFinish = async (values) => {
    setSubmitting(true);
    try {
      const username = values.username.trim();
      await authApi.register({
        username,
        email: values.email.trim(),
        password: values.password,
      });

      // Đăng ký xong -> quay về trang đăng nhập (không tự đăng nhập)
      message.success("Đăng ký thành công! Vui lòng đăng nhập.");
      navigate("/login", { replace: true, state: { username } });
    } catch (error) {
      const data = error?.response?.data;

      // Lỗi validation từ @Valid: { validationErrors: { field: message } }
      if (data?.validationErrors && Object.keys(data.validationErrors).length) {
        form.setFields(
          Object.entries(data.validationErrors).map(([name, msg]) => ({
            name,
            errors: [msg],
          })),
        );
        message.error("Thông tin đăng ký chưa hợp lệ!");
      } else if (data?.message) {
        // Lỗi nghiệp vụ: trùng username / email
        const msg = data.message;
        if (/tên đăng nhập/i.test(msg)) {
          form.setFields([{ name: "username", errors: [msg] }]);
        } else if (/email/i.test(msg)) {
          form.setFields([{ name: "email", errors: [msg] }]);
        }
        message.error(msg);
      } else {
        message.error("Đăng ký thất bại! Vui lòng thử lại sau.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        padding: 16,
        background: "#f0f2f5",
      }}
    >
      <Card style={{ width: 400, maxWidth: "100%", boxShadow: "0 4px 12px rgba(0,0,0,0.1)" }}>
        <div style={{ textAlign: "center", marginBottom: 24 }}>
          <Title level={3} style={{ margin: 0 }}>
            Đăng Ký Tài Khoản
          </Title>
          <p style={{ color: "gray", marginTop: 8 }}>
            Tạo tài khoản giảng viên để sử dụng hệ thống
          </p>
        </div>

        <Form form={form} name="register" onFinish={onFinish} layout="vertical">
          <Form.Item
            name="username"
            rules={[
              { required: true, whitespace: true, message: "Vui lòng nhập tên đăng nhập!" },
              { min: 4, max: 20, message: "Tên đăng nhập phải từ 4 đến 20 ký tự!" },
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder="Tên đăng nhập" size="large" autoComplete="username" />
          </Form.Item>

          <Form.Item
            name="email"
            rules={[
              { required: true, whitespace: true, message: "Vui lòng nhập email!" },
              { type: "email", message: "Định dạng Email không hợp lệ!" },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="Email" size="large" autoComplete="email" />
          </Form.Item>

          <Form.Item
            name="password"
            hasFeedback
            rules={[
              { required: true, message: "Vui lòng nhập mật khẩu!" },
              { min: 6, message: "Mật khẩu phải có ít nhất 6 ký tự!" },
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Mật khẩu" size="large" autoComplete="new-password" />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            dependencies={["password"]}
            hasFeedback
            rules={[
              { required: true, message: "Vui lòng nhập lại mật khẩu!" },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue("password") === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error("Mật khẩu nhập lại không khớp!"));
                },
              }),
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Nhập lại mật khẩu" size="large" autoComplete="new-password" />
          </Form.Item>

          <Form.Item style={{ marginTop: 32 }}>
            <Button type="primary" htmlType="submit" size="large" block loading={submitting}>
              Đăng Ký
            </Button>
          </Form.Item>
        </Form>

        <div style={{ textAlign: "center" }}>
          <Text type="secondary">Đã có tài khoản? </Text>
          <Link to="/login">Đăng nhập</Link>
        </div>
      </Card>
    </div>
  );
}
