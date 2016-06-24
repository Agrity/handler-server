package models;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import models.Almond.AlmondVariety;

import test_helpers.EbeanTest;

import static org.mockito.Mockito.*;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableList;


/**
*
* Offer JUnit Test.
*
*/
public class OfferTest extends EbeanTest {

  private static final Handler UNUSED_HANDLER = new Handler("Test Company");
  private static final List<Grower> UNUSED_GROWERS = new ImmutableList.Builder<Grower>()
      .add(new Grower(UNUSED_HANDLER, "F1", "L1"))
      .add(new Grower(UNUSED_HANDLER, "F2", "L2"))
      .build();

  private static final AlmondVariety UNUSED_VARIETY = AlmondVariety.NONPAREIL;
  private static final Integer UNUSED_POUNDS = 44_000;
  private static final String UNUSED_PRICE = "$2.66";
  private static final LocalDate UNUSED_DATE = LocalDate.of(2015, Month.JANUARY, 1);
  private static final String UNUSED_COMMENT = "Test Comment.";

  @BeforeClass
  public static void loadInitialData() {
    Ebean.save(UNUSED_HANDLER);
    Ebean.saveAll(UNUSED_GROWERS);
  }

  @Test
  public void testInitialization() {
    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            UNUSED_GROWERS,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    assertThat(offer, is(notNullValue()));
    saveModel(offer);

    assertThat(offer.getId(), is(notNullValue()));

  }

  
  @Test
  public void testHandler() {

    Long handlerId = 1L;

    Handler mockHandler = mock(Handler.class);
    when(mockHandler.getId()).thenReturn(handlerId);

    Offer offer
        = new Offer(
            mockHandler,
            UNUSED_GROWERS,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    assertThat(offer.getHandler().getId(), is(equalTo(handlerId)));
  }

  @Test
  public void testGrowers() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(Grower.createGrower(UNUSED_HANDLER, "FN1", "LN1"))
        .add(Grower.createGrower(UNUSED_HANDLER, "FN2", "LN2"))
        .add(Grower.createGrower(UNUSED_HANDLER, "FN3", "LN3"))
        .build();

    saveModels(growersList);

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer);

    // Check 3 growers present.
    assertThat(offer.getAllGrowers().size(), is(equalTo(3)));

