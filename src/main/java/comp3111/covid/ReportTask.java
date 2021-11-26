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
	static String[] quartiles = {"Quartile 1", "Quartile 2", "Quartile 3", "Quartile 4"};
	public static Map<String, Set<String>> locByGDP = new HashMap<>();
	
	/** TODO: refactor code - such bad writing here
	 *  Generate Data for Chart 1 in Report C
	 * Input: String iDataset
	 * Output: ObservableList chartData
	 */
	public static ObservableList generateChartC1(String iDataset) {
		Float[] gdpQuartile = DataAnalysis.getQuartiles(iDataset, "gdp_per_capita");
		
		List<Map<LocalDate, Float>> dateVacMap = new ArrayList<>();
		for (int i = 0; i < 4; ++i) {
			locByGDP.put(quartiles[i], new LinkedHashSet<String>());
			dateVacMap.add(new TreeMap<>());
		}
		
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
			
			for (int i = 0; i < 4; ++i) {
				if (gdp < gdpQuartile[i]) {
					if (dateVacMap.get(i).containsKey(date))
						dateVacMap.get(i).put(date, dateVacMap.get(i).get(date) + numDoses);
					else 
						dateVacMap.get(i).put(date, numDoses);
					locByGDP.get(quartiles[i]).add(rec.get("location"));
					break;
				}
			}
		}
		// form the series from the Map
		ObservableList<XYChart.Series<String, Float>> allData = 
				FXCollections.<XYChart.Series<String, Float>>observableArrayList();
		for (int i = 0; i < 4; ++i) {
			float prev = 0.0f;
			XYChart.Series<String, Float> data = new XYChart.Series();
			int numLoc = locByGDP.get(quartiles[i]).size();
			for (Map.Entry<LocalDate, Float> mapElement : dateVacMap.get(i).entrySet()) {
				prev = (mapElement.getValue() / numLoc > prev)? mapElement.getValue() / numLoc : prev;
				data.getData().add(new XYChart.Data(mapElement.getKey().toString(), prev));
			}
			data.setName(quartiles[i]);
			allData.add(data);
		}
		return allData;
	}
	
	/** 
	 * Generate Data for Table 1 in Report C
	 * @return ObservableList
	 */
	public static ObservableList generateTableC1() {
		try {
			ObservableList<Map<String, String>> tableData = FXCollections.<Map<String, String>>observableArrayList();
			boolean add = true; int index = 0;
			Map<String, String[]> temp = new HashMap<>();
			for (String q : quartiles) {
				String[] locs = new String[locByGDP.get(q).size()];
				locByGDP.get(q).toArray(locs);
				temp.put(q, locs);
				System.out.println(locs[0]);
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
	public static ObservableList generateChartC2(String iDataset, LocalDate lastDate) {
		// human_development_index 
		// continent - to know which series to add into - consider enum
		// people_fully_vaccinated_per_hundred - use the last date in iDataset 
		String[] continents = {"Africa", "Asia", "Europe", "North America", "South America", "Oceania"};
		
		List<XYChart.Series> data = new ArrayList<>();
	
		for (int i = 0; i < continents.length; ++i) {
			data.add(new XYChart.Series<>());
			data.get(i).setName(continents[i]);
		}
		// record for one country 
		String prev = "";
		float hdi = 0.0f; // human_development_index
		float vac_rate = 0.0f; // people_fully_vaccinated_per_hundred
		String continent = "";
		String temp = "";
		LocalDate readDate = null;
		for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
			continent = rec.get("continent");
			temp = rec.get("human_development_index");
			if (temp.isEmpty() || continent.isEmpty()) 
				continue;
			hdi = Float.parseFloat(temp);
			temp = rec.get("iso_code");
			if (!temp.equals(prev)) {
				vac_rate = 0.0f; // reset values 	
				prev = temp;
			}
			temp = rec.get("people_fully_vaccinated_per_hundred");
			if (!temp.isEmpty()) 
				vac_rate = Float.parseFloat(temp);
			readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
			if (readDate.equals(lastDate)) {
				int index = StringIndex.ContinentIndex(continent);
				data.get(index).getData().add(new XYChart.Data(hdi, vac_rate));

			}
		}
		// debugging
		System.out.println("Gave data to input");
		ObservableList<XYChart.Series> allData = FXCollections.<XYChart.Series>observableArrayList();
		for (int i = 0; i < continents.length; ++i) {
			allData.add(data.get(i));
		}
		return allData;
	}
	
	/**
	 * Generate Data for Chart 3 in Report C
	 * @param iDataset
	 * @param iISO
	 * @param iPeriod
	 * @return ObservableList
	 */
	public static ObservableList generateChartC3(String iDataset, String iISO, List<String> iPeriod) {
		LocalDate startDate = LocalDate.parse(iPeriod.get(0), inputFormatter);
		LocalDate endDate = LocalDate.parse(iPeriod.get(1), inputFormatter);
		ObservableList<XYChart.Series<String, Float>> allData = 
				FXCollections.<XYChart.Series<String, Float>>observableArrayList();

		XYChart.Series<String, Float> vacData = new XYChart.Series();
		XYChart.Series<String, Float> icuData = new XYChart.Series();
		XYChart.Series<String, Float> hospData = new XYChart.Series();
		
		float vac_rate = 0.0f;
		float hosp = 0.0f;
		float icu = 0.0f;
		boolean found = false;
		//System.out.println("TARGET " + iISO);
		for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
			if (rec.get("iso_code").equals(iISO)) {
				//System.out.println("Found ISO");
				LocalDate readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
				
				String s1 = rec.get("people_fully_vaccinated_per_hundred");
				if (!s1.isEmpty())
					vac_rate = Float.parseFloat(s1); // cumulative data 
				if (readDate.isBefore(startDate) && readDate.isAfter(endDate)) 
					continue;
				String s2 = rec.get("icu_patients_per_million");
				String s3 = rec.get("hosp_patients_per_million");
				System.out.println("Get data " + s2 + s3);
				if (s2.isEmpty() || s3.isEmpty()) 
					continue;
				found = true;
				icu = Float.parseFloat(s2) / 10;
				hosp = Float.parseFloat(s3) / 10;
				vacData.getData().add(new XYChart.Data(readDate.toString(), vac_rate));
				icuData.getData().add(new XYChart.Data(readDate.toString(), icu));
				hospData.getData().add(new XYChart.Data(readDate.toString(), hosp));
				System.out.println("Found icu and hosp data");
			}
		}
	
		if (found == true) {
			vacData.setName("Rate of vaccination");
			icuData.setName("# of ICU patients per 100,000");
			hospData.setName("# of hospital patients per 100,000");
			allData.addAll(vacData, icuData, hospData);
			System.out.println("end of fcn");
			return allData;
		}
		else return null;		
	}
}	