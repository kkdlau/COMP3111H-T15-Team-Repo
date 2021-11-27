package comp3111.covid;

import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.*;
import edu.duke.*;

/**
 * Class to generate data for Task 1 and 2
 *
 * @author Magdalene
 */
class TableChartTask {
    static DateTimeFormatter datasetFormatter = DateTimeFormatter.ofPattern("M/d/uuuu");
    static DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("uuuu-M-d");

    /**
     * @param iDataset    Filename of dataset
     * @param iISOStrings Target countries
     * @param iStrDate    Target date
     * @param focusedData Target data of interest
     * @return ObservableList to populate Table
     */
    public static ObservableList generateTable(String iDataset, List<String> iISOStrings, String iStrDate, InterestedData focusedData) {
        String col1target = "", col2target = "";
        switch (focusedData) {
            case ConfirmedCases:
                col1target = "total_cases";
                col2target = "total_cases_per_million";
                break;
            case ConfirmedDeaths:
                col1target = "total_deaths";
                col2target = "total_deaths_per_million";
                break;
            case RateOfVaccination:
                col1target = "people_fully_vaccinated";
                col2target = "people_fully_vaccinated_per_hundred";
                break;
        }
        LocalDate iDate = LocalDate.parse(iStrDate, inputFormatter);

        ObservableList<Map<String, Object>> data =
                FXCollections.<Map<String, Object>>observableArrayList();

        for (String iso : iISOStrings) {
            long col1datum = 0;
            float col2datum = 0.0f;
            int found = 0;
            String loc = "";
            Map<String, Object> datum = new HashMap<>();
            //datum.put("country", loc);
            for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
                if (rec.get("iso_code").equals(iso)) {
                    loc = rec.get("location");
                    datum.put("country", loc);
                    found = 1;
                    LocalDate readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
                    if (readDate.isEqual(iDate)) {
                        String s1 = rec.get(col1target);
                        String s2 = rec.get(col2target);
                        if (!s1.equals("")) {
                            col1datum = Long.parseLong(s1);
                            datum.put("col1data", col1datum);
                        } else datum.put("col1data", "No records");

                        if (!s2.equals("")) {
                            col2datum = Float.parseFloat(s2);
                            datum.put("col2data", col2datum);
                        } else datum.put("col2data", "No records");
                        data.add(datum);
                        break;
                    }
                } else if (found == 1) { // date not in range
                    datum.put("col1data", "No records");
                    datum.put("col2data", "No records");
                    data.add(datum);
                    break;
                }
            }
        }
        return data;
    }

    /**
     * @param iDataset    Filename of dataset
     * @param iISOStrings Target countries
     * @param iPeriod     Target date range
     * @param focusedData Target data of interest
     * @return ObservableList to populate LineChart
     */
    public static ObservableList generateChart(String iDataset, List<String> iISOStrings, List<String> iPeriod, InterestedData focusedData) {
        String dataTarget = "";
        switch (focusedData) {
            case ConfirmedCases:
                dataTarget = "total_cases_per_million";
                break;
            case ConfirmedDeaths:
                dataTarget = "total_deaths_per_million";
                break;
            case RateOfVaccination:
                dataTarget = "people_fully_vaccinated_per_hundred";
                break;
        }
        LocalDate startDate = LocalDate.parse(iPeriod.get(0), inputFormatter);
        LocalDate endDate = LocalDate.parse(iPeriod.get(1), inputFormatter);
        ObservableList<XYChart.Series<String, Float>> allData =
                FXCollections.<XYChart.Series<String, Float>>observableArrayList();

        for (String iso : iISOStrings) {
            XYChart.Series<String, Float> data = new XYChart.Series();
            String loc = "";
            float rate = 0.0f;
            int found = 0;
            LocalDate readDate = null;
            LocalDate temp = null;
            for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
                if (rec.get("iso_code").equals(iso)) {
                    if (rec.get("date").equals("")) continue;
                    readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
                    if (found == 0) {
                        found = 1;
                        loc = rec.get("location");
                        if (startDate.isBefore(readDate)) {
                            temp = startDate;
                            rate = 0.0f;
                            while (!temp.isAfter(endDate) && temp.isBefore(readDate)) {
                                data.getData().add(new XYChart.Data(temp.toString(), rate));
                                temp = temp.plusDays(1);
                            }
                        }
                    }
                    String s1 = rec.get(dataTarget);
                    if (!s1.isEmpty()) rate = Float.parseFloat(s1);
                    if (!readDate.isBefore(startDate) && !readDate.isAfter(endDate)) {
                        data.getData().add(new XYChart.Data(readDate.toString(), rate));
                    }
                } else if (found == 1) break;
            }
            temp = LocalDate.now();
            while (readDate != null && !readDate.isAfter(endDate) && !readDate.isAfter(temp)) { // for dates behind period in dataset
                if (!readDate.isBefore(startDate)) data.getData().add(new XYChart.Data(readDate.toString(), rate));
                readDate = readDate.plusDays(1);
            }
            data.setName(loc);
            allData.add(data);
        }
        return allData;
    }
}	