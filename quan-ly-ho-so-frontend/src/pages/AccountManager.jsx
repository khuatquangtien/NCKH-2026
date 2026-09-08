import { useEffect, useMemo, useState } from "react";
import {
  Avatar,
  Button,
  Card,
  Col,
  Form,
  Input,
  Modal,
  Popconfirm,
  Row,
  Select,
  Space,
  Statistic,
  Table,
  Tag,
  Tooltip,
  Typography,
  message,
} from "antd";
import {
  CheckCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  LockOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  TeamOutlined,
  UserOutlined,
} from "@ant-design/icons";
import adminApi from "../api/adminApi";
import authApi from "../api/authApi";
import EditAccountModal from "../components/AccountManager/EditAccountModal";

const roleMeta = {
  ADMIN: { label: "Quản trị viên", color: "blue" },
  LECTURER: { label: "Giảng viên", color: "cyan" },
  COUNCIL: { label: "Hội đồng", color: "purple" },
  COUNCIL_MEMBER: { label: "Thành viên hội đồng", color: "purple" },
};

export default function AccountManager() {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [query, setQuery] = useState("");
  const [roleFilter, setRoleFilter] = useState();
  const [statusFilter, setStatusFilter] = useState();
  const [editing, setEditing] = useState(null);
  const [createOpen, setCreateOpen] = useState(false);
  const [creating, setCreating] = useState(false);
  const [createForm] = Form.useForm();

  const loadAccounts = async () => {
    setLoading(true);
    try {
      const data = await adminApi.getAllAccounts();
      setAccounts(Array.isArray(data) ? data : []);
    } catch {
      message.error("Không thể tải danh sách tài khoản.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  const filteredAccounts = useMemo(
    () =>
      accounts.filter((account) => {
        const role =
          account.rolename || account.roleName || account.role?.name || "";
        const active = account.isActive ?? account.is_active ?? false;
        const keyword = query.trim().toLocaleLowerCase("vi");
        const matchesKeyword =
          !keyword ||
          [account.username, account.email, account.id].some((value) =>
            String(value ?? "")
              .toLocaleLowerCase("vi")
              .includes(keyword),
          );
        return (
          matchesKeyword &&
          (!roleFilter || role === roleFilter) &&
          (statusFilter === undefined || active === statusFilter)
        );
      }),
    [accounts, query, roleFilter, statusFilter],
  );

  const activeCount = accounts.filter(
    (item) => item.isActive ?? item.is_active,
  ).length;
  const adminCount = accounts.filter(
    (item) => (item.rolename || item.roleName || item.role?.name) === "ADMIN",
  ).length;

  const remove = async (account) => {
    try {
      await adminApi.deleteAccount(account.id);
      message.success(`Đã xóa tài khoản ${account.username}.`);
      loadAccounts();
    } catch (error) {
      message.error(
        error.response?.data?.message || "Không thể xóa tài khoản này.",
      );
    }
  };

  const createAccount = async (values) => {
    setCreating(true);
    try {
      await authApi.register({
        username: values.username,
        email: values.email,
        password: values.password,
      });
      if (values.roleId && values.roleId !== 2)
        await adminApi.assignRole({
          username: values.username,
          roleId: values.roleId,
        });
      message.success("Đã tạo tài khoản mới.");
      setCreateOpen(false);
      createForm.resetFields();
      loadAccounts();
    } catch (error) {
      message.error(
        typeof error.response?.data === "string"
          ? error.response.data
          : "Không thể tạo tài khoản.",
      );
    } finally {
      setCreating(false);
    }
  };

  const columns = [
    {
      title: "Tài khoản",
      key: "account",
      render: (_, record) => (
        <div className="account-cell">
          <Avatar icon={<UserOutlined />} className="account-avatar" />
          <div>
            <Typography.Text strong>{record.username}</Typography.Text>
            <Typography.Text type="secondary">ID: {record.id}</Typography.Text>
          </div>
        </div>
      ),
    },
    {
      title: "Email",
      dataIndex: "email",
      ellipsis: true,
      render: (value) =>
        value || (
          <Typography.Text type="secondary">Chưa cập nhật</Typography.Text>
        ),
    },
    {
      title: "Vai trò",
      key: "role",
      render: (_, record) => {
        const role =
          record.rolename || record.roleName || record.role?.name || "NO_ROLE";
        const meta = roleMeta[role] || {
          label: role === "NO_ROLE" ? "Chưa phân quyền" : role,
          color: "default",
        };
        return <Tag color={meta.color}>{meta.label}</Tag>;
      },
    },
    {
      title: "Trạng thái",
      key: "status",
      render: (_, record) => {
        const active = record.isActive ?? record.is_active;
        return (
          <span className={`status-pill ${active ? "is-active" : "is-locked"}`}>
            <span />
            {active ? "Đang hoạt động" : "Đã khóa"}
          </span>
        );
      },
    },
    {
      title: "Thao tác",
      key: "actions",
      width: 120,
      align: "right",
      render: (_, record) => (
        <Space size={4}>
          <Tooltip title="Chỉnh sửa">
            <Button
              type="text"
              icon={<EditOutlined />}
              onClick={() => setEditing(record)}
            />
          </Tooltip>
          <Popconfirm
            title="Xóa tài khoản?"
            description={`Tài khoản ${record.username} sẽ bị xóa khỏi hệ thống.`}
            okText="Xóa"
            cancelText="Hủy"
            okButtonProps={{ danger: true }}
            onConfirm={() => remove(record)}
          >
            <Tooltip title="Xóa">
              <Button type="text" danger icon={<DeleteOutlined />} />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="accounts-page">
      <div className="page-heading">
        <div>
          <Typography.Title level={3}>Quản lý tài khoản</Typography.Title>
          <Typography.Text type="secondary">
            Quản lý người dùng, vai trò và quyền truy cập hệ thống.
          </Typography.Text>
        </div>
        <Button
          type="primary"
          size="large"
          icon={<PlusOutlined />}
          onClick={() => setCreateOpen(true)}
        >
          Thêm tài khoản
        </Button>
      </div>
      <Row gutter={[16, 16]} className="account-stats">
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Tổng tài khoản"
              value={accounts.length}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Đang hoạt động"
              value={activeCount}
              prefix={<CheckCircleOutlined className="stat-active" />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Quản trị viên"
              value={adminCount}
              prefix={<LockOutlined className="stat-admin" />}
            />
          </Card>
        </Col>
      </Row>
      <Card className="page-card accounts-card">
        <div className="account-toolbar">
          <Input
            allowClear
            prefix={<SearchOutlined />}
            placeholder="Tìm theo tên, email hoặc mã tài khoản"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <Select
            allowClear
            placeholder="Tất cả vai trò"
            value={roleFilter}
            onChange={setRoleFilter}
            options={Object.entries(roleMeta)
              .filter(([key]) => key !== "COUNCIL_MEMBER")
              .map(([value, meta]) => ({ value, label: meta.label }))}
          />
          <Select
            allowClear
            placeholder="Tất cả trạng thái"
            value={statusFilter}
            onChange={setStatusFilter}
            options={[
              { value: true, label: "Đang hoạt động" },
              { value: false, label: "Đã khóa" },
            ]}
          />
          <Tooltip title="Tải lại">
            <Button
              icon={<ReloadOutlined />}
              onClick={loadAccounts}
              loading={loading}
            />
          </Tooltip>
        </div>
        <Table
          rowKey="id"
          columns={columns}
          dataSource={filteredAccounts}
          loading={loading}
          pagination={{
            pageSize: 8,
            showSizeChanger: false,
            showTotal: (total) => `${total} tài khoản`,
          }}
          scroll={{ x: 850 }}
          locale={{ emptyText: "Không tìm thấy tài khoản phù hợp" }}
        />
      </Card>
      <EditAccountModal
        isOpen={!!editing}
        onClose={() => setEditing(null)}
        accountData={editing}
        onSuccess={loadAccounts}
      />
      <Modal
        title="Thêm tài khoản mới"
        open={createOpen}
        onCancel={() => setCreateOpen(false)}
        onOk={() => createForm.submit()}
        confirmLoading={creating}
        okText="Tạo tài khoản"
        cancelText="Hủy"
        destroyOnHidden
      >
        <Form
          form={createForm}
          layout="vertical"
          onFinish={createAccount}
          initialValues={{ roleId: 2 }}
          requiredMark="optional"
        >
          <Form.Item
            name="username"
            label="Tên đăng nhập"
            rules={[
              { required: true, message: "Vui lòng nhập tên đăng nhập" },
              { min: 4, max: 20, message: "Tên đăng nhập gồm 4–20 ký tự" },
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder="Ví dụ: nguyenvana" />
          </Form.Item>
          <Form.Item
            name="email"
            label="Địa chỉ email"
            rules={[
              { required: true, message: "Vui lòng nhập email" },
              { type: "email", message: "Email không hợp lệ" },
            ]}
          >
            <Input placeholder="name@epu.edu.vn" />
          </Form.Item>
          <Form.Item
            name="password"
            label="Mật khẩu ban đầu"
            rules={[
              { required: true, message: "Vui lòng nhập mật khẩu" },
              { min: 6, message: "Mật khẩu có ít nhất 6 ký tự" },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Ít nhất 6 ký tự"
            />
          </Form.Item>
          <Form.Item name="roleId" label="Vai trò">
            <Select
              options={[
                { value: 1, label: "Quản trị viên" },
                { value: 2, label: "Giảng viên" },
                { value: 3, label: "Hội đồng" },
              ]}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
