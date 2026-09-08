import axiosClient from "./axiosClient";
const authApi = {
  login: (data) => {
    console.log("Gọi API đăng nhập với dữ liệu:", data);
    const url = "/auth/login";
    return axiosClient.post(url, data);
  },
};
export default authApi;