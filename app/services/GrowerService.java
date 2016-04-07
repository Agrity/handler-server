package services;

import com.fasterxml.jackson.databind.JsonNode;

import models.Grower;
import models.Handler;

import play.mvc.Controller;


public class GrowerService extends Controller {

  // Used to generate fake grower.
  private static Integer curGrowerNum = 0;

  public static Grower getGrower(Long id) {
    return Grower.find.byId(id);
  }

  // TODO Change RuntimeException to more specific/custom throw.
  // TODO Implement actual Grower.
  public static Grower createGrower(Handler handler, JsonNode data) {
    return null;
  }

  public static Grower createFakeGrower(Handler handler) {
    Grower fakeGrower = new Grower("Test", "Grower " + curGrowerNum);
    fakeGrower.setHandler(handler);
    fakeGrower.save();

    curGrowerNum++;

    return fakeGrower;
  }
}
