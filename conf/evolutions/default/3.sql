# Add Grade to Trader Bids

# ================================================================
# --- !Ups


alter table if exists bid_responses drop constraint if exists ck_bid_responses_response_status;
alter table if exists bid_responses add constraint ck_bid_responses_response_status check (response_status in (0,1,2,3,4,5));


# --- !Downs


alter table if exists bid_responses drop constraint if exists ck_bid_responses_response_status;
alter table if exists bid_responses add constraint ck_bid_responses_response_status check (response_status in (0,1,2,3));
