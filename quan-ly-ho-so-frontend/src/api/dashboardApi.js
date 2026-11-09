import axiosInstance from "./axiosInstance";
export default { get: () => axiosInstance.get("/dashboard") };
