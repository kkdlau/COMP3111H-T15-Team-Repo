package comp3111.covid;

import comp3111.covid.CheckInput;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.LocalDate;

public class CheckInputTest{
	
	String dataset;
	String ISO_code;
	LocalDate[] datelist = new LocalDate[6];
	
	@Before
	public void setup() {
		dataset = "COVID_Dataset_V1.0.csv";
		ISO_code = "AFG";
		datelist[0] = LocalDate.parse("2017-11-10"); //Too Early
		datelist[1] = LocalDate.parse("2018-11-10"); //Too Early
		datelist[2] = LocalDate.parse("2020-10-10"); //Valid
		datelist[3] = LocalDate.parse("2020-12-12"); //Valid
		datelist[4] = LocalDate.parse("2021-10-10"); //Too Late
		datelist[5] = LocalDate.now(); //Too Late
	}
	@Test
	public void DateValidationNull() {
		try {
		CheckInput.checkValidDate(null, dataset);

		} catch (Exception e) {

		}
	}
	@Test
	public void DateValidationOutRange() {
		CheckInput.checkValidDate(datelist[0], dataset);
		CheckInput.checkValidDate(datelist[5], dataset);
	}
	@Test
	public void DateValidationValid() {
		CheckInput.checkValidDate(datelist[2], dataset);
		CheckInput.checkValidDate(datelist[3], dataset);
	}
	@Test
	public void PeriodValidationNull() {
		CheckInput.checkValidPeriod(null, null, dataset);
		CheckInput.checkValidPeriod(datelist[0], null, dataset);
		CheckInput.checkValidPeriod(null, datelist[2], dataset);
		CheckInput.checkValidPeriod(null, datelist[5], dataset);
	}
	@Test
	public void PeriodValidationOutRange() {
		CheckInput.checkValidPeriod(datelist[0], datelist[1], dataset);
		CheckInput.checkValidPeriod(datelist[4], datelist[5], dataset);
	}
	@Test
	public void PeriodValidationSwapped() {
		CheckInput.checkValidPeriod(datelist[3], datelist[2], dataset);
	}
	@Test
	public void PeriodValidationValid() {
		CheckInput.checkValidPeriod(datelist[2], datelist[3], dataset);
	}
	@Test
	public void PeriodValidationDay() {
		CheckInput.checkValidPeriod(datelist[2], datelist[2], dataset);
	}
    @Test
    public void LocationValidationNull() {
    	CheckInput.checkValidLocations(ISO_code,null);
    }
    @Test
    public void LocationValidationNoISOcode() {
try {
	CheckInput.checkValidLocations(null,dataset);
	CheckInput.checkValidLocations("",dataset);
} catch (Exception e) {

}
    }
    @Test
    public void LocationValidationWrongISOcode() {
    	CheckInput.checkValidLocations("Wakanda",dataset);
    	CheckInput.checkValidLocations("@f3qtojqpojmh",dataset);
    }
    @Test
    public void LocationValidationValid() {
    	CheckInput.checkValidLocations(ISO_code,dataset);
    }
	
}