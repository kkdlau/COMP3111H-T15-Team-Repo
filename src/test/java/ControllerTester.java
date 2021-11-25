import comp3111.covid.Controller;
import comp3111.covid.MyApplication;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;


public class ControllerTester {

	Controller controller;
	
    @Before
    public void setup() {
//    	controller = new Controller();
//    	controller.initialize();
    }
    
    @Test
    public void checkConstructController() {
    	controller = new Controller();
    }
    
    @Test
    public void checkInitializeController() {
    	controller.initialize();
    }
    
    @Test
    public void checkUpdateUIDataModel() {
    	controller.updateUIDataModel();
    	
    }
}