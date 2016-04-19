package services;

import java.time.LocalDate;

public class DateService {

  public static boolean verifyDateString(String dateString) {
    // TODO Implement
    
    return dateString != null;
  }

  /* Converts properly formatted date String into LocalDate object. Input parameter dateString
   * must be checked as valid before calling this function.
   * 
   * WARNING: Error will be thrown if dateString is not a validFormat.
   */
  public static LocalDate stringToDate(String dateString) {
    if (!verifyDateString(dateString)) {
      throw new RuntimeException("Invalid Date string passed to date service.");
    }

    // TODO Implement

    return LocalDate.now();
  }
}
