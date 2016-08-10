# Add Grade to Trader Bids

# ================================================================
# --- !Ups

alter table if exists bids drop constraint if exists ck_bids_almond_variety;
alter table if exists bids alter column if exists almond_variety type varchar(255);


# --- !Downs


alter table if exists bids alter column if exists almond_variety type varchar(2);
alter table if exists bids add constraint ck_bids_almond_variety check (almond_variety in ('PD','FR','PR','MI','MT','PL','BT','SN','NP','CR'));