-- 1. USERS (insert first, everything else depends on this)
INSERT INTO users (username, email, password, role) VALUES
('alice_admin', 'alice@example.com', 'hashedpassword000', 'ROLE_ADMIN'),
('bob_manager', 'bob@example.com', 'hashedpassword789', 'ROLE_MANAGER'),
('john_doe', 'john@example.com', 'hashedpassword123', 'ROLE_USER'),
('jane_smith', 'jane@example.com', 'hashedpassword456', 'ROLE_USER'),
('charlie_doe', 'charlie@example.com', 'hashedpassword111', 'ROLE_USER');

-- 2. PROJECTS (insert second, needs owner_id from users)
INSERT INTO projects (name, description, owner_id) VALUES
('Website Redesign', 'Redesign the company website', 1),        -- owned by alice_admin
('Mobile App', 'Build a mobile app for customers', 2),          -- owned by bob_manager
('API Integration', 'Integrate third party payment API', 2),    -- owned by bob_manager
('Internal Dashboard', 'Build internal analytics dashboard', 1); -- owned by alice_admin

-- 3. TASKS (insert third, needs project_id and assignee_id)
INSERT INTO tasks (title, description, status, project_id, assignee_id) VALUES
('Design homepage mockup', 'Create wireframes for homepage', 'TODO', 1, 3),
('Set up React project', 'Initialize React with Tailwind', 'IN_PROGRESS', 1, 4),
('Build login screen', 'Mobile login with JWT', 'TODO', 2, 3),
('Push notifications', 'Integrate Firebase push notifications', 'TODO', 2, 5),
('Stripe integration', 'Connect Stripe payment gateway', 'IN_PROGRESS', 3, 4),
('Dashboard charts', 'Add charts using Chart.js', 'TODO', 4, 5),
('Deploy to AWS', 'Deploy API to AWS EC2', 'DONE', 3, 2);

-- 4. COMMENTS (insert last, needs task_id and author_id)
INSERT INTO comments (content, task_id, author_id) VALUES
('Wireframes are ready for review', 1, 3),
('Looks good, approved!', 1, 1),
('React project initialized successfully', 2, 4),
('Can you add dark mode support?', 2, 2),
('Login screen design is done', 3, 3),
('Stripe keys have been configured', 5, 4),
('Deployment was successful', 7, 2);
