# --- Sample dataset

# --- !Ups

insert into user (id, username, password, fullname, email, employee, admin) values (1, 'leonard', 'secret', 'Leonard Punt', 'leonard@test.nl', 0, 0);
insert into user (id, username, password, fullname, email, employee, admin) values (2, 'gerrit', 'secret', 'Gerrit Foo', 'foo@test.nl', 0, 0);
insert into user (id, username, password, fullname, email, employee, admin) values (3, 'kees', 'secret', 'Kees Bar', 'bar@test.nl', 0, 0);
insert into user (id, username, password, fullname, email, employee, admin) values (4, 'klaas', 'secret', 'Klaas Bar', 'klaas@test.nl', 0, 0);
insert into user (id, username, password, fullname, email, employee, admin) values (5, 'piet', 'secret', 'Piet Foo', 'piet@test.nl', 0, 0);

insert into customer (id, name, code, description) values (1, 'Lunatech', 'LUNA', '');
insert into customer (id, name, code, description) values (2, 'Belastingdienst', 'BEL', 'Leuker kunnen we het niet maken, wel makkelijker..');
insert into customer (id, name, code, description) values (3, 'Albert Heijn', 'AH', '');
insert into customer (id, name, code, description) values (4, 'Rabobank', 'RABO', '');

insert into project (id, name, code, description, type, customer_id, customercontact, projectmanager_id) values (1, 'NoTime', 'TIME', 'Timesheet application for Lunatech', 0, 1, 'Leonard Punt', 1);
insert into project (id, name, code, description, type, customer_id, customercontact, projectmanager_id) values (2, 'My2Cents', 'MY2C', '', 1, 2, '', 5);
insert into project (id, name, code, description, type, customer_id, customercontact, projectmanager_id) values (3, 'Appie', 'APP', '', 0, 3, '', 4);
insert into project (id, name, code, description, type, customer_id, customercontact, projectmanager_id) values (4, 'LOSS Timesheet', 'LOSS', '', 0, 1, '', 2);

insert into projectassignment (id, project_id, user_id, startdate, enddate, hourlyrate) values (1, 1, 1, '2012-02-01 00:00:00.0', '2012-03-30 23:59:59.999', '35.50');
insert into projectassignment (id, project_id, user_id, startdate, enddate, hourlyrate) values (2, 1, 3, '2012-02-01 00:00:00.0', '2012-03-30 23:59:59.999', '47.50');
insert into projectassignment (id, project_id, user_id, startdate, enddate, hourlyrate) values (3, 1, 4, '2012-02-01 00:00:00.0', '2012-03-30 23:59:59.999', '56.45');
insert into projectassignment (id, project_id, user_id, startdate, enddate, hourlyrate) values (4, 3, 1, '2012-02-01 00:00:00.0', '2012-03-30 23:59:59.999', '54.50');

# --- !Downs

delete from projectassignment;
delete from project;
delete from customer;
delete from user;