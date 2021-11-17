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
	public static ObservableList generateVacTable(String iDataset, List<String> iLocations, String iStrDate) {
		
		LocalDate iDate = LocalDate.parse(iStrDate, inputFormatter);
		
		ObservableList <Map<String, Object>> data = 
				FXCollections.<Map<String, Object>>observableArrayList();
		
		for (String loc : iLocations) {
			long fullyVaccinated = 0;
			float rate = 0.0f;
			int found = 0;
			Map<String, Object> datum = new HashMap<>();
			datum.put("country", loc);
			for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
				if (rec.get("location").equals(loc)) {
					found = 1;
					LocalDate readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
					if (readDate.isEqual(iDate)) {
						String s1 = rec.get("people_fully_vaccinated");
						String s2 = rec.get("people_fully_vaccinated_per_hundred");
						if (!s1.equals("")) {
							fullyVaccinated = Long.parseLong(s1);
							datum.put("fully_vaccinated", fullyVaccinated);
						} else datum.put("fully_vaccinated", "No records");
							
						if (!s2.equals("")) {
							rate = Float.parseFloat(s2);
							datum.put("rate_of_vaccination", rate);
						} else datum.put("rate_of_vaccination", "No records");
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
					datum.put("fully_vaccinated", "No records");
					datum.put("rate_of_vaccination", "No records");
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
			LocalDate firstDate = null;
			LocalDate readDate = null;
			for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
				if (rec.get("location").equals(loc)) {
					readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
					if (found == 0) {
						found = 1; firstDate = readDate;
					}
					String s1 = rec.get("people_fully_vaccinated_per_hundred");
					if (!s1.isEmpty()) rate = Float.parseFloat(s1);
					if (!readDate.isBefore(startDate) && !readDate.isAfter(endDate)) {
						data.getData().add(new XYChart.Data(readDate.toString(), rate));
					}
				} 
				else if (found == 1) break;
			}
			while (!readDate.isAfter(endDate)) { // for dates behind period in dataset 
				if (!readDate.isBefore(startDate)) data.getData().add(new XYChart.Data(readDate.toString(), rate));
				readDate = readDate.plusDays(1);
			}
			if (endDate.isBefore(firstDate)) {
				System.out.println(firstDate.toString());
				readDate = startDate;
				rate = 0.0f; 
				while (!readDate.isAfter(endDate)) {
					data.getData().add(new XYChart.Data(readDate.toString(), rate));
					readDate = readDate.plusDays(1);
				}
			}
			allData.add(data);
		}
		return allData;
	}
	
}	