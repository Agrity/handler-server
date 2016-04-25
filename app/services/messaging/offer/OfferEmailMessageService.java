package services.messaging.offer;

import com.google.inject.Inject;

import models.Grower;
import models.Offer;

import play.Logger;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import services.messaging.MessageServiceConstants.EmailFields;

public class OfferEmailMessageService implements OfferMessageService {

  private final MailerClient mailer;

  @Inject
  public OfferEmailMessageService(MailerClient mailerClient) {
    this.mailer = mailerClient;
  }

  @Override
  public boolean send(Offer offer) {
    boolean success = true;

    for (Grower grower : offer.getAllGrowers()) {
      final Email email = new Email()
        .setSubject(EmailFields.getSubjectLine())
        .setFrom(EmailFields.getFromAddress())
        .addTo(grower.getPrefferedContact())
        .setBodyHtml(views.html.emailOfferBody.render(offer, grower).toString());
      try {
        mailer.send(email);
      } catch (Exception e) {
        success = false;
        Logger.error("=== Error Sending Email ===\n" + e.getCause() + "\n\n");
      }
    }

    return success;
  }
}
