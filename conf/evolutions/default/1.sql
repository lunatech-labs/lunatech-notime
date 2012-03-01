# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table customer (
  id                        bigint not null,
  name                      varchar(255),
  code                      varchar(255),
  description               varchar(255),
  constraint uq_customer_name unique (name),
  constraint uq_customer_code unique (code),
  constraint pk_customer primary key (id))
;

create table hourentry (
  id                        bigint not null,
  assignment_id             bigint,
  date                      timestamp,
  hours                     integer,
  minutes                   integer,
  constraint pk_hourentry primary key (id))
;

create table project (
  id                        bigint not null,
  name                      varchar(255),
  code                      varchar(255),
  description               varchar(255),
  type                      integer,
  customer_id               bigint,
  customer_contact          varchar(255),
  project_manager_id        bigint,
  constraint ck_project_type check (type in (0,1)),
  constraint uq_project_name unique (name),
  constraint uq_project_code unique (code),
  constraint pk_project primary key (id))
;

create table projectassignment (
  id                        bigint not null,
  project_id                bigint,
  user_id                   bigint,
  start_date                timestamp,
  end_date                  timestamp,
  hourly_rate               decimal(38,2),
  constraint pk_projectassignment primary key (id))
;

create table tag (
  id                        bigint not null,
  tag                       varchar(255),
  constraint pk_tag primary key (id))
;

create table user (
  id                        bigint not null,
  username                  varchar(255),
  password                  varchar(255),
  fullname                  varchar(255),
  email                     varchar(255),
  employee                  boolean,
  admin                     boolean,
  constraint uq_user_username unique (username),
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;


create table customer_user (
  customer_id                    bigint not null,
  user_id                        bigint not null,
  constraint pk_customer_user primary key (customer_id, user_id))
;

create table hour_entry_tag (
  hourentry_id                  bigint not null,
  tag_id                         bigint not null,
  constraint pk_hourentry_tag primary key (hourentry_id, tag_id))
;
create sequence customer_seq;

create sequence hourentry_seq;

create sequence project_seq;

create sequence projectassignment_seq;

create sequence tag_seq;

create sequence user_seq;

alter table hourentry add constraint fk_hourentry_assignment_1 foreign key (assignment_id) references projectassignment (id) on delete restrict on update restrict;
create index ix_hourentry_assignment_1 on hourentry (assignment_id);
alter table project add constraint fk_project_customer_2 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_project_customer_2 on project (customer_id);
alter table project add constraint fk_project_projectManager_3 foreign key (project_manager_id) references user (id) on delete restrict on update restrict;
create index ix_project_projectManager_3 on project (project_manager_id);
alter table projectassignment add constraint fk_projectassignment_project_4 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_projectassignment_project_4 on projectassignment (project_id);
alter table projectassignment add constraint fk_projectassignment_user_5 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_projectassignment_user_5 on projectassignment (user_id);



alter table customer_user add constraint fk_customer_user_customer_01 foreign key (customer_id) references customer (id) on delete restrict on update restrict;

alter table customer_user add constraint fk_customer_user_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table hour_entry_tag add constraint fk_hour_entry_tag_hour_entry_01 foreign key (hourentry_id) references hourentry (id) on delete restrict on update restrict;

alter table hour_entry_tag add constraint fk_hour_entry_tag_tag_02 foreign key (tag_id) references tag (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists customer;

drop table if exists customer_user;

drop table if exists hourentry;

drop table if exists hour_entry_tag;

drop table if exists project;

drop table if exists projectassignment;

drop table if exists tag;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists customer_seq;

drop sequence if exists hourentry_seq;

drop sequence if exists project_seq;

drop sequence if exists projectassignment_seq;

drop sequence if exists tag_seq;

drop sequence if exists user_seq;

