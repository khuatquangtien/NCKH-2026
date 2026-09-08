import { Button, Card, Empty, Progress, Skeleton } from "antd";
import { projectStatus } from "./projectStatus";
export default function ProjectStatusCard({ projects, loading, onViewAll }) {
  const counts = Object.keys(projectStatus).reduce(
    (result, status) => ({
      ...result,
      [status]: projects.filter((item) => item.status === status).length,
    }),
    {},
  );
  return (
    <Card
      title="Tình trạng hồ sơ đề tài"
      className="page-card overview-status-card"
      extra={
        <Button type="link" onClick={onViewAll}>
          Xem tất cả
        </Button>
      }
    >
      {loading ? (
        <Skeleton active />
      ) : projects.length ? (
        <div className="status-list">
          {Object.entries(projectStatus).map(([key, meta]) => {
            const count = counts[key] || 0;
            const percent = Math.round((count / projects.length) * 100);
            return (
              <div className="status-row" key={key}>
                <div className="status-row-heading">
                  <span>
                    <i style={{ background: meta.color }} />
                    {meta.label}
                  </span>
                  <strong>{count}</strong>
                </div>
                <Progress
                  percent={percent}
                  strokeColor={meta.color}
                  showInfo={false}
                  size="small"
                />
              </div>
            );
          })}
        </div>
      ) : (
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description="Chưa có hồ sơ đề tài"
        />
      )}
    </Card>
  );
}
