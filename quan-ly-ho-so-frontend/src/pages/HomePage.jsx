import { useEffect, useMemo, useState } from "react";
import { Col, Row } from "antd";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import adminApi from "../api/adminApi";
import projectApi from "../api/projectApi";
import paperApi from "../api/paperApi";
import councilApi from "../api/councilApi";
import WelcomeBanner from "../components/HomePage/WelcomeBanner";
import AccountStatCard from "../components/HomePage/AccountStatCard";
import ProjectStatCard from "../components/HomePage/ProjectStatCard";
import PendingPaperStatCard from "../components/HomePage/PendingPaperStatCard";
import CouncilStatCard from "../components/HomePage/CouncilStatCard";
import ProjectStatusCard from "../components/HomePage/ProjectStatusCard";
import RecentProjectsCard from "../components/HomePage/RecentProjectsCard";
import SystemInfoCard from "../components/HomePage/SystemInfoCard";

export default function HomePage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.role?.name === "ADMIN";
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState({ accounts: [], projects: [], papers: [], councils: [] });

  useEffect(() => {
    let active = true;
    const load = async () => {
      setLoading(true);
      const requests = isAdmin
        ? [adminApi.getAllAccounts(), projectApi.search({}), paperApi.getPending(), councilApi.getAll()]
        : [Promise.resolve([]), Promise.resolve([]), user?.id ? paperApi.getByLecturer(user.id) : Promise.resolve([]), councilApi.getAll()];
      const results = await Promise.allSettled(requests);
      if (!active) return;
      const value = (index) => results[index].status === "fulfilled" && Array.isArray(results[index].value) ? results[index].value : [];
      setData({ accounts: value(0), projects: value(1), papers: value(2), councils: value(3) });
      setLoading(false);
    };
    load();
    return () => { active = false; };
  }, [isAdmin, user?.id]);

  const counts = useMemo(() => ({
    activeAccounts: data.accounts.filter((item) => item.isActive ?? item.is_active).length,
    completedProjects: data.projects.filter((item) => item.status === "COMPLETED").length,
  }), [data.accounts, data.projects]);

  const go = (path) => () => navigate(path);

  return <div className="overview-page">
    <WelcomeBanner user={user} isAdmin={isAdmin} />
    <Row gutter={[16, 16]} className="overview-stats">
      {isAdmin && <Col xs={24} sm={12} xl={6}><AccountStatCard total={data.accounts.length} active={counts.activeAccounts} loading={loading} onClick={go("/dashboard/accounts")} /></Col>}
      {isAdmin && <Col xs={24} sm={12} xl={6}><ProjectStatCard total={data.projects.length} completed={counts.completedProjects} loading={loading} onClick={go("/dashboard/projects")} /></Col>}
      <Col xs={24} sm={12} xl={isAdmin ? 6 : 12}><PendingPaperStatCard total={data.papers.length} loading={loading} personal={!isAdmin} onClick={go("/dashboard/papers")} /></Col>
      <Col xs={24} sm={12} xl={isAdmin ? 6 : 12}><CouncilStatCard total={data.councils.length} loading={loading} personal={!isAdmin} onClick={go("/dashboard/councils")} /></Col>
    </Row>
    {isAdmin && <Row gutter={[16, 16]} className="overview-detail-row"><Col xs={24} xl={9}><ProjectStatusCard projects={data.projects} loading={loading} onViewAll={go("/dashboard/projects")} /></Col><Col xs={24} xl={15}><RecentProjectsCard projects={data.projects} loading={loading} onViewAll={go("/dashboard/projects")} /></Col></Row>}
    <SystemInfoCard isAdmin={isAdmin} />
  </div>;
}
