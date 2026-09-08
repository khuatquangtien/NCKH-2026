import { useEffect, useState } from "react";
import { Button, Card, Form, Input, Modal, Table, Tag, message } from "antd";
import councilApi from "../api/councilApi";
import { useAuth } from "../context/AuthContext";

export default function CouncilManager() {
  const { user } = useAuth();
  const isAdmin = user?.role?.name === "ADMIN";
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();
  const load = async () => {
    setLoading(true);
    try {
      setRows(await councilApi.getAll());
    } catch {
      message.error("Không thể tải danh sách hội đồng.");
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    load();
  }, []);
  const create = async (v) => {
    try {
      await councilApi.create({
        name: v.name,
        members: [
          { lecturerId: v.chairmanId, position: "CHAIRMAN" },
          { lecturerId: v.secretaryId, position: "SECRETARY" },
          ...v.reviewerIds
            .split(",")
            .map(Number)
            .filter(Boolean)
            .map((lecturerId) => ({ lecturerId, position: "REVIEWER" })),
        ],
      });
      message.success("Đã tạo hội đồng.");
      setOpen(false);
      form.resetFields();
      load();
    } catch {
      message.error("Không thể tạo hội đồng.");
    }
  };
  const columns = [
    { title: "Mã", dataIndex: "id", width: 80 },
    { title: "Tên hội đồng", dataIndex: "name" },
    {
      title: "Trạng thái",
      dataIndex: "status",
      render: (v) => <Tag color="blue">{v || "Đã thành lập"}</Tag>,
    },
    {
      title: "Ngày thành lập",
      dataIndex: "createdAt",
      render: (v) => (v ? new Date(v).toLocaleDateString("vi-VN") : "—"),
    },
  ];
  return (
    <>
      <Card
        className="page-card"
        title="Hội đồng khoa học"
        extra={
          isAdmin && (
            <Button type="primary" onClick={() => setOpen(true)}>
              Thành lập hội đồng
            </Button>
          )
        }
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={rows}
          loading={loading}
        />
      </Card>
      <Modal
        width={650}
        title="Thành lập hội đồng"
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
        okText="Tạo hội đồng"
      >
        <Form form={form} layout="vertical" onFinish={create}>
          <Form.Item
            name="name"
            label="Tên hội đồng"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <div className="form-grid">
            <Form.Item
              name="chairmanId"
              label="Mã giảng viên chủ tịch"
              rules={[{ required: true }]}
            >
              <Input type="number" />
            </Form.Item>
            <Form.Item
              name="secretaryId"
              label="Mã giảng viên thư ký"
              rules={[{ required: true }]}
            >
              <Input type="number" />
            </Form.Item>
          </div>
          <Form.Item
            name="reviewerIds"
            label="Mã giảng viên phản biện"
            rules={[{ required: true }]}
          >
            <Input placeholder="Ví dụ: 3, 5, 8" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
