import comp3111.covid.CheckInput;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.LocalDate;


public class CheckInputTester {
    
	String dataset;
    String ISO_code;
    LocalDate localdate_late,localdate_early,localdate_inRange;
    
    @Before
    public void setup() {
        dataset = "COVID_Dataset_v1.0.csv";
        ISO_code = "AFG";
        localdate_late = LocalDate.now();
        localdate_early = LocalDate.parse("2018-11-10");
        localdate_inRange = LocalDate.parse("2020-11-10");
    }

    @Test
    public void checkDateValidation_Null() {
    	CheckInput.checkValidDate(null, dataset);
    }
    
    @Test
    public void checkDateValidation_Late() {
    	CheckInput.checkValidDate(localdate_late, dataset);
    }
    
    @Test
    public void checkDateValidation_Early() {
    	CheckInput.checkValidDate(localdate_early, dataset);
    }
    
    @Test
    public void checkDateValidation_InRange() {
    	CheckInput.checkValidDate(localdate_inRange, dataset);
    }
}