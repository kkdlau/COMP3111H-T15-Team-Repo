package comp3111.covid;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.csv.*;
import edu.duke.*;

class VaccinationRate {
	static DateTimeFormatter datasetFormatter = DateTimeFormatter.ofPattern( "M/d/uuuu" ) ;
	static DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern( "uuuu-M-d" );
	
	/** Method 1: Generate Data for Table - Country, Fully Vaccinated, Rate of Vaccination
	 * Input: List<String> Country, String iDate
	 * Output: Something that can be put into Table View 
	 * 
	 * in csv "people_fully_vaccinated" and "people_fully_vaccinated_per_hundred"
	 * once find the correct country, need to keep the latest value seen
	 * because some countries have many empty vaccination records 
	 * 
	 * List list = new ArrayList();
	 * list.add(new TableRow("Country", "Fully Vaccinated", "Rate of Vaccination"));
	 * ...
	 * 
	 * ObservableList data = FXCollections.observableList(list);
	 * return data 
	 */
	public static ObservableList generateVacTable(String iDataset, List<String> iISOStrings, String iStrDate, InterestedData focusedData) {
		String col1target = "", col2target = "";
		switch(focusedData) {
		case ConfirmedCases:
			col1target = "total_cases";
			col2target = "total_cases_per_million";
			break;
		case ConfirmedDeaths:
			col1target = "total_deaths";
			col2target = "total_deaths_per_million";
			break;
		case RateOfVaccination:
			col1target = "people_fully_vaccinated";
			col2target = "people_fully_vaccinated_per_hundred";
			break;
		}
		LocalDate iDate = LocalDate.parse(iStrDate, inputFormatter);
		
		ObservableList <Map<String, Object>> data = 
				FXCollections.<Map<String, Object>>observableArrayList();
		
		for (String iso : iISOStrings) {
			long col1datum = 0;
			float col2datum = 0.0f;
			int found = 0;
			String loc = "";
			Map<String, Object> datum = new HashMap<>();
			//datum.put("country", loc);
			for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
				if (rec.get("iso_code").equals(iso)) {
					loc = rec.get("location");
					datum.put("country", loc);
					found = 1;
					LocalDate readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
					if (readDate.isEqual(iDate)) {
						String s1 = rec.get(col1target);
						String s2 = rec.get(col2target);
						if (!s1.equals("")) {
							col1datum = Long.parseLong(s1);
							datum.put("col1data", col1datum);
						} else datum.put("col1data", "No records");
							
						if (!s2.equals("")) {
							col2datum = Float.parseFloat(s2);
							datum.put("col2data", col2datum);
						} else datum.put("col2data", "No records");
						data.add(datum);
						break;
					}
					/**
					if (readDate.isAfter(iDate)) break;
					String s1 = rec.get("people_fully_vaccinated");
					String s2 = rec.get("people_fully_vaccinated_per_hundred");
					if (!s1.equals(""))
						fullyVaccinated = Long.parseLong(s1);
					if (!s2.equals(""))
						rate = Float.parseFloat(s2);
					**/
				}
				else if (found == 1) { // date not in range 
					datum.put("col1data", "No records");
					datum.put("col2data", "No records");
					data.add(datum);
					break;
				}
			}
			/**
			Map<String, Object> datum = new HashMap<>();
			datum.put("country", loc);
			datum.put("fully_vaccinated", fullyVaccinated);
			datum.put("rate_of_vaccination", rate);
			data.add(datum);
			**/
		}
		return data;
	}
	
	/** Method 2: Generate Data for Chart 
	 * Input: List<String> iLocations, List<String> iPeriod, String iDataset
	 * Output: people_fully_vaccinated_per_hundred
	 */
	public static ObservableList generateVacChart(List<String> iLocations, List<String> iPeriod, String iDataset) {
		LocalDate startDate = LocalDate.parse(iPeriod.get(0), inputFormatter);
		LocalDate endDate = LocalDate.parse(iPeriod.get(1), inputFormatter);
		ObservableList<XYChart.Series<String, Float>> allData = 
				FXCollections.<XYChart.Series<String, Float>>observableArrayList();
		
		for (String loc : iLocations) {
			XYChart.Series<String, Float> data = new XYChart.Series();
			data.setName(loc);
			float rate = 0.0f; 
			int found = 0;
			LocalDate readDate = null;
			LocalDate temp = null;
			for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
				if (rec.get("location").equals(loc)) {
					readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
					if (found == 0) {
						found = 1;
						if (startDate.isBefore(readDate)) {
							temp = startDate;
							rate = 0.0f; 
							while (!temp.isAfter(endDate) && temp.isBefore(readDate)) {
								data.getData().add(new XYChart.Data(temp.toString(), rate));
								temp = temp.plusDays(1);
							}
						}
					}
					String s1 = rec.get("people_fully_vaccinated_per_hundred");
					if (!s1.isEmpty()) rate = Float.parseFloat(s1);
					if (!readDate.isBefore(startDate) && !readDate.isAfter(endDate)) {
						data.getData().add(new XYChart.Data(readDate.toString(), rate));
					}
				} 
				else if (found == 1) break;
			}
			temp = LocalDate.now();
			while (!readDate.isAfter(endDate) && !readDate.isAfter(temp)) { // for dates behind period in dataset 
				if (!readDate.isBefore(startDate)) data.getData().add(new XYChart.Data(readDate.toString(), rate));
				readDate = readDate.plusDays(1);
			}
			allData.add(data);
		}
		return allData;
	}
	
}	