import React, { useEffect, useState } from "react";
import { Table, Tag, Space, Button, Card, message } from "antd";
// LƯU Ý: Nhìn vào tab quản lý của bạn, file cấu hình tên là axiosClient.js đúng không?
// Hãy import đúng đường dẫn đến file axiosClient đó nhé!
import axiosClient from "../api/axiosClient";

export default function AccountManager() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
    
  
  // 1. Cấu hình các cột hiển thị của bảng Ant Design
  const columns = [
    {
      title: "ID",
      dataIndex: "id", // Khớp với trường id trong database
      key: "id",
      width: 80,
    },
    {
      title: "Tên tài khoản",
      dataIndex: "username", // Khớp với trường username
      key: "username",
    },
    {
      title: "Email",
      dataIndex: "email", // Khớp với trường email
      key: "email",
    },
    {
      title: "Quyền hạn",
      key: "role",
      // Nhận vào cả object dòng dữ liệu để đề phòng backend trả về role_id hoặc roleId
      render: (_, record) => {
        const roleId = record.role_id || record.roleId;
        return (
          <Tag color={roleId === 1 ? "geekblue" : "green"}>
            {roleId === 1 ? "Quản trị viên (Admin)" : "Người dùng"}
          </Tag>
        );
      },
    },
    {
      title: "Trạng thái",
      key: "status",
      render: (_, record) => {
        // Đề phòng trường hợp backend trả về is_active hoặc isActive
        const isActive = record.is_active ?? record.isActive;
        return (
          <Tag color={isActive ? "success" : "warning"}>
            {isActive ? "Đang hoạt động" : "Chưa kích hoạt / Khóa"}
          </Tag>
        );
      },
    },
    {
      title: "Hành động",
      key: "action",
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" onClick={update()}>
            Sửa
          </Button>
          <Button type="link" size="small" danger onClick={delete} >
            Xóa
          </Button>
        </Space>
      ),
    },
  ];
  //hàm gọi api xoá
  const delete = async (id) => {
    try {
      await axiosClient.delete(`/admin/users/${id}`);
        // Cập nhật lại danh sách sau khi xóa
        fetchAccounts();
    } catch (error) {
        console.error("Lỗi khi xóa tài khoản:", error);
        message.error("Không thể xóa tài khoản!");
    }
  }
  // 2. Hàm gọi API lấy danh sách tài khoản từ Backend
  const fetchAccounts = async () => {
    setLoading(true);
    try {
      // HÃY THAY ĐỔI PATH NÀY cho đúng với @GetMapping bên Controller của bạn
      // Ví dụ: '/api/accounts' hoặc '/api/users'
      const response = await axiosClient.get("/admin/users");

      setUsers(response);
    } catch (error) {
      console.error("Lỗi khi lấy danh sách tài khoản:", error);
      message.error("Không thể tải danh sách tài khoản từ hệ thống!");
    } finally {
      setLoading(false);
    }
  };

  // 3. Tự động kích hoạt hàm lấy dữ liệu khi màn hình Quản lý tài khoản được mở
  useEffect(() => {
    fetchAccounts();
  }, []);

  return (
    <Card
      title="Hệ thống Quản lý Tài khoản"
      extra={<Button type="primary">Thêm tài khoản mới</Button>}
    >
      <Table
        columns={columns}
        dataSource={users}
        rowKey="id" // Dùng trường id làm khóa định danh độc nhất cho mỗi dòng
        loading={loading}
        pagination={{ pageSize: 5 }} // Phân trang: mỗi trang 5 dòng dữ liệu
      />
    </Card>
  );
}
