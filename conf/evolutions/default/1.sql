# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table bids (
  dtype                         varchar(31) not null,
  id                            bigserial not null,
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
  handler_id                    bigint,
  almond_size                   varchar(255),
  start_payment_date            date,
  end_payment_date              date,
  constraint ck_bids_almond_variety check (almond_variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR')),
  constraint ck_bids_bid_status check (bid_status in (0,1,2,3)),
  constraint pk_bids primary key (id)
);

create table trader_bids_handler_sellers (
  bids_id                       bigint not null,
  sellers_id                    bigint not null,
  constraint pk_trader_bids_handler_sellers primary key (bids_id,sellers_id)
);

create table handler_bids_growers (
  bids_id                       bigint not null,
  sellers_id                    bigint not null,
  constraint pk_handler_bids_growers primary key (bids_id,sellers_id)
);

create table bid_responses (
  dtype                         varchar(31) not null,
  id                            bigserial not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  response_status               integer,
  grower_id                     bigint,
  handler_bid_id                bigint,
  handler_seller_id             bigint,
  trader_bid_id                 bigint,
  constraint ck_bid_responses_response_status check (response_status in (0,1,2,3)),
  constraint pk_bid_responses primary key (id)
);

create table sellers (
  dtype                         varchar(31) not null,
  id                            bigserial not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  first_name                    varchar(255),
  last_name                     varchar(255),
  handler_id                    bigint,
  trader_id                     bigint,
  constraint pk_sellers primary key (id)
);

create table email_address (
  id                            bigserial not null,
  base_seller_id                bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  email_address                 varchar(255),
  constraint pk_email_address primary key (id)
);

create table phone_number (
  id                            bigserial not null,
  base_seller_id                bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  phone_number                  varchar(255),
  constraint pk_phone_number primary key (id)
);

create table users (
  dtype                         varchar(31) not null,
  id                            bigserial not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  company_name                  varchar(255) not null,
  first_name                    varchar(255),
  last_name                     varchar(255),
  email_address                 varchar(255) not null,
  phone_number_id               bigint,
  sha_password                  varchar(255) not null,
  auth_token                    varchar(255),
  constraint uq_users_phone_number_id unique (phone_number_id),
  constraint pk_users primary key (id)
);

create table users_sellers (
  users_id                      bigint not null,
  sellers_id                    bigint not null,
  constraint pk_users_sellers primary key (users_id,sellers_id)
);

alter table bids add constraint fk_bids_trader_id foreign key (trader_id) references users (id) on delete restrict on update restrict;
create index ix_bids_trader_id on bids (trader_id);

alter table bids add constraint fk_bids_handler_id foreign key (handler_id) references users (id) on delete restrict on update restrict;
create index ix_bids_handler_id on bids (handler_id);

alter table trader_bids_handler_sellers add constraint fk_trader_bids_handler_sellers_bids foreign key (bids_id) references bids (id) on delete restrict on update restrict;
create index ix_trader_bids_handler_sellers_bids on trader_bids_handler_sellers (bids_id);

alter table trader_bids_handler_sellers add constraint fk_trader_bids_handler_sellers_sellers foreign key (sellers_id) references sellers (id) on delete restrict on update restrict;
create index ix_trader_bids_handler_sellers_sellers on trader_bids_handler_sellers (sellers_id);

alter table handler_bids_growers add constraint fk_handler_bids_growers_bids foreign key (bids_id) references bids (id) on delete restrict on update restrict;
create index ix_handler_bids_growers_bids on handler_bids_growers (bids_id);

alter table handler_bids_growers add constraint fk_handler_bids_growers_sellers foreign key (sellers_id) references sellers (id) on delete restrict on update restrict;
create index ix_handler_bids_growers_sellers on handler_bids_growers (sellers_id);

alter table bid_responses add constraint fk_bid_responses_grower_id foreign key (grower_id) references sellers (id) on delete restrict on update restrict;
create index ix_bid_responses_grower_id on bid_responses (grower_id);

alter table bid_responses add constraint fk_bid_responses_handler_bid_id foreign key (handler_bid_id) references bids (id) on delete restrict on update restrict;
create index ix_bid_responses_handler_bid_id on bid_responses (handler_bid_id);

alter table bid_responses add constraint fk_bid_responses_handler_seller_id foreign key (handler_seller_id) references sellers (id) on delete restrict on update restrict;
create index ix_bid_responses_handler_seller_id on bid_responses (handler_seller_id);

