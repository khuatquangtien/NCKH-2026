import { useCallback, useEffect, useState } from "react";
import {
  Button,
  Card,
  Drawer,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
  Tooltip,
  Typography,
  Upload,
  message,
} from "antd";
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  FilePdfOutlined,
  InfoCircleOutlined,
  PlusOutlined,
  UploadOutlined,
} from "@ant-design/icons";
import paperApi from "../api/paperApi";
import attachmentApi from "../api/attachmentApi";
import { useAuth } from "../context/AuthContext";
import { showApiError } from "../utils/apiError";

const labels = {
  PENDING: ["Chờ duyệt", "gold"],
  APPROVED: ["Đã duyệt", "green"],
  REJECTED: ["Từ chối", "red"],
};

export default function PaperManager() {
  const { user } = useAuth();
  const isAdmin = user?.role?.name === "ADMIN";
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editingPaper, setEditingPaper] = useState(null);
  const [rejectModal, setRejectModal] = useState(false);
  const [rejectingId, setRejectingId] = useState(null);
  const [rejectReason, setRejectReason] = useState("");
  const [evidenceDrawer, setEvidenceDrawer] = useState(null);
  const [evidenceList, setEvidenceList] = useState([]);
  const [uploadModal, setUploadModal] = useState(false);
  const [uploadFile, setUploadFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [form] = Form.useForm();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = isAdmin ? await paperApi.getPending() : await paperApi.getMine();
      setRows(Array.isArray(data) ? data : []);
    } catch (e) {
      showApiError(e, "Không thể tải danh sách bài báo.");
    } finally {
      setLoading(false);
    }
  }, [isAdmin]);

  useEffect(() => {
    load();
  }, [load]);

  const loadEvidences = async (paperId) => {
    try {
      const list = await attachmentApi.getByPaper(paperId);
      setEvidenceList(Array.isArray(list) ? list : []);
    } catch {
      setEvidenceList([]);
    }
  };

  const openEvidenceDrawer = (paper) => {
    setEvidenceDrawer(paper);
    loadEvidences(paper.id);
  };

  const openCreateModal = () => {
    setEditingPaper(null);
    form.resetFields();
    setOpen(true);
  };

  const openEditModal = (record) => {
    setEditingPaper(record);
    form.setFieldsValue({
      title: record.title,
      journalName: record.journalName,
      publicationYear: record.publicationYear,
      journalRanking: record.journalRanking,
      issnIsbn: record.issnIsbn,
    });
    setOpen(true);
  };

  const savePaper = async (values) => {
    try {
      if (editingPaper) {
        await paperApi.update(editingPaper.id, values);
        message.success("Đã cập nhật thông tin bài báo.");
      } else {
        await paperApi.create(values);
        message.success("Đã khai báo bài báo mới.");
      }
      setOpen(false);
      setEditingPaper(null);
      form.resetFields();
      load();
    } catch (e) {
      showApiError(e, "Không thể lưu bài báo.");
    }
  };

  const handleVerify = async (id, status) => {
    if (status === "REJECTED") {
      setRejectingId(id);
      setRejectReason("");
      setRejectModal(true);
      return;
    }
    try {
      await paperApi.verify(id, status);
      message.success("Đã phê duyệt bài báo.");
      load();
    } catch (e) {
      showApiError(e, "Không thể phê duyệt bài báo.");
    }
  };

  const confirmReject = async () => {
    if (!rejectReason.trim()) {
      message.warning("Vui lòng nhập lý do từ chối bài báo.");
      return;
    }
    try {
      await paperApi.verify(rejectingId, "REJECTED", rejectReason.trim());
      message.success("Đã từ chối bài báo.");
      setRejectModal(false);
      setRejectingId(null);
      load();
    } catch (e) {
      showApiError(e, "Thao tác từ chối thất bại.");
    }
  };

  const handleUploadEvidence = async () => {
    if (!uploadFile) {
      message.warning("Vui lòng chọn tệp PDF minh chứng.");
      return;
    }
    setUploading(true);
    try {
      await attachmentApi.upload(uploadFile, "PAPER_EVIDENCE", null, evidenceDrawer.id);
      message.success("Tải lên minh chứng bài báo thành công.");
      setUploadModal(false);
      setUploadFile(null);
      loadEvidences(evidenceDrawer.id);
    } catch (e) {
      showApiError(e, "Không thể tải lên tệp minh chứng.");
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
      link.setAttribute("download", attachment.fileName || "minh-chung.pdf");
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (e) {
      showApiError(e, "Không thể tải xuống tài liệu.");
    }
  };

  const handleDeleteAttachment = async (attachmentId) => {
    try {
      await attachmentApi.remove(attachmentId);
      message.success("Đã xóa minh chứng.");
      loadEvidences(evidenceDrawer.id);
    } catch (e) {
      showApiError(e, "Không thể xóa tài liệu.");
    }
  };

  const columns = [
    { title: "Tên bài báo", dataIndex: "title", ellipsis: true },
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
      render: (v, r) => (
        <Space size={4}>
          <Tag color={(labels[v] || [v, "default"])[1]}>
            {(labels[v] || [v])[0]}
          </Tag>
          {v === "REJECTED" && r.rejectionReason && (
            <Tooltip title={`Lý do: ${r.rejectionReason}`}>
              <InfoCircleOutlined style={{ color: "#ff4d4f", cursor: "pointer" }} />
            </Tooltip>
          )}
        </Space>
      ),
    },
    {
      title: "Minh chứng",
      render: (_, r) => (
        <Button
          type="link"
          icon={<FilePdfOutlined />}
          onClick={() => openEvidenceDrawer(r)}
        >
          Tệp đính kèm
        </Button>
      ),
    },
    {
      title: "Thao tác",
      render: (_, r) =>
        isAdmin ? (
          <Space>
            <Button type="link" onClick={() => handleVerify(r.id, "APPROVED")}>
              Duyệt
            </Button>
            <Button danger type="link" onClick={() => handleVerify(r.id, "REJECTED")}>
              Từ chối
            </Button>
          </Space>
        ) : (
          <Space>
            {r.status === "PENDING" && (
              <>
                <Button
                  type="link"
                  icon={<EditOutlined />}
                  onClick={() => openEditModal(r)}
                >
                  Sửa
                </Button>
                <Popconfirm
                  title="Xóa bài báo?"
                  description="Bạn có chắc chắn muốn xóa bài báo này?"
                  onConfirm={async () => {
                    await paperApi.remove(r.id);
                    message.success("Đã xóa bài báo.");
                    load();
                  }}
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
        className="page-card"
        title={isAdmin ? "Bài báo khoa học chờ xét duyệt" : "Bài báo khoa học"}
        extra={
          !isAdmin && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={openCreateModal}
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
          scroll={{ x: 850 }}
          locale={{
            emptyText: isAdmin
              ? "Không có bài báo chờ duyệt"
              : "Chưa có bài báo khoa học nào",
          }}
        />
      </Card>

      {/* Modal Khai báo / Chỉnh sửa bài báo */}
      <Modal
        title={editingPaper ? "Chỉnh sửa thông tin bài báo" : "Khai báo bài báo khoa học"}
        open={open}
        onCancel={() => {
          setOpen(false);
          setEditingPaper(null);
        }}
        onOk={() => form.submit()}
        okText={editingPaper ? "Cập nhật" : "Khai báo"}
      >
        <Form form={form} layout="vertical" onFinish={savePaper}>
          <Form.Item
            name="title"
            label="Tên bài báo"
            rules={[{ required: true, message: "Vui lòng nhập tên bài báo" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="journalName"
            label="Tên tạp chí"
            rules={[{ required: true, message: "Vui lòng nhập tên tạp chí" }]}
          >
            <Input />
          </Form.Item>
          <div className="form-grid">
            <Form.Item
              name="publicationYear"
              label="Năm công bố"
              rules={[{ required: true, message: "Vui lòng nhập năm công bố" }]}
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
              rules={[{ required: true, message: "Vui lòng chọn xếp hạng" }]}
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
            <Input placeholder="Ví dụ: 2045-2322" />
          </Form.Item>
        </Form>
      </Modal>

      {/* Modal Lý do từ chối bài báo */}
      <Modal
        title="Từ chối phê duyệt bài báo"
        open={rejectModal}
        onCancel={() => setRejectModal(false)}
        onOk={confirmReject}
        okText="Xác nhận từ chối"
        okButtonProps={{ danger: true }}
      >
        <Form layout="vertical">
          <Form.Item label="Lý do từ chối" required>
            <Input.TextArea
              rows={4}
              placeholder="Nhập lý do bài báo chưa hợp lệ hoặc cần bổ sung minh chứng..."
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* Drawer Minh chứng bài báo */}
      <Drawer
        title={`Minh chứng bài báo: ${evidenceDrawer?.title || ""}`}
        width={600}
        open={!!evidenceDrawer}
        onClose={() => setEvidenceDrawer(null)}
      >
        <div style={{ marginBottom: 16, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <Typography.Title level={5} style={{ margin: 0 }}>
            Tệp minh chứng đã đính kèm
          </Typography.Title>
          {!isAdmin && (
            <Button
              size="small"
              type="primary"
              icon={<UploadOutlined />}
              onClick={() => {
                setUploadFile(null);
                setUploadModal(true);
              }}
            >
              Tải lên minh chứng
            </Button>
          )}
        </div>

        {evidenceList.length ? (
          <Table
            size="small"
            rowKey="id"
            pagination={false}
            dataSource={evidenceList}
            columns={[
              { title: "Tên tệp", dataIndex: "fileName", ellipsis: true },
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
                    {!isAdmin && (
                      <Popconfirm
                        title="Xóa minh chứng này?"
                        onConfirm={() => handleDeleteAttachment(att.id)}
                      >
                        <Button size="small" danger type="link" icon={<DeleteOutlined />}>
                          Xóa
                        </Button>
                      </Popconfirm>
                    )}
                  </Space>
                ),
              },
            ]}
          />
        ) : (
          <Typography.Text type="secondary">
            Chưa có tài liệu minh chứng nào cho bài báo này.
          </Typography.Text>
        )}
      </Drawer>

      {/* Modal Upload Minh chứng */}
      <Modal
        title="Tải lên tệp minh chứng bài báo"
        open={uploadModal}
        onCancel={() => setUploadModal(false)}
        onOk={handleUploadEvidence}
        confirmLoading={uploading}
        okText="Tải lên"
      >
        <Form layout="vertical">
          <Form.Item label="Chọn tệp PDF minh chứng" required>
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
