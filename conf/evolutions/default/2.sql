
# --- !Ups


# Add Management Type, Expiration Time, and Overall Accept/Reject.
# ================================================================

# Management Service
alter table if exists offer add column management_service varchar(255) set default "NOT SET";

# Drop then Add Currently Open Column with Different Data Types
alter table if exists offer drop column if exists offer_currently_open;
alter table if exists offer add column offer_currently_open integer set default 2;

# Add Expiration Time Column
alter table if exists offer add column expiration_time timestamp set default CURRENT_TIMESTAMP;




# --- !Downs


alter table if exists offer drop column if exists  management_service;

alter table if exists offer drop column if exists offer_currently_open;
alter table if exists offer add column if exists offer_currently_open boolean default false;

alter table if exists offer drop column if exists expiration_time;
