import { SafetyCertificateOutlined } from "@ant-design/icons";
import DashboardStatCard from "./DashboardStatCard";
export default function CouncilStatCard({
  total,
  loading,
  onClick,
  personal = false,
}) {
  return (
    <DashboardStatCard
      label="Hội đồng khoa học"
      value={total}
      note={personal ? "Trong hệ thống" : "Đã thành lập"}
      icon={<SafetyCertificateOutlined />}
      tone="purple"
      loading={loading}
      onClick={onClick}
    />
  );
}
