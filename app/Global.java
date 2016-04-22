import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import play.Application;
import play.GlobalSettings;

import services.messaging.offer.OfferEmailMessageService;
import services.messaging.offer.OfferMessageService;

public class Global extends GlobalSettings {

    private Injector injector;

    @Override
    public void onStart(Application application) {
      super.onStart(application);

      injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            //bind(OfferMessageService.class).to(OfferEmailMessageService.class);
          }
      });
    }

    public Injector getInjector() {
      return injector;
    }
}
