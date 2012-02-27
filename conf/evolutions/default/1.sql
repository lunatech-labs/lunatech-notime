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
create sequence customer_seq;

create sequence project_seq;

create sequence user_seq;

alter table project add constraint fk_project_customer_1 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_project_customer_1 on project (customer_id);
alter table project add constraint fk_project_projectManager_2 foreign key (project_manager_id) references user (id) on delete restrict on update restrict;
create index ix_project_projectManager_2 on project (project_manager_id);



alter table customer_user add constraint fk_customer_user_customer_01 foreign key (customer_id) references customer (id) on delete restrict on update restrict;

alter table customer_user add constraint fk_customer_user_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists customer;

drop table if exists customer_user;

drop table if exists project;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists customer_seq;

drop sequence if exists project_seq;

drop sequence if exists user_seq;

