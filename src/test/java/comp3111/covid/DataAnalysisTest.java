import comp3111.covid.DataAnalysis;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.LocalDate;


public class DataAnalysisTest {
	String blank;
    String dataset;
    String ISO_code,ISO_code2;
    LocalDate[] datelist = new LocalDate[6];
    String dataProperty1,dataProperty2;

    @Before
    public void setup() {
        dataset = "COVID_Dataset_v1.0.csv";
        blank = "";
        ISO_code = "AFG";
        ISO_code2 = "DNK";
		datelist[0] = LocalDate.parse("2017-11-10"); //Too Early
		datelist[1] = LocalDate.parse("2018-11-10"); //Too Early
		datelist[2] = LocalDate.parse("2020-10-10"); //Valid
		datelist[3] = LocalDate.parse("2020-12-12"); //Valid
		datelist[4] = LocalDate.parse("2021-10-10"); //Too Late
		datelist[5] = LocalDate.now(); //Too Late
		dataProperty1 = "gdp_per_capita";
		dataProperty2 = "diabetes_prevalence";
    }

    @Test
    public void TaskZero() {
        DataAnalysis.getConfirmedCases(dataset, ISO_code);
        DataAnalysis.getConfirmedDeaths(dataset, ISO_code);
        DataAnalysis.getRateOfVaccination(dataset, ISO_code);
        DataAnalysis.getFileParser(dataset);
    }
    @Test
    public void GetValidDateOutRange() {
    	DataAnalysis.getValidDate(dataset, datelist[0]);
    	DataAnalysis.getValidDate(dataset, datelist[5]);
    }
    @Test
    public void GetValidDateNull() {
    	DataAnalysis.getValidDate(dataset, null);
    }
    @Test
    public void GetValidDateValid() {
    	DataAnalysis.getValidDate(dataset, datelist[2]);
    }
    @Test
    public void GetValidPeriodValid() {
    	DataAnalysis.getValidPeriod(dataset);
    }
    @Test
    public void GetValidPeriodInvalid() {
try {
    DataAnalysis.getValidPeriod(blank);
    DataAnalysis.getValidPeriod(null);
} catch (Exception e) {

}
    }
    @Test
    public void GetAllLocationIsoValid() {
    	DataAnalysis.getAllLocationIso(dataset);
    }
    @Test
    public void GetAllLocationIsoInvalid() {
    	DataAnalysis.getAllLocationIso(blank);
    	DataAnalysis.getAllLocationIso(null);
    }
    @Test
    public void GetCasesAndDeathsDataNull() {
        try {
            DataAnalysis.casesAndDeathsData(blank,null,null);
            DataAnalysis.casesAndDeathsData(null,ISO_code,null);
        } catch (Exception e) {

        }
    }
    @Test
    public void GetCasesAndDeathsDataDuplicated() {
    	DataAnalysis.casesAndDeathsData(dataset,ISO_code,ISO_code);
    }
    @Test
    public void GetCasesAndDeathsDataGarble() {
        try {
            DataAnalysis.casesAndDeathsData(dataset,"12rjfig","wbq");
            DataAnalysis.casesAndDeathsData("n3b1bwev",ISO_code,"HKG");
        } catch (Exception e) {

        }
    }
    @Test
    public void GetCasesAndDeathsDataValid() {
    	DataAnalysis.casesAndDeathsData(dataset,ISO_code,ISO_code2);
    }
    @Test
    public void GetQuartilesNull() {
try {
    DataAnalysis.getQuartiles(null,null);
    DataAnalysis.getQuartiles(null,dataProperty1);
    DataAnalysis.getQuartiles(dataset,null);
} catch (Exception e) {

}
    }
    @Test
    public void GetQuartilesGarble() {
        try {
            DataAnalysis.getQuartiles("fqfqfq",dataProperty1);
            DataAnalysis.getQuartiles(dataset,"n3qwa");
        } catch (Exception e) {

        }
    }
    @Test
    public void GetQuartilesBlank() {
        try {
            DataAnalysis.getQuartiles(blank,dataProperty1);
            DataAnalysis.getQuartiles(dataset,blank);
        } catch (Exception e) {

        }
    }
    @Test
    public void GetQuartilesValid() {
    	DataAnalysis.getQuartiles(dataset,dataProperty1);
    	DataAnalysis.getQuartiles(dataset,dataProperty2);
    }
}