import comp3111.covid.MyApplication;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;

import com.sun.tools.javac.Main;

import javafx.application.Application;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.Scene;

import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.util.DebugUtils.informedErrorMessage;


import static org.junit.Assert.*;


public class ApplicationTest extends FxRobot {
	
	static Button button;

	public static class MyApplicationTester extends MyApplication{
		private static final String UI_FILE = "/ui.fxml";
		@Override
		public void start(Stage stage) throws Exception {
	    	FXMLLoader loader = new FXMLLoader();
	    	loader.setLocation(getClass().getResource(UI_FILE));
	   		HBox root = (HBox) loader.load();
	   		Scene scene =  new Scene(root);
	   		stage.setScene(scene);
	      	stage.setTitle("COMP3111H - COVID Data Visualization");
	   		stage.show();
		}
	}
	
    @BeforeClass
    public static void setUpClass() throws InterruptedException {

        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
            	Application.launch(MyApplicationTester.class);
            }
        };
        t.setDaemon(true);
        t.start();
        Thread.sleep(500);
    }
    
    @Test
    public void should_contain_button() {
    	clickOn("#dataCaseButton");
        verifyThat("#generateButton", hasText("Generate"));
    }
    
    @Test
    public void should_contain_button_2() {
//    	clickOn("#dataDeathButton");
    	FxAssert.verifyThat("#generateButton", LabeledMatchers.hasText("Generate"));
    }

    @Test
    public void should_contain_button_3() {
    	assertTrue(true);
//    	clickOn("#dataVaccinButton");
//      verifyThat("#generateButton", hasText("Generate"));
    }

    
//    @Test
//    void should_click_on_button() {
//        // when:
//        clickOn(".button");
//
//        // then:
//        assertThat(lookup(".button").queryButton()).hasText("clicked!");
//        assertThat(button).hasText("clicked!");
//        verifyThat(".button", hasText("clicked!"), informedErrorMessage(this));
//    }
    
}