alter table bid_responses add constraint fk_bid_responses_trader_bid_id foreign key (trader_bid_id) references bids (id) on delete restrict on update restrict;
create index ix_bid_responses_trader_bid_id on bid_responses (trader_bid_id);

alter table sellers add constraint fk_sellers_handler_id foreign key (handler_id) references users (id) on delete restrict on update restrict;
create index ix_sellers_handler_id on sellers (handler_id);

alter table sellers add constraint fk_sellers_trader_id foreign key (trader_id) references users (id) on delete restrict on update restrict;
create index ix_sellers_trader_id on sellers (trader_id);

alter table email_address add constraint fk_email_address_base_seller_id foreign key (base_seller_id) references sellers (id) on delete restrict on update restrict;
create index ix_email_address_base_seller_id on email_address (base_seller_id);

alter table phone_number add constraint fk_phone_number_base_seller_id foreign key (base_seller_id) references sellers (id) on delete restrict on update restrict;
create index ix_phone_number_base_seller_id on phone_number (base_seller_id);

alter table users add constraint fk_users_phone_number_id foreign key (phone_number_id) references phone_number (id) on delete restrict on update restrict;

alter table users_sellers add constraint fk_users_sellers_users foreign key (users_id) references users (id) on delete restrict on update restrict;
create index ix_users_sellers_users on users_sellers (users_id);

alter table users_sellers add constraint fk_users_sellers_sellers foreign key (sellers_id) references sellers (id) on delete restrict on update restrict;
create index ix_users_sellers_sellers on users_sellers (sellers_id);


# --- !Downs

alter table if exists bids drop constraint if exists fk_bids_trader_id;
drop index if exists ix_bids_trader_id;

alter table if exists bids drop constraint if exists fk_bids_handler_id;
drop index if exists ix_bids_handler_id;

alter table if exists trader_bids_handler_sellers drop constraint if exists fk_trader_bids_handler_sellers_bids;
drop index if exists ix_trader_bids_handler_sellers_bids;

alter table if exists trader_bids_handler_sellers drop constraint if exists fk_trader_bids_handler_sellers_sellers;
drop index if exists ix_trader_bids_handler_sellers_sellers;

alter table if exists handler_bids_growers drop constraint if exists fk_handler_bids_growers_bids;
drop index if exists ix_handler_bids_growers_bids;

alter table if exists handler_bids_growers drop constraint if exists fk_handler_bids_growers_sellers;
drop index if exists ix_handler_bids_growers_sellers;

alter table if exists bid_responses drop constraint if exists fk_bid_responses_grower_id;
drop index if exists ix_bid_responses_grower_id;

alter table if exists bid_responses drop constraint if exists fk_bid_responses_handler_bid_id;
drop index if exists ix_bid_responses_handler_bid_id;

alter table if exists bid_responses drop constraint if exists fk_bid_responses_handler_seller_id;
drop index if exists ix_bid_responses_handler_seller_id;

alter table if exists bid_responses drop constraint if exists fk_bid_responses_trader_bid_id;
drop index if exists ix_bid_responses_trader_bid_id;

alter table if exists sellers drop constraint if exists fk_sellers_handler_id;
drop index if exists ix_sellers_handler_id;

alter table if exists sellers drop constraint if exists fk_sellers_trader_id;
drop index if exists ix_sellers_trader_id;

alter table if exists email_address drop constraint if exists fk_email_address_base_seller_id;
drop index if exists ix_email_address_base_seller_id;

alter table if exists phone_number drop constraint if exists fk_phone_number_base_seller_id;
drop index if exists ix_phone_number_base_seller_id;

alter table if exists users drop constraint if exists fk_users_phone_number_id;

alter table if exists users_sellers drop constraint if exists fk_users_sellers_users;
drop index if exists ix_users_sellers_users;

alter table if exists users_sellers drop constraint if exists fk_users_sellers_sellers;
drop index if exists ix_users_sellers_sellers;

drop table if exists bids cascade;

drop table if exists trader_bids_handler_sellers cascade;

drop table if exists handler_bids_growers cascade;

drop table if exists bid_responses cascade;

drop table if exists sellers cascade;

drop table if exists email_address cascade;

drop table if exists phone_number cascade;

drop table if exists users cascade;

drop table if exists users_sellers cascade;

