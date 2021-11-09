import comp3111.covid.DataAnalysis;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class AnalysisTester {
    String dataset;
    String ISO_code;

    @Before
    public void setup() {
        dataset = "COVID_Dataset_v1.0.csv";
        ISO_code = "AFG";
    }

    @Test
    public void getConfirmedDeaths() {
        String report = DataAnalysis.getConfirmedCases(dataset, ISO_code);
        assertTrue(report.contains("Number of Confirmed Cases: "));
    }
}
