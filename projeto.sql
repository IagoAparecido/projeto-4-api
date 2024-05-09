--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4
-- Dumped by pg_dump version 15.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: chat_message; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chat_message (
    id uuid NOT NULL,
    chat_id uuid,
    content character varying(255),
    recipient_id uuid,
    sender_id uuid,
    "timestamp" timestamp(6) without time zone
);


ALTER TABLE public.chat_message OWNER TO postgres;

--
-- Name: chat_notification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chat_notification (
    id uuid NOT NULL,
    content character varying(255),
    recipient_id uuid,
    sender_id uuid
);


ALTER TABLE public.chat_notification OWNER TO postgres;

--
-- Name: chat_room; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chat_room (
    id uuid NOT NULL,
    chat_id uuid,
    recipient_id uuid,
    sender_id uuid,
    status boolean
);


ALTER TABLE public.chat_room OWNER TO postgres;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.comments (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(255) NOT NULL,
    post_id uuid NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.comments OWNER TO postgres;

--
-- Name: images; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.images (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    url character varying(255) NOT NULL,
    post_id uuid NOT NULL
);


ALTER TABLE public.images OWNER TO postgres;

--
-- Name: posts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.posts (
    id uuid NOT NULL,
    uf character varying(255) NOT NULL,
    age character varying(255),
    city character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(255) NOT NULL,
    name character varying(255),
    race character varying(255),
    sex character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.posts OWNER TO postgres;

--
-- Name: sub_comments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sub_comments (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(255) NOT NULL,
    comment_id uuid NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.sub_comments OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    code character varying(255),
    code_password character varying(255),
    created_at timestamp(6) without time zone,
    email character varying(255) NOT NULL,
    image_url character varying(1000),
    is_authenticated boolean NOT NULL,
    name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    role character varying(255),
    status character varying(255),
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'USER'::character varying])::text[]))),
    CONSTRAINT users_status_check CHECK (((status)::text = ANY ((ARRAY['AUTHORIZED'::character varying, 'UNAUTHORIZED'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: chat_message chat_message_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_message
    ADD CONSTRAINT chat_message_pkey PRIMARY KEY (id);


--
-- Name: chat_notification chat_notification_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_notification
    ADD CONSTRAINT chat_notification_pkey PRIMARY KEY (id);


--
-- Name: chat_room chat_room_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_room
    ADD CONSTRAINT chat_room_pkey PRIMARY KEY (id);


--
-- Name: comments comments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);


--
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id);


--
-- Name: posts posts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT posts_pkey PRIMARY KEY (id);


--
-- Name: sub_comments sub_comments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sub_comments
    ADD CONSTRAINT sub_comments_pkey PRIMARY KEY (id);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: posts fk5lidm6cqbc7u4xhqpxm898qme; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT fk5lidm6cqbc7u4xhqpxm898qme FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: comments fk8omq0tc18jd43bu5tjh6jvraq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fk8omq0tc18jd43bu5tjh6jvraq FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: images fkcp0pycisii8ub3q4b7x5mfpn1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT fkcp0pycisii8ub3q4b7x5mfpn1 FOREIGN KEY (post_id) REFERENCES public.posts(id);


--
-- Name: comments fkh4c7lvsc298whoyd4w9ta25cr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fkh4c7lvsc298whoyd4w9ta25cr FOREIGN KEY (post_id) REFERENCES public.posts(id);


--
-- Name: sub_comments fkkbgycvypkn7psmevbawlwimvi; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sub_comments
    ADD CONSTRAINT fkkbgycvypkn7psmevbawlwimvi FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: sub_comments fkno36ufastfwtemn89tiybd0e4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sub_comments
    ADD CONSTRAINT fkno36ufastfwtemn89tiybd0e4 FOREIGN KEY (comment_id) REFERENCES public.comments(id);


--
-- PostgreSQL database dump complete
--

