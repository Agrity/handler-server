package services;

import java.util.List;

import models.interfaces.PrettyString;

public class ModelService {
  public static String listToPrettyString(List<? extends PrettyString> list) {
    StringBuilder builder = new StringBuilder();

    for (PrettyString elem : list) {
      builder.append(elem.toPrettyString());
    }

    return builder.toString();
  }

  public static String phoneNumberToPrettyString(String phoneNumber) {
  	return "(" + phoneNumber.substring(2, 5) + ") "
  	  + phoneNumber.substring(5, 8) + " " + phoneNumber.substring(8);
  }
}
