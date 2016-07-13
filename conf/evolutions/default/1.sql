# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table bid_response (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  grower_id                     bigint,
  handler_bid_id                bigint,
  response_status               integer,
  constraint ck_bid_response_response_status check (response_status in (0,1,2,3)),
  constraint pk_bid_response primary key (id)
);

create table email_address (
  id                            bigint auto_increment not null,
  grower_id                     bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  email_address                 varchar(255),
  constraint pk_email_address primary key (id)
);

create table grower (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  handler_id                    bigint,
  first_name                    varchar(255),
  last_name                     varchar(255),
  constraint pk_grower primary key (id)
);

create table handler (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  company_name                  varchar(255) not null,
  email_address                 varchar(255) not null,
  sha_password                  varchar(255) not null,
  auth_token                    varchar(255),
  constraint pk_handler primary key (id)
);

create table handler_bid (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  almond_variety                varchar(2),
  almond_pounds                 integer,
  price_per_pound               varchar(255),
  comment                       TEXT,
  management_service            varchar(255),
  expiration_time               timestamp,
  bid_status                    integer,
  handler_id                    bigint,
  almond_size                   varchar(255),
  start_payment_date            date,
  end_payment_date              date,
  constraint ck_handler_bid_almond_variety check (almond_variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR')),
  constraint ck_handler_bid_bid_status check (bid_status in (0,1,2,3)),
  constraint pk_handler_bid primary key (id)
);

create table handler_bid_grower (
  handler_bid_id                bigint not null,
  grower_id                     bigint not null,
  constraint pk_handler_bid_grower primary key (handler_bid_id,grower_id)
);

create table phone_number (
  id                            bigint auto_increment not null,
  grower_id                     bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  phone_number                  varchar(255),
  constraint pk_phone_number primary key (id)
);

create table trader (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  company_name                  varchar(255) not null,
  email_address                 varchar(255) not null,
  sha_password                  varchar(255) not null,
  auth_token                    varchar(255),
  constraint pk_trader primary key (id)
);

create table trader_handler (
  trader_id                     bigint not null,
  handler_id                    bigint not null,
  constraint pk_trader_handler primary key (trader_id,handler_id)
);

create table trader_bid (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  almond_variety                varchar(2),
  almond_pounds                 integer,
  price_per_pound               varchar(255),
  comment                       TEXT,
  management_service            varchar(255),
  expiration_time               timestamp,
  bid_status                    integer,
  trader_id                     bigint,
  constraint ck_trader_bid_almond_variety check (almond_variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR')),
  constraint ck_trader_bid_bid_status check (bid_status in (0,1,2,3)),
  constraint pk_trader_bid primary key (id)
);

create table trader_bid_handler (
  trader_bid_id                 bigint not null,
  handler_id                    bigint not null,
  constraint pk_trader_bid_handler primary key (trader_bid_id,handler_id)
);

alter table bid_response add constraint fk_bid_response_grower_id foreign key (grower_id) references grower (id) on delete restrict on update restrict;
create index ix_bid_response_grower_id on bid_response (grower_id);

alter table bid_response add constraint fk_bid_response_handler_bid_id foreign key (handler_bid_id) references handler_bid (id) on delete restrict on update restrict;
create index ix_bid_response_handler_bid_id on bid_response (handler_bid_id);

alter table email_address add constraint fk_email_address_grower_id foreign key (grower_id) references grower (id) on delete restrict on update restrict;
create index ix_email_address_grower_id on email_address (grower_id);

alter table grower add constraint fk_grower_handler_id foreign key (handler_id) references handler (id) on delete restrict on update restrict;
create index ix_grower_handler_id on grower (handler_id);

alter table handler_bid add constraint fk_handler_bid_handler_id foreign key (handler_id) references handler (id) on delete restrict on update restrict;
create index ix_handler_bid_handler_id on handler_bid (handler_id);

alter table handler_bid_grower add constraint fk_handler_bid_grower_handler_bid foreign key (handler_bid_id) references handler_bid (id) on delete restrict on update restrict;
create index ix_handler_bid_grower_handler_bid on handler_bid_grower (handler_bid_id);

alter table handler_bid_grower add constraint fk_handler_bid_grower_grower foreign key (grower_id) references grower (id) on delete restrict on update restrict;
create index ix_handler_bid_grower_grower on handler_bid_grower (grower_id);

alter table phone_number add constraint fk_phone_number_grower_id foreign key (grower_id) references grower (id) on delete restrict on update restrict;
create index ix_phone_number_grower_id on phone_number (grower_id);

alter table trader_handler add constraint fk_trader_handler_trader foreign key (trader_id) references trader (id) on delete restrict on update restrict;
create index ix_trader_handler_trader on trader_handler (trader_id);

alter table trader_handler add constraint fk_trader_handler_handler foreign key (handler_id) references handler (id) on delete restrict on update restrict;
create index ix_trader_handler_handler on trader_handler (handler_id);

alter table trader_bid add constraint fk_trader_bid_trader_id foreign key (trader_id) references trader (id) on delete restrict on update restrict;
create index ix_trader_bid_trader_id on trader_bid (trader_id);

alter table trader_bid_handler add constraint fk_trader_bid_handler_trader_bid foreign key (trader_bid_id) references trader_bid (id) on delete restrict on update restrict;
create index ix_trader_bid_handler_trader_bid on trader_bid_handler (trader_bid_id);

alter table trader_bid_handler add constraint fk_trader_bid_handler_handler foreign key (handler_id) references handler (id) on delete restrict on update restrict;
create index ix_trader_bid_handler_handler on trader_bid_handler (handler_id);


# --- !Downs

alter table bid_response drop constraint if exists fk_bid_response_grower_id;
drop index if exists ix_bid_response_grower_id;

alter table bid_response drop constraint if exists fk_bid_response_handler_bid_id;
drop index if exists ix_bid_response_handler_bid_id;

alter table email_address drop constraint if exists fk_email_address_grower_id;
drop index if exists ix_email_address_grower_id;

alter table grower drop constraint if exists fk_grower_handler_id;
drop index if exists ix_grower_handler_id;

alter table handler_bid drop constraint if exists fk_handler_bid_handler_id;
drop index if exists ix_handler_bid_handler_id;

alter table handler_bid_grower drop constraint if exists fk_handler_bid_grower_handler_bid;
drop index if exists ix_handler_bid_grower_handler_bid;

alter table handler_bid_grower drop constraint if exists fk_handler_bid_grower_grower;
drop index if exists ix_handler_bid_grower_grower;

alter table phone_number drop constraint if exists fk_phone_number_grower_id;
drop index if exists ix_phone_number_grower_id;

alter table trader_handler drop constraint if exists fk_trader_handler_trader;
drop index if exists ix_trader_handler_trader;

alter table trader_handler drop constraint if exists fk_trader_handler_handler;
drop index if exists ix_trader_handler_handler;

alter table trader_bid drop constraint if exists fk_trader_bid_trader_id;
drop index if exists ix_trader_bid_trader_id;

alter table trader_bid_handler drop constraint if exists fk_trader_bid_handler_trader_bid;
drop index if exists ix_trader_bid_handler_trader_bid;

alter table trader_bid_handler drop constraint if exists fk_trader_bid_handler_handler;
drop index if exists ix_trader_bid_handler_handler;

drop table if exists bid_response;

drop table if exists email_address;

drop table if exists grower;

drop table if exists handler;

drop table if exists handler_bid;

drop table if exists handler_bid_grower;

drop table if exists phone_number;

drop table if exists trader;

drop table if exists trader_handler;

drop table if exists trader_bid;

drop table if exists trader_bid_handler;

