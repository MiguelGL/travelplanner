--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.1
-- Dumped by pg_dump version 9.6.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: tp_destination_id_seq; Type: SEQUENCE; Schema: public; Owner: travelplanner-user
--

CREATE SEQUENCE tp_destination_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tp_destination_id_seq OWNER TO "travelplanner-user";

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: tp_destination; Type: TABLE; Schema: public; Owner: travelplanner-user
--

CREATE TABLE tp_destination (
    id bigint DEFAULT nextval('tp_destination_id_seq'::regclass) NOT NULL,
    updated timestamp without time zone DEFAULT timezone('utc'::text, now()) NOT NULL,
    name character varying(256) NOT NULL
);


ALTER TABLE tp_destination OWNER TO "travelplanner-user";

--
-- Name: tp_tp_destination_id_seq; Type: SEQUENCE; Schema: public; Owner: travelplanner-user
--

CREATE SEQUENCE tp_tp_destination_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tp_tp_destination_id_seq OWNER TO "travelplanner-user";

--
-- Name: tp_tp_trip_id_seq; Type: SEQUENCE; Schema: public; Owner: travelplanner-user
--

CREATE SEQUENCE tp_tp_trip_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tp_tp_trip_id_seq OWNER TO "travelplanner-user";

--
-- Name: tp_tp_user_id_seq; Type: SEQUENCE; Schema: public; Owner: travelplanner-user
--

CREATE SEQUENCE tp_tp_user_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tp_tp_user_id_seq OWNER TO "travelplanner-user";

--
-- Name: tp_trip_id_seq; Type: SEQUENCE; Schema: public; Owner: travelplanner-user
--

CREATE SEQUENCE tp_trip_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tp_trip_id_seq OWNER TO "travelplanner-user";

--
-- Name: tp_trip; Type: TABLE; Schema: public; Owner: travelplanner-user
--

CREATE TABLE tp_trip (
    id bigint DEFAULT nextval('tp_trip_id_seq'::regclass) NOT NULL,
    updated timestamp without time zone DEFAULT timezone('utc'::text, now()) NOT NULL,
    comment character varying(512) DEFAULT ''::character varying NOT NULL,
    end_date date NOT NULL,
    start_date date NOT NULL,
    destination_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE tp_trip OWNER TO "travelplanner-user";

--
-- Name: tp_user_id_seq; Type: SEQUENCE; Schema: public; Owner: travelplanner-user
--

CREATE SEQUENCE tp_user_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tp_user_id_seq OWNER TO "travelplanner-user";

--
-- Name: tp_user; Type: TABLE; Schema: public; Owner: travelplanner-user
--

CREATE TABLE tp_user (
    id bigint DEFAULT nextval('tp_user_id_seq'::regclass) NOT NULL,
    updated timestamp without time zone DEFAULT timezone('utc'::text, now()) NOT NULL,
    email character varying(64) NOT NULL,
    first_name character varying(64) NOT NULL,
    last_name character varying(128) DEFAULT ''::character varying NOT NULL,
    password character varying(60) NOT NULL
);


ALTER TABLE tp_user OWNER TO "travelplanner-user";

--
-- Data for Name: tp_destination; Type: TABLE DATA; Schema: public; Owner: travelplanner-user
--

COPY tp_destination (id, updated, name) FROM stdin;
\.


--
-- Name: tp_destination_id_seq; Type: SEQUENCE SET; Schema: public; Owner: travelplanner-user
--

SELECT pg_catalog.setval('tp_destination_id_seq', 1, false);


--
-- Name: tp_tp_destination_id_seq; Type: SEQUENCE SET; Schema: public; Owner: travelplanner-user
--

SELECT pg_catalog.setval('tp_tp_destination_id_seq', 1, false);


--
-- Name: tp_tp_trip_id_seq; Type: SEQUENCE SET; Schema: public; Owner: travelplanner-user
--

SELECT pg_catalog.setval('tp_tp_trip_id_seq', 1, false);


--
-- Name: tp_tp_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: travelplanner-user
--

SELECT pg_catalog.setval('tp_tp_user_id_seq', 1, false);


--
-- Data for Name: tp_trip; Type: TABLE DATA; Schema: public; Owner: travelplanner-user
--

COPY tp_trip (id, updated, comment, end_date, start_date, destination_id, user_id) FROM stdin;
\.


--
-- Name: tp_trip_id_seq; Type: SEQUENCE SET; Schema: public; Owner: travelplanner-user
--

SELECT pg_catalog.setval('tp_trip_id_seq', 1, false);


--
-- Data for Name: tp_user; Type: TABLE DATA; Schema: public; Owner: travelplanner-user
--

COPY tp_user (id, updated, email, first_name, last_name, password) FROM stdin;
\.


--
-- Name: tp_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: travelplanner-user
--

SELECT pg_catalog.setval('tp_user_id_seq', 1, false);


--
-- Name: tp_destination destination__name_uidx; Type: CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_destination
    ADD CONSTRAINT destination__name_uidx UNIQUE (name);


--
-- Name: tp_destination tp_destination_pkey; Type: CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_destination
    ADD CONSTRAINT tp_destination_pkey PRIMARY KEY (id);


--
-- Name: tp_trip tp_trip_pkey; Type: CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_trip
    ADD CONSTRAINT tp_trip_pkey PRIMARY KEY (id);


--
-- Name: tp_user tp_user_pkey; Type: CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_user
    ADD CONSTRAINT tp_user_pkey PRIMARY KEY (id);


--
-- Name: tp_trip trip__user__end_date_uidx; Type: CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_trip
    ADD CONSTRAINT trip__user__end_date_uidx UNIQUE (user_id, end_date);


--
-- Name: tp_trip trip__user__start_date_uidx; Type: CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_trip
    ADD CONSTRAINT trip__user__start_date_uidx UNIQUE (user_id, start_date);


--
-- Name: tp_user user__email_uidx; Type: CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_user
    ADD CONSTRAINT user__email_uidx UNIQUE (email);


--
-- Name: trip__end_date_idx; Type: INDEX; Schema: public; Owner: travelplanner-user
--

CREATE INDEX trip__end_date_idx ON tp_trip USING btree (end_date);


--
-- Name: trip__start_date_idx; Type: INDEX; Schema: public; Owner: travelplanner-user
--

CREATE INDEX trip__start_date_idx ON tp_trip USING btree (start_date);


--
-- Name: user__first_name_idx; Type: INDEX; Schema: public; Owner: travelplanner-user
--

CREATE INDEX user__first_name_idx ON tp_user USING btree (first_name);


--
-- Name: user__last_name_idx; Type: INDEX; Schema: public; Owner: travelplanner-user
--

CREATE INDEX user__last_name_idx ON tp_user USING btree (last_name);


--
-- Name: tp_trip fki8l4ombkrus1y09yqsoum2bqr; Type: FK CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_trip
    ADD CONSTRAINT fki8l4ombkrus1y09yqsoum2bqr FOREIGN KEY (destination_id) REFERENCES tp_destination(id);


--
-- Name: tp_trip fksuje1k85sfgw95p4xkcs1qvjw; Type: FK CONSTRAINT; Schema: public; Owner: travelplanner-user
--

ALTER TABLE ONLY tp_trip
    ADD CONSTRAINT fksuje1k85sfgw95p4xkcs1qvjw FOREIGN KEY (user_id) REFERENCES tp_user(id);


--
-- PostgreSQL database dump complete
--

