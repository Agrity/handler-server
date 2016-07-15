# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table base_bid (
  dtype                         varchar(31) not null,
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
  handler_id                    bigint,
  almond_size                   varchar(255),
  start_payment_date            date,
  end_payment_date              date,
  constraint ck_base_bid_almond_variety check (almond_variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR')),
  constraint ck_base_bid_bid_status check (bid_status in (0,1,2,3)),
  constraint pk_base_bid primary key (id)
);

create table base_bid_base_seller (
  base_bid_id                   bigint not null,
  base_seller_id                bigint not null,
  constraint pk_base_bid_base_seller primary key (base_bid_id,base_seller_id)
);

create table ryans_grower_bid_join (
  named_column                  bigint not null,
  base_seller_id                bigint not null,
  constraint pk_ryans_grower_bid_join primary key (named_column,base_seller_id)
);

create table base_bid_response (
  dtype                         varchar(31) not null,
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  response_status               integer,
  grower_id                     bigint,
  handler_bid_id                bigint,
  handler_seller_id             bigint,
  trader_bid_id                 bigint,
  constraint ck_base_bid_response_response_status check (response_status in (0,1,2,3)),
  constraint pk_base_bid_response primary key (id)
);

create table base_seller (
  dtype                         varchar(31) not null,
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  first_name                    varchar(255),
  last_name                     varchar(255),
  handler_id                    bigint,
  trader_id                     bigint,
  constraint pk_base_seller primary key (id)
);

create table email_address (
  id                            bigint auto_increment not null,
  handler_seller_id             bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  email_address                 varchar(255),
  constraint pk_email_address primary key (id)
);

create table phone_number (
  id                            bigint auto_increment not null,
  handler_seller_id             bigint not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  phone_number                  varchar(255),
  constraint pk_phone_number primary key (id)
);

create table user (
  dtype                         varchar(31) not null,
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  updated_at                    timestamp,
  company_name                  varchar(255) not null,
  first_name                    varchar(255),
  last_name                     varchar(255),
  email_address                 varchar(255) not null,
  phone_number_id               bigint,
  sha_password                  varchar(255) not null,
  auth_token                    varchar(255),
  constraint uq_user_phone_number_id unique (phone_number_id),
  constraint pk_user primary key (id)
);

create table user_base_seller (
  user_id                       bigint not null,
  base_seller_id                bigint not null,
  constraint pk_user_base_seller primary key (user_id,base_seller_id)
);

alter table base_bid add constraint fk_base_bid_trader_id foreign key (trader_id) references user (id) on delete restrict on update restrict;
create index ix_base_bid_trader_id on base_bid (trader_id);

alter table base_bid add constraint fk_base_bid_handler_id foreign key (handler_id) references user (id) on delete restrict on update restrict;
create index ix_base_bid_handler_id on base_bid (handler_id);

alter table base_bid_base_seller add constraint fk_base_bid_base_seller_base_bid foreign key (base_bid_id) references base_bid (id) on delete restrict on update restrict;
create index ix_base_bid_base_seller_base_bid on base_bid_base_seller (base_bid_id);

alter table base_bid_base_seller add constraint fk_base_bid_base_seller_base_seller foreign key (base_seller_id) references base_seller (id) on delete restrict on update restrict;
create index ix_base_bid_base_seller_base_seller on base_bid_base_seller (base_seller_id);

alter table ryans_grower_bid_join add constraint fk_ryans_grower_bid_join_base_bid foreign key (named_column) references base_bid (id) on delete restrict on update restrict;
create index ix_ryans_grower_bid_join_base_bid on ryans_grower_bid_join (named_column);

alter table ryans_grower_bid_join add constraint fk_ryans_grower_bid_join_base_seller foreign key (base_seller_id) references base_seller (id) on delete restrict on update restrict;
create index ix_ryans_grower_bid_join_base_seller on ryans_grower_bid_join (base_seller_id);

alter table base_bid_response add constraint fk_base_bid_response_grower_id foreign key (grower_id) references base_seller (id) on delete restrict on update restrict;
create index ix_base_bid_response_grower_id on base_bid_response (grower_id);

alter table base_bid_response add constraint fk_base_bid_response_handler_bid_id foreign key (handler_bid_id) references base_bid (id) on delete restrict on update restrict;
create index ix_base_bid_response_handler_bid_id on base_bid_response (handler_bid_id);

