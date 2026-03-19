import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container, Box, Typography, Button,
  Card, CardContent, CardActions,
  TextField, Alert, CircularProgress
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import api from '../api/axios';
import Navbar from '../components/Navbar';

export default function CommentsPage() {
  const { taskId } = useParams();
  const navigate = useNavigate();

  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newComment, setNewComment] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchComments();
  }, [taskId]);

  const fetchComments = async () => {
    try {
      const response = await api.get(`/api/tasks/${taskId}/comments?page=0&size=20`);
      setComments(response.data.content);
    } catch (err) {
      setError('Failed to load comments');
    } finally {
      setLoading(false);
    }
  };

  const handleAddComment = async () => {
    if (!newComment.trim()) return;
    setSubmitting(true);
    try {
      await api.post(`/api/tasks/${taskId}/comments`, {
        content: newComment,
        taskId: parseInt(taskId)
      });
      setNewComment('');
      fetchComments();
    } catch (err) {
      setError('Failed to add comment');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteComment = async (commentId) => {
    try {
      await api.delete(`/api/tasks/comments/${commentId}`);
      fetchComments();
    } catch (err) {
      setError('Failed to delete comment');
    }
  };

  return (
    <>
      <Navbar />
      <Container maxWidth="md" sx={{ mt: 4 }}>

        {/* Header */}
        <Box display="flex" alignItems="center" gap={2} mb={3}>
          <Button onClick={() => navigate(-1)}>← Back</Button>
          <Typography variant="h5">Comments</Typography>
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        {/* Add comment box */}
        <Box sx={{ mb: 4 }}>
          <TextField
            label="Write a comment..."
            fullWidth
            multiline
            rows={3}
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
          />
          <Button
            variant="contained"
            sx={{ mt: 1 }}
            onClick={handleAddComment}
            disabled={submitting || !newComment.trim()}
          >
            {submitting ? 'Posting...' : 'Post Comment'}
          </Button>
        </Box>

        {/* Loading spinner */}
        {loading && (
          <Box display="flex" justifyContent="center" mt={4}>
            <CircularProgress />
          </Box>
        )}

        {/* Empty state */}
        {!loading && comments.length === 0 && (
          <Typography color="text.secondary" textAlign="center" mt={4}>
            No comments yet. Be the first to comment!
          </Typography>
        )}

        {/* Comments list */}
        {comments.map((comment) => (
          <Card key={comment.id} sx={{ mb: 2 }}>
            <CardContent>
              <Typography variant="body2" color="text.secondary" mb={1}>
                {comment.authorEmail} • {new Date(comment.createdAt).toLocaleString()}
              </Typography>
              <Typography variant="body1">
                {comment.content}
              </Typography>
            </CardContent>
            <CardActions>
              <Button
                size="small"
                color="error"
                startIcon={<DeleteIcon />}
                onClick={() => handleDeleteComment(comment.id)}
              >
                Delete
              </Button>
            </CardActions>
          </Card>
        ))}

      </Container>
    </>
  );
}