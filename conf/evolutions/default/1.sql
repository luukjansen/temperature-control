# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table device (
  id                        bigint not null,
  name                      varchar(255),
  ip_address                varchar(255),
  last_update               timestamp not null,
  constraint pk_device primary key (id))
;

create sequence device_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists device;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists device_seq;

