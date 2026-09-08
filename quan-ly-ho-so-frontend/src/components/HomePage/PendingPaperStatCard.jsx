import { ReadOutlined } from "@ant-design/icons";
import DashboardStatCard from "./DashboardStatCard";
export default function PendingPaperStatCard({
  total,
  loading,
  onClick,
  personal = false,
}) {
  return (
    <DashboardStatCard
      label={personal ? "Bài báo của tôi" : "Bài báo chờ duyệt"}
      value={total}
      note={personal ? "Đã khai báo" : "Cần xác thực"}
      icon={<ReadOutlined />}
      tone={personal ? "blue" : "orange"}
      loading={loading}
      onClick={onClick}
    />
  );
}
