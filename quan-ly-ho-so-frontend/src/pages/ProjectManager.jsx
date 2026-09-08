import { useCallback, useEffect, useState } from "react";
import {
  Button,
  Card,
  Descriptions,
  Drawer,
  Form,
  Input,
  InputNumber,
  Modal,
  Progress,
  Select,
  Space,
  Table,
  Tag,
  Typography,
  message,
} from "antd";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import projectApi from "../api/projectApi";
import { useAuth } from "../context/AuthContext";

const statusMap = {
  PENDING: ["Chờ duyệt", "gold"],
  ONGOING: ["Đang thực hiện", "blue"],
  COMPLETED: ["Hoàn thành", "green"],
  REJECTED: ["Từ chối", "red"],
};
const money = (value) =>
  value == null ? "—" : `${Number(value).toLocaleString("vi-VN")} ₫`;

export default function ProjectManager() {
  const { user } = useAuth();
  const isAdmin = user?.role?.name === "ADMIN";
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState({});
  const [detail, setDetail] = useState(null);
  const [openCreate, setOpenCreate] = useState(false);
  const [action, setAction] = useState(null);
  const [form] = Form.useForm();
  const [actionForm] = Form.useForm();
  const load = useCallback(async () => {
    if (!isAdmin) return;
    setLoading(true);
    try {
      setRows(await projectApi.search(filters));
    } catch {
      message.error("Không thể tải danh sách đề tài.");
    } finally {
      setLoading(false);
    }
  }, [filters, isAdmin]);
  useEffect(() => {
    load();
  }, [load]);
  const showDetail = async (record) => {
    try {
      setDetail(await projectApi.getById(record.id, isAdmin));
    } catch {
      message.error("Không thể tải chi tiết đề tài.");
    }
  };
  const create = async (values) => {
    try {
      await projectApi.create({
        ...values,
        memberIds: values.memberIds
          ? values.memberIds.split(",").map(Number).filter(Boolean)
          : [],
      });
      message.success("Đã tạo hồ sơ đề tài.");
      setOpenCreate(false);
      form.resetFields();
      load();
    } catch (e) {
      message.error(e.response?.data?.message || "Không thể tạo đề tài.");
    }
  };
  const submitAction = async (values) => {
    try {
      if (action.type === "status")
        await projectApi.updateStatus(action.id, values);
      else if (action.type === "budget")
        await projectApi.updateBudget(action.id, values);
      else if (action.type === "report")
        await projectApi.submitReport(action.id, values);
      else await projectApi.submitFinal(action.id, values);
      message.success("Đã cập nhật đề tài.");
      setAction(null);
      actionForm.resetFields();
      load();
    } catch (e) {
      message.error(e.response?.data?.message || "Thao tác không thành công.");
    }
  };
  const columns = [
    { title: "Mã", dataIndex: "id", width: 70 },
    { title: "Tên đề tài", dataIndex: "title", ellipsis: true },
    { title: "Chủ nhiệm", dataIndex: "leaderName" },
    { title: "Khoa", dataIndex: "faculty" },
    {
      title: "Trạng thái",
      dataIndex: "status",
      render: (s) => (
        <Tag color={(statusMap[s] || [s, "default"])[1]}>
          {(statusMap[s] || [s])[0]}
        </Tag>
      ),
    },
    {
      title: "Kinh phí đề xuất",
      dataIndex: "estimatedBudget",
      align: "right",
      render: money,
    },
    {
      title: "",
      render: (_, r) => (
        <Space>
          <Button type="link" onClick={() => showDetail(r)}>
            Chi tiết
          </Button>
          {isAdmin && (
            <Button
              type="link"
              onClick={() => setAction({ type: "status", id: r.id })}
            >
              Duyệt
            </Button>
          )}
        </Space>
      ),
    },
  ];
  return (
    <>
      <Card
        title="Quản lý hồ sơ đề tài"
        className="page-card"
        extra={
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => setOpenCreate(true)}
          >
            Tạo đề tài
          </Button>
        }
      >
        {isAdmin ? (
          <>
            <div className="filter-bar">
              <Input
                allowClear
                prefix={<SearchOutlined />}
                placeholder="Tìm theo tên, mục tiêu..."
                onPressEnter={(e) =>
                  setFilters((f) => ({
                    ...f,
                    keyword: e.currentTarget.value || undefined,
                  }))
                }
              />
              <Select
                allowClear
                placeholder="Trạng thái"
                options={Object.entries(statusMap).map(([value, [label]]) => ({
                  value,
                  label,
                }))}
                onChange={(status) => setFilters((f) => ({ ...f, status }))}
              />
              <Input
                allowClear
                placeholder="Khoa"
                onPressEnter={(e) =>
                  setFilters((f) => ({
                    ...f,
                    faculty: e.currentTarget.value || undefined,
                  }))
                }
              />
            </div>
            <Table
              rowKey="id"
              columns={columns}
              dataSource={rows}
              loading={loading}
              scroll={{ x: 900 }}
            />
          </>
        ) : (
          <div className="empty-note">
            <Typography.Title level={4}>
              Tạo hồ sơ nghiên cứu mới
            </Typography.Title>
            <Typography.Paragraph>
              Backend hiện chưa cung cấp API danh sách đề tài theo giảng viên.
              Bạn vẫn có thể tạo hồ sơ mới hoặc mở hồ sơ bằng mã khi được cung
              cấp.
            </Typography.Paragraph>
            <Input.Search
              enterButton="Mở hồ sơ"
              placeholder="Nhập mã đề tài"
              style={{ maxWidth: 360 }}
              onSearch={(id) => id && showDetail({ id })}
            />
          </div>
        )}
      </Card>
      <Modal
        title="Tạo hồ sơ đề tài"
        open={openCreate}
        onCancel={() => setOpenCreate(false)}
        onOk={() => form.submit()}
        okText="Tạo hồ sơ"
        width={720}
      >
        <Form form={form} layout="vertical" onFinish={create}>
          <Form.Item
            name="title"
            label="Tên đề tài"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <div className="form-grid">
            <Form.Item name="faculty" label="Khoa" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item
              name="estimatedBudget"
              label="Kinh phí đề xuất"
              rules={[{ required: true }]}
            >
              <InputNumber min={1} style={{ width: "100%" }} addonAfter="VNĐ" />
            </Form.Item>
          </div>
          <Form.Item name="objective" label="Mục tiêu">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="expectedProduct" label="Sản phẩm dự kiến">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="memberIds" label="Mã giảng viên tham gia">
            <Input placeholder="Ví dụ: 2, 5, 8" />
          </Form.Item>
        </Form>
      </Modal>
      <Drawer
        title="Chi tiết hồ sơ đề tài"
        width={720}
        open={!!detail}
        onClose={() => setDetail(null)}
      >
        {detail && (
          <>
            <Descriptions column={2} bordered size="small">
              <Descriptions.Item label="Tên đề tài" span={2}>
                {detail.title}
              </Descriptions.Item>
              <Descriptions.Item label="Chủ nhiệm">
                {detail.leaderName || "—"}
              </Descriptions.Item>
              <Descriptions.Item label="Khoa">
                {detail.faculty}
              </Descriptions.Item>
              <Descriptions.Item label="Trạng thái">
                <Tag color={(statusMap[detail.status] || [null, "default"])[1]}>
                  {(statusMap[detail.status] || [detail.status])[0]}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Kinh phí">
                {money(detail.approvedBudget ?? detail.estimatedBudget)}
              </Descriptions.Item>
              <Descriptions.Item label="Mục tiêu" span={2}>
                {detail.objective || "—"}
              </Descriptions.Item>
              <Descriptions.Item label="Sản phẩm dự kiến" span={2}>
                {detail.expectedProduct || "—"}
              </Descriptions.Item>
            </Descriptions>
            <Typography.Title level={5} style={{ marginTop: 24 }}>
              Tiến độ
            </Typography.Title>
            {detail.reports?.length ? (
              detail.reports.map((r) => (
                <Card size="small" key={r.id} style={{ marginBottom: 8 }}>
                  <Progress percent={r.progressPercentage} />
                  <div>{r.reportContent}</div>
                </Card>
              ))
            ) : (
              <Typography.Text type="secondary">
                Chưa có báo cáo tiến độ.
              </Typography.Text>
            )}
            <Space wrap style={{ marginTop: 24 }}>
              <Button
                onClick={() => setAction({ type: "report", id: detail.id })}
              >
                Nộp báo cáo tiến độ
              </Button>
              <Button
                onClick={() => setAction({ type: "final", id: detail.id })}
              >
                Nộp báo cáo tổng kết
              </Button>
              {isAdmin && (
                <Button
                  onClick={() => setAction({ type: "budget", id: detail.id })}
                >
                  Phê duyệt kinh phí
                </Button>
              )}
            </Space>
          </>
        )}
      </Drawer>
      <Modal
        title={
          {
            status: "Cập nhật trạng thái",
            budget: "Phê duyệt kinh phí",
            report: "Báo cáo tiến độ",
            final: "Báo cáo tổng kết",
          }[action?.type]
        }
        open={!!action}
        onCancel={() => setAction(null)}
        onOk={() => actionForm.submit()}
      >
        <Form form={actionForm} layout="vertical" onFinish={submitAction}>
          {action?.type === "status" && (
            <>
              <Form.Item
                name="status"
                label="Trạng thái"
                rules={[{ required: true }]}
              >
                <Select
                  options={Object.entries(statusMap).map(
                    ([value, [label]]) => ({ value, label }),
                  )}
                />
              </Form.Item>
              <Form.Item name="reason" label="Lý do">
                <Input.TextArea />
              </Form.Item>
            </>
          )}
          {action?.type === "budget" && (
            <Form.Item
              name="approvedBudget"
              label="Kinh phí được duyệt"
              rules={[{ required: true }]}
            >
              <InputNumber min={0} style={{ width: "100%" }} addonAfter="VNĐ" />
            </Form.Item>
          )}
          {action?.type === "report" && (
            <>
              <Form.Item
                name="progressPercentage"
                label="Tiến độ (%)"
                rules={[{ required: true }]}
              >
                <InputNumber min={0} max={100} style={{ width: "100%" }} />
              </Form.Item>
              <Form.Item name="reportContent" label="Nội dung">
                <Input.TextArea rows={4} />
              </Form.Item>
            </>
          )}
          {action?.type === "final" && (
            <Form.Item
              name="summaryReport"
              label="Báo cáo tóm tắt"
              rules={[{ required: true }]}
            >
              <Input.TextArea rows={5} />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </>
  );
}
