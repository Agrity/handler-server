package test_helpers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.H2Platform;
import com.avaje.ebean.dbmigration.DdlGenerator;
import com.avaje.ebeaninternal.api.SpiEbeanServer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import play.Application;
import play.test.Helpers;

import java.io.IOException;

public abstract class EbeanTest {

    public static Application app;

    @BeforeClass
    public static void startApp() throws IOException {

      app = Helpers.fakeApplication(/*new GlobalSettings() {
        //@Override
        //public void onStart(Application app) {
        //  System.out.println("Starting Fake Application for Testing...");
        //}
      }*/);
      Helpers.start(app);
    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }

    @Before
    public void dropCreateDb() throws IOException {

      //String serverName = "default";

      //EbeanServer server = Ebean.getServer(serverName);

      //ServerConfig config = new ServerConfig();
      //
      //DdlGenerator ddl = new DdlGenerator();
      //ddl.setup((SpiEbeanServer) server, new H2Platform(), config);

      //// Drop
      //ddl.runScript(false, ddl.generateDropDdl());

      //// Create
      //ddl.runScript(false, ddl.generateCreateDdl());
    }
}
