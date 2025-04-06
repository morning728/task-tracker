import React, { useContext, useState, useEffect } from "react";
import {
  Box,
  Grid,
  Typography,
  Button,
  Divider,
  TextField,
  MenuItem,
  IconButton,
  Autocomplete
} from "@mui/material";
import { useTranslation } from "react-i18next";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import ClearIcon from "@mui/icons-material/Whatshot"; // 🔥 Иконка для очистки
import Header from "../../components/Header/Header";
import TaskCard from "../../components/Tasks/TaskCard/TaskCard";
import AddTaskDialog from "../../components/Tasks/AddTaskDialog/AddTaskDialog";
import TaskDetailsDialog from "../../components/Tasks/TaskDetailsDialog/TaskDetailsDialog";
import { ThemeContext } from "../../ThemeContext";
import { ProjectAccessProvider } from "../../context/ProjectAccessContext";
import { useFilteredTasksWithDates } from "../../hooks/useFilteredTasks";
import { useTasks } from "../../hooks/useTasks";
import "./MainPage.css";
import { useParams } from "react-router-dom"; // ← нужный импорт
import KanbanBoard from "../../components/KanbanBoard/KanbanBoard";
import "./MainPage.css";
import { restoreTask } from "../../services/api";


const MainPage = () => {
  const { projectId } = useParams(); // <== получаем projectId из URL
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);
  const [DDView, setDDView] = useState(false);

  const [tags, setTags] = useState([]);
  const [statuses, setStatuses] = useState([]);

  const [filterTitle, setFilterTitle] = useState("");
  const [filterTag, setFilterTag] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [showArchived, setShowArchived] = useState(false);
  const handleToggleArchived = () => setShowArchived(prev => !prev);


  useEffect(() => {
    if (projectId) {
      getTags().then(setTags);
      getStatuses().then(setStatuses);

    }
  }, [projectId]);

  const {
    tasks,
    isEditing,
    selectedTask,
    openAddDialog,
    openDetailsDialog,
    addTask,
    editTask,
    archiveTask,
    restoreArchivedTask,
    handleOpenAddDialog,
    handleOpenDetailsDialog,
    handleCloseAddDialog,
    handleCloseDetailsDialog,
    getTags,
    getStatuses,
    handleStatusChange,
  } = useTasks(projectId); // <== передаем projectId

  const filteredTasks = useFilteredTasksWithDates(
    tasks,
    filterTag,
    filterStatus,
    filterTitle,
    startDate,
    endDate,
    showArchived,
    statuses
  );

  const toggleView = () => {
    setDDView((prev) => !prev);
  };

  const tagNames = tags.map(tag => tag.name);
  const statusNames = statuses.map(status => status.name);

  return (
    <ProjectAccessProvider projectId={projectId}>
      <Box className={`main-content ${darkMode ? "night" : "day"}`}>
        <Header />
        <Box mt={2} sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <Typography className={`main-title ${darkMode ? "night" : "day"}`} variant="h4" gutterBottom>{t("main_page_title")}</Typography>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <TextField
              sx={{ width: "30em" }}
              label={t("filter_title")}
              value={filterTitle}
              onChange={(e) => setFilterTitle(e.target.value)}
              fullWidth
              margin="normal"
            />
          </Box>
          {!showArchived && (
            <Button
              variant="contained"
              color="secondary"
              onClick={toggleView} // твоя функция переключения DDView
            >
              {!DDView ? t("kanban_view") : t("list_view")}
            </Button>
          )}
          {!DDView && (
            <Button variant="contained" sx={{ backgroundcolor: "#9932CC" }} onClick={handleToggleArchived}>
              {showArchived ? t("hide_archive") : t("show_archive")}
            </Button>
          )}
          {DDView &&
            <Button variant="contained" sx={{ backgroundcolor: "#9932CC" }} onClick={handleOpenAddDialog}>
              {t("main_page_add_task")}
            </Button>
          }
        </Box>

        {/* Фильтры */}
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>

          {/* Фильтры (слева) */}
          <Box display="flex" gap={2} sx={{ width: "50%" }}>
            {/* Filter Tag */}
            <Autocomplete
              sx={{ width: "50%" }}
              options={tagNames} // Передаем список строк
              value={filterTag} // Сохраняем строку
              onChange={(event, newValue) => setFilterTag(newValue)} // Сохраняем выбранную строку
              renderInput={(params) => <TextField {...params} label={t("filter_tag")} fullWidth margin="normal" />}
              freeSolo // Разрешаем вводить произвольные значения
            />

            {/* Filter Status */}
            <Autocomplete
              sx={{ width: "50%" }}
              options={statusNames} // Передаем список строк
              value={filterStatus} // Сохраняем строку
              onChange={(event, newValue) => setFilterStatus(newValue)} // Сохраняем выбранную строку
              renderInput={(params) => <TextField {...params} label={t("filter_status")} fullWidth margin="normal" />}
              freeSolo // Разрешаем вводить произвольные значения
            />
          </Box>

          {/* Даты (справа) */}
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <Box display="flex" gap={2} sx={{ width: "40%", alignItems: "center" }}>

              {/* Start Date + кнопка очистки */}
              <Box display="flex" alignItems="center">
                <DatePicker
                  label={t("start_date")}
                  value={startDate}
                  onChange={setStartDate}
                  renderInput={(params) => <TextField {...params} fullWidth margin="normal" />}
                />
                {startDate && (
                  <IconButton onClick={() => setStartDate(null)} sx={{ color: "#A020F0" }}>
                    <ClearIcon />
                  </IconButton>
                )}
              </Box>

              {/* End Date + кнопка очистки */}
              <Box display="flex" alignItems="center">
                <DatePicker
                  label={t("end_date")}
                  value={endDate}
                  onChange={setEndDate}
                  renderInput={(params) => <TextField {...params} fullWidth margin="normal" />}
                />
                {endDate && (
                  <IconButton onClick={() => setEndDate(null)} sx={{ color: "#A020F0" }}>
                    <ClearIcon />
                  </IconButton>
                )}
              </Box>

            </Box>
          </LocalizationProvider>

        </Box>

        <Divider sx={{ mb: 2 }} />

        {!DDView && (
          <>
            <Grid container spacing={2}>
              {filteredTasks.map((task) => (
                <Grid item xs={12} sm={6} md={4} key={task.id}>
                  <TaskCard task={task} tags={tags} statuses={statuses} onClick={() => handleOpenDetailsDialog(task)} />
                </Grid>
              ))}
            </Grid>
            {!showArchived &&
              <Box mt={4} textAlign="center">
                <Button variant="contained" color="primary" size="large" onClick={handleOpenAddDialog}>
                  {t("main_page_add_task")}
                </Button>
              </Box>
            }
          </>
        )}

        <Box style={{ display: DDView ? "block" : "none" }}>
          <KanbanBoard
            tasks={filteredTasks}
            statuses={statuses}
            onCardClick={handleOpenDetailsDialog}
            onStatusChange={handleStatusChange}
          />
        </Box>


        {/* Диалоги */}
        <AddTaskDialog
          open={openAddDialog.isOpen}
          handleClose={handleCloseAddDialog}
          handleAddTask={addTask}
          task={selectedTask}
          isEditing={isEditing}
          tags={tags}
          statuses={statuses}
        />

        {selectedTask && (
          <TaskDetailsDialog
            open={openDetailsDialog.isOpen}
            task={selectedTask}
            handleClose={handleCloseDetailsDialog}
            onEdit={editTask}
            onArchive={archiveTask}
            onRestore={restoreArchivedTask}
            statuses={statuses}
          />
        )}
      </Box>
    </ProjectAccessProvider>
  );
};

export default MainPage;
