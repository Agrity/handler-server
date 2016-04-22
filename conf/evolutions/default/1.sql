# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table email_address (
  id                        bigint auto_increment not null,
  grower_grower_id          bigint not null,
  email_address             varchar(255),
  constraint pk_email_address primary key (id))
;

create table grower (
  grower_id                 bigint auto_increment not null,
  handler_handler_id        bigint,
  first_name                varchar(255),
  last_name                 varchar(255),
  constraint pk_grower primary key (grower_id))
;

create table grower_offer_response (
  grower_id                 bigint auto_increment not null,
  offer_offer_id            bigint not null,
  grower_grower_id          bigint,
  grower_response           integer,
  constraint ck_grower_offer_response_grower_response check (grower_response in (0,1,2,3)),
  constraint pk_grower_offer_response primary key (grower_id))
;

create table handler (
  handler_id                bigint auto_increment not null,
  company_name              varchar(255),
  constraint pk_handler primary key (handler_id))
;

create table offer (
  offer_id                  bigint auto_increment not null,
  handler_handler_id        bigint,
  almond_variety            varchar(2),
  almond_pounds             integer,
  price_per_pound           varchar(255),
  payment_date              date,
  comments                  varchar(255),
  constraint ck_offer_almond_variety check (almond_variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR')),
  constraint pk_offer primary key (offer_id))
;

alter table email_address add constraint fk_email_address_grower_1 foreign key (grower_grower_id) references grower (grower_id) on delete restrict on update restrict;
create index ix_email_address_grower_1 on email_address (grower_grower_id);
alter table grower add constraint fk_grower_handler_2 foreign key (handler_handler_id) references handler (handler_id) on delete restrict on update restrict;
create index ix_grower_handler_2 on grower (handler_handler_id);
alter table grower_offer_response add constraint fk_grower_offer_response_offer_3 foreign key (offer_offer_id) references offer (offer_id) on delete restrict on update restrict;
create index ix_grower_offer_response_offer_3 on grower_offer_response (offer_offer_id);
alter table grower_offer_response add constraint fk_grower_offer_response_growe_4 foreign key (grower_grower_id) references grower (grower_id) on delete restrict on update restrict;
create index ix_grower_offer_response_growe_4 on grower_offer_response (grower_grower_id);
alter table offer add constraint fk_offer_handler_5 foreign key (handler_handler_id) references handler (handler_id) on delete restrict on update restrict;
create index ix_offer_handler_5 on offer (handler_handler_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists email_address;

drop table if exists grower;

drop table if exists grower_offer_response;

drop table if exists handler;

drop table if exists offer;

SET REFERENTIAL_INTEGRITY TRUE;

