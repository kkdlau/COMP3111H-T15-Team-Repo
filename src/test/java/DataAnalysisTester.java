import comp3111.covid.DataAnalysis;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class DataAnalysisTester {
    String dataset;
    String ISO_code;

    @Before
    public void setup() {
        dataset = "COVID_Dataset_v1.0.csv";
        ISO_code = "AFG";
    }

    @Test
    public void checkTaskZero() {
        DataAnalysis.getConfirmedCases(dataset, ISO_code);
        DataAnalysis.getConfirmedDeaths(dataset, ISO_code);
        DataAnalysis.getRateOfVaccination(dataset, ISO_code);
    }
    
    @Test
    public void checkGetAllLocationIso() {
    	DataAnalysis.getAllLocationIso(dataset);
    }
    
    @Test
    public void checkGetValidPeriod() {
    	DataAnalysis.getValidPeriod(dataset);
    }
}