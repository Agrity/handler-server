package servies.impl;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import models.Almond.AlmondVariety;
import models.Grower;
import models.Handler;
import models.Offer;

import services.OfferService;
import services.impl.EbeanOfferService;

import test_helpers.EbeanTest;

import static org.mockito.Mockito.*;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableList;


/**
*
* EbeanOfferService JUnit Test.
*
*/
public class OfferServiceTest extends EbeanTest {

  private static final Handler HANDLER_1 = new Handler("Test Company 1");
  private static final Handler HANDLER_2 = new Handler("Test Company 2");

  private static final Grower GROWER_1 = new Grower(HANDLER_2, "F1", "L1");
  private static final Grower GROWER_2 = new Grower(HANDLER_1, "F2", "L2");

  private static final List<Grower> GROWER_LIST = new ArrayList<Grower>() {{
    add(GROWER_1);
    add(GROWER_2);
  }};

  private static final AlmondVariety UNUSED_VARIETY = AlmondVariety.NONPAREIL;
  private static final Integer UNUSED_POUNDS = 44_000;
  private static final String UNUSED_PRICE = "$2.66";
  private static final LocalDate UNUSED_DATE = LocalDate.of(2015, Month.JANUARY, 1);
  private static final String UNUSED_COMMENT = "Test Comment.";

  // TODO Change to Injected.
  private static final OfferService offerService = new EbeanOfferService();

  @BeforeClass
  public static void loadInitialData() {
    Ebean.save(HANDLER_1);
    Ebean.save(HANDLER_2);
    Ebean.saveAll(GROWER_LIST);
  }

  @Test
  public void testGetAllOffers() {
    Offer offer1
        = new Offer(
            HANDLER_1,
            GROWER_LIST,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    Offer offer2
        = new Offer(
            HANDLER_2,
            GROWER_LIST,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer1);
    saveModel(offer2);

    List<Offer> allOffers = offerService.getAll();

    // Check that both offers returned
    //assertThat(allOffers.size(), is(equalTo(2)));

    // Get IDs to verify correct orders returned.
    List<Long> offerIds = allOffers.stream()
      .map(offer -> offer.getId())
      .distinct()
      .collect(Collectors.toList());

    assertThat(offerIds.size(), is(equalTo(2)));

    // Check correct IDs.
    assertThat(offerIds.get(0), anyOf(
        is(equalTo(offer1.getId())),
        is(equalTo(offer2.getId()))));

    assertThat(offerIds.get(1), anyOf(
        is(equalTo(offer1.getId())),
        is(equalTo(offer2.getId()))));
  }

  @Test
  public void testGetById() {
    Offer offer
        = new Offer(
            HANDLER_1,
            GROWER_LIST,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer);

    Offer offerResult = offerService.getById(offer.getId());

    assertThat(offerResult, is(equalTo(offer)));
  }

  @Test
  public void testGetByGrower() {
    List<Grower> offer1Growers = new ArrayList<Grower>(){{
      add(GROWER_1);
    }};

    List<Grower> offer2Growers = new ArrayList<Grower>(){{
      add(GROWER_2);
    }};

    saveModels(offer1Growers);
    saveModels(offer2Growers);

    Offer offer1
        = new Offer(
            HANDLER_1,
            offer1Growers,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    Offer offer2
        = new Offer(
            HANDLER_1,
            offer2Growers,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    // Should not be returned in any searches.
    Offer unusedOffer
        = new Offer(
            HANDLER_1,
            GROWER_LIST,
            UNUSED_VARIETY,
            UNUSED_POUNDS,
            UNUSED_PRICE,
            UNUSED_DATE,
            UNUSED_COMMENT);

    saveModel(offer1);
    saveModel(offer2);
    saveModel(unusedOffer);

    List<Offer> offer1Result = offerService.getByGrower(GROWER_1.getId());

    assertThat(offer1Result.size(), is(equalTo(1)));
    assertThat(offer1Result.get(0), is(equalTo(offer1)));

    List<Offer> offer2Result = offerService.getByGrower(GROWER_2.getId());
    assertThat(offer2Result.size(), is(equalTo(1)));
    assertThat(offer2Result.get(0), is(equalTo(offer2)));
  }
}
