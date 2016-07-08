package services;

import java.time.LocalDate;
import java.util.Date;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.Instant;

public class DateService {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM yyyy");


  public static boolean verifyDateString(String dateString) {
    // TODO Implement
    
    return dateString != null;
  }

  /* Expected format of dateString: "June 2016"
   * Converts properly formatted date String into LocalDate object. Input parameter dateString
   * must be checked as valid before calling this function.
   * 
   * WARNING: Error will be thrown if dateString is not a validFormat and null will be returned.
   */
  public static LocalDate stringToDate(String dateString) {
    if (!verifyDateString(dateString)) {
      throw new RuntimeException("Invalid Date string passed to date service.");
    }
 
    try {
      Date date = dateFormat.parse(dateString);
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    } catch (ParseException e) {
      /* TODO: handle error */
    }
    return null;
  }

  public static String dateToString(LocalDate localDate) {
    Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    Date date = Date.from(instant);
    return dateFormat.format(date);
  }
}
