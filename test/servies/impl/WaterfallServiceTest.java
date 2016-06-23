package servies.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Duration;
import java.time.Month;
import java.util.List;
import java.lang.Thread;

import models.Almond.AlmondVariety;
import models.Grower;
import models.Handler;
import models.Offer;
import services.offer_management.WaterfallService;
import test_helpers.EbeanTest;

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

    while(wservice.getGrowersInLine().size() > 0) {
      try {
        Thread.sleep(10);
      } catch(InterruptedException ex) {
        Thread.currentThread().interrupt();
      }

    }
    assertThat(wservice.getGrowersInLine().size(), is(0));
  }

  @Test
  public void testAllRejected() {
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

    WaterfallService wservice = new WaterfallService(offer, Duration.ofMillis(100));

    int index = 0;
    while(wservice.getGrowersInLine().size() > 0) {
      List<Grower> g = wservice.getGrowersInLine();
      assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
      wservice.reject(wservice.getGrowersInLine().get(0).getId());
      index++;
    }
    assertThat(wservice.getGrowersInLine().size(), is(0));
  
  }

  @Test
  public void testFirstAccepted() {
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
    while(wservice.getGrowersInLine().size() > 0) {
      List<Grower> g = wservice.getGrowersInLine();
      assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
      wservice.accept(offer.getAlmondPounds(), UNUSED_GROWERS.get(0).getId()); break;
    }
    assertThat(wservice.getGrowersInLine().size(), is(4));

  }

  @Test
  public void test2Reject3rdAccepts() {
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
    assertThat(wservice.getGrowersInLine().size(), is(4));

    int index = 0;
    while(wservice.getGrowersInLine().size() > 0) {
      List<Grower> g = wservice.getGrowersInLine();
      assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
      
      if(index == 2) {
        wservice.accept(offer.getAlmondPounds(), UNUSED_GROWERS.get(2).getId());
        break;
      } else if (index < 2) {
        wservice.reject(wservice.getGrowersInLine().get(0).getId());
      }
      
      index++;
    }
    assertThat(wservice.getGrowersInLine().size(), is(2));
  }

  @Test
  public void test3rdAccepts() {
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

    WaterfallService wservice = new WaterfallService(offer, Duration.ofSeconds(1));
    assertThat(wservice.getGrowersInLine().size(), is(4));

    int index = 0;
    while(wservice.getGrowersInLine().size() > 0) {
      List<Grower> g = wservice.getGrowersInLine();
      assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));
      
      if(index == 2) {
        wservice.accept(offer.getAlmondPounds(), 	UNUSED_GROWERS.get(2).getId());
        break;
      } else {
        try {
          Thread.sleep(1100);
        } catch(InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }
      
      index++;
    }
    assertThat(wservice.getGrowersInLine().size(), is(2));
  }

  /* First and third grower reject, second has no response, and final grower accepts. */
  @Test
  public void testMixed() {
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

    WaterfallService wservice = new WaterfallService(offer, Duration.ofSeconds(1));
    assertThat(wservice.getGrowersInLine().size(), is(4));

    int index = 0;
    while(wservice.getGrowersInLine().size() > 0) {
      List<Grower> g = wservice.getGrowersInLine();
      assertThat(g.get(0), is(equalTo(UNUSED_GROWERS.get(index))));

      if(index == 0) {
        wservice.reject(UNUSED_GROWERS.get(0).getId());
        assertThat(wservice.getGrowersInLine().size(), is(3));
        assertThat(offer.getOfferCurrentlyOpen(), is(true));
      }
      
      if(index == 1) {
      	wservice.reject(UNUSED_GROWERS.get(1).getId()); 
      	assertThat(wservice.getGrowersInLine().size(), is(2));
      	assertThat(offer.getOfferCurrentlyOpen(), is(true));
      }
      
      if (index == 2) {
      	try {
          Thread.sleep(1100);
        } catch(InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      	assertThat(wservice.getGrowersInLine().size(), is(1));
      	assertThat(offer.getOfferCurrentlyOpen(), is(true));
      }
      	
      if (index == 3) {
      	wservice.accept(offer.getAlmondPounds(), UNUSED_GROWERS.get(3).getId()); 
      	assertThat(wservice.getGrowersInLine().size(), is(1));
      	assertThat(offer.getOfferCurrentlyOpen(), is(false));
      	break;
      }
      
      index ++;
    }
  }
  
  @Test
  public void testFirstRejects_SecondAcceptsHalf_ThirdExpires_FourthAcceptsRemaining() {
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
    
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    //Grower 1 Declines
    assertThat(wservice.getGrowersInLine().size(), is(4));
    wservice.reject(wservice.getGrowersInLine().get(0).getId());
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    //Grower 2 Accepts Half
    assertThat(wservice.getGrowersInLine().size(), is(3));
    wservice.accept(offer.getAlmondPounds() / 2, wservice.getGrowersInLine().get(0).getId());
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    //Grower 3 Expires
    assertThat(wservice.getGrowersInLine().size(), is(2));
    try {
      Thread.sleep(1100);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    //Grower 4 Accepts Remaining
    assertThat(wservice.getGrowersInLine().size(), is(1));
    wservice.accept(offer.getAlmondPounds() - (offer.getAlmondPounds() / 2), wservice.getGrowersInLine().get(0).getId());
    assertThat(offer.getOfferCurrentlyOpen(), is(false));
  }
  
  @Test
  public void testFirstAcceptsHalf_SecondRejects_ThirdAcceptsRemaining() {
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
    
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    //Grower 1 Accepts Half
    assertThat(wservice.getGrowersInLine().size(), is(4));
    wservice.accept(offer.getAlmondPounds() / 2, wservice.getGrowersInLine().get(0).getId());
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    //Grower 2 Declines
    assertThat(wservice.getGrowersInLine().size(), is(3));
    wservice.reject(wservice.getGrowersInLine().get(0).getId());
    assertThat(offer.getOfferCurrentlyOpen(), is(true));
    
    //Grower 3 Accepts Remaining 
    assertThat(wservice.getGrowersInLine().size(), is(2));
    wservice.accept(offer.getAlmondPounds() - (offer.getAlmondPounds() / 2), wservice.getGrowersInLine().get(0).getId());
    assertThat(offer.getOfferCurrentlyOpen(), is(false));
    
    //Grower 4 Never Contacted
  }
  
  

}