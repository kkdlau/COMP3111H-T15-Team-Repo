package comp3111.covid;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.csv.*;
import edu.duke.*;

class ReportTask {
	static DateTimeFormatter datasetFormatter = DateTimeFormatter.ofPattern( "M/d/uuuu" ) ;
	static DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern( "uuuu-M-d" );
	public static Map<String, Set<String>> locByGDP = new HashMap<>();
	
	/** Generate Data for Chart 1 in Report C
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
	
	/** TODO: refactor code - such bad writing here
	 * Generate Data for Table 1 in Report C
	 * @return ObservableList
	 */
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
	
	/**
	 * Generate Data for Chart 2 in Report C
	 * @param iDataset
	 */
	public static void generateChartC2(String iDataset) {
		// human_development_index 
		// continent - to know which series to add into - consider enum to index your list 
		// people_fully_vaccinated_per_hundred - use the last date in iDataset 
		
	}
}	