alter table base_bid_response add constraint fk_base_bid_response_handler_seller_id foreign key (handler_seller_id) references base_seller (id) on delete restrict on update restrict;
create index ix_base_bid_response_handler_seller_id on base_bid_response (handler_seller_id);

alter table base_bid_response add constraint fk_base_bid_response_trader_bid_id foreign key (trader_bid_id) references base_bid (id) on delete restrict on update restrict;
create index ix_base_bid_response_trader_bid_id on base_bid_response (trader_bid_id);

alter table base_seller add constraint fk_base_seller_handler_id foreign key (handler_id) references user (id) on delete restrict on update restrict;
create index ix_base_seller_handler_id on base_seller (handler_id);

alter table base_seller add constraint fk_base_seller_trader_id foreign key (trader_id) references user (id) on delete restrict on update restrict;
create index ix_base_seller_trader_id on base_seller (trader_id);

alter table email_address add constraint fk_email_address_handler_seller_id foreign key (handler_seller_id) references base_seller (id) on delete restrict on update restrict;
create index ix_email_address_handler_seller_id on email_address (handler_seller_id);

alter table phone_number add constraint fk_phone_number_handler_seller_id foreign key (handler_seller_id) references base_seller (id) on delete restrict on update restrict;
create index ix_phone_number_handler_seller_id on phone_number (handler_seller_id);

alter table user add constraint fk_user_phone_number_id foreign key (phone_number_id) references phone_number (id) on delete restrict on update restrict;

alter table user_base_seller add constraint fk_user_base_seller_user foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_base_seller_user on user_base_seller (user_id);

alter table user_base_seller add constraint fk_user_base_seller_base_seller foreign key (base_seller_id) references base_seller (id) on delete restrict on update restrict;
create index ix_user_base_seller_base_seller on user_base_seller (base_seller_id);


# --- !Downs

alter table base_bid drop constraint if exists fk_base_bid_trader_id;
drop index if exists ix_base_bid_trader_id;

alter table base_bid drop constraint if exists fk_base_bid_handler_id;
drop index if exists ix_base_bid_handler_id;

alter table base_bid_base_seller drop constraint if exists fk_base_bid_base_seller_base_bid;
drop index if exists ix_base_bid_base_seller_base_bid;

alter table base_bid_base_seller drop constraint if exists fk_base_bid_base_seller_base_seller;
drop index if exists ix_base_bid_base_seller_base_seller;

alter table ryans_grower_bid_join drop constraint if exists fk_ryans_grower_bid_join_base_bid;
drop index if exists ix_ryans_grower_bid_join_base_bid;

alter table ryans_grower_bid_join drop constraint if exists fk_ryans_grower_bid_join_base_seller;
drop index if exists ix_ryans_grower_bid_join_base_seller;

alter table base_bid_response drop constraint if exists fk_base_bid_response_grower_id;
drop index if exists ix_base_bid_response_grower_id;

alter table base_bid_response drop constraint if exists fk_base_bid_response_handler_bid_id;
drop index if exists ix_base_bid_response_handler_bid_id;

alter table base_bid_response drop constraint if exists fk_base_bid_response_handler_seller_id;
drop index if exists ix_base_bid_response_handler_seller_id;

alter table base_bid_response drop constraint if exists fk_base_bid_response_trader_bid_id;
drop index if exists ix_base_bid_response_trader_bid_id;

alter table base_seller drop constraint if exists fk_base_seller_handler_id;
drop index if exists ix_base_seller_handler_id;

alter table base_seller drop constraint if exists fk_base_seller_trader_id;
drop index if exists ix_base_seller_trader_id;

alter table email_address drop constraint if exists fk_email_address_handler_seller_id;
drop index if exists ix_email_address_handler_seller_id;

alter table phone_number drop constraint if exists fk_phone_number_handler_seller_id;
drop index if exists ix_phone_number_handler_seller_id;

alter table user drop constraint if exists fk_user_phone_number_id;

alter table user_base_seller drop constraint if exists fk_user_base_seller_user;
drop index if exists ix_user_base_seller_user;

alter table user_base_seller drop constraint if exists fk_user_base_seller_base_seller;
drop index if exists ix_user_base_seller_base_seller;

drop table if exists base_bid;

drop table if exists base_bid_base_seller;

drop table if exists ryans_grower_bid_join;

drop table if exists base_bid_response;

drop table if exists base_seller;

drop table if exists email_address;

drop table if exists phone_number;

drop table if exists user;

drop table if exists user_base_seller;

