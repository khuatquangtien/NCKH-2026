import {
  CheckCircleOutlined,
  FileTextOutlined,
  SafetyCertificateOutlined,
} from "@ant-design/icons";
import { Card, Col, Row } from "antd";
export default function SystemInfoCard({ isAdmin }) {
  return (
    <Card className="page-card overview-system-card" title="Thông tin vận hành">
      <Row gutter={[16, 16]}>
        <Col xs={24} md={8}>
          <div className="system-info-item">
            <CheckCircleOutlined />
            <div>
              <strong>Kết nối API</strong>
              <span>Đang hoạt động</span>
            </div>
          </div>
        </Col>
        <Col xs={24} md={8}>
          <div className="system-info-item">
            <FileTextOutlined />
            <div>
              <strong>Dữ liệu thống kê</strong>
              <span>Cập nhật từ backend khi mở trang</span>
            </div>
          </div>
        </Col>
        <Col xs={24} md={8}>
          <div className="system-info-item">
            <SafetyCertificateOutlined />
            <div>
              <strong>Quyền truy cập</strong>
              <span>
                {isAdmin ? "Quản trị toàn hệ thống" : "Tài khoản giảng viên"}
              </span>
            </div>
          </div>
        </Col>
      </Row>
    </Card>
  );
}
