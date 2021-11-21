import comp3111.covid.DataAnalysis;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class DataAnalysisTester {
    String dataset_valid,dataset_invalid;
    String ISO_code;

    @Before
    public void setup() {
        dataset_valid = "COVID_Dataset_v1.0.csv";
        dataset_invalid = "";
        ISO_code = "AFG";
    }

    @Test
    public void checkTaskZero_ValidDataset() {
        DataAnalysis.getConfirmedCases(dataset_valid, ISO_code);
        DataAnalysis.getConfirmedDeaths(dataset_valid, ISO_code);
        DataAnalysis.getRateOfVaccination(dataset_valid, ISO_code);
        DataAnalysis.getFileParser(dataset_valid);
    }
    
    @Test
    public void checkTaskZero_InvalidDataset() {
        DataAnalysis.getConfirmedCases(dataset_invalid, ISO_code);
        DataAnalysis.getConfirmedDeaths(dataset_invalid, ISO_code);
        DataAnalysis.getRateOfVaccination(dataset_invalid, ISO_code);
        DataAnalysis.getFileParser(dataset_invalid);
    }
    
    @Test
    public void checkGetAllLocationIso_ValidDataset() {
    	DataAnalysis.getAllLocationIso(dataset_valid);
    }
    
    @Test
    public void checkGetAllLocationIso_InvalidDataset() {
    	DataAnalysis.getAllLocationIso(dataset_invalid);
    	DataAnalysis.getAllLocationIso(null);
    }
    
    @Test
    public void checkGetValidPeriod_ValidDataset() {
    	DataAnalysis.getValidPeriod(dataset_valid);
    }
    
    @Test
    public void checkGetValidPeriod_InvalidDataset() {
    	DataAnalysis.getValidPeriod(dataset_invalid);
    	DataAnalysis.getValidPeriod(null);
    }
}