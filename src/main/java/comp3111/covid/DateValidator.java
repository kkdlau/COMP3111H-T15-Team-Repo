package comp3111.covid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidator {
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "M/d/uuuu" ) ;
	public static void main(String arg[]) {
		System.out.println("Class to ensure dates are valid");	
	}
	
	static LocalDate earliest = LocalDate.parse("7/11/2020", formatter);
	static LocalDate latest = LocalDate.parse("11/13/2020", formatter);
	
	public static boolean withinPeriod(LocalDate iDate) {
		if (iDate.compareTo(earliest) > 0 && iDate.compareTo(latest) < 0) {
			return true;
		}
		else 
			return false;
	}
}
