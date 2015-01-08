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

create table device_role (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_device_role primary key (id))
;


create table device_device_role (
  device_id                      bigint not null,
  device_role_id                 bigint not null,
  constraint pk_device_device_role primary key (device_id, device_role_id))
;
create sequence device_seq;

create sequence device_role_seq;




alter table device_device_role add constraint fk_device_device_role_device_01 foreign key (device_id) references device (id) on delete restrict on update restrict;

alter table device_device_role add constraint fk_device_device_role_device__02 foreign key (device_role_id) references device_role (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists device;

drop table if exists device_device_role;

drop table if exists device_role;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists device_seq;

drop sequence if exists device_role_seq;

