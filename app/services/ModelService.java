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
}
