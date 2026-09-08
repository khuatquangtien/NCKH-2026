import { useEffect, useState } from "react";
import {
  Alert,
  Avatar,
  Form,
  Input,
  Modal,
  Select,
  Switch,
  Typography,
  message,
} from "antd";
import { MailOutlined, UserOutlined } from "@ant-design/icons";
import adminApi from "../../api/adminApi";

const roleIds = { ADMIN: 1, LECTURER: 2, COUNCIL: 3, COUNCIL_MEMBER: 3 };

export default function EditAccountModal({
  isOpen,
  onClose,
  accountData,
  onSuccess,
}) {
  const [form] = Form.useForm();
  const [saving, setSaving] = useState(false);
  useEffect(() => {
    if (!isOpen || !accountData) return;
    const roleName =
      accountData.rolename || accountData.roleName || accountData.role?.name;
    form.setFieldsValue({
      email: accountData.email,
      roleId: accountData.role?.id || accountData.roleId || roleIds[roleName],
      isActive: accountData.isActive ?? accountData.is_active ?? false,
    });
  }, [isOpen, accountData, form]);
  const submit = async (values) => {
    setSaving(true);
    try {
      await adminApi.updateAccount(accountData.id, values);
      message.success("Đã cập nhật thông tin tài khoản.");
      onClose();
      onSuccess();
    } catch (error) {
      message.error(
        typeof error.response?.data === "string"
          ? error.response.data
          : "Không thể cập nhật tài khoản.",
      );
    } finally {
      setSaving(false);
    }
  };
  return (
    <Modal
      title="Chỉnh sửa tài khoản"
      open={isOpen}
      onCancel={onClose}
      onOk={() => form.submit()}
      confirmLoading={saving}
      okText="Lưu thay đổi"
      cancelText="Hủy"
      destroyOnHidden
    >
      {accountData && (
        <div className="edit-account-summary">
          <Avatar size={48} icon={<UserOutlined />} />
          <div>
            <Typography.Text strong>{accountData.username}</Typography.Text>
            <Typography.Text type="secondary">
              Mã tài khoản: {accountData.id}
            </Typography.Text>
          </div>
        </div>
      )}
      <Alert
        type="info"
        showIcon
        message="Tên đăng nhập không thể thay đổi sau khi tạo tài khoản."
        className="account-edit-alert"
      />
      <Form
        form={form}
        layout="vertical"
        onFinish={submit}
        requiredMark="optional"
      >
        <Form.Item
          name="email"
          label="Địa chỉ email"
          rules={[
            { required: true, message: "Vui lòng nhập email" },
            { type: "email", message: "Email không hợp lệ" },
          ]}
        >
          <Input prefix={<MailOutlined />} placeholder="name@epu.edu.vn" />
        </Form.Item>
        <Form.Item
          name="roleId"
          label="Vai trò hệ thống"
          rules={[{ required: true, message: "Vui lòng chọn vai trò" }]}
        >
          <Select
            options={[
              { value: 1, label: "Quản trị viên" },
              { value: 2, label: "Giảng viên" },
              { value: 3, label: "Hội đồng" },
            ]}
          />
        </Form.Item>
        <Form.Item
          name="isActive"
          label="Quyền truy cập"
          valuePropName="checked"
        >
          <Switch
            checkedChildren="Đang hoạt động"
            unCheckedChildren="Đã khóa"
          />
        </Form.Item>
      </Form>
    </Modal>
  );
}
