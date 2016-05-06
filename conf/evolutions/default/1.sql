# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table email_address (
  id                            bigint auto_increment not null,
  grower_grower_id              bigint not null,
  email_address                 varchar(255),
  constraint pk_email_address primary key (id)
);

create table grower (
  grower_id                     bigint auto_increment not null,
  handler_handler_id            bigint,
  first_name                    varchar(255),
  last_name                     varchar(255),
  constraint pk_grower primary key (grower_id)
);

create table grower_offer (
  grower_grower_id              bigint not null,
  offer_id                      bigint not null,
  constraint pk_grower_offer primary key (grower_grower_id,offer_id)
);

create table handler (
  handler_id                    bigint auto_increment not null,
  company_name                  varchar(255),
  constraint pk_handler primary key (handler_id)
);

create table offer (
  id                            bigint auto_increment not null,
  handler_handler_id            bigint,
  almond_variety                varchar(2),
  almond_pounds                 integer,
  price_per_pound               varchar(255),
  payment_date                  date,
  comment                       TEXT,
  constraint ck_offer_almond_variety check (almond_variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR')),
  constraint pk_offer primary key (id)
);

create table offer_grower (
  offer_id                      bigint not null,
  grower_grower_id              bigint not null,
  constraint pk_offer_grower primary key (offer_id,grower_grower_id)
);

create table offer_response (
  id                            bigint auto_increment not null,
  grower_grower_id              bigint,
  offer_id                      bigint,
  response_status               integer,
  constraint ck_offer_response_response_status check (response_status in (0,1,2,3)),
  constraint pk_offer_response primary key (id)
);

alter table email_address add constraint fk_email_address_grower_grower_id foreign key (grower_grower_id) references grower (grower_id) on delete restrict on update restrict;
create index ix_email_address_grower_grower_id on email_address (grower_grower_id);

alter table grower add constraint fk_grower_handler_handler_id foreign key (handler_handler_id) references handler (handler_id) on delete restrict on update restrict;
create index ix_grower_handler_handler_id on grower (handler_handler_id);

alter table grower_offer add constraint fk_grower_offer_grower foreign key (grower_grower_id) references grower (grower_id) on delete restrict on update restrict;
create index ix_grower_offer_grower on grower_offer (grower_grower_id);

alter table grower_offer add constraint fk_grower_offer_offer foreign key (offer_id) references offer (id) on delete restrict on update restrict;
create index ix_grower_offer_offer on grower_offer (offer_id);

alter table offer add constraint fk_offer_handler_handler_id foreign key (handler_handler_id) references handler (handler_id) on delete restrict on update restrict;
create index ix_offer_handler_handler_id on offer (handler_handler_id);

alter table offer_grower add constraint fk_offer_grower_offer foreign key (offer_id) references offer (id) on delete restrict on update restrict;
create index ix_offer_grower_offer on offer_grower (offer_id);

alter table offer_grower add constraint fk_offer_grower_grower foreign key (grower_grower_id) references grower (grower_id) on delete restrict on update restrict;
create index ix_offer_grower_grower on offer_grower (grower_grower_id);

alter table offer_response add constraint fk_offer_response_grower_grower_id foreign key (grower_grower_id) references grower (grower_id) on delete restrict on update restrict;
create index ix_offer_response_grower_grower_id on offer_response (grower_grower_id);

alter table offer_response add constraint fk_offer_response_offer_id foreign key (offer_id) references offer (id) on delete restrict on update restrict;
create index ix_offer_response_offer_id on offer_response (offer_id);


# --- !Downs

alter table email_address drop constraint if exists fk_email_address_grower_grower_id;
drop index if exists ix_email_address_grower_grower_id;

alter table grower drop constraint if exists fk_grower_handler_handler_id;
drop index if exists ix_grower_handler_handler_id;

alter table grower_offer drop constraint if exists fk_grower_offer_grower;
drop index if exists ix_grower_offer_grower;

alter table grower_offer drop constraint if exists fk_grower_offer_offer;
drop index if exists ix_grower_offer_offer;

alter table offer drop constraint if exists fk_offer_handler_handler_id;
drop index if exists ix_offer_handler_handler_id;

alter table offer_grower drop constraint if exists fk_offer_grower_offer;
drop index if exists ix_offer_grower_offer;

alter table offer_grower drop constraint if exists fk_offer_grower_grower;
drop index if exists ix_offer_grower_grower;

alter table offer_response drop constraint if exists fk_offer_response_grower_grower_id;
drop index if exists ix_offer_response_grower_grower_id;

alter table offer_response drop constraint if exists fk_offer_response_offer_id;
drop index if exists ix_offer_response_offer_id;

drop table if exists email_address;

drop table if exists grower;

drop table if exists grower_offer;

drop table if exists handler;

drop table if exists offer;

drop table if exists offer_grower;

drop table if exists offer_response;

