import axiosInstance from "./axiosInstance";

const attachmentApi = {
  upload: (file, fileType, projectId, paperId) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("fileType", fileType);
    if (projectId) formData.append("projectId", projectId);
    if (paperId) formData.append("paperId", paperId);
    return axiosInstance.post("/attachments/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
  getByProject: (projectId) =>
    axiosInstance.get(`/attachments/projects/${projectId}`),
  getByPaper: (paperId) =>
    axiosInstance.get(`/attachments/papers/${paperId}`),
  download: (id) =>
    axiosInstance.get(`/attachments/${id}/download`, {
      responseType: "blob",
    }),
  remove: (id) => axiosInstance.delete(`/attachments/${id}`),
};

export default attachmentApi;
