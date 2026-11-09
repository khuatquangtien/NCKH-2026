import { useCallback, useEffect, useState } from "react";
import {
  Button,
  Card,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Space,
  Table,
  Tabs,
  Tag,
  Typography,
  message,
} from "antd";
import {
  DeleteOutlined,
  PlusOutlined,
  TeamOutlined,
} from "@ant-design/icons";
import councilApi from "../api/councilApi";
import lecturerApi from "../api/lecturerApi";
import projectApi from "../api/projectApi";
import { useAuth } from "../context/AuthContext";
import { showApiError } from "../utils/apiError";

const positionLabels = {
  CHAIRMAN: ["Chủ tịch", "gold"],
  SECRETARY: ["Thư ký", "blue"],
  REVIEWER: ["Phản biện", "purple"],
};

const gradeColors = {
  EXCELLENT: "green",
  GOOD: "blue",
  PASSED: "gold",
  FAILED: "red",
};

export default function CouncilManager() {
  const { user } = useAuth();
  const isAdmin = user?.role?.name === "ADMIN";
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [lecturers, setLecturers] = useState([]);
  const [projects, setProjects] = useState([]);
  const [selected, setSelected] = useState(null);
  const [members, setMembers] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [evaluations, setEvaluations] = useState([]);
  const [taskForm] = Form.useForm();
  const [form] = Form.useForm();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setRows(isAdmin ? await councilApi.getAll() : await councilApi.getMine());
    } catch (e) {
      showApiError(e, "Không thể tải danh sách hội đồng.");
    } finally {
      setLoading(false);
    }
  }, [isAdmin]);

  useEffect(() => {
    load();
    lecturerApi.getAll().then(setLecturers).catch(() => setLecturers([]));
    if (isAdmin) {
      projectApi.search({}).then(setProjects).catch(() => setProjects([]));
    } else {
      projectApi.getMine().then(setProjects).catch(() => setProjects([]));
    }
  }, [isAdmin, load]);

  const getLecturerName = (lecturerId) => {
    const found = lecturers.find((l) => l.id === Number(lecturerId));
    return found ? `${found.fullName} (${found.faculty})` : `Giảng viên #${lecturerId}`;
  };

  const getProjectTitle = (projectId) => {
    const found = projects.find((p) => p.id === Number(projectId));
    return found ? found.title : `Đề tài #${projectId}`;
  };

  const openDetail = async (row) => {
    setSelected(row);
    try {
      const [a, e, m] = await Promise.all([
        councilApi.getAssignments(row.id),
        councilApi.getEvaluations(row.id),
        councilApi.getMembers(row.id),
      ]);
      setAssignments(Array.isArray(a) ? a : []);
      setEvaluations(Array.isArray(e) ? e : []);
      setMembers(Array.isArray(m) ? m : []);
    } catch (e) {
      showApiError(e, "Không thể tải thông tin chi tiết hội đồng.");
    }
  };

  const create = async (v) => {
    try {
      await councilApi.create({
        name: v.name,
        members: [
          { lecturerId: v.chairmanId, position: "CHAIRMAN" },
          { lecturerId: v.secretaryId, position: "SECRETARY" },
          ...(v.reviewerIds || []).map((lecturerId) => ({
            lecturerId,
            position: "REVIEWER",
          })),
        ],
      });
      message.success("Đã thành lập hội đồng.");
      setOpen(false);
      form.resetFields();
      load();
    } catch (e) {
      showApiError(e, "Không thể tạo hội đồng.");
    }
  };

  const handleRemoveAssignment = async (projectId) => {
    try {
      await councilApi.removeAssignment(selected.id, projectId);
      message.success("Đã gỡ phân công đề tài.");
      setAssignments(await councilApi.getAssignments(selected.id));
    } catch (e) {
      showApiError(e, "Không thể gỡ phân công đề tài.");
    }
  };

  const columns = [
    { title: "Mã", dataIndex: "id", width: 80 },
    { title: "Tên hội đồng", dataIndex: "name" },
    !isAdmin && {
      title: "Vị trí của tôi",
      dataIndex: "position",
      render: (v) => {
        const [label, color] = positionLabels[v] || [v, "default"];
        return <Tag color={color}>{label}</Tag>;
      },
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      render: (v) => <Tag color="blue">{v || "ACTIVE"}</Tag>,
    },
    {
      title: "Ngày thành lập",
      dataIndex: "createdAt",
      render: (v) => (v ? new Date(v).toLocaleDateString("vi-VN") : "—"),
    },
    {
      title: "Thao tác",
      render: (_, row) => (
        <Space>
          <Button type="link" onClick={() => openDetail(row)}>
            Chi tiết
          </Button>
          {isAdmin && (
            <Popconfirm
              title="Xóa hội đồng này?"
              description={row.name}
              onConfirm={async () => {
                await councilApi.remove(row.id);
                message.success("Đã xóa hội đồng.");
                load();
              }}
            >
              <Button danger type="link" icon={<DeleteOutlined />}>
                Xóa
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ].filter(Boolean);

  return (
    <>
      <Card
        className="page-card"
        title={isAdmin ? "Quản lý hội đồng khoa học" : "Hội đồng của tôi"}
        extra={
          isAdmin && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => setOpen(true)}
            >
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

      {/* Modal Thành lập hội đồng */}
      <Modal
        width={650}
        title="Thành lập hội đồng khoa học"
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
        okText="Thành lập"
      >
        <Form form={form} layout="vertical" onFinish={create}>
          <Form.Item
            name="name"
            label="Tên hội đồng"
            rules={[{ required: true, message: "Vui lòng nhập tên hội đồng" }]}
          >
            <Input placeholder="Ví dụ: Hội đồng nghiệm thu Đề tài NCKH Khoa CNTT 2026" />
          </Form.Item>
          <div className="form-grid">
            <Form.Item
              name="chairmanId"
              label="Chủ tịch hội đồng"
              rules={[{ required: true, message: "Vui lòng chọn chủ tịch" }]}
            >
              <Select
                showSearch
                placeholder="Chọn giảng viên"
                optionFilterProp="label"
                options={lecturers.map((x) => ({
                  value: x.id,
                  label: `${x.fullName} — ${x.faculty}`,
                }))}
              />
            </Form.Item>
            <Form.Item
              name="secretaryId"
              label="Thư ký hội đồng"
              rules={[{ required: true, message: "Vui lòng chọn thư ký" }]}
            >
              <Select
                showSearch
                placeholder="Chọn giảng viên"
                optionFilterProp="label"
                options={lecturers.map((x) => ({
                  value: x.id,
                  label: `${x.fullName} — ${x.faculty}`,
                }))}
              />
            </Form.Item>
          </div>
          <Form.Item
            name="reviewerIds"
            label="Ủy viên phản biện"
            rules={[{ required: true, message: "Vui lòng chọn ít nhất 1 phản biện" }]}
          >
            <Select
              mode="multiple"
              showSearch
              placeholder="Chọn các giảng viên phản biện"
              optionFilterProp="label"
              options={lecturers.map((x) => ({
                value: x.id,
                label: `${x.fullName} — ${x.faculty}`,
              }))}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* Modal Chi tiết hội đồng */}
      <Modal
        width={850}
        title={`Chi tiết: ${selected?.name || ""}`}
        open={!!selected}
        onCancel={() => setSelected(null)}
        footer={null}
      >
        <Tabs
          defaultActiveKey="1"
          items={[
            {
              key: "1",
              label: (
                <span>
                  <TeamOutlined /> Thành viên hội đồng
                </span>
              ),
              children: (
                <Table
                  pagination={false}
                  rowKey="id"
                  dataSource={members}
                  columns={[
                    {
                      title: "Giảng viên",
                      dataIndex: "lecturerId",
                      render: (id) => getLecturerName(id),
                    },
                    {
                      title: "Vai trò",
                      dataIndex: "position",
                      render: (pos) => {
                        const [label, color] = positionLabels[pos] || [pos, "default"];
                        return <Tag color={color}>{label}</Tag>;
                      },
                    },
                  ]}
                  locale={{ emptyText: "Chưa có danh sách thành viên" }}
                />
              ),
            },
            {
              key: "2",
              label: "Đề tài phân công",
              children: (
                <>
                  <Table
                    pagination={false}
                    rowKey="id"
                    dataSource={assignments}
                    columns={[
                      { title: "Mã", dataIndex: "projectId", width: 70 },
                      {
                        title: "Tên đề tài",
                        dataIndex: "projectId",
                        render: (id) => getProjectTitle(id),
                      },
                      {
                        title: "Ngày giao",
                        dataIndex: "assignedAt",
                        render: (v) => (v ? new Date(v).toLocaleDateString("vi-VN") : "—"),
                      },
                      isAdmin && {
                        title: "",
                        render: (_, a) => (
                          <Popconfirm
                            title="Gỡ đề tài khỏi hội đồng?"
                            onConfirm={() => handleRemoveAssignment(a.projectId)}
                          >
                            <Button size="small" danger type="link">
                              Gỡ
                            </Button>
                          </Popconfirm>
                        ),
                      },
                    ].filter(Boolean)}
                    locale={{ emptyText: "Chưa phân công đề tài nào" }}
                  />
                  {isAdmin && (
                    <Form
                      layout="inline"
                      style={{ marginTop: 16 }}
                      onFinish={async ({ projectId }) => {
                        try {
                          await councilApi.assignProject(selected.id, projectId);
                          message.success("Đã giao đề tài cho hội đồng.");
                          setAssignments(await councilApi.getAssignments(selected.id));
                        } catch (e) {
                          showApiError(e, "Không thể giao đề tài.");
                        }
                      }}
                    >
                      <Form.Item
                        name="projectId"
                        rules={[{ required: true, message: "Chọn đề tài" }]}
                        style={{ minWidth: 380 }}
                      >
                        <Select
                          showSearch
                          optionFilterProp="label"
                          placeholder="Chọn đề tài để giao"
                          options={projects.map((p) => ({
                            value: p.id,
                            label: `#${p.id} - ${p.title}`,
                          }))}
                        />
                      </Form.Item>
                      <Button htmlType="submit" type="primary">
                        Giao đề tài
                      </Button>
                    </Form>
                  )}
                </>
              ),
            },
            {
              key: "3",
              label: "Kết quả đánh giá",
              children: (
                <>
                  <Table
                    pagination={false}
                    rowKey="id"
                    dataSource={evaluations}
                    columns={[
                      {
                        title: "Đề tài",
                        dataIndex: "projectId",
                        render: (id) => getProjectTitle(id),
                      },
                      {
                        title: "Người đánh giá",
                        dataIndex: "evaluatorId",
                        render: (id) => getLecturerName(id),
                      },
                      {
                        title: "Điểm",
                        dataIndex: "score",
                        render: (s) => (s != null ? Number(s).toFixed(1) : "—"),
                      },
                      {
                        title: "Xếp loại",
                        dataIndex: "grade",
                        render: (g) => <Tag color={gradeColors[g] || "default"}>{g}</Tag>,
                      },
                      {
                        title: "Nhận xét",
                        dataIndex: "meetingMinutes",
                        ellipsis: true,
                        render: (t) => t || "—",
                      },
                    ]}
                    locale={{ emptyText: "Chưa có đánh giá nào" }}
                  />

                  {/* Form đánh giá cho thành viên hội đồng */}
                  {!isAdmin && assignments.length > 0 && (
                    <Card size="small" title="Đánh giá đề tài" style={{ marginTop: 16 }}>
                      <Form
                        form={taskForm}
                        layout="vertical"
                        onFinish={async (v) => {
                          try {
                            await councilApi.evaluate({ ...v, councilId: selected.id });
                            message.success("Đã lưu kết quả đánh giá.");
                            setEvaluations(await councilApi.getEvaluations(selected.id));
                            taskForm.resetFields();
                          } catch (e) {
                            showApiError(e, "Không thể lưu đánh giá.");
                          }
                        }}
                      >
                        <div className="form-grid">
                          <Form.Item
                            name="projectId"
                            label="Đề tài"
                            rules={[{ required: true, message: "Chọn đề tài" }]}
                          >
                            <Select
                              placeholder="Chọn đề tài đánh giá"
                              options={assignments.map((a) => ({
                                value: a.projectId,
                                label: getProjectTitle(a.projectId),
                              }))}
                            />
                          </Form.Item>
                          <Form.Item
                            name="score"
                            label="Điểm (Thang 10)"
                            rules={[{ required: true, message: "Nhập điểm" }]}
                          >
                            <InputNumber min={0} max={10} step={0.1} style={{ width: "100%" }} />
                          </Form.Item>
                        </div>
                        <Form.Item
                          name="grade"
                          label="Xếp loại"
                          rules={[{ required: true, message: "Chọn xếp loại" }]}
                        >
                          <Select
                            options={[
                              { value: "EXCELLENT", label: "Xuất sắc (EXCELLENT)" },
                              { value: "GOOD", label: "Tốt (GOOD)" },
                              { value: "PASSED", label: "Đạt (PASSED)" },
                              { value: "FAILED", label: "Không đạt (FAILED)" },
                            ]}
                          />
                        </Form.Item>
                        <Form.Item name="meetingMinutes" label="Nhận xét / Ý kiến đánh giá">
                          <Input.TextArea rows={3} placeholder="Ghi nhận xét đánh giá đề tài..." />
                        </Form.Item>
                        <Button htmlType="submit" type="primary">
                          Lưu đánh giá
                        </Button>
                      </Form>
                    </Card>
                  )}
                </>
              ),
            },
          ]}
        />
      </Modal>
    </>
  );
}
