# --- Sample dataset

# --- !Ups

insert into user (id, username, password, fullname, email) values (1, 'leonard', '$2a$10$S4oWmAIMLvUxOuspF2Fg4eMehfZzyJvvgyYkT8mzYvQyih/FyhKBG', 'Leonard Punt', 'leonard@test.nl');
insert into user (id, username, password, fullname, email) values (2, 'gerrit', '$2a$10$S4oWmAIMLvUxOuspF2Fg4eMehfZzyJvvgyYkT8mzYvQyih/FyhKBG', 'Gerrit Foo', 'foo@test.nl');
insert into user (id, username, password, fullname, email) values (3, 'kees', '$2a$10$S4oWmAIMLvUxOuspF2Fg4eMehfZzyJvvgyYkT8mzYvQyih/FyhKBG', 'Kees Bar', 'bar@test.nl');
insert into user (id, username, password, fullname, email) values (4, 'klaas', '$2a$10$S4oWmAIMLvUxOuspF2Fg4eMehfZzyJvvgyYkT8mzYvQyih/FyhKBG', 'Klaas Bar', 'klaas@test.nl');
insert into user (id, username, password, fullname, email) values (5, 'piet', '$2a$10$S4oWmAIMLvUxOuspF2Fg4eMehfZzyJvvgyYkT8mzYvQyih/FyhKBG', 'Piet Foo', 'piet@test.nl');

insert into customer (id, name, code, description) values (1, 'Lunatech', 'LUNA', '');
insert into customer (id, name, code, description) values (2, 'Belastingdienst', 'BEL', 'Leuker kunnen we het niet maken, wel makkelijker..');
insert into customer (id, name, code, description) values (3, 'Albert Heijn', 'AH', '');
insert into customer (id, name, code, description) values (4, 'Rabobank', 'RABO', '');

insert into project (id, name, code, description, type, customer_id, customercontact, projectmanager_id, defaultproject) values (1, 'NoTime', 'TIME', 'Timesheet application for Lunatech', 0, 1, 'Leonard Punt', 1, 0);
insert into project (id, name, code, description, type, customer_id, customercontact, projectmanager_id, defaultproject) values (2, 'My2Cents', 'MY2C', '', 1, 2, '', 5, 0);
insert into project (id, name, code, description, type, customer_id, customercontact, projectmanager_id, defaultproject) values (3, 'Appie', 'APP', '', 0, 3, '', 4, 0);
insert into project (id, name, code, description, type, customer_id, customercontact, projectmanager_id, defaultproject) values (4, 'LOSS Timesheet', 'LOSS', '', 0, 1, '', 2, 0);

insert into projectassignment (id, project_id, user_id, startdate, enddate, hourlyrate, starred) values (1, 1, 1, '2012-02-01 00:00:00.0', '2012-05-30 23:59:59.999', '35.50', 0);
insert into projectassignment (id, project_id, user_id, startdate, enddate, hourlyrate, starred) values (2, 1, 3, '2012-02-01 00:00:00.0', '2012-05-30 23:59:59.999', '47.50', 0);
insert into projectassignment (id, project_id, user_id, startdate, enddate, hourlyrate, starred) values (3, 1, 4, '2012-02-01 00:00:00.0', '2012-05-30 23:59:59.999', '56.45', 0);
insert into projectassignment (id, project_id, user_id, startdate, enddate, hourlyrate, starred) values (4, 3, 1, '2012-02-01 00:00:00.0', '2012-05-30 23:59:59.999', '54.50', 0);

insert into hourentry (id, assignment_id, date, hours, minutes) values (1, 1, '2012-04-02 00:00:00.0', 7, 40);
insert into hourentry (id, assignment_id, date, hours, minutes) values (2, 1, '2012-04-03 00:00:00.0', 8, 0);
insert into hourentry (id, assignment_id, date, hours, minutes) values (3, 1, '2012-04-04 00:00:00.0', 0, 0);
insert into hourentry (id, assignment_id, date, hours, minutes) values (4, 1, '2012-04-05 00:00:00.0', 5, 30);
insert into hourentry (id, assignment_id, date, hours, minutes) values (5, 1, '2012-04-06 00:00:00.0', 8, 0);
insert into hourentry (id, assignment_id, date, hours, minutes) values (6, 1, '2012-04-07 00:00:00.0', 0, 0);
insert into hourentry (id, assignment_id, date, hours, minutes) values (7, 1, '2012-04-08 00:00:00.0', 0, 0);
insert into hourentry (id, assignment_id, date, hours, minutes) values (8, 4, '2012-04-02 00:00:00.0', 0, 40);
insert into hourentry (id, assignment_id, date, hours, minutes) values (9, 2, '2012-04-04 00:00:00.0', 3, 50);
insert into hourentry (id, assignment_id, date, hours, minutes) values (10, 3, '2012-04-05 00:00:00.0', 8, 30);
insert into hourentry (id, assignment_id, date, hours, minutes) values (11, 4, '2012-04-05 00:00:00.0', 2, 10);


# --- !Downs

delete from hourentry;
delete from projectassignment;
delete from project;
delete from customer;
delete from user;