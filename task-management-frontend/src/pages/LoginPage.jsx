import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Container, Box, TextField, Button,
  Typography, Alert, Paper
} from '@mui/material';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.post('/auth/login', { email, password });
      login(response.data.token, {
        email: response.data.email,
        username: response.data.username,
      });
      navigate('/projects');
    } catch (err) {
      setError('Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box mt={10}>
        <Paper elevation={3} sx={{ padding: 4 }}>
          <Typography variant="h5" mb={3} textAlign="center">
            Login to Task Manager
          </Typography>

          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

          <TextField
            label="Email"
            type="email"
            fullWidth
            margin="normal"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            label="Password"
            type="password"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <Button
            variant="contained"
            fullWidth
            sx={{ mt: 2 }}
            onClick={handleLogin}
            disabled={loading}
          >
            {loading ? 'Logging in...' : 'Login'}
          </Button>

          <Typography mt={2} textAlign="center">
            Don't have an account?{' '}
            <Link to="/register">Register</Link>
          </Typography>
        </Paper>
      </Box>
    </Container>
  );
}