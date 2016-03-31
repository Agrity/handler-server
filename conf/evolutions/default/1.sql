# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table grower (
  id                        bigint auto_increment not null,
  handler_handler_id        bigint,
  first_name                varchar(255),
  last_name                 varchar(255),
  constraint pk_grower primary key (id))
;

create table handler (
  handler_id                bigint auto_increment not null,
  company_name              varchar(255),
  constraint pk_handler primary key (handler_id))
;

create table offer (
  id                        bigint auto_increment not null,
  variety                   varchar(2),
  quantity                  integer,
  payment_date              timestamp,
  comments                  varchar(255),
  constraint ck_offer_variety check (variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR')),
  constraint pk_offer primary key (id))
;

alter table grower add constraint fk_grower_handler_1 foreign key (handler_handler_id) references handler (handler_id) on delete restrict on update restrict;
create index ix_grower_handler_1 on grower (handler_handler_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists grower;

drop table if exists handler;

drop table if exists offer;

SET REFERENTIAL_INTEGRITY TRUE;

