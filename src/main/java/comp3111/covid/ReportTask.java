package comp3111.covid;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.*;

import java.time.temporal.ChronoUnit;

/**
 * A class to generate data for Reports
 *
 */
class ReportTask {

    static DateTimeFormatter datasetFormatter = DateTimeFormatter.ofPattern("M/d/uuuu");
    static DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("uuuu-M-d");
    static String[] quartiles = {"Quartile 1", "Quartile 2", "Quartile 3", "Quartile 4"};
    public static Map<String, Set<String>> locByGDP = new HashMap<>();


    /**
     * Generate a data series for scatter plot in report B
     *
     * @param iDataset			the path to the dataset
     * @param iISO 				the country ISO code
     * @param x_axis			the name of x_axis
     * @param y_axis			the name of y_axis
     * @param result			the regression analysis result will be stored here
     * @param y_data_cumulation	the number of record cumulated
     * @return Series series of data
     */
    public static Series<Float, Float> generateChartB(String iDataset, String iISO, String x_axis, String y_axis, double[] result, int y_data_cumulation) {

        Series<Float, Float> data = new Series<Float, Float>();
        double sum_x = 0, sum_y = 0, sum_xy = 0, sum_x2 = 0, sum_y2 = 0;
        int length = 0;


        if (y_data_cumulation == 1) {
            for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
                if (rec.get("iso_code").equals(iISO)) {
                    if (!rec.get(x_axis).isEmpty() && !rec.get(y_axis).isEmpty()) {
                        float x_data_float = Float.parseFloat(rec.get(x_axis));
                        float y_data_float = Float.parseFloat(rec.get(y_axis));
                        if (x_data_float > 0 && y_data_float > 0) {
                            data.getData().addAll(new XYChart.Data(x_data_float, y_data_float));
                            sum_x += x_data_float;
                            sum_y += y_data_float;
                            sum_xy += x_data_float * y_data_float;
                            sum_x2 += x_data_float * x_data_float;
                            sum_y2 += y_data_float * y_data_float;
                            length++;
                        }
                    }
                }
            }
        } else if (y_data_cumulation > 1) {
            float[] sum_of_y_data = new float[y_data_cumulation];
            Arrays.fill(sum_of_y_data, 0);
            float[] value_of_x_data = new float[y_data_cumulation];
            Arrays.fill(value_of_x_data, 0);
            LocalDate startDate = null;
            LocalDate currentDate = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/uuuu");

            for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
                if (rec.get("iso_code").equals(iISO)) {

                    if (startDate == null) {
                        startDate = LocalDate.parse(rec.get("date"), formatter);
                    }
                    currentDate = LocalDate.parse(rec.get("date"), formatter);
                    int dayDiff = (int) ChronoUnit.DAYS.between(startDate, currentDate);

                    if (dayDiff >= y_data_cumulation) {
                        int index = dayDiff % y_data_cumulation;
                        float x_data_float = value_of_x_data[index];
                        float y_data_float = sum_of_y_data[index];
                        value_of_x_data[index] = 0;
                        sum_of_y_data[index] = 0;
                        LocalDate dateBefore;
                        LocalDate dateAfter;
                        if (x_data_float > 0 && y_data_float > 0) {
                            data.getData().addAll(new XYChart.Data(x_data_float, y_data_float));
                            sum_x += x_data_float;
                            sum_y += y_data_float;
                            sum_xy += x_data_float * y_data_float;
                            sum_x2 += x_data_float * x_data_float;
                            sum_y2 += y_data_float * y_data_float;
                            length++;
                        }
                    }

                    if (!rec.get(y_axis).isEmpty()) {
                        for (int i = 0; i < Math.min(dayDiff, y_data_cumulation); i++) {
                            if (sum_of_y_data[i] == 0) {
                                sum_of_y_data[i] += Float.parseFloat(rec.get(y_axis));
                            } else {
                                sum_of_y_data[i] = (float) (sum_of_y_data[i] * 0.4 + Float.parseFloat(rec.get(y_axis)) * 0.6);
                            }
                        }
                    }
                    if (!rec.get(x_axis).isEmpty()) {
                        value_of_x_data[dayDiff % y_data_cumulation] = Float.parseFloat(rec.get(x_axis));
                    }
                }
            }
        }

        double correlationCoef = (length * sum_xy - sum_x * sum_y) / Math.sqrt((double) (length * sum_x2 - sum_x * sum_x) * (length * sum_y2 - sum_y * sum_y));
        double slope = (length * sum_xy - sum_x * sum_y) / (length * sum_x2 - sum_x * sum_x);
        result[0] = correlationCoef;
        result[1] = length;
        result[2] = slope;
        return data;
    }

    /**
     * Generate a string to describe the correlation
     *
     * @param result	regression analysis result
     * @param x_data	the name of x_data
     * @param y_data	the name of y_data
     * @return String for regression analysis
     */
    private static String correlationAnalysisB(double[] result, String x_data, String y_data) {
        double correlation = (double) Math.round(result[0] * 100) / 100;
        int length = (int) result[1];
        if (length <= 2) {
            return "There are not sufficient data to make a conclusion.";
        }
        String message = "The correlation between " + x_data + " and " + y_data + " is " + correlation + " that implies ";
        if (correlation > 0.6) {
            message += "a strongly positive";
        } else if (correlation > 0.2) {
            message += "a slightly positive";
        } else if (correlation > -0.2) {
            message += "no";
        } else if (correlation > -0.6) {
            message += "a slightly negative";
        } else {
            message += "a strongly negative";
        }
        message += " relationship between " + x_data + " and " + y_data + ".";
        return message;
    }

    /**
     * Generate a conclusion of chart 1 in report B
     *
     * @param result regression analysis result
     * @return String for the reportB chart 1
     */
    public static String correlationAnalysisB1(double[] result) {
        double correlation = (double) Math.round(result[0] * 100) / 100;
        String message = correlationAnalysisB(result, "death cases", "confirmed cases");
        if (message.equals("There are not sufficient data to make a conclusion.")) {
            return message;
        }

        if (correlation > 0.2) {
            message += " Government should implement bounder shutdown and social distancing policies to reduce the death cases.";
        } else {
            message += " Most of the death cases are not related to the infection of covid-19.";
            message += " Bounder shutdown and social distancing can not effectively reduce the deaths.";
        }
        return message;
    }

    /**
     * Generate a conclusion of chart 2 in report B
     *
     * @param result regression analysis result
     * @return String for the reportB chart 2
     */
    public static String correlationAnalysisB2(double[] result) {
        double correlation = (double) Math.round(result[0] * 100) / 100;
        String message = correlationAnalysisB(result, "death cases", "vaccination rate");
        if (message.equals("There are not sufficient data to make a conclusion.")) {
            return message;
        }

        if (correlation > 0.2) {
            message += " The positive correlation implies the public trust the efficiency of vaccine that can prevent death from covid-19.";
            message += " It implies the government can provide enough and reliable vaccines during the breakout.";
        } else if (correlation < -0.2) {
            message += " The negative correlation implies the public did not decide to/ cannot be vaccinated when a new death case happens.";
            message += " It implies government did not have enough vaccines or had no reliable vaccine to use during the breakout.";
        }
        return message;
    }

    /**
     * Generate a conclusion of chart 3 in report B
     *
     * @param result 		regression analysis result
     * @param dayChecked	the number of day that the death cases observed
     * @return String for the reportB chart 1
     */
    public static String correlationAnalysisB3(double[] result, int dayChecked) {
        double correlation = (double) Math.round(result[0] * 100) / 100;
        String message = correlationAnalysisB(result, "vaccination rate", dayChecked + "-days death cases");
        if (message.equals("There are not sufficient data to make a conclusion.")) {
            return message;
        }

        if (correlation > 0.2) {
            message += " The positive relationship implies the vaccine is deadly that increases the number of death cases in " + dayChecked + "-days.";
        } else if (correlation > -0.2) {
            message += " That implies the vaccine cannot effectively prevent a death cases in " + dayChecked + "-days.";
        } else {
            message += " The negative relationship implies the vaccine can effectively prevent a death cases in " + dayChecked + "-days.";
        }
        return message;
    }
    
    /**
     * Generate data for Chart C1 in Report C
     * 
     * @param iDataset Filename of dataset
     * @return ObservableList Data for LineChart
     */

    public static ObservableList generateChartC1(String iDataset) {
        Float[] gdpQuartile = DataAnalysis.getQuartiles(iDataset, "gdp_per_capita");

        List<Map<LocalDate, Float>> dateVacMap = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            locByGDP.put(quartiles[i], new LinkedHashSet<String>());
            dateVacMap.add(new TreeMap<>());
        }

        String prevISO = "";
        String ISO = "", s1 = "";
        LocalDate earliest = LocalDate.parse("12/1/2020", datasetFormatter);
        LocalDate date = null;
        float numDoses = 0.0f;
        float gdp = 0.0f;
        int valid = 1;
        for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
            date = LocalDate.parse(rec.get("date"), datasetFormatter);
            if (date.isBefore(earliest)) continue;
            ISO = rec.get("iso_code");
            if (!ISO.equals(prevISO)) { // new ISO record
                s1 = rec.get("gdp_per_capita");
                if (s1.isEmpty()) { // no GDP data
                    valid = 0;
                    continue;
                } else {	
                    numDoses = 0.0f; // reset to zero
                    gdp = Float.parseFloat(s1);
                    valid = 1;
                }
            } else if (valid == 0) continue;
            s1 = rec.get("total_vaccinations_per_hundred");
            if (!s1.isEmpty())
                numDoses = Float.parseFloat(s1);

            for (int i = 0; i < 4; ++i) {
                if (gdp < gdpQuartile[i]) {
                    if (dateVacMap.get(i).containsKey(date))
                        dateVacMap.get(i).put(date, dateVacMap.get(i).get(date) + numDoses);
                    else
                        dateVacMap.get(i).put(date, numDoses);
                    locByGDP.get(quartiles[i]).add(rec.get("location"));
                    break;
                }
            }
        }
        // form the series from the Map
        ObservableList<XYChart.Series<String, Float>> allData =
                FXCollections.<XYChart.Series<String, Float>>observableArrayList();
        for (int i = 0; i < 4; ++i) {
            float prev = 0.0f;
            XYChart.Series<String, Float> data = new XYChart.Series();
            int numLoc = locByGDP.get(quartiles[i]).size();
            for (Map.Entry<LocalDate, Float> mapElement : dateVacMap.get(i).entrySet()) {
                prev = (mapElement.getValue() / numLoc > prev) ? mapElement.getValue() / numLoc : prev;
                data.getData().add(new XYChart.Data(mapElement.getKey().toString(), prev));
            }
            data.setName(quartiles[i]);
            allData.add(data);
        }
        return allData;
    }

    /**
     * Generate data for the Table 1 in Report C
     * 
     * @return ObservableList Data to populate TableView
     */
    public static ObservableList generateTableC1() {
        try {
            ObservableList<Map<String, String>> tableData = FXCollections.<Map<String, String>>observableArrayList();
            boolean add = true;
            int index = 0;
            Map<String, String[]> temp = new HashMap<>();
            for (String q : quartiles) {
                String[] locs = new String[locByGDP.get(q).size()];
                locByGDP.get(q).toArray(locs);
                temp.put(q, locs);
                System.out.println(locs[0]);
            }
            // 4 lists of different length
            while (add) {
                Map<String, String> datum = new HashMap<>();
                add = false;
                for (String q : quartiles) {
                    if (index < locByGDP.get(q).size()) {
                        datum.put(q, temp.get(q)[index]);
                        add = true;
                    }
                }
                index += 1;
                if (add) tableData.add(datum);
            }
            return tableData;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Generate data for Chart 2 in Report C 
     * @param iDataset Filename of dataset
     * @param lastDate Last date with data in dataset
     * @return ObservableList Data for ScatterChart
     */
    public static ObservableList generateChartC2(String iDataset, LocalDate lastDate) {
        // human_development_index
        // continent - to know which series to add into - consider enum
        // people_fully_vaccinated_per_hundred - use the last date in iDataset
        String[] continents = {"Africa", "Asia", "Europe", "North America", "South America", "Oceania"};

        List<XYChart.Series> data = new ArrayList<>();

        for (int i = 0; i < continents.length; ++i) {
            data.add(new XYChart.Series<>());
            data.get(i).setName(continents[i]);
        }
        // record for one country
        String prev = "";
        float hdi = 0.0f; // human_development_index
        float vac_rate = 0.0f; // people_fully_vaccinated_per_hundred
        String continent = "";
        String temp = "";
        LocalDate readDate = null;
        for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
            continent = rec.get("continent");
            temp = rec.get("human_development_index");
            if (temp.isEmpty() || continent.isEmpty())
                continue;
            hdi = Float.parseFloat(temp);
            temp = rec.get("iso_code");
            if (!temp.equals(prev)) {
                vac_rate = 0.0f; // reset values
                prev = temp;
            }
            temp = rec.get("people_fully_vaccinated_per_hundred");
            if (!temp.isEmpty())
                vac_rate = Float.parseFloat(temp);
            readDate = LocalDate.parse(rec.get("date"), datasetFormatter);
            if (readDate.equals(lastDate)) {
                int index = StringIndexUtils.ContinentIndex(continent);
                data.get(index).getData().add(new XYChart.Data(hdi, vac_rate));

            }
        }
        ObservableList<XYChart.Series> allData = FXCollections.<XYChart.Series>observableArrayList();
        for (int i = 0; i < continents.length; ++i) {
            allData.add(data.get(i));
        }
        return allData;
    }

    /**
     * Generate data for Chart 3 in Report C
     * @param iDataset Filename of dataset
     * @param iISO     ISO chosen by user
     * @param iPeriod  Date range chosen by user
     * @return ObservableList Data to populate LineChart
     */
    public static ObservableList generateChartC3(String iDataset, String iISO, List<String> iPeriod) {
        LocalDate startDate = LocalDate.parse(iPeriod.get(0), inputFormatter);
        LocalDate endDate = LocalDate.parse(iPeriod.get(1), inputFormatter);
        ObservableList<XYChart.Series<String, Float>> allData =
                FXCollections.<XYChart.Series<String, Float>>observableArrayList();

        XYChart.Series<String, Float> vacData = new XYChart.Series();
        XYChart.Series<String, Float> icuData = new XYChart.Series();
        XYChart.Series<String, Float> hospData = new XYChart.Series();

        float vac_rate = 0.0f;
        float hosp = 0.0f;
        float icu = 0.0f;
        boolean found = false;
        //System.out.println("TARGET " + iISO);
        for (CSVRecord rec : DataAnalysis.getFileParser(iDataset)) {
            if (rec.get("iso_code").equals(iISO)) {
                //System.out.println("Found ISO");
                LocalDate readDate = LocalDate.parse(rec.get("date"), datasetFormatter);

                String s1 = rec.get("people_fully_vaccinated_per_hundred");
                if (!s1.isEmpty())
                    vac_rate = Float.parseFloat(s1); // cumulative data
                if (readDate.isBefore(startDate) || readDate.isAfter(endDate))
                    continue;
                String s2 = rec.get("icu_patients_per_million");
                String s3 = rec.get("hosp_patients_per_million");
                System.out.println("Get data " + s2 + s3);
                if (s2.isEmpty() || s3.isEmpty())
                    continue;
                found = true;
                icu = Float.parseFloat(s2) / 10;
                hosp = Float.parseFloat(s3) / 10;
                vacData.getData().add(new XYChart.Data(readDate.toString(), vac_rate));
                icuData.getData().add(new XYChart.Data(readDate.toString(), icu));
                hospData.getData().add(new XYChart.Data(readDate.toString(), hosp));
                System.out.println("Found icu and hosp data");
            }
        }

        if (found) {
            vacData.setName("Rate of vaccination");
            icuData.setName("# of ICU patients per 100,000");
            hospData.setName("# of hospital patients per 100,000");
            allData.addAll(vacData, icuData, hospData);
            System.out.println("end of fcn");
            return allData;
        } else return null;
    }
}	