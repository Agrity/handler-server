package services.parsers;

/**
 * Base class to parse json data. Intended to be extended for specific json data types.
 */
public abstract class JsonParser {
  // Error variables
  private boolean valid;
  private String errorMessage;

  private boolean validitySet = false;

  public JsonParser() {}

  public boolean isValid() {
    ensureValidityIsSet();
    return valid;
  }

  // WARNING: Should only be called after isValid() has been checked to be false
  public String getErrorMessage() {
    ensureInvalid();

    return errorMessage;
  }

  protected void setValid() {
    validitySet = true;

    valid = true;
    errorMessage = null;
  }

  protected void setInvalid(String errorMessge) {
    validitySet = true;

    valid = false;
    errorMessage = errorMessge;
  }

  protected void ensureValid() {
    ensureValidityIsSet();
    if (!isValid()) {
      throw new RuntimeException("Parser Invalid: valid parser expected.");
    }
  }

  protected void ensureInvalid() {
    ensureValidityIsSet();
    if (isValid()) {
      throw new RuntimeException("Parser Valid: invalid parser expected.");
    }
  }

  protected static String missingParameterError(String paramaterName) {
    return "Missing parameter [ " + paramaterName + " ]\n";
  }

  private void ensureValidityIsSet() {
    if (!validitySet) {
      throw new RuntimeException("Parser Validity Not Set: validity expected to be set");
    }
  }
}
