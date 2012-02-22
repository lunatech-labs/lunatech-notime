# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                        bigint not null,
  username                  varchar(255),
  password                  varchar(255),
  fullname                  varchar(255),
  email                     varchar(255),
  employee                  boolean,
  admin                     boolean,
  constraint uq_user_username unique (username),
  constraint pk_user primary key (id))
;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_seq;

