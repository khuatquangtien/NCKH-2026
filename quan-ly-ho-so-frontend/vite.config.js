import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Gọi API cùng origin với trang để trình duyệt không cần kiểm tra CORS
    proxy: {
      '/api': 'http://localhost:8082',
    },
  },
})
