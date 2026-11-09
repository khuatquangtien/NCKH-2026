import axiosInstance from "./axiosInstance";
export default {
  getAll: (keyword) => axiosInstance.get("/lecturers", { params: keyword ? { keyword } : {} }),
  getById: (id) => axiosInstance.get(`/lecturers/${id}`),
};
