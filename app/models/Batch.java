package models;

import models.Trader;
import models.TraderBid;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

@Entity
public class Batch extends BaseModel {

  private final Trader trader;

  @OneToMany(cascade = CascadeType.ALL)
  private final List<TraderBid> traderBids;

  public Batch(Trader trader, List<TraderBid> traderBids) {
    this.trader = trader;
    this.traderBids = traderBids;
  }

  public Trader getTrader() {
    return trader;
  }

  public List<TraderBid> getTraderBids() {
    return traderBids;
  }
}