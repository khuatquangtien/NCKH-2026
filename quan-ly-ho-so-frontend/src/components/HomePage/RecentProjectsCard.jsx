import { Button, Card, Table, Tag } from "antd";
import { projectStatus } from "./projectStatus";
const columns = [
  { title: "Tên đề tài", dataIndex: "title", ellipsis: true },
  {
    title: "Chủ nhiệm",
    dataIndex: "leaderName",
    render: (value) => value || "—",
  },
  { title: "Khoa", dataIndex: "faculty", ellipsis: true },
  {
    title: "Trạng thái",
    dataIndex: "status",
    render: (value) => (
      <Tag color={projectStatus[value]?.tag}>
        {projectStatus[value]?.label || value}
      </Tag>
    ),
  },
];
export default function RecentProjectsCard({ projects, loading, onViewAll }) {
  const rows = [...projects]
    .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
    .slice(0, 5);
  return (
    <Card
      title="Hồ sơ đề tài gần đây"
      className="page-card recent-project-card"
      extra={
        <Button type="link" onClick={onViewAll}>
          Xem tất cả
        </Button>
      }
    >
      <Table
        rowKey="id"
        columns={columns}
        dataSource={rows}
        loading={loading}
        pagination={false}
        size="middle"
        scroll={{ x: 620 }}
        locale={{ emptyText: "Chưa có dữ liệu đề tài" }}
      />
    </Card>
  );
}
