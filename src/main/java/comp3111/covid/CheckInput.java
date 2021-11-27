package comp3111.covid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CheckInput {
	/**
	 * Checks if the given ISO within the dataset or not.
	 *
	 * @param iLocations ISO
	 * @param iDataset Path to dataset
	 *
	 * @return String List that [0] = error message, and [1] = valid iso codes.
	 */
	public static List<String> checkValidLocations(String iLocations, String iDataset) {
		
		String errorMsg = "";
		Map<String, String> locIsoMap = DataAnalysis.getAllLocationIso(iDataset);
		System.out.println("Found all pairs of loc + isocode");
		String[] iLocationsArr = iLocations.split("\n");
		List<String> validationResult = new ArrayList<>();
		for (String loc : iLocationsArr) {
			loc = loc.trim();
			if (!locIsoMap.containsKey(loc)) {
				errorMsg += (loc + " is not found in the dataset\n");
			}
			else {
				if (!validationResult.contains(loc)) validationResult.add(loc);
			}
		}
		if (validationResult.isEmpty()) errorMsg = "Please enter at least one valid country location. ";
		validationResult.add(errorMsg);
		return validationResult;
	}
	/** Method 2:
	 * input: LocalDate 
	 * output: string[] - first is notification of using default values 
	 * 					  second is date to use 
	 * This method is for the table task where only "No input" is handled by giving today's date as default.
	 */
	public static String[] checkValidDate(LocalDate iDate, String iDataset) {
		List<LocalDate> validPeriod = DataAnalysis.getValidPeriod(iDataset);
		String errorMsg = "";
		String[] validationResult = new String[2];
		if (iDate == null) {
			errorMsg = "User did not input any date, so today's date is used.\n";
			validationResult[1] = LocalDate.now().toString();
		}
		else {
			validationResult[1] = iDate.toString();
		}
		if (iDate.isBefore(validPeriod.get(0))) errorMsg += String.format("Data records start on or after %s for all countries.\n", validPeriod.get(0).toString());
		if (iDate.isAfter(validPeriod.get(1))) errorMsg += String.format("No new data recorded after %s.\n", validPeriod.get(1).toString());
		validationResult[0] = errorMsg;
		return validationResult;
	}
	/** Method 3: 
	 * input: LocalDate iStartDate, LocalDate iEndDate, String iDataset 
	 * output: return List - error message, start date, end date 
	 */
	public static List<String> checkValidPeriod(LocalDate iStartDate, LocalDate iEndDate, String iDataset) {
		String errorMsg = "";
		List<LocalDate> validPeriod = DataAnalysis.getValidPeriod(iDataset);
		List<String> validationResult = new ArrayList<>();
		if (iStartDate != null && iEndDate != null) {
			if (iEndDate.isBefore(iStartDate)) {
				errorMsg += "Please enter a valid period with starting date before ending date.\n";
				validationResult.add(errorMsg); 
				return validationResult;
			}
			if (iStartDate.isBefore(validPeriod.get(0))) errorMsg += String.format("Data records start on or after %s for all countries.\n", validPeriod.get(0).toString());
			if (iEndDate.isAfter(validPeriod.get(1))) errorMsg += String.format("No new data recorded after %s.\n", validPeriod.get(1).toString());
		} else {
			if (iStartDate == null) {
				iStartDate = validPeriod.get(0);
				errorMsg += "The empty start date is replaced with the earliest date in the dataset.\n";
			}
			if (iEndDate == null) {
				iEndDate = validPeriod.get(1);
				errorMsg += "The empty end date is replaced with the latest date in the dataset.\n";
			}
		}
		validationResult.add(iStartDate.toString());
		validationResult.add(iEndDate.toString());
		validationResult.add(errorMsg);
		return validationResult;		
	}
}


