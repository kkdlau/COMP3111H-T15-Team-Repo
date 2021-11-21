import comp3111.covid.CheckInput;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.LocalDate;


public class CheckInputTester {
    
	String dataset;
    String ISO_code;
    LocalDate localdate_late_1,localdate_late_2,localdate_early_1,localdate_early_2,localdate_valid_1,localdate_valid_2;
    
    @Before
    public void setup() {
        dataset = "COVID_Dataset_v1.0.csv";
        ISO_code = "AFG";
        localdate_late_1 = LocalDate.now();
        localdate_early_1 = LocalDate.parse("2018-11-10");
        localdate_early_2 = LocalDate.parse("2017-11-10");
        localdate_valid_1 = LocalDate.parse("2020-11-10");
        localdate_valid_2 = LocalDate.parse("2020-11-13");
    }

    @Test
    public void checkDateValidation_InvalidInput_null() {
    	CheckInput.checkValidDate(null, dataset);
    }
    
    @Test
    public void checkDateValidation_InvalidInput_outrange() {
    	CheckInput.checkValidDate(localdate_late_1, dataset);
    	CheckInput.checkValidDate(localdate_late_2, dataset);
    	CheckInput.checkValidDate(localdate_early_1, dataset);
    	CheckInput.checkValidDate(localdate_early_2, dataset);
    }
    @Test
    public void checkDateValidation_ValidInput() {
    	CheckInput.checkValidDate(localdate_valid_1, dataset);
    }
    
    @Test
    public void checkPeriodValidation_ValidInput_Period() {
    	CheckInput.checkValidPeriod(localdate_valid_1,localdate_valid_2, dataset);
    }
    @Test
    public void checkPeriodValidation_ValidInput_Date() {
    	CheckInput.checkValidPeriod(localdate_valid_1,localdate_valid_1, dataset);
    }
    @Test
    public void checkPeriodValidation_InvalidInput_Outrange() {
    	CheckInput.checkValidPeriod(localdate_early_2,localdate_valid_2, dataset);
    	CheckInput.checkValidPeriod(localdate_early_2,localdate_early_1, dataset);
    	CheckInput.checkValidPeriod(localdate_early_1,localdate_late_1, dataset);
    	CheckInput.checkValidPeriod(localdate_valid_1,localdate_late_2, dataset);
    }
    @Test
    public void checkPeriodValidation_InvalidInput_EndBeforeStart() {
    	CheckInput.checkValidPeriod(localdate_valid_2,localdate_valid_1, dataset);
    	CheckInput.checkValidPeriod(localdate_late_1,localdate_early_1, dataset);
    }
    @Test
    public void checkPeriodValidation_InvalidInput_Null() {
    	CheckInput.checkValidPeriod(null,localdate_valid_1, dataset);
    	CheckInput.checkValidPeriod(localdate_valid_1,null, dataset);
    	CheckInput.checkValidPeriod(null,null, dataset);
    }
    @Test
    public void checkLocationValidation_ValidInput() {
    	CheckInput.checkValidLocations(ISO_code,dataset);
    }
    @Test
    public void checkLocationValidation_InvalidInput_noDataset() {
    	CheckInput.checkValidLocations(ISO_code,null);
    }
    @Test
    public void checkLocationValidation_InvalidInput_noISOcode() {
    	CheckInput.checkValidLocations(null,dataset);
    }
    @Test
    public void checkLocationValidation_InvalidInput_wrongISOcode() {
    	CheckInput.checkValidLocations("Wakanda",dataset);
    }
}