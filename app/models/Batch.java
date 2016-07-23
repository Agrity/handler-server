package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.Trader;
import models.TraderBid;
import models.HandlerSeller;

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

  @JsonIgnore
  public List<HandlerSeller> getAllHandlerSellers() {
    return traderBids.get(0).getAllHandlerSellers();
  }
}