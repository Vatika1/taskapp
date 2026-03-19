import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Container, Box, TextField, Button,
  Typography, Alert, Paper
} from '@mui/material';
import api from '../api/axios';

export default function RegisterPage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleRegister = async () => {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await api.post('/auth/register', { username, email, password });
      setSuccess('Account created! Redirecting to login...');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box mt={10}>
        <Paper elevation={3} sx={{ padding: 4 }}>
          <Typography variant="h5" mb={3} textAlign="center">
            Create an Account
          </Typography>

          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

          <TextField
            label="Username"
            fullWidth
            margin="normal"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
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
            onClick={handleRegister}
            disabled={loading}
          >
            {loading ? 'Creating account...' : 'Register'}
          </Button>

          <Typography mt={2} textAlign="center">
            Already have an account?{' '}
            <Link to="/login">Login</Link>
          </Typography>
        </Paper>
      </Box>
    </Container>
  );
}