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
  
    Map<String, String[]> map = request().headers();
    for (Map.Entry<String, String[]> entry : map.entrySet()) {
      Logger.info("Header: " + entry.getKey() + " Value: " + Arrays.toString(entry.getValue()));
    }
    Map<String, String[]> bodyMap = request().body().asFormUrlEncoded();
    for (Map.Entry<String, String[]> entry : bodyMap.entrySet()) {
      Logger.info("Body Key: " + entry.getKey() + "     Body Value: " + Arrays.toString(entry.getValue()));
    }

    String phoneNum = bodyMap.get("From");
    String smsMessage = bodyMap.get("Body");
    Logger.info("From: " + phoneNum + "\nmessage: " + smsMessage);
    return ok("From: " + phoneNum + "\nmessage: " + smsMessage);
  }

    
}
