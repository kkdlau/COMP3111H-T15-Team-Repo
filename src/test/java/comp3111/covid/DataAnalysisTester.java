package comp3111.covid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class DataAnalysisTester {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "M/d/uuuu" ) ;
	LocalDate correct;
	LocalDate before;
	LocalDate after;
	LocalDate earliest;
	LocalDate latest;
	String dataset = "COVID_Dataset_v1.0.csv";
	@Before
	public void setUp() {
		correct = LocalDate.parse("12/1/2020", formatter); // returns date 
		before = LocalDate.parse("8/1/2019", formatter); // returns earliest date
		after = LocalDate.parse("8/1/2021", formatter); // returns latest date 
		earliest = LocalDate.parse("2/24/2020", formatter); 
		latest = LocalDate.parse("7/20/2021", formatter);
	}
	
	@Test
	public void getValidDateActualDate() {
		assertTrue(DataAnalysis.getValidDate(dataset, correct).compareTo(correct) == 0);
	}
	
	@Test
	public void getValidDateLatestDate() {
		assertTrue(DataAnalysis.getValidDate(dataset, after).compareTo(latest) == 0);
	}
}