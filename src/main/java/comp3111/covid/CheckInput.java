package comp3111.covid;

import java.time.LocalDate;
import java.util.*;

public class CheckInput {
	/** Method 1:
	 * input: string of country names 
	 * output: object[] - first is String - error messages for wrong inputs 
	 * 					  second is String[] of valid iso codes 
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
				validationResult.add(loc);
			}
		}
		validationResult.add(errorMsg);
		return validationResult;
	}
	/** Method 2:
	 * input: LocalDate 
	 * output: object[] - first is String - notify user of any default values 
	 * 					  second is LocalDate 
	 * This method is for the table task where only "No input" is handled by giving today's date as default.
	 */
	public static String[] checkValidDate(LocalDate iDate) {
		String errorMsg = "";
		String[] validationResult = new String[2];
		if (iDate == null) {
			errorMsg = "User did not input any date, so today's date is used.\n";
			validationResult[1] = LocalDate.now().toString();
		}
		else {
			validationResult[1] = iDate.toString();
		}
		validationResult[0] = errorMsg;
		return validationResult;
	}
}


