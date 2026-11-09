import axiosInstance from "./axiosInstance";

export default {
  getAll: () => axiosInstance.get("/councils"),
  getMine: () => axiosInstance.get("/councils/me"),
  getAssignments: (id) => axiosInstance.get(`/councils/${id}/assignments`),
  getEvaluations: (id) => axiosInstance.get(`/councils/${id}/evaluations`),
  getMembers: (id) => axiosInstance.get(`/councils/${id}/members`),
  create: (data) => axiosInstance.post("/councils", data),
  assignProject: (id, projectId) =>
    axiosInstance.post(`/councils/${id}/assignments`, { projectId }),
  evaluate: (data) => axiosInstance.post("/councils/evaluations", data),
  update: (id, data) => axiosInstance.put(`/councils/${id}`, data),
  updateStatus: (id, status) => axiosInstance.patch(`/councils/${id}/status`, { status }),
  remove: (id) => axiosInstance.delete(`/councils/${id}`),
  removeAssignment: (id, projectId) => axiosInstance.delete(`/councils/${id}/assignments/${projectId}`),
};
