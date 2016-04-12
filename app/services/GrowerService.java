package services;

import com.fasterxml.jackson.databind.JsonNode;

import models.Grower;
import models.Handler;

import play.mvc.Controller;
import play.mvc.Result;

import services.parsers.GrowerJsonParser;



// TODO Figure out how to construct Result without needing to extend Controller.
public class GrowerService extends Controller {

  // Used to generate fake grower.
  private static Integer curGrowerNum = 0;

  public static Grower getGrower(Long id) {
    return Grower.find.byId(id);
  }

  public static Result createGrowerResult(JsonNode data) {
    GrowerJsonParser parser = new GrowerJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(parser.getErrorMessage());
    }

    Grower grower = createGrower(parser);

    return created("Grower Created: " + grower + "\n");
  }

  public static Grower createFakeGrower(Handler handler) {
    Grower fakeGrower = new Grower("Test", "Grower " + curGrowerNum);
    fakeGrower.setHandler(handler);
    fakeGrower.save();

    curGrowerNum++;

    return fakeGrower;
  }

  private static Grower createGrower(GrowerJsonParser parser) {
    Grower newGrower = new Grower(
        parser.getHandler(),
        parser.getFirstName(),
        parser.getLastName(),
        parser.getEmailAddresses(),
        parser.getPhoneNumbers());

    newGrower.save();
    return newGrower;
  }
}
