import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  Chip,
  IconButton
} from "@mui/material";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import { useTranslation } from "react-i18next";
import { useContext, useState } from "react";
import { ThemeContext } from "../../../ThemeContext";
import "./ProjectDetailsDialog.css";

const ProjectDetailsDialog = ({ open, project, handleClose, onEdit, onDelete, onRemoveMember }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);
  const [openConfirm, setOpenConfirm] = useState(false);
  if (!project) return null;

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}>{project.icon} {project.title}</DialogTitle>
      <DialogContent className="project-details-dialog"
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}>
        <Typography variant="subtitle1" gutterBottom>{t("description")}:</Typography>
        <Typography variant="body2" paragraph>{project.description}</Typography>

        <Box mt={2}>
          <Typography variant="subtitle1">{t("status")}: {project.status}</Typography>
          <Typography variant="subtitle1">{t("priority")}: {project.priority}</Typography>
          <Typography variant="subtitle1">{t("deadline")}: {project.deadline || t("no_deadline")}</Typography>
        </Box>

        <Box mt={2}>
          <Typography variant="subtitle1">{t("members")}:</Typography>
          <Box className="members-box">
            {project.members && project.members.length > 0 ? (
              project.members.map((member) => (
                <Chip
                  key={member.userId}
                  label={`${member.firstName} ${member.lastName}`}
                  deleteIcon={<HighlightOffIcon />}
                  // onDelete={() => removeMember(project.id, member.username)}
                  className="member-chip"
                />
              ))
            ) : (
              <Typography variant="caption">{t("no_members")}</Typography>
            )}
          </Box>
        </Box>
      </DialogContent>
      <DialogActions
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}>
        <Button onClick={handleClose}>{t("close")}</Button>
        <Button onClick={() => onEdit(project.id)} color="primary">{t("edit")}</Button>
        <>
          <Button onClick={() => setOpenConfirm(true)} color="error">
            {t("delete")}
          </Button>

          <Dialog open={openConfirm} onClose={() => setOpenConfirm(false)}>
            <DialogTitle>{t("confirm_delete_title")}</DialogTitle>
            <DialogContent>{t("confirm_delete_project")}</DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenConfirm(false)}>{t("cancel")}</Button>
              <Button onClick={() => onDelete(project.id)} color="error">{t("delete")}</Button>
            </DialogActions>
          </Dialog>
        </>
      </DialogActions>
    </Dialog>
  );
};

export default ProjectDetailsDialog;
