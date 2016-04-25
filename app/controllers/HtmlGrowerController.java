package controllers;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import controllers.ErrorMessages;

import models.EmailAddress;
import models.Grower;
import models.Handler;
// TODO Remove *
import play.mvc.*;
import play.data.Form;

import services.GrowerService;
import services.HandlerService;

public class HtmlGrowerController extends Controller {

  public Result growerCreate() {
    // TODO Change from hardcode
    long handlerId = 1L;
    Handler handler = HandlerService.getHandler(handlerId);

    if (handler == null) {
      return badRequest(ErrorMessages.handlerNotFoundMessage(handlerId));
    }

    Form growerForm = Form.form().bindFromRequest();

    String firstName = getFieldValue(growerForm, "first_name");
    if (firstName == null) 
      return badRequest("First Name Field Not Present");

    String lastName = getFieldValue(growerForm, "last_name");
    if (lastName == null) 
      return badRequest("Last Name Field Not Present");

    String email = getFieldValue(growerForm, "email_address");
    if (email == null) 
      return badRequest("Email Address Field Not Present");
    List<EmailAddress> emailAddresses = new ArrayList<>();
    emailAddresses.add(new EmailAddress(email));

    Grower grower = new Grower(handler, firstName, lastName, emailAddresses,
        new ArrayList<String>());

    grower.save();

    return redirect("/html/growers/" + grower.getId());
  }

  private String getFieldValue(Form form, String fieldName) {
    Form.Field field = form.field(fieldName);
    return field == null ? null : field.value();
  }

  public Result growerCreateForm() {
    return ok(views.html.growers.growerCreate.render());
  }

  public Result handlerGrowerList(long handlerId) {
    Handler handler = HandlerService.getHandler(handlerId);

    if (handler == null) {
      return badRequest(ErrorMessages.handlerNotFoundMessage(handlerId));
    }

    return ok(views.html.growers.growerList.render(handler.getGrowersList()));
  }

  public Result growerView(long growerId) {
    Grower grower = GrowerService.getGrower(growerId);

    if (grower == null) {
      return badRequest(ErrorMessages.growerNotFoundMessage(growerId));
    }

    return ok(views.html.growers.growerView.render(grower));
  }
}
