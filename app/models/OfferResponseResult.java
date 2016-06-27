package models;

  public class OfferResponseResult { 
    private Boolean valid;
    private String invalidResponseMessage; 
    
    public OfferResponseResult(Boolean bool, String message) {
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
    
    public static OfferResponseResult getValidResult() {
      return new OfferResponseResult(true, null);
    }
    
    public static OfferResponseResult getInvalidResult(String invalidResponseMessage) {
      return new OfferResponseResult(false, invalidResponseMessage);
    }  
 }
