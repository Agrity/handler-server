package servies.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableList;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import models.Almond.AlmondVariety;
import models.Grower;
import models.Handler;
import models.Offer;
import models.Offer.OfferStatus;
import services.offer_management.FCFSService;
import test_helpers.EbeanTest;

public class FCFSServiceTest extends EbeanTest {  
  private static final String UNUSED_EMAIL_ADDRESS = "unused@domain.com";
  private static final String UNUSED_PASSWORD = "dummy_password"; 
  private static final Handler UNUSED_HANDLER = new Handler("Test Company", UNUSED_EMAIL_ADDRESS, UNUSED_PASSWORD);
  private static final List<Grower> UNUSED_GROWERS = new ImmutableList.Builder<Grower>()
    .add(new Grower(UNUSED_HANDLER, "F1", "L1"))
    .add(new Grower(UNUSED_HANDLER, "F2", "L2"))
    .add(new Grower(UNUSED_HANDLER, "F3", "L3"))
    .add(new Grower(UNUSED_HANDLER, "F4", "L4"))
    .build();

  private static final AlmondVariety UNUSED_VARIETY = AlmondVariety.NONPAREIL;
  private static final String UNUSED_SIZE = "23/25";
  private static final Integer UNUSED_POUNDS = 44_000;
  private static final String UNUSED_PRICE = "$2.66";
  private static final LocalDate UNUSED_DATE = LocalDate.of(2015, Month.JANUARY, 1);
  private static final LocalDate UNUSED_DATE2 = LocalDate.of(2016, Month.JANUARY, 1);
  private static final String UNUSED_COMMENT = "Test Comment.";
  private static final String UNUSED_MANAGEMENT_TYPE = "FCFSService";

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
          UNUSED_SIZE,
          UNUSED_POUNDS,
          UNUSED_PRICE,
          UNUSED_DATE,
          UNUSED_DATE2,
          UNUSED_COMMENT,
          UNUSED_MANAGEMENT_TYPE);

    assertThat(offer, is(notNullValue()));
    saveModel(offer);

    FCFSService service = new FCFSService(offer, Duration.ofMillis(1000));
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    try {
      Thread.sleep(1500);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    
    assertThat(offer.getOfferCurrentlyOpen(), is(false)); 
   
  }

  @Test
  public void testAllReject() {
    Offer offer
      = new Offer(
          UNUSED_HANDLER,
          UNUSED_GROWERS,
          UNUSED_VARIETY,
          UNUSED_SIZE,
          UNUSED_POUNDS,
          UNUSED_PRICE,
          UNUSED_DATE,
          UNUSED_DATE2,
          UNUSED_COMMENT,
          UNUSED_MANAGEMENT_TYPE);

    assertThat(offer, is(notNullValue()));
    saveModel(offer);

    FCFSService service = new FCFSService(offer, Duration.ofMillis(5000));
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    for(Grower g : UNUSED_GROWERS) {
      service.reject(g.getId());
    }  
    assertThat(offer.getOfferCurrentlyOpen(), is(false)); 
   
  }

  @Test
  public void testOneAcceptPartial() {
    Offer offer
      = new Offer(
          UNUSED_HANDLER,
          UNUSED_GROWERS,
          UNUSED_VARIETY,
          UNUSED_SIZE,
          UNUSED_POUNDS,
          UNUSED_PRICE,
          UNUSED_DATE,
          UNUSED_DATE2,
          UNUSED_COMMENT,
          UNUSED_MANAGEMENT_TYPE);

    assertThat(offer, is(notNullValue()));
    saveModel(offer);

    FCFSService service = new FCFSService(offer, Duration.ofMillis(5000));
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    for(int i = 0; i < UNUSED_GROWERS.size(); i++) {
      if(i == 0) {
        service.accept(offer.getAlmondPounds() / 2, UNUSED_GROWERS.get(i).getId());
        assertThat(offer.getOfferCurrentlyOpen(), is(true));
      } else {
        service.reject(UNUSED_GROWERS.get(i).getId());
      }
    }
    
    assertThat(offer.getOfferCurrentlyOpen(), is(false)); 
   
  }
  
  @Test
  public void testAcceptedInFull() {
    Offer offer
      = new Offer(
          UNUSED_HANDLER,
          UNUSED_GROWERS,
          UNUSED_VARIETY,
          UNUSED_SIZE,
          UNUSED_POUNDS,
          UNUSED_PRICE,
          UNUSED_DATE,
          UNUSED_DATE2,
          UNUSED_COMMENT, 
          UNUSED_MANAGEMENT_TYPE);

    assertThat(offer, is(notNullValue()));
    saveModel(offer);

    FCFSService service = new FCFSService(offer, Duration.ofMillis(10000));
    
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    service.accept(offer.getAlmondPounds(), UNUSED_GROWERS.get(0).getId());
    assertThat(offer.getOfferCurrentlyOpen(), is(false)); 
  }
  
  @Test 
  public void testAcceptedInHalves() { 
     Offer offer
     = new Offer(
         UNUSED_HANDLER,
         UNUSED_GROWERS,
         UNUSED_VARIETY,
         UNUSED_SIZE,
         UNUSED_POUNDS,
         UNUSED_PRICE,
         UNUSED_DATE,
         UNUSED_DATE2,
         UNUSED_COMMENT, 
         UNUSED_MANAGEMENT_TYPE);

   assertThat(offer, is(notNullValue()));
   saveModel(offer);

   FCFSService service = new FCFSService(offer, Duration.ofMillis(10000));
   
   assertThat(offer.getOfferCurrentlyOpen(), is((true)));
   service.accept(offer.getAlmondPounds() / 2, UNUSED_GROWERS.get(0).getId());
   assertThat(offer.getOfferCurrentlyOpen(), is((true)));
   service.accept(offer.getAlmondPounds() - (offer.getAlmondPounds()/2), UNUSED_GROWERS.get(1).getId());
   assertThat(offer.getOfferCurrentlyOpen(), is(false));
 }
  
  @Test 
  public void testHalfAcceptedThenExpired() { 
     Offer offer
     = new Offer(
         UNUSED_HANDLER,
         UNUSED_GROWERS,
         UNUSED_VARIETY,
         UNUSED_SIZE,
         UNUSED_POUNDS,
         UNUSED_PRICE,
         UNUSED_DATE,
         UNUSED_DATE2,
         UNUSED_COMMENT, 
         UNUSED_MANAGEMENT_TYPE);

   assertThat(offer, is(notNullValue()));
   saveModel(offer);

   FCFSService service = new FCFSService(offer, Duration.ofMillis(1000));
   
   assertThat(offer.getOfferCurrentlyOpen(), is((true)));
   service.accept(offer.getAlmondPounds() / 2, UNUSED_GROWERS.get(0).getId());
   assertThat(offer.getOfferCurrentlyOpen(), is(true));
   try {
     Thread.sleep(1500);
   } catch(InterruptedException ex) {
     Thread.currentThread().interrupt();
   }
   assertThat(offer.getOfferCurrentlyOpen(), is(false));
 }
}

