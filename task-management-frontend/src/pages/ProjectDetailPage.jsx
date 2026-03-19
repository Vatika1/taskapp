import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Container, Box, Typography, Button,
  Card, CardContent, CardActions,
  Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, Alert,
  CircularProgress, Chip, Select,
  MenuItem, FormControl, InputLabel
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import ChatBubbleIcon from '@mui/icons-material/ChatBubble';
import api from '../api/axios';
import Navbar from '../components/Navbar';

export default function ProjectDetailPage() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  const [project, setProject] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Dialog state
  const [dialogOpen, setDialogOpen] = useState(false);
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskDesc, setNewTaskDesc] = useState('');
  const [createError, setCreateError] = useState('');

  useEffect(() => {
    fetchProject();
    fetchTasks();
  }, [projectId]);

  const fetchProject = async () => {
    try {
      const response = await api.get(`/api/projects/${projectId}`);
      setProject(response.data);
    } catch (err) {
      setError('Failed to load project');
    }
  };

  const fetchTasks = async () => {
    try {
      const response = await api.get(`/api/projects/${projectId}/tasks?page=0&size=20`);
      setTasks(response.data.content);
    } catch (err) {
      setError('Failed to load tasks');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTask = async () => {
    setCreateError('');
    try {
      await api.post('/api/tasks', {
        title: newTaskTitle,
        description: newTaskDesc,
        projectId: parseInt(projectId)
      });
      setDialogOpen(false);
      setNewTaskTitle('');
      setNewTaskDesc('');
      fetchTasks();
    } catch (err) {
      setCreateError('Failed to create task');
    }
  };

  const handleStatusChange = async (taskId, newStatus) => {
    try {
      const task = tasks.find(t => t.id === taskId);
      await api.put(`/api/tasks/${taskId}`, {
        title: task.title,
        description: task.description,
        status: newStatus,
        assigneeId: task.assigneeId
      });
      fetchTasks();
    } catch (err) {
      setError('Failed to update task status');
    }
  };

  const handleDeleteTask = async (taskId) => {
    try {
      await api.delete(`/api/tasks/${taskId}`);
      fetchTasks();
    } catch (err) {
      setError('Failed to delete task');
    }
  };

  const getStatusColor = (status) => {
    if (status === 'TODO') return 'default';
    if (status === 'IN_PROGRESS') return 'primary';
    if (status === 'DONE') return 'success';
  };

  return (
    <>
      <Navbar />
      <Container maxWidth="md" sx={{ mt: 4 }}>

        {/* Back button + Project name */}
        <Box display="flex" alignItems="center" gap={2} mb={3}>
          <Button onClick={() => navigate('/projects')}>← Back</Button>
          <Typography variant="h4">
            {project ? project.name : 'Loading...'}
          </Typography>
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        {/* New Task button */}
        <Box display="flex" justifyContent="flex-end" mb={3}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setDialogOpen(true)}
          >
            New Task
          </Button>
        </Box>

        {/* Loading spinner */}
        {loading && (
          <Box display="flex" justifyContent="center" mt={4}>
            <CircularProgress />
          </Box>
        )}

        {/* Empty state */}
        {!loading && tasks.length === 0 && (
          <Typography color="text.secondary" textAlign="center" mt={4}>
            No tasks yet. Create your first one!
          </Typography>
        )}

        {/* Tasks list */}
        {tasks.map((task) => (
          <Card key={task.id} sx={{ mb: 2 }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Typography variant="h6">{task.title}</Typography>
                <Chip
                  label={task.status}
                  color={getStatusColor(task.status)}
                  size="small"
                />
              </Box>
              <Typography variant="body2" color="text.secondary" mt={1}>
                {task.description}
              </Typography>

              {/* Status dropdown */}
              <FormControl size="small" sx={{ mt: 2, minWidth: 150 }}>
                <InputLabel>Status</InputLabel>
                <Select
                  value={task.status}
                  label="Status"
                  onChange={(e) => handleStatusChange(task.id, e.target.value)}
                >
                  <MenuItem value="TODO">TODO</MenuItem>
                  <MenuItem value="IN_PROGRESS">IN_PROGRESS</MenuItem>
                  <MenuItem value="DONE">DONE</MenuItem>
                </Select>
              </FormControl>
            </CardContent>

            <CardActions>
              <Button
                size="small"
                startIcon={<ChatBubbleIcon />}
                onClick={() => navigate(`/tasks/${task.id}/comments`)}
              >
                Comments
              </Button>
              <Button
                size="small"
                color="error"
                startIcon={<DeleteIcon />}
                onClick={() => handleDeleteTask(task.id)}
              >
                Delete
              </Button>
            </CardActions>
          </Card>
        ))}

        {/* Create Task Dialog */}
        <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth>
          <DialogTitle>Create New Task</DialogTitle>
          <DialogContent>
            {createError && <Alert severity="error" sx={{ mb: 2 }}>{createError}</Alert>}
            <TextField
              label="Task Title"
              fullWidth
              margin="normal"
              value={newTaskTitle}
              onChange={(e) => setNewTaskTitle(e.target.value)}
            />
            <TextField
              label="Description"
              fullWidth
              margin="normal"
              multiline
              rows={3}
              value={newTaskDesc}
              onChange={(e) => setNewTaskDesc(e.target.value)}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
            <Button variant="contained" onClick={handleCreateTask}>
              Create
            </Button>
          </DialogActions>
        </Dialog>

      </Container>
    </>
  );
}