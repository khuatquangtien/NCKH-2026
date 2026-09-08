import { useEffect, useState } from "react";
import { Button, Card, Form, Input, Skeleton, message } from "antd";
import profileApi from "../api/profileApi";
import { useAuth } from "../context/AuthContext";

export default function ProfilePage() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const { setUser } = useAuth();
  useEffect(() => {
    profileApi
      .getMe()
      .then((data) => form.setFieldsValue(data))
      .catch(() => message.error("Không thể tải hồ sơ cá nhân."))
      .finally(() => setLoading(false));
  }, [form]);
  const save = async (values) => {
    setSaving(true);
    try {
      const data = await profileApi.updateMe(values);
      form.setFieldsValue(data);
      setUser((old) => ({ ...old, ...data }));
      message.success("Đã cập nhật hồ sơ.");
    } catch {
      message.error("Cập nhật hồ sơ không thành công.");
    } finally {
      setSaving(false);
    }
  };
  return (
    <Card title="Hồ sơ cá nhân" className="page-card">
      {loading ? (
        <Skeleton active />
      ) : (
        <Form form={form} layout="vertical" onFinish={save}>
          <div className="form-grid">
            <Form.Item
              name="fullName"
              label="Họ và tên"
              rules={[{ required: true }]}
            >
              <Input />
            </Form.Item>
            <Form.Item name="faculty" label="Khoa" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item name="academicRank" label="Học hàm">
              <Input placeholder="Ví dụ: Phó giáo sư" />
            </Form.Item>
            <Form.Item name="academicDegree" label="Học vị">
              <Input placeholder="Ví dụ: Tiến sĩ" />
            </Form.Item>
          </div>
          <Form.Item name="researchDirection" label="Hướng nghiên cứu">
            <Input.TextArea rows={4} />
          </Form.Item>
          <Button type="primary" htmlType="submit" loading={saving}>
            Lưu thay đổi
          </Button>
        </Form>
      )}
    </Card>
  );
}
