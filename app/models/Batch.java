package models;

import models.Trader;
import models.TraderBid;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Column;

@Entity
public class Batch extends BaseModel {

  @ManyToOne(cascade = CascadeType.ALL)
  private final Trader trader;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "batch")
  private final List<TraderBid> traderBids;

  public Batch(Trader trader, List<TraderBid> traderBids) {
    this.trader = trader;
    trader.setBatch(this);
    
    this.traderBids = traderBids;
    for(TraderBid bid: traderBids) {
      bid.setBatch(this);
    }
  }

  public Trader getTrader() {
    return trader;
  }

  public List<TraderBid> getTraderBids() {
    return traderBids;
  }
}