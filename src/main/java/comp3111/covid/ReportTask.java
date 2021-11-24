package comp3111.covid;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.csv.*;
import edu.duke.*;

class ReportTask {
	static DateTimeFormatter datasetFormatter = DateTimeFormatter.ofPattern( "M/d/uuuu" ) ;
	static DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern( "uuuu-M-d" );
	public static Map<String, Set<String>> locByGDP = new HashMap<>();
	
	/** TODO: refactor code - such bad writing here
	 *  Generate Data for Chart 1 in Report C
	 * Input: String iDataset
	 * Output: ObservableList chartData
	 */
	
	/** 
	 * Generate Data for Table 1 in Report B
	 * @return ObservableList
	 */
	public static Series<Float, Float> generateChartB(String iDataset, String iISO, String x_axis, String y_axis) {
		
		XYChart.Series data = new Series<Float, Float>();
		
		for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
			if(rec.get("iso_code").equals(iISO)) {
				String x_data_string = rec.get(x_axis);
				String y_data_string = rec.get(y_axis);
				if(!x_data_string.isEmpty() && !y_data_string.isEmpty()) {
					Float x_data_float = Float.parseFloat(x_data_string);
					Float y_data_float = Float.parseFloat(y_data_string);
					if(x_data_float > 0 && y_data_float > 0) {
						data.getData().addAll(new XYChart.Data(x_data_float,y_data_float));
					}
				}
			}
		}
		
		return data;
	}
	
	
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
	
	/** 
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
	public static ObservableList generateChartC2(String iDataset) {
		// human_development_index 
		// continent - to know which series to add into - consider enum
		// people_fully_vaccinated_per_hundred - use the last date in iDataset 
		List<LocalDate> period = DataAnalysis.getValidPeriod(iDataset);
		LocalDate lastDate = period.get(1);
		
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
		System.out.println(iISO);
		float vac_rate = 0.0f;
		float hosp = 0.0f;
		float icu = 0.0f;
		boolean found = true;
		for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
			String temp = rec.get("iso_code");
			if (!temp.equals(iISO)) {
				if (found) break;
				else continue;
			}
			String s1 = rec.get("people_fully_vaccinated_per_hundred");
			if (!s1.isEmpty())
				vac_rate = Float.parseFloat(s1);
			LocalDate readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
			String s2 = rec.get("icu_patients_per_million");
			String s3 = rec.get("hosp_patients_per_million");
			if (!readDate.isBefore(startDate) && !readDate.isAfter(endDate)) {
				System.out.println(readDate.toString());
				if (!s2.isEmpty()) icu = Float.parseFloat(s2);
				if (!s3.isEmpty()) hosp = Float.parseFloat(s3);
				vacData.getData().add(new XYChart.Data(readDate.toString(), vac_rate));
				icuData.getData().add(new XYChart.Data(readDate.toString(), icu));
				hospData.getData().add(new XYChart.Data(readDate.toString(), hosp));
			}
		}
		
		vacData.setName("Rate of vaccination");
		icuData.setName("# of ICU patients per million");
		hospData.setName("# of hospital patients per million");
		allData.addAll(vacData, icuData, hospData);
		System.out.println("end of fcn");
		return allData;
		
	}
}	