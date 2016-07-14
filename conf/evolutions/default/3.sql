# Add Pounds Accepted

# ================================================================
# --- !Ups

alter table if exists offer_response add column pounds_accepted bigint default 0;

# --- !Downs

alter table if exists offer_response drop column if exists pounds_accepted;