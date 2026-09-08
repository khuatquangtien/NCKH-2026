import axiosInstance from "./axiosInstance";

export default {
  getMe: () => axiosInstance.get("/profile/me"),
  updateMe: (data) => axiosInstance.put("/profile/me", data),
};
