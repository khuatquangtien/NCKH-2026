import { useContext, useEffect } from "react";
import { Avatar, Button, Layout, Menu, Skeleton, Typography } from "antd";
import {
  FileTextOutlined,
  HomeOutlined,
  LogoutOutlined,
  ReadOutlined,
  SafetyCertificateOutlined,
  TeamOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";

const { Header, Sider, Content } = Layout;
const { Text } = Typography;
export default function Dashboard() {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, loading, setUser } = useContext(AuthContext);
  useEffect(() => {
    if (!localStorage.getItem("token")) navigate("/login", { replace: true });
  }, [navigate]);
  const isAdmin = user?.role?.name === "ADMIN";
  const items = [
    { key: "/dashboard", icon: <HomeOutlined />, label: "Tổng quan" },
    {
      key: "/dashboard/projects",
      icon: <FileTextOutlined />,
      label: "Hồ sơ đề tài",
    },
    {
      key: "/dashboard/papers",
      icon: <ReadOutlined />,
      label: "Bài báo khoa học",
    },
    {
      key: "/dashboard/councils",
      icon: <SafetyCertificateOutlined />,
      label: "Hội đồng",
    },
    {
      key: "/dashboard/profile",
      icon: <UserOutlined />,
      label: "Hồ sơ cá nhân",
    },
    isAdmin && {
      key: "/dashboard/accounts",
      icon: <TeamOutlined />,
      label: "Quản lý tài khoản",
    },
  ].filter(Boolean);
  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("Role");
    setUser(null);
    navigate("/login", { replace: true });
  };
  return (
    <Layout className="app-shell">
      <Sider
        breakpoint="lg"
        collapsedWidth="0"
        width={248}
        className="app-sider"
      >
        <div className="brand">
          <div className="brand-mark" >
            <img src="/images/epu-logo.svg" alt="Logo Trường Đại học Điện lực"   />
          </div>
          <div>
            <strong>QL Nghiên cứu</strong>
            <small>Hồ sơ khoa học</small>
          </div>
        </div>
        <div className="user-card">
          <Avatar size={42} icon={<UserOutlined />} />
          {loading ? (
            <Skeleton active paragraph={false} />
          ) : (
            <div>
              <Text ellipsis>
                {user?.fullName || user?.username || "Người dùng"}
              </Text>
              <small>{isAdmin ? "Quản trị viên" : "Giảng viên"}</small>
            </div>
          )}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          onClick={({ key }) => navigate(key)}
          items={items}
        />
      </Sider>
      <Layout>
        <Header className="app-header">
          <div>
            <strong>Hệ thống Quản lý hồ sơ khoa học</strong>
            <small>Trường Đại học Điện lực</small>
          </div>
          <Button icon={<LogoutOutlined />} onClick={logout}>
            Đăng xuất
          </Button>
        </Header>
        <Content className="app-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
