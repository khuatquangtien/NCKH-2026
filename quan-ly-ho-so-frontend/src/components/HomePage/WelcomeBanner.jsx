import { ClockCircleOutlined } from "@ant-design/icons";
import { Typography } from "antd";

const { Title, Paragraph, Text } = Typography;
export default function WelcomeBanner({ user, isAdmin }) {
  return (
    <div className="welcome-panel overview-welcome">
      <div>
        <Text className="eyebrow">TỔNG QUAN HỆ THỐNG</Text>
        <Title level={2}>
          Xin chào, {user?.fullName || user?.username || "bạn"}
        </Title>
        <Paragraph>
          {isAdmin
            ? "Theo dõi nhanh hoạt động nghiên cứu khoa học trong toàn hệ thống."
            : "Theo dõi hồ sơ và hoạt động nghiên cứu khoa học của bạn."}
        </Paragraph>
      </div>
      <div className="overview-date">
        <ClockCircleOutlined />
        <span>
          {new Intl.DateTimeFormat("vi-VN", {
            weekday: "long",
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
          }).format(new Date())}
        </span>
      </div>
    </div>
  );
}
