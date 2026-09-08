import axiosInstance from "./axiosInstance";

export default {
  getAll: () => axiosInstance.get("/councils"),
  getMembers: (id) => axiosInstance.get(`/councils/${id}/members`),
  create: (data) => axiosInstance.post("/councils", data),
  assignProject: (id, projectId) =>
    axiosInstance.post(`/councils/${id}/assignments`, { projectId }),
  evaluate: (data) => axiosInstance.post("/councils/evaluations", data),
};
