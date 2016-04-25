package models;

import com.avaje.ebean.annotation.EnumMapping;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import play.Logger;

public class Almond {

  /*
   * NOTE: Must be kept in sync with AlmondVariety enum and its Annotation.
   */
  private static Map<String, AlmondVariety> almondVarietyMap =
      new HashMap<String, AlmondVariety>() {
        {
          put("NP", AlmondVariety.NONPAREIL);
          put("NONPAREIL", AlmondVariety.NONPAREIL);

          put("CR", AlmondVariety.CARMEL);
          put("CARMEL", AlmondVariety.CARMEL);

          put("BT", AlmondVariety.BUTTE);
          put("BUTTE", AlmondVariety.BUTTE);

          put("PD", AlmondVariety.PADRE);
          put("PADRE", AlmondVariety.PADRE);

          put("MI", AlmondVariety.MISSION);
          put("MISSION", AlmondVariety.MISSION);

          put("MT", AlmondVariety.MONTEREY);
          put("MONTEREY", AlmondVariety.MONTEREY);

          put("SN", AlmondVariety.SONORA);
          put("SONORA", AlmondVariety.SONORA);

          put("FR", AlmondVariety.FRITZ);
          put("FRITZ", AlmondVariety.FRITZ);
          
          put("PR", AlmondVariety.PRICE);
          put("PRICE", AlmondVariety.PRICE);

          put("PR", AlmondVariety.PEERLESS);
          put("PEERLESS", AlmondVariety.PEERLESS);
        }
      };

  @EnumMapping(nameValuePairs = "NONPAREIL=NP, CARMEL=CR, BUTTE=BT, PADRE=PD, MISSION=MI,"
      + "MONTEREY=MT, SONORA=SN, FRITZ=FR, PRICE=PR, PEERLESS=PL")
  public enum AlmondVariety {
    NONPAREIL, CARMEL, BUTTE, PADRE, MISSION, MONTEREY, SONORA, FRITZ, PRICE, PEERLESS
  }

  public static List<String> getAlmondVarietyStrings() {
    return Stream.of(AlmondVariety.values()).map(AlmondVariety::name).collect(Collectors.toList());
  }

  public static AlmondVariety stringToAlmondVariety(String almondVarietyString) {
    return almondVarietyString != null ? almondVarietyMap.get(almondVarietyString) : null;
  }
}
