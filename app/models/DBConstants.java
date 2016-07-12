
package models;

public class DBConstants {

  public static final String HANDLER_ID = "handler_id";
  public static final String GROWER_ID = "grower_id";

  /*
   * Explicitly named columns belonging to the Handler table.
   */
  public class HandlerColumns {
    public static final String ID = "handler_id";
    public static final String COMPANY_NAME = "company_name";
  }

  /*
   * Explicitly named columns belonging to the Grower table.
   */
  public class GrowerColumns {
    public static final String ID = "grower_id";
  }

  /*
   * Explicitly named columns belonging to the HandlerBid table.
   */
  public class HandlerBidColumns {
    public static final String ID = "handler_bid_id";
  }
}
