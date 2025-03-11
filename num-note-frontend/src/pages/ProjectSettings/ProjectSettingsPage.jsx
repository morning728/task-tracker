import React, { useContext, useEffect, useState } from "react";
import { Tabs, Tab, Box, Typography } from "@mui/material";
import { ThemeContext } from "../../ThemeContext";
import { useTranslation } from "react-i18next";
import "./ProjectSettingsPage.css";
import Header from "../../components/Header/Header";
import { useParams } from "react-router-dom";
import { getFullProjectInfoById } from "../../services/api";
import GeneralSettingsTab from "./tabs/GeneralSettingsTab";
import MembersTab from "./tabs/MembersTab";
import RolesTab from "./tabs/RolesTab";
import TagsTab from "./tabs/TagsTab";


const ProjectSettingsPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();
  const { projectId } = useParams();
  const [tabIndex, setTabIndex] = useState(0);
  const [project, setProject] = useState(null);

  useEffect(() => {
    getFullProjectInfoById(projectId).then((res) => setProject(res.data));
  }, [projectId]);

  const handleTabChange = (_, newValue) => setTabIndex(newValue);

  if (!project) return <Typography>{t("loading")}</Typography>;

  return (
    <Box className={`settings-page ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box className="settings-container">
        <Tabs
          value={tabIndex}
          onChange={handleTabChange}
          variant="scrollable"
          className="settings-tabs"
        >
          <Tab label={t("general")} />
          <Tab label={t("members")} />
          <Tab label={t("roles")} />
          <Tab label={t("tags")} />
          <Tab label={t("statuses")} />
          <Tab label={t("resources")} />
        </Tabs>

        <Box className="tab-content">
          {tabIndex === 0 && <GeneralSettingsTab project={project} />}
          {tabIndex === 1 && <MembersTab projectId={projectId} />}
          {tabIndex === 2 && <RolesTab projectId={projectId} />}
          {tabIndex === 3 && <TagsTab projectId={projectId} />}
          {/* Остальные вкладки будут добавлены позже */}
        </Box>
      </Box>
    </Box>
  );
};

export default ProjectSettingsPage;
