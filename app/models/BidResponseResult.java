package models;

  public class BidResponseResult { 
    private Boolean valid;
    private String invalidResponseMessage; 
    
    public BidResponseResult(Boolean bool, String message) {
      this.valid = bool;
      this.invalidResponseMessage = message; 
    }
    
    public Boolean isValid() {
        return valid;
    }
    
    public String getInvalidResponseMessage() {
      if (isValid()) {
        throw new RuntimeException("Attempting to get invalid message from valid OfferResponseResult.");
      }
      return invalidResponseMessage;
    }
    
    public static BidResponseResult getValidResult() {
      return new BidResponseResult(true, null);
    }
    
    public static BidResponseResult getInvalidResult(String invalidResponseMessage) {
      return new BidResponseResult(false, invalidResponseMessage);
    }  
 }
