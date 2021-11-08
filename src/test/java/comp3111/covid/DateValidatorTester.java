package comp3111.covid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class DateValidatorTester {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "M/d/uuuu" ) ;
	LocalDate correct;
	LocalDate before;
	LocalDate after;
	@Before
	public void setUp() {
		correct = LocalDate.parse("8/1/2020", formatter);
		before = LocalDate.parse("8/1/2019", formatter);
		after = LocalDate.parse("8/1/2021", formatter);
	}
	
	@Test
	public void withinPeriodIsTrue() {
		assertTrue(DateValidator.withinPeriod(correct));
	}
	
	@Test 
	public void withinPeriodIsFalse() {
		assertFalse(DateValidator.withinPeriod(before));
	}
	
	@Test
	public void withinPeriodIsFalse2() {
		assertFalse(DateValidator.withinPeriod(after));
	}
}