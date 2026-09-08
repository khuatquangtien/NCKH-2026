import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import Dashboard from "./pages/Dashboard";
import HomePage from "./pages/HomePage";
import AccountManager from "./pages/AccountManager";
import ProjectManager from "./pages/ProjectManager";
import ProfilePage from "./pages/ProfilePage";
import PaperManager from "./pages/PaperManager";
import CouncilManager from "./pages/CouncilManager";
import ProtectedRoute from "./components/ProtectedRoute";
import { useAuth } from "./context/AuthContext";

export default function App() {
  const { user, loading } = useAuth();
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/dashboard" element={<Dashboard />}>
        <Route index element={<HomePage />} />
        <Route path="projects" element={<ProjectManager />} />
        <Route path="papers" element={<PaperManager />} />
        <Route path="councils" element={<CouncilManager />} />
        <Route path="profile" element={<ProfilePage />} />
        <Route element={<ProtectedRoute user={user} loading={loading} />}>
          <Route path="accounts" element={<AccountManager />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
