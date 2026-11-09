import { message } from "antd";

/**
 * Lấy thông báo lỗi dễ đọc từ lỗi axios.
 * Ưu tiên message backend trả về (ErrorResponse), sau đó tới lỗi validation,
 * cuối cùng mới dùng câu mặc định `fallback`.
 */
export function getApiErrorMessage(error, fallback = "Đã có lỗi xảy ra.") {
  const data = error?.response?.data;
  if (!data) return fallback;

  // Lỗi @Valid: message chung là "Validation failed", chi tiết nằm trong validationErrors
  const validationErrors = data.validationErrors;
  if (validationErrors && Object.keys(validationErrors).length) {
    return Object.values(validationErrors).join(" ");
  }

  if (typeof data === "string" && data.trim()) return data;
  if (typeof data.message === "string" && data.message.trim()) return data.message;
  return fallback;
}

/**
 * Hiển thị lỗi API bằng antd message.
 * - PROFILE_INCOMPLETE: cảnh báo (màu vàng), chỉ hiện 1 lần dù nhiều request cùng lỗi.
 * - Các lỗi khác: message.error với nội dung từ backend hoặc `fallback`.
 */
export function showApiError(error, fallback) {
  const data = error?.response?.data;
  const content = getApiErrorMessage(error, fallback);

  if (data?.code === "PROFILE_INCOMPLETE") {
    message.warning({ key: "profile-incomplete", content });
    return;
  }
  message.error(content);
}
