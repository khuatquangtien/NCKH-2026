import { FileTextOutlined } from "@ant-design/icons";
import DashboardStatCard from "./DashboardStatCard";
export default function ProjectStatCard({
  total,
  completed,
  loading,
  onClick,
}) {
  return (
    <DashboardStatCard
      label="Hồ sơ đề tài"
      value={total}
      note={`${completed} đã hoàn thành`}
      icon={<FileTextOutlined />}
      tone="cyan"
      loading={loading}
      onClick={onClick}
    />
  );
}
