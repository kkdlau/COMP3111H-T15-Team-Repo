package comp3111.covid;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.csv.*;
import edu.duke.*;

class VaccinationRate {
	
	/** Method 1: Generate Table - Country, Fully Vaccinated, Rate of Vaccination
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
	public static ObservableList generateVacTable(String iDataset, List<String> iCountries, String iStrDate) {
		DateTimeFormatter datasetFormatter = DateTimeFormatter.ofPattern( "M/d/uuuu" ) ;
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern( "uuuu-M-d" );
		LocalDate iDate = LocalDate.parse(iStrDate, inputFormatter);
		
		ObservableList <Map<String, Object>> data = 
				FXCollections.<Map<String, Object>>observableArrayList();
		
		for (String loc : iCountries) {
			long fullyVaccinated = 0;
			float rate = 0.0f;
			
			for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
				if (rec.get("location").equals(loc)) {
					LocalDate readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
					if (readDate.isAfter(iDate)) break;
					String s1 = rec.get("people_fully_vaccinated");
					String s2 = rec.get("people_fully_vaccinated_per_hundred");
					if (!s1.equals(""))
						fullyVaccinated = Long.parseLong(s1);
					if (!s2.equals(""))
						rate = Float.parseFloat(s2);
				}
			}
			Map<String, Object> datum = new HashMap<>();
			datum.put("country", loc);
			datum.put("fully_vaccinated", fullyVaccinated);
			datum.put("rate_of_vaccination", rate);
			data.add(datum);
		}
		return data;
	}
}	