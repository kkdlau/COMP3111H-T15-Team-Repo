package comp3111.covid;

import java.util.*;
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
	public static Map<String, Set<String>> locByGDP = new HashMap<>();
	
	/** Method 1: Generate Data for Table in Task 1
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
	public static ObservableList generateTable(String iDataset, List<String> iISOStrings, String iStrDate, InterestedData focusedData) {
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
				} else if (found == 1) { // date not in range 
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
	
	/** Method 2: Generate Data for Chart in Task 2
	 * Input: List<String> iLocations, List<String> iPeriod, String iDataset
	 * Output: people_fully_vaccinated_per_hundred
	 */
	public static ObservableList generateChart(String iDataset, List<String> iISOStrings, List<String> iPeriod, InterestedData focusedData) {
		String dataTarget = "";
		switch (focusedData) {
		case ConfirmedCases:
			dataTarget = "total_cases_per_million"; break;
		case ConfirmedDeaths:
			dataTarget = "total_deaths_per_million"; break;
		case RateOfVaccination:
			dataTarget = "people_fully_vaccinated_per_hundred"; break;
		}
		LocalDate startDate = LocalDate.parse(iPeriod.get(0), inputFormatter);
		LocalDate endDate = LocalDate.parse(iPeriod.get(1), inputFormatter);
		ObservableList<XYChart.Series<String, Float>> allData = 
				FXCollections.<XYChart.Series<String, Float>>observableArrayList();
		
		for (String iso : iISOStrings) {
			XYChart.Series<String, Float> data = new XYChart.Series();
			String loc = "";
			float rate = 0.0f; 
			int found = 0;
			LocalDate readDate = null;
			LocalDate temp = null;
			for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
				if (rec.get("iso_code").equals(iso)) {
					readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
					if (found == 0) {
						found = 1;
						loc = rec.get("location");
						if (startDate.isBefore(readDate)) {
							temp = startDate;
							rate = 0.0f; 
							while (!temp.isAfter(endDate) && temp.isBefore(readDate)) {
								data.getData().add(new XYChart.Data(temp.toString(), rate));
								temp = temp.plusDays(1);
							}
						}
					}
					String s1 = rec.get(dataTarget);
					if (!s1.isEmpty()) rate = Float.parseFloat(s1);
					if (!readDate.isBefore(startDate) && !readDate.isAfter(endDate)) {
						data.getData().add(new XYChart.Data(readDate.toString(), rate));
					}
				} else if (found == 1) break;
			}
			temp = LocalDate.now();
			while (readDate != null && !readDate.isAfter(endDate) && !readDate.isAfter(temp)) { // for dates behind period in dataset 
				if (!readDate.isBefore(startDate)) data.getData().add(new XYChart.Data(readDate.toString(), rate));
				readDate = readDate.plusDays(1);
			}
			data.setName(loc);
			allData.add(data);
		}
		return allData;
	}
	
	/** Method 3: Generate Data for Chart 1 in Report C
	 * Input: String iDataset
	 * Output: ObservableList chartData
	 */
	public static ObservableList generateChartC1(String iDataset) {
		Float[] gdp_quartile = DataAnalysis.getQuartiles(iDataset, "gdp_per_capita");
		locByGDP.put("q1", new LinkedHashSet<String>());
		locByGDP.put("q2", new LinkedHashSet<String>());
		locByGDP.put("q3", new LinkedHashSet<String>());
		locByGDP.put("q4", new LinkedHashSet<String>());
		
		
		// four maps - Map<String, Float> date in string format & total_vaccinations_per_hundred
		Map<LocalDate, Float> q1 = new TreeMap<>(), q2 = new TreeMap<>(), q3 = new TreeMap<>(), q4 = new TreeMap<>();
		String prevISO = "";
		String ISO = "", s1 = "";
		LocalDate earliest = LocalDate.parse("12/1/2020", datasetFormatter);
		LocalDate date = null;
		float numDoses = 0.0f;
		float gdp = 0.0f;
		int valid = 1;
		for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
			date = LocalDate.parse(rec.get("date"), datasetFormatter);
			if (date.isBefore(earliest)) continue; 
			ISO = rec.get("iso_code");
			if (!ISO.equals(prevISO)) { // new ISO record 
				s1 = rec.get("gdp_per_capita");
				if (s1.isEmpty()) { // no GDP data
					valid = 0;
					continue; 
				} else {
					numDoses = 0.0f; // reset to zero 
					gdp = Float.parseFloat(s1);
					valid = 1;
				}
			}
			else if (valid == 0) continue;
			s1 = rec.get("total_vaccinations_per_hundred");
			if (!s1.isEmpty()) 
				numDoses = Float.parseFloat(s1);
			
			if (gdp < gdp_quartile[0]) { // first quartile 
				if (q1.containsKey(date)) q1.put(date, q1.get(date) + numDoses);
				else q1.put(date, numDoses);
				locByGDP.get("q1").add(rec.get("location"));
			} else if (gdp < gdp_quartile[1]) {
				if (q2.containsKey(date)) q2.put(date, q2.get(date) + numDoses);
				else q2.put(date, numDoses);				
				locByGDP.get("q2").add(rec.get("location"));
			} else if (gdp < gdp_quartile[2]) {
				if (q3.containsKey(date)) q3.put(date, q3.get(date) + numDoses);
				else q3.put(date, numDoses);
				locByGDP.get("q3").add(rec.get("location"));
			} else {
				if (q4.containsKey(date)) q4.put(date, q4.get(date) + numDoses);
				else q4.put(date, numDoses);
				locByGDP.get("q4").add(rec.get("location"));
			}
		}
		// form the series from the Map
		float prev = 0.0f;
		
		ObservableList<XYChart.Series<String, Float>> allData = 
				FXCollections.<XYChart.Series<String, Float>>observableArrayList();
		// first quartile 
		XYChart.Series<String, Float> q1data = new XYChart.Series();
		int numLoc = locByGDP.get("q1").size();
		for (Map.Entry<LocalDate, Float> mapElement : q1.entrySet()) {
			prev = (mapElement.getValue() / numLoc > prev)? mapElement.getValue() / numLoc : prev;
            q1data.getData().add(new XYChart.Data(mapElement.getKey().toString(), prev));
        }
		q1data.setName("First quartile");
		
		// 2nd quartile 
		prev = 0.0f;
		XYChart.Series<String, Float> q2data = new XYChart.Series();
		numLoc = locByGDP.get("q2").size();
		for (Map.Entry<LocalDate, Float> mapElement : q2.entrySet()) {
			prev = (mapElement.getValue() / numLoc >= prev)? mapElement.getValue() / numLoc : prev;
            q2data.getData().add(new XYChart.Data(mapElement.getKey().toString(), prev));
        }
		q2data.setName("Second quartile");
		
		// 3rd quartile 
		prev = 0.0f;
		XYChart.Series<String, Float> q3data = new XYChart.Series();
		numLoc = locByGDP.get("q3").size();
		for (Map.Entry<LocalDate, Float> mapElement : q3.entrySet()) {
			prev = (mapElement.getValue() / numLoc > prev)? mapElement.getValue() / numLoc : prev;
            q3data.getData().add(new XYChart.Data(mapElement.getKey().toString(), prev));
        }
		q3data.setName("Third quartile");
		
		// 4th quartile 
		prev = 0.0f;
		XYChart.Series<String, Float> q4data = new XYChart.Series();
		numLoc = locByGDP.get("q4").size();
		for (Map.Entry<LocalDate, Float> mapElement : q4.entrySet()) {
			prev = (mapElement.getValue() / numLoc > prev)? mapElement.getValue() / numLoc : prev;
            q4data.getData().add(new XYChart.Data(mapElement.getKey().toString(), prev));
        }
		q4data.setName("Fourth quartile");
		
		allData.addAll(q4data, q3data, q2data, q1data);
		return allData;
	}
	
	public static ObservableList generateTableC1() {
		String[] quartiles = new String[] {"q1", "q2", "q3", "q4"};
		try {
			ObservableList<Map<String, String>> tableData = FXCollections.<Map<String, String>>observableArrayList();
			boolean add = true; int index = 0;
			Map<String, String[]> temp = new HashMap<>();
			for (String q : quartiles) {
				String[] locs = new String[locByGDP.get(q).size()];
				locByGDP.get(q).toArray(locs);
				temp.put(q, locs);
			}
			// 4 lists of different length 
			while (add) {
				Map<String, String> datum = new HashMap<>();
				add = false;
				for (String q : quartiles) {
					if (index < locByGDP.get(q).size()) {
						datum.put(q, temp.get(q)[index]); add = true;
					}
				}
				index += 1;
				if (add) tableData.add(datum);
			}
			return tableData;
		} catch (Exception e){
			System.out.println(e);
			return null;
		}
	}
}	