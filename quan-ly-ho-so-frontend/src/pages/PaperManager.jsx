import { useCallback, useEffect, useState } from "react";
import {
  Button,
  Card,
  Form,
  Input,
  InputNumber,
  Modal,
  Select,
  Space,
  Table,
  Tag,
  message,
} from "antd";
import { PlusOutlined } from "@ant-design/icons";
import paperApi from "../api/paperApi";
import { useAuth } from "../context/AuthContext";

const labels = {
  PENDING: ["Chờ duyệt", "gold"],
  APPROVED: ["Đã duyệt", "green"],
  REJECTED: ["Từ chối", "red"],
};
export default function PaperManager() {
  const { user } = useAuth();
  const isAdmin = user?.role?.name === "ADMIN";
  const lecturerId = user?.id;
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();
  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = isAdmin
        ? await paperApi.getPending()
        : lecturerId
          ? await paperApi.getByLecturer(lecturerId)
          : [];
      setRows(Array.isArray(data) ? data : []);
    } catch {
      message.error("Không thể tải danh sách bài báo.");
    } finally {
      setLoading(false);
    }
  }, [isAdmin, lecturerId]);
  useEffect(() => {
    load();
  }, [load]);
  const create = async (values) => {
    try {
      await paperApi.create({ ...values, creatorId: user?.id });
      message.success("Đã khai báo bài báo.");
      setOpen(false);
      form.resetFields();
      load();
    } catch {
      message.error("Không thể khai báo bài báo.");
    }
  };
  const verify = async (id, status) => {
    try {
      await paperApi.verify(id, status);
      message.success("Đã cập nhật trạng thái.");
      load();
    } catch {
      message.error("Không thể xác thực bài báo.");
    }
  };
  const columns = [
    { title: "Tên bài báo", dataIndex: "title" },
    { title: "Tạp chí", dataIndex: "journalName" },
    { title: "Năm", dataIndex: "publicationYear", width: 80 },
    {
      title: "Xếp hạng",
      dataIndex: "journalRanking",
      render: (v) => <Tag>{v}</Tag>,
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      render: (v) => (
        <Tag color={(labels[v] || [v, "default"])[1]}>
          {(labels[v] || [v])[0]}
        </Tag>
      ),
    },
    {
      title: "",
      render: (_, r) =>
        isAdmin ? (
          <Space>
            <Button type="link" onClick={() => verify(r.id, "APPROVED")}>
              Duyệt
            </Button>
            <Button danger type="link" onClick={() => verify(r.id, "REJECTED")}>
              Từ chối
            </Button>
          </Space>
        ) : (
          <Button
            danger
            type="link"
            onClick={async () => {
              await paperApi.remove(r.id);
              load();
            }}
          >
            Xóa
          </Button>
        ),
    },
  ];
  return (
    <>
      <Card
        className="page-card"
        title={isAdmin ? "Bài báo chờ xác thực" : "Bài báo khoa học"}
        extra={
          !isAdmin && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => setOpen(true)}
            >
              Khai báo bài báo
            </Button>
          )
        }
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={rows}
          loading={loading}
          scroll={{ x: 760 }}
          locale={{
            emptyText: isAdmin
              ? "Không có bài báo chờ duyệt"
              : "Chưa có dữ liệu hoặc hồ sơ chưa có mã giảng viên",
          }}
        />
      </Card>
      <Modal
        title="Khai báo bài báo khoa học"
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
        okText="Khai báo"
      >
        <Form form={form} layout="vertical" onFinish={create}>
          <Form.Item
            name="title"
            label="Tên bài báo"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="journalName"
            label="Tên tạp chí"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <div className="form-grid">
            <Form.Item
              name="publicationYear"
              label="Năm công bố"
              rules={[{ required: true }]}
            >
              <InputNumber
                min={1900}
                max={new Date().getFullYear()}
                style={{ width: "100%" }}
              />
            </Form.Item>
            <Form.Item
              name="journalRanking"
              label="Xếp hạng"
              rules={[{ required: true }]}
            >
              <Select
                options={["SCOPUS", "ISI", "DOMESTIC"].map((value) => ({
                  value,
                  label: value,
                }))}
              />
            </Form.Item>
          </div>
          <Form.Item name="issnIsbn" label="ISSN/ISBN">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
