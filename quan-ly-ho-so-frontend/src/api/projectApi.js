import axiosInstance from "./axiosInstance";

const projectApi = {
  search: (params) => axiosInstance.get("/admin/projects", { params }),
  getById: (id, admin = false) =>
    axiosInstance.get(`${admin ? "/admin" : ""}/projects/${id}`),
  create: (data) => axiosInstance.post("/projects", data),
  updateStatus: (id, data) =>
    axiosInstance.patch(`/admin/projects/${id}/status`, data),
  updateBudget: (id, data) =>
    axiosInstance.patch(`/admin/projects/${id}/budget`, data),
  getReports: (id) => axiosInstance.get(`/projects/${id}/reports`),
  submitReport: (id, data) =>
    axiosInstance.post(`/projects/${id}/reports`, data),
  getSubmission: (id) => axiosInstance.get(`/projects/${id}/submissions`),
  submitFinal: (id, data) =>
    axiosInstance.post(`/projects/${id}/submissions`, data),
};

export default projectApi;
