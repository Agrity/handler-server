package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.List;

import models.Offer;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.OfferService;
import services.messaging.offer.OfferMessageService;
import services.parsers.OfferJsonParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

public class MessageReceivingController extends Controller {

  private static Integer numResponses = 0;

  private final OfferService offerService;
  private final OfferMessageService offerMessageService;

  @Inject
  public MessageReceivingController(OfferService offerService, OfferMessageService offerMessageService) {
    this.offerService = offerService;
    this.offerMessageService = offerMessageService;
  }

  public Result numberTwilioResponses() {
    return ok("Number Responses Recieved: " + numResponses);
  }

  public Result receiveTwilioResponse() {
    numResponses++;
    Document dom = request().body().asXml();
    if (dom == null) {
      return badRequest("Expecting Xml data");
    }
    return ok("Have a non-null XML doc");
    //XPath xPath = XPathFactory.newInstance().newXPath();
    //String text = xPath.getTextContent("//body", dom);
    //if (text == null) {
    //  return badRequest("Missing parameter [text]");
    //} else {
    //  return ok(text);
    //}



  /*  JsonNode data = request().body().asJson();

    if (data == null) {
      return ok("Expecting Some Data.\n"); 
    }

    String num = data.findValue("From").textValue();
    if (num == null) {
      return ok("From parameter is empty."); 
    }
    String text = data.findValue("Body").textValue();
    if (text == null) {
      return ok("Body parameter is empty."); \
    }
    return ok(num + "\n" + text);
  */
  }

    
}
