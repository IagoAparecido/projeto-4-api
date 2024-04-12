INSERT INTO users (name, id, role, image_url, status, is_authenticated, created_at, email, password) 
SELECT 'Admin', '77f5572b-462f-4f53-8562-f7284a1f5a12', 'ADMIN' , null, 'AUTHORIZED', true, '2024-04-12T14:38:17.7346764', 'admin@gmail.com', '$2a$10$EXTmge.TsiISfF//G9XFLeVKINNcAiOu9lWWPPKG81EpOi6OzR/X.'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE id = '77f5572b-462f-4f53-8562-f7284a1f5a12'
);
