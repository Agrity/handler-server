package controllers;

import java.util.ArrayList;
import java.util.List;

public class Helpers {
  public static <T> List<T> fetchList(List<T> lazyList) {
    ArrayList<T> fetchedList = new ArrayList<>();
    for (T lazyItem : lazyList) {
      fetchedList.add(lazyItem);
    }
    return fetchedList;

  }
}
