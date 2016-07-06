# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table email_address (
  id                            bigint not null,
  grower_id                     bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  email_address                 varchar(255),
  constraint pk_email_address primary key (id)
);
create sequence email_address_seq;

create table grower (
  id                            bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  handler_id                    bigint,
  first_name                    varchar(255),
  last_name                     varchar(255),
  constraint pk_grower primary key (id)
);
create sequence grower_seq;

create table handler (
  id                            bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  company_name                  varchar(255) not null,
  email_address                 varchar(255) not null,
  sha_password                  varchar(255) not null,
  auth_token                    varchar(255),
  constraint pk_handler primary key (id)
);
create sequence handler_seq;

create table offer (
  id                            bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  handler_id                    bigint,
  almond_variety                varchar(2),
  almond_size                   varchar(255),
  almond_pounds                 integer,
  price_per_pound               varchar(255),
  start_payment_date            date,
  end_payment_date              date,
  comment                       TEXT,
  offer_currently_open          boolean,
  constraint ck_offer_almond_variety check (almond_variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR')),
  constraint pk_offer primary key (id)
);
create sequence offer_seq;

create table offer_grower (
  offer_id                      bigint not null,
  grower_id                     bigint not null,
  constraint pk_offer_grower primary key (offer_id,grower_id)
);

create table offer_response (
  id                            bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  grower_id                     bigint,
  offer_id                      bigint,
  response_status               integer,
  constraint ck_offer_response_response_status check (response_status in (0,1,2,3)),
  constraint pk_offer_response primary key (id)
);
create sequence offer_response_seq;

create table phone_number (
  id                            bigint not null,
  grower_id                     bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  phone_number                  varchar(255),
  constraint pk_phone_number primary key (id)
);
create sequence phone_number_seq;

alter table email_address add constraint fk_email_address_grower_id foreign key (grower_id) references grower (id) on delete restrict on update restrict;
create index ix_email_address_grower_id on email_address (grower_id);

alter table grower add constraint fk_grower_handler_id foreign key (handler_id) references handler (id) on delete restrict on update restrict;
create index ix_grower_handler_id on grower (handler_id);

alter table offer add constraint fk_offer_handler_id foreign key (handler_id) references handler (id) on delete restrict on update restrict;
create index ix_offer_handler_id on offer (handler_id);

alter table offer_grower add constraint fk_offer_grower_offer foreign key (offer_id) references offer (id) on delete restrict on update restrict;
create index ix_offer_grower_offer on offer_grower (offer_id);

alter table offer_grower add constraint fk_offer_grower_grower foreign key (grower_id) references grower (id) on delete restrict on update restrict;
create index ix_offer_grower_grower on offer_grower (grower_id);

alter table offer_response add constraint fk_offer_response_grower_id foreign key (grower_id) references grower (id) on delete restrict on update restrict;
create index ix_offer_response_grower_id on offer_response (grower_id);

alter table offer_response add constraint fk_offer_response_offer_id foreign key (offer_id) references offer (id) on delete restrict on update restrict;
create index ix_offer_response_offer_id on offer_response (offer_id);

alter table phone_number add constraint fk_phone_number_grower_id foreign key (grower_id) references grower (id) on delete restrict on update restrict;
create index ix_phone_number_grower_id on phone_number (grower_id);


# --- !Downs

alter table email_address drop constraint if exists fk_email_address_grower_id;
drop index if exists ix_email_address_grower_id;

alter table grower drop constraint if exists fk_grower_handler_id;
drop index if exists ix_grower_handler_id;

alter table offer drop constraint if exists fk_offer_handler_id;
drop index if exists ix_offer_handler_id;

alter table offer_grower drop constraint if exists fk_offer_grower_offer;
drop index if exists ix_offer_grower_offer;

alter table offer_grower drop constraint if exists fk_offer_grower_grower;
drop index if exists ix_offer_grower_grower;

alter table offer_response drop constraint if exists fk_offer_response_grower_id;
drop index if exists ix_offer_response_grower_id;

alter table offer_response drop constraint if exists fk_offer_response_offer_id;
drop index if exists ix_offer_response_offer_id;

alter table phone_number drop constraint if exists fk_phone_number_grower_id;
drop index if exists ix_phone_number_grower_id;

drop table if exists email_address;
drop sequence if exists email_address_seq;

drop table if exists grower;
drop sequence if exists grower_seq;

drop table if exists handler;
drop sequence if exists handler_seq;

drop table if exists offer;
drop sequence if exists offer_seq;

drop table if exists offer_grower;

drop table if exists offer_response;
drop sequence if exists offer_response_seq;

drop table if exists phone_number;
drop sequence if exists phone_number_seq;

