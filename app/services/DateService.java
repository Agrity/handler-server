package services;

import java.time.LocalDate;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class DateService {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

  public static boolean verifyDateString(String dateString) {
    // TODO Implement
    
    return dateString != null;
  }

  /* Converts properly formatted date String into LocalDate object. Input parameter dateString
   * must be checked as valid before calling this function.
   * 
   * WARNING: Error will be thrown if dateString is not a validFormat and null will be returned.
   */
  public static Date stringToDate(String dateString) {
    if (!verifyDateString(dateString)) {
      throw new RuntimeException("Invalid Date string passed to date service.");
    }
 
    try {
      return dateFormat.parse(dateString);
    } catch (ParseException e) {
      /* TODO: handle error */
    }
    return null;
  }

  public static String dateToString(Date date) {
    return dateFormat.format(date);
  }
}
