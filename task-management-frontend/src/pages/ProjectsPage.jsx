import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container, Box, Typography, Button,
  Card, CardContent, CardActions,
  Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, Alert, CircularProgress
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import api from '../api/axios';
import Navbar from '../components/Navbar';

export default function ProjectsPage() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Dialog (popup) state
  const [dialogOpen, setDialogOpen] = useState(false);
  const [newProjectName, setNewProjectName] = useState('');
  const [newProjectDesc, setNewProjectDesc] = useState('');
  const [createError, setCreateError] = useState('');

  const navigate = useNavigate();

  // Runs once when page loads — fetches all projects
  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      const response = await api.get('/api/projects?page=0&size=20');
      setProjects(response.data.content);
    } catch (err) {
      setError('Failed to load projects');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProject = async () => {
    setCreateError('');
    try {
      await api.post('/api/projects', {
        name: newProjectName,
        description: newProjectDesc
      });
      setDialogOpen(false);
      setNewProjectName('');
      setNewProjectDesc('');
      fetchProjects(); // refresh list
    } catch (err) {
      setCreateError('Failed to create project');
    }
  };

  const handleDeleteProject = async (id) => {
    try {
      await api.delete(`/api/projects/${id}`);
      fetchProjects(); // refresh list
    } catch (err) {
      setError('Failed to delete project');
    }
  };

  return (
    <>
      <Navbar />
      <Container maxWidth="md" sx={{ mt: 4 }}>

        {/* Header row */}
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h4">My Projects</Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setDialogOpen(true)}
          >
            New Project
          </Button>
        </Box>

        {/* Error message */}
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        {/* Loading spinner */}
        {loading && (
          <Box display="flex" justifyContent="center" mt={4}>
            <CircularProgress />
          </Box>
        )}

        {/* Projects list */}
        {!loading && projects.length === 0 && (
          <Typography color="text.secondary" textAlign="center" mt={4}>
            No projects yet. Create your first one!
          </Typography>
        )}

        {projects.map((project) => (
          <Card key={project.id} sx={{ mb: 2 }}>
            <CardContent>
              <Typography variant="h6">{project.name}</Typography>
              <Typography variant="body2" color="text.secondary">
                {project.description}
              </Typography>
            </CardContent>
            <CardActions>
              <Button
                size="small"
                onClick={() => navigate(`/projects/${project.id}`)}
              >
                View Tasks
              </Button>
              <Button
                size="small"
                color="error"
                startIcon={<DeleteIcon />}
                onClick={() => handleDeleteProject(project.id)}
              >
                Delete
              </Button>
            </CardActions>
          </Card>
        ))}

        {/* Create Project Dialog (popup) */}
        <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth>
          <DialogTitle>Create New Project</DialogTitle>
          <DialogContent>
            {createError && <Alert severity="error" sx={{ mb: 2 }}>{createError}</Alert>}
            <TextField
              label="Project Name"
              fullWidth
              margin="normal"
              value={newProjectName}
              onChange={(e) => setNewProjectName(e.target.value)}
            />
            <TextField
              label="Description"
              fullWidth
              margin="normal"
              multiline
              rows={3}
              value={newProjectDesc}
              onChange={(e) => setNewProjectDesc(e.target.value)}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
            <Button variant="contained" onClick={handleCreateProject}>
              Create
            </Button>
          </DialogActions>
        </Dialog>

      </Container>
    </>
  );
}