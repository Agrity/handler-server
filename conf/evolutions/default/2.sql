# Add Grade to Trader Bids

# ================================================================
# --- !Ups


alter table if exists bids add column grade varchar(255) default '';


# --- !Downs


alter table if exists bids drop column if exists grade;
