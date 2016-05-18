package test_helpers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.Application;
import play.test.Helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class EbeanTest {

    public static Application app;

    private static String SERVER_NAME = "default";

    private List<Model> toUnloadList = new ArrayList<>();

    @BeforeClass
    public static void startApp() throws IOException {
      app = Helpers.fakeApplication(Helpers.inMemoryDatabase(SERVER_NAME));
      Helpers.start(app);
    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }

    @After
    public void afterEachTest() {
      Ebean.deleteAll(toUnloadList);
      toUnloadList.clear();
    }

    public void saveModel(Model model) {
      Ebean.save(model);
      toUnloadList.add(model);
    }

    public void saveModels(List<? extends Model> models) {
      Ebean.saveAll(models);
      toUnloadList.addAll(models);
    }
}
