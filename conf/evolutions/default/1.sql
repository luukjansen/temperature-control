# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table action (
  id                        bigint not null,
  name                      varchar(255),
  temp_low                  float,
  temp_high                 float,
  pin                       integer,
  sensor_id                 bigint,
  device_id                 bigint,
  action_up                 boolean,
  fix                       boolean,
  last_action               timestamp not null,
  constraint pk_action primary key (id))
;

create table action_role (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_action_role primary key (id))
;

create table device (
  id                        bigint not null,
  name                      varchar(255),
  ip_address                varchar(255),
  unique_id                 varchar(255),
  latest_error_id           bigint,
  last_update               timestamp not null,
  constraint pk_device primary key (id))
;

create table log_item (
  id                        bigint not null,
  message                   varchar(255),
  time                      timestamp,
  last_update               timestamp,
  accepted                  boolean,
  device_id                 bigint,
  sensor_id                 bigint,
  constraint pk_log_item primary key (id))
;

create table sensor (
  id                        bigint not null,
  name                      varchar(255),
  sensor_id                 varchar(255),
  value                     float,
  latest_error_id           bigint,
  device_id                 bigint,
  last_update               timestamp not null,
  constraint pk_sensor primary key (id))
;

create table sensor_role (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_sensor_role primary key (id))
;


create table action_action_role (
  action_id                      bigint not null,
  action_role_id                 bigint not null,
  constraint pk_action_action_role primary key (action_id, action_role_id))
;

create table sensor_sensor_role (
  sensor_id                      bigint not null,
  sensor_role_id                 bigint not null,
  constraint pk_sensor_sensor_role primary key (sensor_id, sensor_role_id))
;
create sequence action_seq;

create sequence action_role_seq;

create sequence device_seq;

create sequence log_item_seq;

create sequence sensor_seq;

create sequence sensor_role_seq;

alter table action add constraint fk_action_sensor_1 foreign key (sensor_id) references sensor (id) on delete restrict on update restrict;
create index ix_action_sensor_1 on action (sensor_id);
alter table action add constraint fk_action_device_2 foreign key (device_id) references device (id) on delete restrict on update restrict;
create index ix_action_device_2 on action (device_id);
alter table device add constraint fk_device_latestError_3 foreign key (latest_error_id) references log_item (id) on delete restrict on update restrict;
create index ix_device_latestError_3 on device (latest_error_id);
alter table log_item add constraint fk_log_item_device_4 foreign key (device_id) references device (id) on delete restrict on update restrict;
create index ix_log_item_device_4 on log_item (device_id);
alter table log_item add constraint fk_log_item_sensor_5 foreign key (sensor_id) references sensor (id) on delete restrict on update restrict;
create index ix_log_item_sensor_5 on log_item (sensor_id);
alter table sensor add constraint fk_sensor_latestError_6 foreign key (latest_error_id) references log_item (id) on delete restrict on update restrict;
create index ix_sensor_latestError_6 on sensor (latest_error_id);
alter table sensor add constraint fk_sensor_device_7 foreign key (device_id) references device (id) on delete restrict on update restrict;
create index ix_sensor_device_7 on sensor (device_id);



alter table action_action_role add constraint fk_action_action_role_action_01 foreign key (action_id) references action (id) on delete restrict on update restrict;

alter table action_action_role add constraint fk_action_action_role_action__02 foreign key (action_role_id) references action_role (id) on delete restrict on update restrict;

alter table sensor_sensor_role add constraint fk_sensor_sensor_role_sensor_01 foreign key (sensor_id) references sensor (id) on delete restrict on update restrict;

alter table sensor_sensor_role add constraint fk_sensor_sensor_role_sensor__02 foreign key (sensor_role_id) references sensor_role (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists action;

drop table if exists action_action_role;

drop table if exists action_role;

drop table if exists sensor_sensor_role;

drop table if exists device;

drop table if exists log_item;

drop table if exists sensor;

drop table if exists sensor_role;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists action_seq;

drop sequence if exists action_role_seq;

drop sequence if exists device_seq;

drop sequence if exists log_item_seq;

drop sequence if exists sensor_seq;

drop sequence if exists sensor_role_seq;

