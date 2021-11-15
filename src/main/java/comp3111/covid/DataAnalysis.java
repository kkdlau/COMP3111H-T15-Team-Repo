package comp3111.covid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.commons.csv.*;
import edu.duke.*;

/**
 * 
 * Data Explorer on COVID-19
 * @author <a href=mailto:namkiu@ust.hk>Namkiu Chan</a>
 * @version	1.1
 * 
 */
public class DataAnalysis {
 
	public static CSVParser getFileParser(String dataset) {
	     FileResource fr = new FileResource("dataset/" + dataset);
	     return fr.getCSVParser(true);
		}
	

	public static String getConfirmedCases(String dataset, String iso_code) {
		String oReport = "";	
		long confirmedCases = 0;
		long numRecord = 0;
		long totalNumRecord = 0;
		
		for (CSVRecord rec : getFileParser(dataset)) {
			
			if (rec.get("iso_code").equals(iso_code)) {
				String s = rec.get("new_cases");
				if (!s.equals("")) {
					confirmedCases += Long.parseLong(s);
					numRecord++;
				}
			}		
			totalNumRecord++;
		}
		
		oReport = String.format("Dataset (%s): %,d Records\n\n", dataset, totalNumRecord);
		oReport += String.format("[Summary (%s)]\n", iso_code);
		oReport += String.format("Number of Confirmed Cases: %,d\n", confirmedCases);
		oReport += String.format("Number of Days Reported: %,d\n", numRecord);
		
		return oReport;
	}
	
	 public static String getConfirmedDeaths(String dataset, String iso_code) {
			String oReport = "";	
			long confirmedDeaths = 0;
			long numRecord = 0;
			long totalNumRecord = 0;
			
			for (CSVRecord rec : getFileParser(dataset)) {
				
				if (rec.get("iso_code").equals(iso_code)) {
					String s = rec.get("new_deaths");
					if (!s.equals("")) {
						confirmedDeaths += Long.parseLong(s);
						numRecord++;
					}
				}		
				totalNumRecord++;
			}
			
			oReport = String.format("Dataset (%s): %,d Records\n\n", dataset, totalNumRecord);
			oReport += String.format("[Summary (%s)]\n", iso_code);
			oReport += String.format("Number of Deaths: %,d\n", confirmedDeaths);
			oReport += String.format("Number of Days Reported: %,d\n", numRecord);
			
			return oReport;
	 }
	 
	 public static String getRateOfVaccination(String dataset, String iso_code) {
			String oReport = "";	
			long fullyVaccinated = 0;
			long numRecord = 0;
			long totalNumRecord = 0;
			long population = 0;
			float rate = 0.0f;
						
			for (CSVRecord rec : getFileParser(dataset)) {
				
				if (rec.get("iso_code").equals(iso_code)) {
					
					String s1 = rec.get("people_fully_vaccinated");
					String s2 = rec.get("population");		
					if (!s1.equals("") && !s2.equals("")) {
						fullyVaccinated = Long.parseLong(s1);
						population = Long.parseLong(s2);						
						numRecord++;
					}
				}		
				totalNumRecord++;
			}
			
			if (population !=0)
				rate = (float) fullyVaccinated / population * 100;
			
			oReport = String.format("Dataset (%s): %,d Records\n\n", dataset, totalNumRecord);
			oReport += String.format("[Summary (%s)]\n", iso_code);
			oReport += String.format("Number of People Fully Vaccinated: %,d\n", fullyVaccinated);
			oReport += String.format("Population: %,d\n", population);
			oReport += String.format("Rate of Vaccination: %.2f%%\n", rate);
			oReport += String.format("Number of Days Reported: %,d\n", numRecord);
			
			return oReport;
	 }
	public static LocalDate getValidDate(String dataset, LocalDate date) {

    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "M/d/uuuu" ) ;
		LocalDate latest = null, earliest = null;

		for (CSVRecord rec : getFileParser(dataset)) {
			LocalDate readDate = LocalDate.parse(rec.get("date"),formatter);
			if(latest==null && earliest==null) {latest = readDate; earliest = readDate;}
			if(readDate.compareTo(latest)>0) {latest = readDate;}
			if(readDate.compareTo(earliest)<0) {earliest = readDate;}
		}

		if(date==null || date.compareTo(latest)>0) {return latest;}
		if(date.compareTo(earliest)<0) {return earliest;}
		return date;
	}
 
	public static Map<String, String> getAllLocationIso(String dataset) {
		LinkedHashSet<String> uniqueLocations = new LinkedHashSet();
		LinkedHashSet<String> uniqueIsoCodes = new LinkedHashSet();
		for (CSVRecord rec : getFileParser(dataset)) {
			uniqueLocations.add(rec.get("location"));
			uniqueIsoCodes.add(rec.get("iso_code"));
		}
		List<String> uniqueLocList = new ArrayList<String>( uniqueLocations );
		List<String> uniqueIsoCodeList = new ArrayList<String>( uniqueIsoCodes );
		
		if (uniqueLocList.size() != uniqueIsoCodeList.size()) {
			System.out.println("Something wrong with dataset");
		}
		else {
			System.out.println("Ok, same length");
		}
		Map<String, String> locIsoCodeMap = new HashMap<>();
		for (int i = 0; i < uniqueLocations.size(); ++i) {
			locIsoCodeMap.put(uniqueLocList.get(i), uniqueIsoCodeList.get(i));
			//System.out.println(uniqueLocList.get(i) + "--");
		}
		System.out.println("Finished allocating to map");
		return locIsoCodeMap;
	}
}