// src/components/admin/AssignRoleForm.jsx
import { useState } from "react";
import adminApi from "../../api/adminApi";
export default function AssignRoleForm() {
  const [username, setUsername] = useState("");
  const [roleId, setRoleId] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      // Chuyển roleId sang kiểu số (Number) để khớp với Backend nếu cần
      const payload = {
        username: username,
        roleId: Number(roleId),
      };

      const response = await adminApi.assignRole(payload);

      // Backend của bạn trả về ResponseEntity.ok(message) là dạng string
      setMessage(`✅ Thành công: ${response.data || "Đã cập nhật quyền"}`);

      // Reset form
      setUsername("");
      setRoleId("");
    } catch (error) {
      // Xử lý lỗi trả về từ Exception của Backend
      const errorMsg =
        error.response?.data || "❌ Lỗi hệ thống, không thể phân quyền!";
      setMessage(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="assign-role-container"
      style={{ padding: "20px", maxWidth: "400px" }}
    >
      <h3>Phân Quyền Người Dùng (Admin)</h3>

      <form
        onSubmit={handleSubmit}
        style={{ display: "flex", flexDirection: "column", gap: "15px" }}
      >
        <div>
          <label>Tên tài khoản (Username):</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            style={{ width: "100%", padding: "8px" }}
          />
        </div>

        <div>
          <label>Chọn Quyền:</label>
          <select
            value={roleId}
            onChange={(e) => setRoleId(e.target.value)}
            required
            style={{ width: "100%", padding: "8px" }}
          >
            <option value="" disabled>
              -- Chọn quyền --
            </option>
            {/* Giả sử ID 1 là Admin, ID 2 là User. Bạn tự điều chỉnh theo DB của bạn nhé */}
            <option value="1">Admin (Quản trị viên)</option>
            <option value="2">User (Người dùng thường)</option>
          </select>
        </div>

        <button
          type="submit"
          disabled={loading}
          style={{ padding: "10px", cursor: "pointer" }}
        >
          {loading ? "Đang xử lý..." : "Cập Nhật Quyền"}
        </button>
      </form>

      {/* Hiển thị thông báo thành công hoặc lỗi */}
      {message && (
        <p style={{ marginTop: "15px", fontWeight: "bold" }}>{message}</p>
      )}
    </div>
  );
}
