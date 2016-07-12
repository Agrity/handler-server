# Add Management Type, Expiration Time, and Overall Accept/Reject.

# ================================================================
# --- !Ups



alter table if exists offer add column management_service varchar(255) default 'NOT SET';

alter table if exists offer drop column if exists offer_currently_open;
alter table if exists offer add column offer_currently_open integer default 2;

alter table if exists offer add column expiration_time timestamp default CURRENT_TIMESTAMP;




# --- !Downs


alter table if exists offer drop column if exists management_service;

alter table if exists offer drop column if exists offer_currently_open;
alter table if exists offer add column offer_currently_open boolean default false;

alter table if exists offer drop column if exists expiration_time;
