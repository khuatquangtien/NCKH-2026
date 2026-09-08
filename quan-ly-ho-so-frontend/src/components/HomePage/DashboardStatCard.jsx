import { ArrowRightOutlined } from "@ant-design/icons";
import { Card, Typography } from "antd";

export default function DashboardStatCard({
  label,
  value,
  note,
  icon,
  tone,
  loading,
  onClick,
}) {
  return (
    <Card loading={loading} className="overview-stat-card" onClick={onClick}>
      <div className={`overview-stat-icon ${tone}`}>{icon}</div>
      <div className="overview-stat-info">
        <Typography.Text type="secondary">{label}</Typography.Text>
        <strong>{value.toLocaleString("vi-VN")}</strong>
        <small>{note}</small>
      </div>
      <ArrowRightOutlined className="overview-stat-arrow" />
    </Card>
  );
}
