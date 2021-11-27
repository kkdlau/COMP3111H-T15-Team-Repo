package comp3111.covid;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TableChartTaskTester {
	String dataset = "COVID_Dataset_v1.0.csv";
	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern( "uuuu-M-d" );
	List<String> oneISO = new ArrayList<>();
	List<String> twoISO = new ArrayList<>();
	InterestedData cases = InterestedData.ConfirmedCases;
	InterestedData deaths = InterestedData.ConfirmedDeaths;
	InterestedData vac = InterestedData.RateOfVaccination;	
	String locOneISO = "Afghanistan";
	@Before 
	public void setUp() throws Exception {
		oneISO = Arrays.asList("AFG");
		twoISO = Arrays.asList("AFG", "ALB"); // Afghanistan, Albania
	}
	@Test
	public void generateTableWithValidRecord() {
		// one country, date in valid period, new_cases 
		// output: correct record
		LocalDate validDate1 = LocalDate.parse("2020-2-24", inputFormatter);
		ObservableList output = TableChartTask.generateTable(dataset, oneISO, validDate1.toString(), cases);
		ObservableList <Map<String, Object>> predict = FXCollections.<Map<String, Object>>observableArrayList();
		Map<String, Object> datum = new HashMap<>();
		String s1 = "1", s2 = "0.026";
		datum.put("country", "Afghanistan");
		datum.put("col1data", Long.parseLong(s1));
		datum.put("col2data", Float.parseFloat(s2));
		predict.add(datum);
		assertTrue(output.equals(predict));
	}
	@Test
	public void generateTableWithEmptyRecord() {
		// dataset, one country, date in valid period, rate_of_vaccination
		// output: No records 
		LocalDate validDate1 = LocalDate.parse("2020-2-24", inputFormatter);
		ObservableList output = TableChartTask.generateTable(dataset, oneISO, validDate1.toString(), vac);
		ObservableList <Map<String, Object>> predict = FXCollections.<Map<String, Object>>observableArrayList();
		Map<String, Object> datum = new HashMap<>();
		
		datum.put("country", "Afghanistan");
		datum.put("col1data", "No records");
		datum.put("col2data", "No records");
		predict.add(datum);
		assertTrue(output.equals(predict));
	}
	@Test
	public void generateTableWithEarlyDate() {
		// dataset, one country, date before valid period, new_cases 
		// output: No records
		LocalDate earlyDate = LocalDate.parse("2019-10-1", inputFormatter);
		ObservableList output = TableChartTask.generateTable(dataset, oneISO, earlyDate.toString(), cases);
		ObservableList <Map<String, Object>> predict = FXCollections.<Map<String, Object>>observableArrayList();
		Map<String, Object> datum = new HashMap<>();
	
		datum.put("country", "Afghanistan");
		datum.put("col1data", "No records");
		datum.put("col2data", "No records");
		predict.add(datum);
		assertTrue(output.equals(predict));
	}
	@Test
	public void generateTableWithLateDate() {
		// dataset, one country, date after valid period, new_deaths
		// output: No records
		LocalDate lateDate = LocalDate.parse("2021-12-4", inputFormatter);
		ObservableList output = TableChartTask.generateTable(dataset, oneISO, lateDate.toString(), deaths);
		ObservableList <Map<String, Object>> predict = FXCollections.<Map<String, Object>>observableArrayList();
		Map<String, Object> datum = new HashMap<>();
	
		datum.put("country", "Afghanistan");
		datum.put("col1data", "No records");
		datum.put("col2data", "No records");
		predict.add(datum);
		assertTrue(output.equals(predict));
	}
	@Test
	public void generateTableWithValidRecordMultipleISO() {
		// dataset, two country, date in valid period, new_cases
		// output: correct records 
		LocalDate validDate2 = LocalDate.parse("2021-7-20", inputFormatter);
		ObservableList output = TableChartTask.generateTable(dataset, twoISO, validDate2.toString(), cases);
		ObservableList <Map<String, Object>> predict = FXCollections.<Map<String, Object>>observableArrayList();
		Map<String, Object> datum1 = new HashMap<>();
		String s1 = "142414", s2 = "3658.363";
		datum1.put("country", "Afghanistan");
		datum1.put("col1data", Long.parseLong(s1));
		datum1.put("col2data", Float.parseFloat(s2));
		predict.add(datum1);
		
		Map<String, Object> datum2 = new HashMap<>();
		s1 = "132740"; s2 = "46125.513";
		datum2.put("country", "Albania");
		datum2.put("col1data", Long.parseLong(s1));
		datum2.put("col2data", Float.parseFloat(s2));
		predict.add(datum2);
		assertTrue(output.equals(predict));
	}	

	@Test
	public void generateChartWithValidRecord() {
		// dataset, one ISO, period in date range, cases 
		// correct record 
		LocalDate start = LocalDate.parse("2020-3-2", inputFormatter);
		LocalDate end = LocalDate.parse("2020-3-3", inputFormatter);
		List<String> period = Arrays.asList(start.toString(), end.toString());
		ObservableList<XYChart.Series<String, Float>> output = TableChartTask.generateChart(dataset, oneISO, period, cases);
		XYChart.Series<String, Float> data = new XYChart.Series();
		String s1 = "0.026", s2 = "0.051";
		data.getData().add(new XYChart.Data(start.toString(), Float.parseFloat(s1)));
		//System.out.println("Testing: " + start.toString());
		data.getData().add(new XYChart.Data("2020-03-03", Float.parseFloat(s2)));
		data.setName(locOneISO);
		XYChart.Series<String, Float> temp = output.get(0);
		//System.out.println("Output " + temp.getData().get(0));
		//System.out.println("Predict " + data.getData().get(0));
		assertEquals(temp.getData().toString(), data.getData().toString());
	}
	
	@Test
	public void generateChartWithNoRecord() {
		// dataset, one ISO, period in date range, vaccination 
		LocalDate start = LocalDate.parse("2021-5-19", inputFormatter);
		LocalDate end = LocalDate.parse("2021-5-20", inputFormatter);
		List<String> period = Arrays.asList(start.toString(), end.toString());
		ObservableList<XYChart.Series<String, Float>> output = TableChartTask.generateChart(dataset, oneISO, period, vac);
		XYChart.Series<String, Float> temp = output.get(0);
		
		XYChart.Series<String, Float> data = new XYChart.Series();
		String s1 = "0.14", s2 = "0.2";
		data.getData().add(new XYChart.Data(start.toString(), Float.parseFloat(s1)));
		data.getData().add(new XYChart.Data(end.toString(), Float.parseFloat(s2)));
		data.setName(locOneISO);
		System.out.println("Output " + temp.getData().toString());
		System.out.println("Predict " + data.getData().toString());
		assertEquals(temp.getData().toString(), data.getData().toString());
	}
	
	@Test
	public void generateChartWithPeriodOutRange() {
		LocalDate start = LocalDate.parse("2021-12-1", inputFormatter);
		LocalDate end = LocalDate.parse("2021-12-1", inputFormatter);
		List<String> period = Arrays.asList(start.toString(), end.toString());
		ObservableList<XYChart.Series<String, Float>> output = TableChartTask.generateChart(dataset, oneISO, period, vac);
		XYChart.Series<String, Float> temp = output.get(0);
		
		XYChart.Series<String, Float> data = new XYChart.Series();
		String s1 = "0.56";
		data.getData().add(new XYChart.Data(start.toString(), Float.parseFloat(s1)));
		data.setName(locOneISO);
		//System.out.println("Output " + temp.toString());
		//System.out.println("Predict " + data.toString());
		assertEquals(temp.getData().toString(), data.getData().toString());
	}
	
	@Test
	public void generateChartWithTwoISO() {
		LocalDate start = LocalDate.parse("2021-7-15", inputFormatter);
		LocalDate end = LocalDate.parse("2021-7-15", inputFormatter);
		List<String> period = Arrays.asList(start.toString(), end.toString());
		ObservableList<XYChart.Series<String, Float>> output = TableChartTask.generateChart(dataset, twoISO, period, deaths);
		XYChart.Series<String, Float> temp = output.get(0);
		XYChart.Series<String, Float> temp2 = output.get(1);
		
		XYChart.Series<String, Float> data = new XYChart.Series();
		String s1 = "153.693";
		data.getData().add(new XYChart.Data(start.toString(), Float.parseFloat(s1)));
		data.setName("Afghanistan");
		assertEquals(temp.getData().toString(), data.getData().toString());
		
		XYChart.Series<String, Float> data2 = new XYChart.Series();
		s1 = "853.43";
		data2.getData().add(new XYChart.Data(start.toString(), Float.parseFloat(s1)));
		data2.setName("Albania");
		assertEquals(temp2.getData().toString(), data2.getData().toString());
	}
	
	@Test
	public void generateChartWithEarlyStart() {
		LocalDate start = LocalDate.parse("2020-2-1", inputFormatter);
		LocalDate end = LocalDate.parse("2020-2-1", inputFormatter);
		List<String> period = Arrays.asList(start.toString(), end.toString());
		ObservableList<XYChart.Series<String, Float>> output = TableChartTask.generateChart(dataset, oneISO, period, vac);
		XYChart.Series<String, Float> temp = output.get(0);
		
		XYChart.Series<String, Float> data = new XYChart.Series();
		String s1 = "";
		data.getData().add(new XYChart.Data(start.toString(), 0.0f));
		data.setName(locOneISO);
		System.out.println("Output " + temp.getData().toString());
		System.out.println("Predict " + data.getData().toString());
		assertEquals(temp.getData().toString(), data.getData().toString());
	}
}	