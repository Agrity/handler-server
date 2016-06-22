package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

//import java.util.List;
import java.util.*;

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

import play.Logger;

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
    //Document doc = request().body();

    
    //if (doc == null) {
    //  Logger.error("Expecting Xml data");
    //  return badRequest("Expecting Xml data");
    //}
    Map<String, String[]> map = request().headers();
    for (Map.Entry<String, String[]> entry : map.entrySet()) {
      Logger.info("Header: " + entry.getKey() + "Value: " + entry.getValue());
    }
    Logger.info("++++ : " + request().body().toString());
    return ok(request().body().toString());

    /*XPath xPath = XPathFactory.newInstance().newXPath();
    String text = xPath.getTextContent("//body", doc);
    if (text == null) {
      return badRequest("Missing parameter [text]");
    } else {
      return ok(text);
    } */



   /* JsonNode data = request().body().asJson();

    if (data == null) {
      Logger.error("Expecting json data");
      return badRequest("Expecting Some Data.\n"); 
    }

    String num = data.findValue("From").textValue();
    if (num == null) {
      Logger.error("Expecting from parameter");
      return badRequest("From parameter is empty."); 
    }
    String text = data.findValue("Body").textValue();
    if (text == null) {
      Logger.error("Expecting body parameter");
      return badRequest("Body parameter is empty."); 
    }
    Logger.info(num + "\n" + text);
    return ok(num + "\n" + text);
  */
  }

    
}
