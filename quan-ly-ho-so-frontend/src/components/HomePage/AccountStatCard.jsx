import { TeamOutlined } from "@ant-design/icons";
import DashboardStatCard from "./DashboardStatCard";
export default function AccountStatCard({ total, active, loading, onClick }) {
  return (
    <DashboardStatCard
      label="Tổng tài khoản"
      value={total}
      note={`${active} đang hoạt động`}
      icon={<TeamOutlined />}
      tone="blue"
      loading={loading}
      onClick={onClick}
    />
  );
}
