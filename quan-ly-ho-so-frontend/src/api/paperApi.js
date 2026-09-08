import axiosInstance from "./axiosInstance";

export default {
  getByLecturer: (lecturerId) =>
    axiosInstance.get(`/papers/lecturer/${lecturerId}`),
  getPending: () => axiosInstance.get("/papers/pending"),
  create: (data) => axiosInstance.post("/papers", data),
  update: (id, data) => axiosInstance.put(`/papers/${id}`, data),
  remove: (id) => axiosInstance.delete(`/papers/${id}`),
  verify: (id, status) =>
    axiosInstance.patch(`/papers/${id}/verify`, null, { params: { status } }),
};