    // Check 3 distint ids (aka growers) are present.
    long distinctGrowerIds =
        offer.getAllGrowers().stream().map(grower -> grower.getId()).distinct().count();
    assertThat(distinctGrowerIds, is(equalTo(3L)));
  }
 
  @Test
  public void testVariety() {
    AlmondVariety almondVariety = AlmondVariety.CARMEL;

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            UNUSED_GROWERS,
            almondVariety,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    assertThat(offer.getAlmondVariety(), is(equalTo(almondVariety)));
  }

  @Test
  public void testPounds() {
    Integer almondPounds = 88_000;

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            UNUSED_GROWERS,
            UNUSED_VARIETY,
            almondPounds,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    assertThat(offer.getAlmondPounds(), is(equalTo(almondPounds)));
  }

  @Test
  public void testPricePerPound() {

    String pricePerPound = "$3.00";

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            UNUSED_GROWERS,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            pricePerPound,
            UNUSED_DATE,
            UNUSED_COMMENT);

    assertThat(offer.getPricePerPound(), is(equalTo(pricePerPound)));
  }


  @Test
  public void testPaymentDate() {

    LocalDate paymentDate = LocalDate.of(2016, Month.FEBRUARY, 20);

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            UNUSED_GROWERS,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            paymentDate,
            UNUSED_COMMENT);

    assertThat(offer.getPaymentDate(), is(equalTo(paymentDate)));
  }

  @Test
  public void testComment() {

    String comment = "Comment Comment Comment.";

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            UNUSED_GROWERS,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            comment);

    assertThat(offer.getComment(), is(equalTo(comment)));
  }

  @Test
  public void testNoResponse() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(UNUSED_HANDLER, "FN4", "LN4"))
        .add(new Grower(UNUSED_HANDLER, "FN5", "LN5"))
        .add(new Grower(UNUSED_HANDLER, "FN6", "LN6"))
        .build();

    saveModels(growersList);

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer);

    Long noResponseCount = offer.getNoResponseGrowers().stream()
        .map(grower -> grower.getId())
        .distinct()
        .count();
    
    // Check all 3 growers initialized to no response.
    assertThat(noResponseCount, is(equalTo(3L)));
  }

  @Test
  public void testAcceptOffer() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(UNUSED_HANDLER, "FN7", "LN7"))
        .add(new Grower(UNUSED_HANDLER, "FN8", "LN8"))
        .add(new Grower(UNUSED_HANDLER, "FN9", "LN9"))
        .build();

    saveModels(growersList);

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer);

    offer.growerAcceptOffer(growersList.get(0).getId(), 0 /* Not Testing Load Percent*/);
    offer.growerAcceptOffer(growersList.get(2).getId(), 0);

    List<Grower> acceptedList = offer.getAcceptedGrowers();
    
    // Check 2 growers accepted.
    assertThat(acceptedList.size(), is(equalTo(2)));

    // Check that the correct 2 growers accepted.
    assertThat(acceptedList.get(0).getFullName(), anyOf(
        equalTo(growersList.get(0).getFullName()),
        equalTo(growersList.get(2).getFullName())));

    assertThat(acceptedList.get(1).getFullName(), anyOf(
        equalTo(growersList.get(0).getFullName()),
        equalTo(growersList.get(2).getFullName())));

    List<Grower> noResponseList = offer.getNoResponseGrowers();

    // Check there is still a no response grower.
    assertThat(noResponseList.size(), is(equalTo(1)));

    // Check the correct grower is still no response.
    assertThat(noResponseList.get(0).getFullName(), is(equalTo(growersList.get(1).getFullName())));
  }

  @Test
  public void testRejectOffer() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(UNUSED_HANDLER, "FN10", "LN10"))
        .add(new Grower(UNUSED_HANDLER, "FN11", "LN11"))
        .add(new Grower(UNUSED_HANDLER, "FN12", "LN12"))
        .build();

    saveModels(growersList);

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer);

    offer.growerRejectOffer(growersList.get(1).getId());
    offer.growerRejectOffer(growersList.get(2).getId());

    List<Grower> rejectedList = offer.getRejectedGrowers();
    
    // Check 2 growers rejected.
    assertThat(rejectedList.size(), is(equalTo(2)));

    // Check that the correct 2 growers rejected.
    assertThat(rejectedList.get(0).getFullName(), anyOf(
          equalTo(growersList.get(1).getFullName()),
          equalTo(growersList.get(2).getFullName())));

    assertThat(rejectedList.get(1).getFullName(), anyOf(
          equalTo(growersList.get(1).getFullName()),
          equalTo(growersList.get(2).getFullName())));

    List<Grower> noResponseList = offer.getNoResponseGrowers();

    // Check there is still a no response grower.
    assertThat(noResponseList.size(), is(equalTo(1)));

    // Check the correct grower is still no response.
    assertThat(noResponseList.get(0).getFullName(), is(equalTo(growersList.get(0).getFullName())));
  }

  @Test
  public void testRequestCall() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(UNUSED_HANDLER, "FN13", "LN13"))
        .add(new Grower(UNUSED_HANDLER, "FN14", "LN14"))
        .add(new Grower(UNUSED_HANDLER, "FN15", "LN15"))
        .build();

    saveModels(growersList);

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer);

    offer.growerRequestCall(growersList.get(1).getId());
    offer.growerRequestCall(growersList.get(0).getId());

    List<Grower> requestCallList = offer.getCallRequestedGrowers();
    
    // Check 2 growers requested call.
    assertThat(requestCallList.size(), is(equalTo(2)));

    // Check that the correct 2 growers requested call.
    assertThat(requestCallList.get(0).getFullName(), anyOf(
          equalTo(growersList.get(0).getFullName()),
          equalTo(growersList.get(1).getFullName())));

    assertThat(requestCallList.get(1).getFullName(), anyOf(
          equalTo(growersList.get(0).getFullName()),
          equalTo(growersList.get(1).getFullName())));

    List<Grower> noResponseList = offer.getNoResponseGrowers();

    // Check there is still a no response grower.
    assertThat(noResponseList.size(), is(equalTo(1)));

    // Check the correct grower is still no response.
    assertThat(noResponseList.get(0).getFullName(), is(equalTo(growersList.get(2).getFullName())));
  }

  @Test
  public void testGrowerResponse_robust() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(UNUSED_HANDLER, "FN16", "LN16"))
        .add(new Grower(UNUSED_HANDLER, "FN17", "LN17"))
        .add(new Grower(UNUSED_HANDLER, "FN18", "LN18"))
        .add(new Grower(UNUSED_HANDLER, "FN19", "LN19"))
        .build();

    saveModels(growersList);

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer);

    offer.growerAcceptOffer(growersList.get(0).getId(), 0 /* Not Testing Load Percent*/);
    offer.growerRejectOffer(growersList.get(1).getId());
    offer.growerRequestCall(growersList.get(2).getId());
    // Grower 4 no response.

    // Check accepted grower
    List<Grower> acceptedList = offer.getAcceptedGrowers();
    assertThat(acceptedList.size(), is(equalTo(1)));
    assertThat(acceptedList.get(0).getFullName(), is(equalTo(growersList.get(0).getFullName())));


    // Check rejected grower
    List<Grower> rejectedList = offer.getRejectedGrowers();
    assertThat(rejectedList.size(), is(equalTo(1)));
    assertThat(rejectedList.get(0).getFullName(), is(equalTo(growersList.get(1).getFullName())));

    // Check call requested grower
    List<Grower> callRequestedList = offer.getCallRequestedGrowers();
    assertThat(callRequestedList.size(), is(equalTo(1)));
    assertThat(callRequestedList.get(0).getFullName(), is(equalTo(growersList.get(2).getFullName())));

    // Check no response grower
    List<Grower> noResponseList = offer.getNoResponseGrowers();
    assertThat(noResponseList.size(), is(equalTo(1)));
    assertThat(noResponseList.get(0).getFullName(), is(equalTo(growersList.get(3).getFullName())));
  }


  // TODO Futher Tests:
  //  - Email Functionality

}
