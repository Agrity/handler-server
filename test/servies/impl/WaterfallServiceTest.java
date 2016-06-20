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

import WaterfallService.java;



public class WaterfallServiceTest extends Ebean {	

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

    w

  }

}