# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table action (
  id                        bigint not null,
  temp_low                  float,
  temp_high                 float,
  port                      integer,
  trigger_id                bigint,
  action_up                 boolean,
  trigger_cv                boolean,
  is_cv                     boolean,
  status                    boolean,
  constraint pk_action primary key (id))
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
  temp                      float,
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


create table device_action (
  device_id                      bigint not null,
  action_id                      bigint not null,
  constraint pk_device_action primary key (device_id, action_id))
;

create table sensor_sensor_role (
  sensor_id                      bigint not null,
  sensor_role_id                 bigint not null,
  constraint pk_sensor_sensor_role primary key (sensor_id, sensor_role_id))
;
create sequence action_seq;

create sequence device_seq;

create sequence log_item_seq;

create sequence sensor_seq;

create sequence sensor_role_seq;

alter table action add constraint fk_action_trigger_1 foreign key (trigger_id) references sensor (id) on delete restrict on update restrict;
create index ix_action_trigger_1 on action (trigger_id);
alter table device add constraint fk_device_latestError_2 foreign key (latest_error_id) references log_item (id) on delete restrict on update restrict;
create index ix_device_latestError_2 on device (latest_error_id);
alter table log_item add constraint fk_log_item_device_3 foreign key (device_id) references device (id) on delete restrict on update restrict;
create index ix_log_item_device_3 on log_item (device_id);
alter table log_item add constraint fk_log_item_sensor_4 foreign key (sensor_id) references sensor (id) on delete restrict on update restrict;
create index ix_log_item_sensor_4 on log_item (sensor_id);
alter table sensor add constraint fk_sensor_latestError_5 foreign key (latest_error_id) references log_item (id) on delete restrict on update restrict;
create index ix_sensor_latestError_5 on sensor (latest_error_id);
alter table sensor add constraint fk_sensor_device_6 foreign key (device_id) references device (id) on delete restrict on update restrict;
create index ix_sensor_device_6 on sensor (device_id);



alter table device_action add constraint fk_device_action_device_01 foreign key (device_id) references device (id) on delete restrict on update restrict;

alter table device_action add constraint fk_device_action_action_02 foreign key (action_id) references action (id) on delete restrict on update restrict;

alter table sensor_sensor_role add constraint fk_sensor_sensor_role_sensor_01 foreign key (sensor_id) references sensor (id) on delete restrict on update restrict;

alter table sensor_sensor_role add constraint fk_sensor_sensor_role_sensor__02 foreign key (sensor_role_id) references sensor_role (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists action;

drop table if exists device;

drop table if exists device_action;

drop table if exists log_item;

drop table if exists sensor;

drop table if exists sensor_sensor_role;

drop table if exists sensor_role;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists action_seq;

drop sequence if exists device_seq;

drop sequence if exists log_item_seq;

drop sequence if exists sensor_seq;

drop sequence if exists sensor_role_seq;

