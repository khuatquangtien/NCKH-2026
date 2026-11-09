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
  Popconfirm,
  Progress,
  Radio,
  Select,
  Space,
  Table,
  Tag,
  Typography,
  Upload,
  message,
} from "antd";
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  FilePdfOutlined,
  PlusOutlined,
  SearchOutlined,
  UploadOutlined,
} from "@ant-design/icons";
import projectApi from "../api/projectApi";
import lecturerApi from "../api/lecturerApi";
import attachmentApi from "../api/attachmentApi";
import { useAuth } from "../context/AuthContext";
import { showApiError } from "../utils/apiError";

const statusMap = {
  PENDING: ["Chờ duyệt", "gold"],
  ONGOING: ["Đang thực hiện", "blue"],
  COMPLETED: ["Hoàn thành", "green"],
  REJECTED: ["Từ chối", "red"],
};

const attachmentTypeLabels = {
  PROJECT_PROPOSAL: ["Thuyết minh đề tài", "geekblue"],
  PROJECT_FINAL: ["Báo cáo nghiệm thu", "purple"],
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
  const [attachments, setAttachments] = useState([]);
  const [openCreate, setOpenCreate] = useState(false);
  const [editingProject, setEditingProject] = useState(null);
  const [action, setAction] = useState(null);
  const [uploadModal, setUploadModal] = useState(false);
  const [uploadFile, setUploadFile] = useState(null);
  const [uploadType, setUploadType] = useState("PROJECT_PROPOSAL");
  const [uploading, setUploading] = useState(false);
  const [lecturers, setLecturers] = useState([]);
  const [form] = Form.useForm();
  const [actionForm] = Form.useForm();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setRows(isAdmin ? await projectApi.search(filters) : await projectApi.getMine());
    } catch (e) {
      showApiError(e, "Không thể tải danh sách đề tài.");
    } finally {
      setLoading(false);
    }
  }, [filters, isAdmin]);

  useEffect(() => {
    load();
  }, [load]);

  useEffect(() => {
    lecturerApi.getAll().then(setLecturers).catch(() => setLecturers([]));
  }, []);

  const loadAttachments = async (projectId) => {
    try {
      const list = await attachmentApi.getByProject(projectId);
      setAttachments(Array.isArray(list) ? list : []);
    } catch {
      setAttachments([]);
    }
  };

  const showDetail = async (record) => {
    try {
      const data = await projectApi.getById(record.id, isAdmin);
      setDetail(data);
      loadAttachments(record.id);
    } catch {
      message.error("Không thể tải chi tiết đề tài.");
    }
  };

  const openEditModal = (record) => {
    setEditingProject(record);
    form.setFieldsValue({
      title: record.title,
      faculty: record.faculty,
      estimatedBudget: record.estimatedBudget,
      objective: record.objective,
      expectedProduct: record.expectedProduct,
      memberIds: record.members?.map((m) => m.lecturerId) || [],
    });
    setOpenCreate(true);
  };

  const openCreateModal = () => {
    setEditingProject(null);
    form.resetFields();
    setOpenCreate(true);
  };

  const saveProject = async (values) => {
    try {
      const payload = {
        ...values,
        memberIds: values.memberIds || [],
      };
      if (editingProject) {
        await projectApi.update(editingProject.id, payload);
        message.success("Đã cập nhật hồ sơ đề tài.");
      } else {
        await projectApi.create(payload);
        message.success("Đã tạo hồ sơ đề tài.");
      }
      setOpenCreate(false);
      setEditingProject(null);
      form.resetFields();
      load();
    } catch (e) {
      message.error(e.response?.data?.message || "Không thể lưu đề tài.");
    }
  };

  const deleteProject = async (id) => {
    try {
      await projectApi.remove(id);
      message.success("Đã xóa đề tài thành công.");
      load();
    } catch (e) {
      message.error(e.response?.data?.message || "Không thể xóa đề tài.");
    }
  };

  const submitAction = async (values) => {
    try {
      if (action.type === "status") {
        await projectApi.updateStatus(action.id, values);
      } else if (action.type === "budget") {
        await projectApi.updateBudget(action.id, values);
      } else if (action.type === "report") {
        await projectApi.submitReport(action.id, values);
      } else {
        await projectApi.submitFinal(action.id, values);
      }
      message.success("Đã cập nhật đề tài.");
      setAction(null);
      actionForm.resetFields();
      load();
      if (detail && detail.id === action.id) {
        showDetail(detail);
      }
    } catch (e) {
      message.error(e.response?.data?.message || "Thao tác không thành công.");
    }
  };

  const handleUploadAttachment = async () => {
    if (!uploadFile) {
      message.warning("Vui lòng chọn tệp PDF cần tải lên.");
      return;
    }
    setUploading(true);
    try {
      await attachmentApi.upload(uploadFile, uploadType, detail.id, null);
      message.success("Tải lên tài liệu thành công.");
      setUploadModal(false);
      setUploadFile(null);
      loadAttachments(detail.id);
    } catch (e) {
      message.error(e.response?.data?.message || "Không thể tải lên tài liệu.");
    } finally {
      setUploading(false);
    }
  };

  const handleDownloadAttachment = async (attachment) => {
    try {
      const response = await attachmentApi.download(attachment.id);
      const url = window.URL.createObjectURL(new Blob([response]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", attachment.fileName || "document.pdf");
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch {
      message.error("Không thể tải xuống tài liệu.");
    }
  };

  const handleDeleteAttachment = async (attachmentId) => {
    try {
      await attachmentApi.remove(attachmentId);
      message.success("Đã xóa tài liệu đính kèm.");
      loadAttachments(detail.id);
    } catch {
      message.error("Không thể xóa tài liệu.");
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
      title: "Thao tác",
      render: (_, r) => (
        <Space>
          <Button type="link" onClick={() => showDetail(r)}>
            Chi tiết
          </Button>
          {isAdmin && r.status === "PENDING" && (
            <Button
              type="link"
              onClick={() => setAction({ type: "status", id: r.id })}
            >
              Xét duyệt
            </Button>
          )}
          {!isAdmin && r.status === "PENDING" && (
            <>
              <Button
                type="link"
                icon={<EditOutlined />}
                onClick={() => openEditModal(r)}
              >
                Sửa
              </Button>
              <Popconfirm
                title="Xóa đề tài?"
                description="Bạn có chắc chắn muốn xóa đề tài này không?"
                onConfirm={() => deleteProject(r.id)}
                okText="Xóa"
                cancelText="Hủy"
              >
                <Button danger type="link" icon={<DeleteOutlined />}>
                  Xóa
                </Button>
              </Popconfirm>
            </>
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
          !isAdmin && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={openCreateModal}
            >
              Tạo đề tài
            </Button>
          )
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
          <Table
            rowKey="id"
            columns={columns}
            dataSource={rows}
            loading={loading}
            scroll={{ x: 900 }}
          />
        )}
      </Card>

      {/* Modal Tạo/Sửa đề tài */}
      <Modal
        title={editingProject ? "Chỉnh sửa hồ sơ đề tài" : "Tạo hồ sơ đề tài"}
        open={openCreate}
        onCancel={() => {
          setOpenCreate(false);
          setEditingProject(null);
        }}
        onOk={() => form.submit()}
        okText={editingProject ? "Cập nhật" : "Tạo hồ sơ"}
        width={720}
      >
        <Form form={form} layout="vertical" onFinish={saveProject}>
          <Form.Item
            name="title"
            label="Tên đề tài"
            rules={[{ required: true, message: "Vui lòng nhập tên đề tài" }]}
          >
            <Input />
          </Form.Item>
          <div className="form-grid">
            <Form.Item
              name="faculty"
              label="Khoa"
              rules={[{ required: true, message: "Vui lòng nhập khoa" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="estimatedBudget"
              label="Kinh phí đề xuất"
              rules={[{ required: true, message: "Vui lòng nhập kinh phí" }]}
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
          <Form.Item name="memberIds" label="Giảng viên tham gia">
            <Select
              mode="multiple"
              options={lecturers
                .filter((x) => x.id !== user?.id)
                .map((x) => ({
                  value: x.id,
                  label: `${x.fullName} — ${x.faculty}`,
                }))}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* Drawer Chi tiết đề tài */}
      <Drawer
        title="Chi tiết hồ sơ đề tài"
        width={750}
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
              {detail.status === "REJECTED" && detail.rejectionReason && (
                <Descriptions.Item label="Lý do từ chối" span={2}>
                  <Typography.Text type="danger">
                    {detail.rejectionReason}
                  </Typography.Text>
                </Descriptions.Item>
              )}
              <Descriptions.Item label="Mục tiêu" span={2}>
                {detail.objective || "—"}
              </Descriptions.Item>
              <Descriptions.Item label="Sản phẩm dự kiến" span={2}>
                {detail.expectedProduct || "—"}
              </Descriptions.Item>
              <Descriptions.Item label="Thành viên tham gia" span={2}>
                {detail.members?.length
                  ? detail.members.map((m) => m.fullName).join(", ")
                  : "Không có"}
              </Descriptions.Item>
            </Descriptions>

            {/* Tài liệu đính kèm */}
            <div style={{ marginTop: 24 }}>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  marginBottom: 12,
                }}
              >
                <Typography.Title level={5} style={{ margin: 0 }}>
                  <FilePdfOutlined style={{ marginRight: 8, color: "#1890ff" }} />
                  Tài liệu đính kèm (PDF)
                </Typography.Title>
                <Button
                  size="small"
                  type="primary"
                  icon={<UploadOutlined />}
                  onClick={() => {
                    setUploadFile(null);
                    setUploadModal(true);
                  }}
                >
                  Tải lên tệp
                </Button>
              </div>

              {attachments.length ? (
                <Table
                  size="small"
                  rowKey="id"
                  pagination={false}
                  dataSource={attachments}
                  columns={[
                    { title: "Tên tệp", dataIndex: "fileName", ellipsis: true },
                    {
                      title: "Loại tài liệu",
                      dataIndex: "fileType",
                      render: (t) => {
                        const [label, color] = attachmentTypeLabels[t] || [t, "default"];
                        return <Tag color={color}>{label}</Tag>;
                      },
                    },
                    {
                      title: "Ngày tải",
                      dataIndex: "uploadedAt",
                      render: (v) => (v ? new Date(v).toLocaleDateString("vi-VN") : "—"),
                    },
                    {
                      title: "",
                      render: (_, att) => (
                        <Space>
                          <Button
                            size="small"
                            type="link"
                            icon={<DownloadOutlined />}
                            onClick={() => handleDownloadAttachment(att)}
                          >
                            Tải về
                          </Button>
                          <Popconfirm
                            title="Xóa tài liệu này?"
                            onConfirm={() => handleDeleteAttachment(att.id)}
                          >
                            <Button size="small" danger type="link" icon={<DeleteOutlined />}>
                              Xóa
                            </Button>
                          </Popconfirm>
                        </Space>
                      ),
                    },
                  ]}
                />
              ) : (
                <Typography.Text type="secondary">
                  Chưa có tài liệu đính kèm.
                </Typography.Text>
              )}
            </div>

            {/* Tiến độ */}
            <Typography.Title level={5} style={{ marginTop: 24 }}>
              Tiến độ thực hiện
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
              {detail.status === "ONGOING" && (
                <>
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
                </>
              )}
              {isAdmin && detail.status === "PENDING" && (
                <Button
                  type="primary"
                  onClick={() => setAction({ type: "status", id: detail.id })}
                >
                  Xét duyệt đề tài
                </Button>
              )}
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

      {/* Modal Thao tác Xét duyệt / Kinh phí / Báo cáo */}
      <Modal
        title={
          {
            status: "Xét duyệt hồ sơ đề tài",
            budget: "Phê duyệt kinh phí đề tài",
            report: "Báo cáo tiến độ",
            final: "Báo cáo tổng kết nghiệm thu",
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
                label="Quyết định phê duyệt"
                rules={[{ required: true, message: "Vui lòng chọn quyết định" }]}
              >
                <Radio.Group>
                  <Radio.Button value="ONGOING">Chấp thuận (ONGOING)</Radio.Button>
                  <Radio.Button value="REJECTED">Từ chối (REJECTED)</Radio.Button>
                </Radio.Group>
              </Form.Item>
              <Form.Item
                noStyle
                shouldUpdate={(prev, curr) => prev.status !== curr.status}
              >
                {({ getFieldValue }) =>
                  getFieldValue("status") === "REJECTED" ? (
                    <Form.Item
                      name="reason"
                      label="Lý do từ chối"
                      rules={[{ required: true, message: "Vui lòng nêu rõ lý do từ chối" }]}
                    >
                      <Input.TextArea rows={3} placeholder="Nêu rõ lý do hồ sơ chưa đạt..." />
                    </Form.Item>
                  ) : null
                }
              </Form.Item>
            </>
          )}
          {action?.type === "budget" && (
            <Form.Item
              name="approvedBudget"
              label="Kinh phí được duyệt"
              rules={[{ required: true, message: "Vui lòng nhập kinh phí" }]}
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
              label="Báo cáo tóm tắt nghiệm thu"
              rules={[{ required: true }]}
            >
              <Input.TextArea rows={5} />
            </Form.Item>
          )}
        </Form>
      </Modal>

      {/* Modal Upload File Attachment */}
      <Modal
        title="Tải lên tài liệu đính kèm"
        open={uploadModal}
        onCancel={() => setUploadModal(false)}
        onOk={handleUploadAttachment}
        confirmLoading={uploading}
        okText="Tải lên"
      >
        <Form layout="vertical">
          <Form.Item label="Loại tài liệu" required>
            <Select
              value={uploadType}
              onChange={setUploadType}
              options={[
                { value: "PROJECT_PROPOSAL", label: "Thuyết minh đề tài (PROJECT_PROPOSAL)" },
                { value: "PROJECT_FINAL", label: "Báo cáo tổng kết/nghiệm thu (PROJECT_FINAL)" },
              ]}
            />
          </Form.Item>
          <Form.Item label="Chọn tệp PDF" required>
            <Upload
              beforeUpload={(file) => {
                if (file.type !== "application/pdf" && !file.name.endsWith(".pdf")) {
                  message.error("Chỉ chấp nhận tệp định dạng PDF!");
                  return Upload.LIST_IGNORE;
                }
                setUploadFile(file);
                return false;
              }}
              maxCount={1}
              onRemove={() => setUploadFile(null)}
            >
              <Button icon={<UploadOutlined />}>Chọn tệp PDF</Button>
            </Upload>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
