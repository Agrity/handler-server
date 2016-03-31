package models;

import com.avaje.ebean.annotation.EnumMapping;

public class Almond {

  @EnumMapping(nameValuePairs = "NONPAREIL=NP, CARMEL=CR, BUTTE=BT, PADRE=PD, MISSION=MI,"
      + "MONTEREY=MT, SONORA=SN, FRITZ=FR, PRICE=PR, PEERLESS=PL")
  public enum AlmondVariety {
    NONPAREIL, CARMEL, BUTTE, PADRE, MISSION, MONTEREY, SONORA, FRITZ, PRICE, PEERLESS
  }
}
