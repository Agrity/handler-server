package servies.impl;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Duration;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.Thread;

import models.Almond.AlmondVariety;
import models.Grower;
import models.Handler;
import models.Offer;
import models.OfferResponse;
import models.OfferResponse.ResponseStatus;

import services.WaterfallService;

import test_helpers.EbeanTest;

import static org.mockito.Mockito.*;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableList;




public class WaterfallServiceTest extends EbeanTest {	

	private static final Handler UNUSED_HANDLER = new Handler("Test Company");
	private static final List<Grower> UNUSED_GROWERS = new ImmutableList.Builder<Grower>()
		.add(new Grower(UNUSED_HANDLER, "F1", "L1"))
		.add(new Grower(UNUSED_HANDLER, "F2", "L2"))
    .add(new Grower(UNUSED_HANDLER, "F3", "L3"))
    .add(new Grower(UNUSED_HANDLER, "F4", "L4"))
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
  public void testExpired() {
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

    WaterfallService wservice = new WaterfallService(offer, Duration.ofMillis(1000));

    int index = 0;
    while(wservice.getCurrentGrowers().size() > 0) {
      assertThat(wservice.getCurrentGrowers().get(0), is(equalTo(UNUSED_GROWERS.get(index))));
      try {
        Thread.sleep(1100);
      } catch(InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
      index ++;

    }
    assertThat(wservice.getCurrentGrowers().size(), is(0));






    // int index = 0;
    // int counter = 0;
    // while(wservice.process()) {
    //   List<Grower> g = wservice.getCurrentGrowers();
    //   assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
    //   try {
    //     Thread.sleep(55);
    //   } catch(InterruptedException ex) {
    //     Thread.currentThread().interrupt();
    //   }
    //   counter += 1;
    //   if(counter % 2 == 0) index += 1;
    // }

    // assertThat(wservice.getCurrentGrowers().size(), is(0));

  }

  // @Test
  // public void testAllRejected() {
  //   Offer offer
  //     = new Offer(
  //         UNUSED_HANDLER,
  //         UNUSED_GROWERS,
  //         UNUSED_VARIETY,
  //         UNUSED_POUNDS,
  //         UNUSED_PRICE,
  //         UNUSED_DATE,
  //         UNUSED_COMMENT);

  //   assertThat(offer, is(notNullValue()));
  //   saveModel(offer);

  //   WaterfallService wservice = new WaterfallService(offer, Duration.ofMillis(30));

  //   int index = 0;
  //   while(wservice.process()) {
  //     List<Grower> g = wservice.getCurrentGrowers();
  //     OfferResponse or = offer.getGrowerOfferResponse(g.get(0).getId());
  //     assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
  //     or.setResponseStatus(ResponseStatus.REJECTED);
  //     index++;
  //   }
  //   assertThat(wservice.getCurrentGrowers().size(), is(0));
  
  // }

  // @Test
  // public void testFirstAccepted() {
  //   Offer offer
  //     = new Offer(
  //         UNUSED_HANDLER,
  //         UNUSED_GROWERS,
  //         UNUSED_VARIETY,
  //         UNUSED_POUNDS,
  //         UNUSED_PRICE,
  //         UNUSED_DATE,
  //         UNUSED_COMMENT);

  //   assertThat(offer, is(notNullValue()));
  //   saveModel(offer);

  //   WaterfallService wservice = new WaterfallService(offer, Duration.ofMillis(300));

  //   int index = 0;
  //   while(wservice.process()) {
  //     List<Grower> g = wservice.getCurrentGrowers();
  //     OfferResponse or = offer.getGrowerOfferResponse(g.get(0).getId());
  //     assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
  //     or.setResponseStatus(ResponseStatus.ACCEPTED);
  //     index++;
  //   }
  //   assertThat(wservice.getCurrentGrowers().size(), is(4));

  // }

  // @Test
  // public void test2Reject3rdAccepts() {
  //   Offer offer
  //     = new Offer(
  //         UNUSED_HANDLER,
  //         UNUSED_GROWERS,
  //         UNUSED_VARIETY,
  //         UNUSED_POUNDS,
  //         UNUSED_PRICE,
  //         UNUSED_DATE,
  //         UNUSED_COMMENT);

  //   assertThat(offer, is(notNullValue()));
  //   saveModel(offer);

  //   WaterfallService wservice = new WaterfallService(offer, Duration.ofMillis(1000));
  //   assertThat(wservice.getCurrentGrowers().size(), is(4));

  //   int index = 0;
  //   while(wservice.process()) {
  //     List<Grower> g = wservice.getCurrentGrowers();
  //     assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
      
  //     if(index == 2) {
  //       OfferResponse or = offer.getGrowerOfferResponse(g.get(0).getId());
  //       or.setResponseStatus(ResponseStatus.ACCEPTED);
  //     } else if (index < 2) {
  //       OfferResponse or = offer.getGrowerOfferResponse(g.get(0).getId());
  //       or.setResponseStatus(ResponseStatus.REJECTED);
  //     }
      
  //     index++;
  //   }
  //   assertThat(wservice.getCurrentGrowers().size(), is(2));
  // }

  // @Test
  // public void test3rdAccepts() {
  //   Offer offer
  //     = new Offer(
  //         UNUSED_HANDLER,
  //         UNUSED_GROWERS,
  //         UNUSED_VARIETY,
  //         UNUSED_POUNDS,
  //         UNUSED_PRICE,
  //         UNUSED_DATE,
  //         UNUSED_COMMENT);

  //   assertThat(offer, is(notNullValue()));
  //   saveModel(offer);

  //   WaterfallService wservice = new WaterfallService(offer, Duration.ofSeconds(1));
  //   assertThat(wservice.getCurrentGrowers().size(), is(4));

  //   int index = 0;
  //   while(wservice.process()) {
  //     List<Grower> g = wservice.getCurrentGrowers();
  //     assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
      
  //     if(index == 2) {
  //       OfferResponse or = offer.getGrowerOfferResponse(g.get(0).getId());
  //       or.setResponseStatus(ResponseStatus.ACCEPTED);
  //     } else {
  //       try {
  //         Thread.sleep(1100);
  //       } catch(InterruptedException ex) {
  //         Thread.currentThread().interrupt();
  //       }
  //     }
      
  //     index++;
  //   }
  //   assertThat(wservice.getCurrentGrowers().size(), is(2));
  // }

  // /* First and third grower reject, second has no response, and final grower accepts. */
  // @Test
  // public void testMixed() {
  //   Offer offer
  //     = new Offer(
  //         UNUSED_HANDLER,
  //         UNUSED_GROWERS,
  //         UNUSED_VARIETY,
  //         UNUSED_POUNDS,
  //         UNUSED_PRICE,
  //         UNUSED_DATE,
  //         UNUSED_COMMENT);

  //   assertThat(offer, is(notNullValue()));
  //   saveModel(offer);

  //   WaterfallService wservice = new WaterfallService(offer, Duration.ofSeconds(1));
  //   assertThat(wservice.getCurrentGrowers().size(), is(4));

  //   int index = 0;
  //   while(wservice.process()) {
  //     List<Grower> g = wservice.getCurrentGrowers();
  //     assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));

  //     if(index == 0 || index == 2) {
  //       OfferResponse or = offer.getGrowerOfferResponse(g.get(0).getId());
  //       or.setResponseStatus(ResponseStatus.REJECTED);
  //     } else if (index == 3) {
  //       OfferResponse or = offer.getGrowerOfferResponse(g.get(0).getId());
  //       or.setResponseStatus(ResponseStatus.ACCEPTED);
  //     } else {
  //       try {
  //         Thread.sleep(1100);
  //       } catch(InterruptedException ex) {
  //         Thread.currentThread().interrupt();
  //       }
  //     }

  //     index ++;
  //   }
  //   assertThat(wservice.getCurrentGrowers().size(), is(1));
  // }

}