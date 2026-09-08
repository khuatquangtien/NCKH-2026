import axiosInstance from "./axiosInstance";
const authApi = {
  login: (data) => {
    console.log("Gọi API đăng nhập với dữ liệu:", data);
    const url = "/auth/login";
    return axiosInstance.post(url, data);
  },
  register: (data) => axiosInstance.post("/auth/register", data),
};

export default authApi;
