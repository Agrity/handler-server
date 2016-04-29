package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import models.Almond.AlmondVariety;

import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;


/**
*
* Offer JUnit Test.
*
*/
public class OfferTest {

  private static final Handler UNUSED_HANDLER = new Handler("Test Company");
  private static final List<Grower> UNUSED_GROWERS = new ImmutableList.Builder<Grower>()
      .add(new Grower(1, "F1", "L1")) // id: 1
      .add(new Grower(2, "F2", "L2")) // id: 2
      .build();

  private static final AlmondVariety UNUSED_VARIETY = AlmondVariety.NONPAREIL;
  private static final Integer UNUSED_POUNDS = 44_000;
  private static final String UNUSED_PRICE = "$2.66";
  private static final LocalDate UNUSED_DATE = LocalDate.of(2015, Month.JANUARY, 1);
  private static final String UNUSED_COMMENT = "Test Comment.";

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
        .add(new Grower(1, "F1", "L1")) // id: 1
        .add(new Grower(2, "F2", "L2")) // id: 2
        .add(new Grower(3, "F3", "L3")) // id: 3
        .build();

    int numGrowers = growersList.size();


    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    // Check 3 growers present.
    assertThat(offer.getAllGrowers().size(), is(equalTo(numGrowers)));

    // Check 3 distint ids (aka growers) are present.
    long distinctGrowerIds =
        offer.getAllGrowers().stream().map(grower -> grower.getId()).distinct().count();
    assertThat(distinctGrowerIds, is(equalTo(numGrowers)));
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
  public void testIdComparator() {

    Grower g1 = new Grower(1, "F1", "L1"); // id: 1
    Grower g2 = new Grower(2, "F2", "L2"); // id: 2

    int compareResult = Grower.ID_COMPARATOR.compare(g1, g2);
    
    // Assert that the comparator catogorized g1 with a lower ID than g2.
    // TODO: Need to change to assertThat() but JUnit packaged with old version of hamcrest.
    //       Change after update.
    assertTrue(compareResult < 0);

    

  }

  @Test
  public void testNoResponse() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(1, "F1", "L1")) // id: 1
        .add(new Grower(2, "F2", "L2")) // id: 2
        .add(new Grower(3, "F3", "L3")) // id: 3
        .build();

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    Long noResponseCount = offer.getNoResponseGrowers().stream()
        .map(grower -> grower.getId())
        .distinct()
        .count();
    
    // Check all 3 growers initialized to no response.
    assertThat(noResponseCount, is(equalTo(3)));
  }

  @Test
  public void testAcceptOffer() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(1, "F1", "L1")) // id: 1
        .add(new Grower(2, "F2", "L2")) // id: 2
        .add(new Grower(3, "F3", "L3")) // id: 3
        .build();

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    offer.growerAcceptOffer(1L);
    offer.growerAcceptOffer(3L);

    List<Grower> acceptedList = offer.getAcceptedGrowers().stream()
        .sorted(Grower.ID_COMPARATOR)
        .collect(Collectors.toList());
    
    // Check 2 growers accepted.
    assertThat(acceptedList.size(), is(equalTo(2)));

    // Check that the correct 2 growers accepted.
    assertThat(acceptedList.get(0).getId(), is(equalTo(1L)));
    assertThat(acceptedList.get(1).getId(), is(equalTo(3L)));

    List<Grower> noResponseList = offer.getNoResponseGrowers();

    // Check there is still a no response grower.
    assertThat(noResponseList.size(), is(equalTo(1L)));

    // Check the correct grower is still no response.
    assertThat(noResponseList.get(0).getId(), is(equalTo(2)));
  }

  @Test
  public void testRejectOffer() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(1, "F1", "L1")) // id: 1
        .add(new Grower(2, "F2", "L2")) // id: 2
        .add(new Grower(3, "F3", "L3")) // id: 3
        .build();

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    offer.growerRejectOffer(2L);
    offer.growerRejectOffer(3L);

    List<Grower> rejectedList = offer.getRejectedGrowers().stream()
        .sorted(Grower.ID_COMPARATOR)
        .collect(Collectors.toList());
    
    // Check 2 growers rejected.
    assertThat(rejectedList.size(), is(equalTo(2)));

    // Check that the correct 2 growers rejected.
    assertThat(rejectedList.get(0).getId(), is(equalTo(2L)));
    assertThat(rejectedList.get(1).getId(), is(equalTo(3L)));

    List<Grower> noResponseList = offer.getNoResponseGrowers();

    // Check there is still a no response grower.
    assertThat(noResponseList.size(), is(equalTo(1)));

    // Check the correct grower is still no response.
    assertThat(noResponseList.get(0).getId(), is(equalTo(1L)));
  }

  @Test
  public void testRequestCall() {

  List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(1, "F1", "L1")) // id: 1
        .add(new Grower(2, "F2", "L2")) // id: 2
        .add(new Grower(3, "F3", "L3")) // id: 3
        .build();

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    offer.growerRequestCall(1L);
    offer.growerRequestCall(2L);

    List<Grower> requestCallList = offer.getCallRequestedGrowers().stream()
        .sorted(Grower.ID_COMPARATOR)
        .collect(Collectors.toList());
    
    // Check 2 growers requested call.
    assertThat(requestCallList.size(), is(equalTo(2)));

    // Check that the correct 2 growers requested call.
    assertThat(requestCallList.get(0).getId(), is(equalTo(1L)));
    assertThat(requestCallList.get(1).getId(), is(equalTo(2L)));

    List<Grower> noResponseList = offer.getNoResponseGrowers();

    // Check there is still a no response grower.
    assertThat(noResponseList.size(), is(equalTo(1)));

    // Check the correct grower is still no response.
    assertThat(noResponseList.get(0).getId(), is(equalTo(3L)));
  }

  @Test
  public void testGrowerResponse_robust() {

    List<Grower> growersList = new ImmutableList.Builder<Grower>()
        .add(new Grower(1, "F1", "L1")) // id: 1
        .add(new Grower(2, "F2", "L2")) // id: 2
        .add(new Grower(3, "F3", "L3")) // id: 3
        .build();

    Offer offer
        = new Offer(
            UNUSED_HANDLER,
            growersList,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    offer.growerAcceptOffer(1L);
    offer.growerRejectOffer(2L);
    offer.growerRequestCall(3L);
    // Grower 4 no response.

    // Check accepted grower
    List<Grower> acceptedList = offer.getAcceptedGrowers();
    assertThat(acceptedList.size(), is(equalTo(1)));
    assertThat(acceptedList.get(0).getId(), is(equalTo(1L)));


    // Check rejected grower
    List<Grower> rejectedList = offer.getRejectedGrowers();
    assertThat(rejectedList.size(), is(equalTo(1)));
    assertThat(rejectedList.get(0).getId(), is(equalTo(2L)));

    // Check call requested grower
    List<Grower> callRequestedList = offer.getCallRequestedGrowers();
    assertThat(callRequestedList.size(), is(equalTo(1)));
    assertThat(callRequestedList.get(0).getId(), is(equalTo(3L)));

    // Check no response grower
    List<Grower> noResponseList = offer.getNoResponseGrowers();
    assertThat(noResponseList.size(), is(equalTo(1)));
    assertThat(noResponseList.get(0).getId(), is(equalTo(4L)));
  }


  // TODO Futher Tests:
  //  - Email Functionality

}
