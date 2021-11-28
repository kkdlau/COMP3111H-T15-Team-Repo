package comp3111.covid;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.ScatterChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A singleton class that consist of deep copy methods.
 */
class DeepCopyUtils {
    private DeepCopyUtils() {

    }

    /**
     * Performs deep copying on a series of data.
     *
     * @param series series to copy
     * @param <S>    type of X axis
     * @param <T>    type of Y axis
     * @return deep copied series
     */
    public static <S, T> XYChart.Series<S, T> copySeries(XYChart.Series<S, T> series) {
        XYChart.Series<S, T> copy = new XYChart.Series<>(series.getName(),
                series.getData().stream()
                        .map(data -> new XYChart.Data<S, T>(data.getXValue(), data.getYValue()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        return copy;
    }
}

public class Controller implements Initializable {

    ToggleGroup ratioButtonGroups = new ToggleGroup();
    UIDataModel dataInstance = new UIDataModel();
    ObservableList<XYChart.Series<String, Float>> unshifted;
    ObservableList<XYChart.Series<String, Float>> shifted;
    DoubleProperty aShift = new SimpleDoubleProperty();
    DoubleProperty bShift = new SimpleDoubleProperty();
    @FXML
    private Tab tabTask12;
    @FXML
    private TextField textfieldDataset;
    @FXML
    private CheckBox acumulativeCheckButton;
    @FXML
    private Label countryInstruction;
    @FXML
    ListView<String> countryListView;
    @FXML
    private RadioButton dataCaseButton;
    @FXML
    private RadioButton dataDeathButton;
    @FXML
    private TableView dataTable;
    @FXML
    private RadioButton dataVaccinButton;
    @FXML
    private Label endDataLabel;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label startDateLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Button generateButton;
    @FXML
    private TitledPane dataRangeTile;
    @FXML
    private TitledPane countryFilter;
    @FXML
    private LineChart chart;
    @FXML
    private NumberAxis chartXAxis;
    @FXML
    private CategoryAxis chartYAxis;
    @FXML
    private StackPane stack;
    // Report B
    @FXML
    private Tab b3Tab;
    @FXML
    ScrollPane reportB;
    @FXML
    private ScatterChart chartReportB1, chartReportB2, chartReportB3;
    @FXML
    private Button buttonReportB1;
    @FXML
    private Label taskB1correlation, taskB2correlation, taskB3correlation, LabelSliderReportB3;
    @FXML
    private Label ResultB1, ResultB2, ResultB3;
    @FXML
    private Slider SliderReportB3;
    // Report C
    @FXML
    private Tab c3Tab;
    @FXML
    ScrollPane reportC;
    @FXML
    private Button buttonReportC1, buttonReportC2, buttonReportC3;
    @FXML
    private LineChart chartReportC1, chartReportC3;
    @FXML
    private ScatterChart chartReportC2;
    @FXML
    private TableView tableReportC1;
    @FXML
    Text chartReportC1Title, chartReportC2Title, chartReportC3Title;

    @FXML
    private HBox rootUI;
    @FXML
    private VBox rightUI;
    @FXML
    TabPane tabGroup;
    @FXML
    private Label title;
    @FXML
    private Button chartGenerateButton;
    @FXML
    private ListView<String> countryAListView;
    @FXML
    Slider countryASlider;
    @FXML
    private ListView<String> countryBListView;
    @FXML
    private Slider countryBSlider;
    @FXML
    private LineChart compareChart;
    @FXML
    private LineChart<Float, Float> caseDeathChart;
    @FXML
    private Button caseDeathGenerateButton;
    @FXML
    private Label countryAShiftText;
    @FXML
    private Label countryBShiftText;
    @FXML
    Label regressionReport;
    @FXML
    ScrollPane reportA;
    @FXML
    private Tab a3Tab;

    private boolean init = false;


    ChangeListener<Tab> onTabChanged = (ov, disSelected, selected) -> {
        rightUI.getChildren().clear();

        if (disSelected == tabTask12) {
        } else if (disSelected == a3Tab) {
            countryFilter.setExpanded(true);
            countryFilter.setVisible(true);
        } else if (disSelected == b3Tab) {

        } else if (disSelected == c3Tab) {
            countryInstruction.setVisible(true);
        }

        if (selected == tabTask12) {
            rightUI.getChildren().add(title);
            rightUI.getChildren().add(stack);
            this.showTaskUI(!dataInstance.acumulativeData.get());
        } else if (selected == a3Tab) {
            countryFilter.setExpanded(false);
            countryFilter.setVisible(false);
            rightUI.getChildren().add(title);
            rightUI.getChildren().add(stack);
            title.setText("COVID-19 Confirmed Cases Report"); // update title
            showPickPeriodUI();
            this.showReportUI(InterestedData.ConfirmedCases);
        } else if (selected == b3Tab) {
            rightUI.getChildren().add(stack);
            this.showReportUI(InterestedData.ConfirmedDeaths);
        } else if (selected == c3Tab) {
            rightUI.getChildren().add(stack);
            this.showReportUI(InterestedData.RateOfVaccination);
        }
    };
    private Stage window;

    /**
     * Sets the stage that the controller belongs to.
     *
     * @param stage stage that the controller belongs to
     */
    public void setStage(Stage stage) {
        this.window = stage;
    }

    /**
     * Initialize the cotroller.
     */
    public void initialize() {
        // default data for data pickers

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        chartXAxis.setAutoRanging(true);
        chartYAxis.setAutoRanging(true);

        this.initializeUIDataModel();
        this.ratioButtonInitialize();

        // Let list view listens to changes of getAvailableCountries().
        // So that when a new dataset is loaded, the list will update also.
        countryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        countryListView.setItems(dataInstance.getAvailableCountries());

        startDatePicker.valueProperty().addListener((ob, oldV, newV) -> dataInstance.start = newV);

        endDatePicker.valueProperty().addListener((ob, oldV, newV) -> dataInstance.end = newV);

        generateButton.setOnAction((e) -> {
            if (dataInstance.acumulativeData.get())
                this.generateChart(dataInstance);
            else
                this.generateTable(dataInstance);
        });

        dataInstance.acumulativeData.addListener((e) -> {
            showTaskUI(!dataInstance.acumulativeData.get());
        });

        tabGroup.getSelectionModel().selectedItemProperty().addListener(onTabChanged);

        acumulativeCheckButton.setSelected(false);
        textfieldDataset.setText(textfieldDataset.getText());

        dataInstance.dataPath.setValue(textfieldDataset.getText());

        tabTaskA3Initialize();
        tabTaskB3Initialize();
        tabTaskC3Initialize();

        stack.getChildren().clear();
        this.showTaskUI(!dataInstance.acumulativeData.get());

        tabGroup.getSelectionModel().select(0); // select task 1&2 tab by default
        init = true;
    }

    /**
     * Initialize UI components for tab B3.
     */
    void tabTaskB3Initialize() {
        buttonReportB1.setOnAction((e) -> {
            errorCheckOneCountry();
            this.generateChartB1(dataInstance);
            this.generateChartB2(dataInstance);
            this.generateChartB3(dataInstance);
        });

        SliderReportB3.setOnMouseReleased((e) -> {
            LabelSliderReportB3.setText((int) SliderReportB3.getValue() + "-days death cases are observed after vaccination ");
            errorCheckOneCountry();
            this.generateChartB3(dataInstance);
        });
    }
    /**
     * Initialize UI components for tab C3.
     */
    void tabTaskC3Initialize() {
        buttonReportC1.setOnAction((e) -> {
            this.generateChartC1(dataInstance);
        });
        buttonReportC2.setOnAction((e) -> {
            this.generateChartC2(dataInstance);
        });
        buttonReportC3.setOnAction((e) -> {
            this.generateChartC3(dataInstance);
        });
    }

    /**
     * Initialize UI components for tab A3.
     */
    void tabTaskA3Initialize() {
        countryAListView.setItems(dataInstance.getAvailableCountries());
        countryBListView.setItems(dataInstance.getAvailableCountries());

        countryAListView.setOnMouseClicked((e) -> countryASlider.setDisable(false));
        countryBListView.setOnMouseClicked((e) -> countryBSlider.setDisable(false));

        if (countryAListView.getSelectionModel().getSelectedItems().size() == 0) {
            countryASlider.setDisable(true);
        } else {
            countryASlider.setDisable(false);
        }

        if (countryBListView.getSelectionModel().getSelectedItems().size() == 0) {
            countryBSlider.setDisable(true);
        } else {
            countryBSlider.setDisable(false);
        }

        chartGenerateButton.setOnAction((e) -> {
            try {
                generateComparisonChart(dataInstance);
            } catch (Exception exception) {
                PromptHelper.showErrorPrompt(window, exception.getMessage());
            }
        });
        caseDeathGenerateButton.setOnAction((e) -> {
            try {
                generateRegressionChart(dataInstance);
            } catch (Exception exception) {
                PromptHelper.showErrorPrompt(window, exception.getMessage());
            }
        });

        aShift.bindBidirectional(countryASlider.valueProperty());
        bShift.bindBidirectional(countryBSlider.valueProperty());

        aShift.addListener((e) -> {
            double d = aShift.getValue();
            countryAShiftText.setText(String.format("X-axis shift for country A(The situation has been postponed for %d days):", (int) d));
            if (shifted != null && d != 0)
                shiftingData();
        });

        bShift.addListener((e) -> {
            double d = bShift.getValue();
            countryBShiftText.setText(String.format("X-axis shift for country B(The situation has been postponed for %d days):", (int) d));
            if (shifted != null && d != 0)
                shiftingData();
        });

        compareChart.getYAxis().setLabel("Confirmed Cases (per M)");
        compareChart.getXAxis().setLabel("Date");

        caseDeathChart.setCreateSymbols(false);
        caseDeathChart.setAnimated(false);
        caseDeathChart.getXAxis().setLabel("Confirmed Cases (per M)");
        caseDeathChart.getYAxis().setLabel("Confirmed Deaths (per M)");
    }

    /**
     * Generates regression model and display on the chart.
     *
     * @param data data model
     * @throws Exception the error occurs during the generation, can be caught to get the fail message
     */
    void generateRegressionChart(UIDataModel data) throws Exception {
        caseDeathChart.getData().clear();

        String iDataset = data.dataPath.get();

        String[] selectedCountries = new String[]{
                countryAListView.getSelectionModel().getSelectedItem(),
                countryBListView.getSelectionModel().getSelectedItem(),
        };

        Object[] ISO = dataInstance.getISOList(FXCollections.observableList(Arrays.asList(selectedCountries)));
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        if (selectedCountries[0] == null || selectedCountries[1] == null) {
            throw new Exception("Please select at least one country");
        }

        ObservableList<XYChart.Series<Float, Float>> d = DataAnalysis.casesAndDeathsData(iDataset, ISOStrings[0], ISOStrings[1]);
        d.get(0).setName(selectedCountries[0]);
        d.get(1).setName(selectedCountries[1]);

        LinearRegression regressionA = LinearRegression.fromSeries(d.get(0));
        XYChart.Series rgA = regressionA.generateMockData();
        rgA.setName(String.format("Regression - %s", selectedCountries[0]));

        LinearRegression regressionB = LinearRegression.fromSeries(d.get(1));
        XYChart.Series rgB = regressionB.generateMockData();
        rgB.setName(String.format("Regression - %s", selectedCountries[1]));

        d.add(rgA);
        d.add(rgB);
        generateRegressionReport(regressionA, regressionB, selectedCountries[0], selectedCountries[1]);

        caseDeathChart.setData(d);
    }

    /**
     * Compares regression model and generate a conclusion that which country is better.
     *
     * @param rgA      regression model of the first country.
     * @param rgB      regression model of the second country.
     * @param countryA first country's name.
     * @param countryB second country's name.
     */
    void generateRegressionReport(LinearRegression rgA, LinearRegression rgB, String countryA, String countryB) {
        String output = "Regression report:\n\n";
        output += String.format("Relationship of %s - %s\n", countryA, rgA.toString());
        output += String.format("Relationship of %s - %s\n\n", countryB, rgB.toString());

        if (rgA.slope() == rgB.slope()) {
            output += String.format("Conclusion: both countries have equally good healthcare (%.3f = %.3f).", rgA.slope(), rgB.slope());
        } else if (rgA.slope() > rgB.slope()) {
            output += String.format("Conclusion: %s has better healthcare since it has lower slope (%.3f < %.3f).", countryA, rgB.slope(), rgA.slope());
        } else {
            output += String.format("Conclusion: %s has better healthcare since it has lower slope (%.3f < %.3f).", countryB, rgA.slope(), rgB.slope());
        }

        regressionReport.setText(output);
    }

    /**
     * Reconfigure the range of slider.
     *
     * @param days the maximum days that a slider can represent.
     */
    void resetSliderRange(int days) {
        if (days < 0) return;
        countryASlider.setValue(0);
        countryBSlider.setValue(0);

        countryASlider.setMax(days);
        countryBSlider.setMax(days);
    }

    /**
     * Groups all ratio buttons.
     */
    public void ratioButtonInitialize() {
        dataCaseButton.setToggleGroup(ratioButtonGroups);
        dataDeathButton.setToggleGroup(ratioButtonGroups);
        dataVaccinButton.setToggleGroup(ratioButtonGroups);

        ratioButtonGroups.selectedToggleProperty().addListener((ob, oldVal, newVal) -> {
            dataInstance.focusedData = buttonDataMapping((RadioButton) ratioButtonGroups.getSelectedToggle());
        });
    }

    /**
     * Gets the default values from UI and initialize the data model with the values.
     */
    public void initializeUIDataModel() {
        dataInstance.dataPath = textfieldDataset.textProperty();
        dataInstance.dataPath.addListener((e) -> {
            UIDataModelUtils.setAvailableCountries(dataInstance);
        });

        dataInstance.focusedData = this.getFocusedData();
        dataInstance.start = startDatePicker.getValue();
        dataInstance.end = endDatePicker.getValue();
        dataInstance.acumulativeData = acumulativeCheckButton.selectedProperty();
    }

    /**
     * Gets current selected data.
     *
     * @return current selected data
     */
    InterestedData getFocusedData() {
        if (dataCaseButton.isSelected())
            return buttonDataMapping(dataCaseButton);
        else if (dataDeathButton.isSelected())
            return buttonDataMapping(dataDeathButton);
        else
            return buttonDataMapping(dataVaccinButton);
    }

    /**
     * Get the interested data that the button represents.
     *
     * @param btn button to check
     * @return interested data
     */
    InterestedData buttonDataMapping(RadioButton btn) {
        if (dataCaseButton == btn)
            return InterestedData.ConfirmedCases;
        else if (dataDeathButton == btn)
            return InterestedData.ConfirmedDeaths;
        else
            return InterestedData.RateOfVaccination;
    }

    /**
     * Generate a table and display via TableView UI.
     *
     * @param data data that indicates dataset to search for, and the interested data
     */
    void generateTable(final UIDataModel data) {
        System.out.println(dataInstance.focusedData);
        String col1Title = "", col2Title = "";
        switch (getFocusedData()) {
            case ConfirmedCases:
                col1Title = "Total Cases";
                col2Title = "Total Cases (per 1M)";
                break;
            case ConfirmedDeaths:
                col1Title = "Total Deaths";
                col2Title = "Total Deaths (per 1M)";
                break;
            case RateOfVaccination:
                col1Title = "Fully Vaccinated";
                col2Title = "Rate of Vaccination";
                break;
        }

        dataTable.getItems().clear();
        dataTable.getColumns().clear();
        TableColumn<Map, String> country = new TableColumn("Country");
        TableColumn<Map, String> col1 = new TableColumn(col1Title);
        TableColumn<Map, String> col2 = new TableColumn(col2Title);
        dataTable.getColumns().addAll(country, col1, col2);
        String[] validDate = CheckInput.checkValidDate(dataInstance.start, dataInstance.dataPath.get());
        System.out.println(validDate[0]);

        ObservableList<String> selectedCountries = countryListView.getSelectionModel().getSelectedItems();
        Object[] ISO = dataInstance.getISOList(selectedCountries);
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        if (ISOStrings.length == 0) {
            PromptHelper.showErrorPrompt(window, "Please select at least one country");
            return;
        }
        ObservableList tableData = TableChartTask.generateTable(dataInstance.dataPath.get(), Arrays.asList(ISOStrings),
                validDate[1], getFocusedData());
        country.setCellValueFactory(new MapValueFactory<>("country"));
        col1.setCellValueFactory(new MapValueFactory<>("col1data"));
        col2.setCellValueFactory(new MapValueFactory<>("col2data"));
        switch (dataInstance.focusedData) {
            case ConfirmedCases:
                title.setText("Number of Confirmed COVID-19 Cases as of ");
                break;
            case ConfirmedDeaths:
                title.setText("Number of Confirmed COVID-19 Deaths as of ");
                break;
            case RateOfVaccination:
                title.setText("Rate of Vaccination against COVID-19 as of ");
                break;
        }
        title.setText(title.getText() + validDate[1]);
        dataTable.getItems().addAll(tableData);
    }

    /**
     * Shifting the data according to countryASlider and countryBSlider.
     */
    void shiftingData() {
        long shiftA = (int) countryASlider.valueProperty().get();
        long shiftB = (int) countryBSlider.valueProperty().get();


        shiftCountryData(unshifted.get(0), shifted.get(0), shiftA);
        shiftCountryData(unshifted.get(1), shifted.get(1), shiftB);

        XYChart.Series<String, Float> s1 = DeepCopyUtils.copySeries(shifted.get(0));
        XYChart.Series<String, Float> s2 = DeepCopyUtils.copySeries(shifted.get(1));

        compareChart.setAnimated(false);
        compareChart.getData().clear();
        compareChart.layout();
        compareChart.getData().add(s1);
        compareChart.getData().add(s2);

    }

    /**
     * Shifts a series of confirmed cases data for a country.
     *
     * @param original unshifted data
     * @param updated reference to the shifted data, will be modified
     * @param days days to shift
     */
    void shiftCountryData(XYChart.Series<String, Float> original, XYChart.Series<String, Float> updated, long days) {
        if (days <= 0) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Iterator<XYChart.Data<String, Float>> ptr = original.getData().iterator();
        Iterator<XYChart.Data<String, Float>> updatePtr = updated.getData().iterator();

        while (ptr.hasNext()) {
            XYChart.Data<String, Float> d = ptr.next();
            XYChart.Data<String, Float> updateD = updatePtr.next();
            LocalDate newDate = LocalDate.parse(d.getXValue(), formatter).plusDays(days);
            updateD.setXValue(newDate.toString());
        }

        updated.getData().sort(Comparator.comparing(XYChart.Data::getXValue));
    }

    /**
     * Fetches the confirmed cases for two selected countries and display the results on the chart.
     *
     * @param data data model
     * @throws Exception the reason why cannot generate the result
     */
    void generateComparisonChart(final UIDataModel data) throws Exception {
        compareChart.getData().clear();

        String iDataset = data.dataPath.get();

        String[] selectedCountries = new String[]{
                countryAListView.getSelectionModel().getSelectedItem(),
                countryBListView.getSelectionModel().getSelectedItem(),
        };

        if (selectedCountries[0] == null || selectedCountries[1] == null) {
            throw new Exception("Please select two countries.");
        }

        Object[] ISO = dataInstance.getISOList(FXCollections.observableList(Arrays.asList(selectedCountries)));
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        LocalDate iStartDate = data.start, iEndDate = data.end;
        List<String> checkPeriodInput = CheckInput.checkValidPeriod(iStartDate, iEndDate, iDataset);

        if (checkPeriodInput.size() == 1) {
            throw new Exception("Please select a valid date period.");
        } else {
            int days = (int) ChronoUnit.DAYS.between(dataInstance.start, dataInstance.end);
            resetSliderRange(days);
            compareChart.setCreateSymbols(false);
        }

        unshifted = TableChartTask.generateChart(iDataset, Arrays.asList(ISOStrings), checkPeriodInput, getFocusedData());
        shifted = FXCollections.observableArrayList();
        shifted.add(DeepCopyUtils.copySeries(unshifted.get(0)));
        shifted.add(DeepCopyUtils.copySeries(unshifted.get(1)));

        compareChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        compareChart.setData(shifted);
    }

    /**
     * @param data User input for all controls
     */
    void generateChart(final UIDataModel data) {
        chart.getData().clear();

        String iDataset = data.dataPath.get();

        ObservableList<String> selectedCountries = countryListView.getSelectionModel().getSelectedItems();
        Object[] ISO = dataInstance.getISOList(selectedCountries);
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        if (ISOStrings.length == 0) {
            PromptHelper.showErrorPrompt(window, "Please select at least one country.");
            return;
        }

        LocalDate iStartDate = data.start, iEndDate = data.end;
        List<String> checkPeriodInput = CheckInput.checkValidPeriod(iStartDate, iEndDate, iDataset);
        if (checkPeriodInput.size() == 1) {
            PromptHelper.showErrorPrompt(window, "Please select a valid date period.");
            return;
        }

        if (!checkPeriodInput.get(checkPeriodInput.size() - 1).isEmpty()) {
            PromptHelper.showInfoPrompt(window, checkPeriodInput.get(checkPeriodInput.size() - 1));
        }

        checkPeriodInput.remove(checkPeriodInput.size() - 1);
        if (checkPeriodInput.get(0).equals(checkPeriodInput.get(1))) chart.setCreateSymbols(true);
        else chart.setCreateSymbols(false);
        ObservableList<XYChart.Series<String, Float>> allData = TableChartTask.generateChart(iDataset, Arrays.asList(ISOStrings), checkPeriodInput, getFocusedData());
        switch (dataInstance.focusedData) {
            case ConfirmedCases:
                title.setText("Cumulative Confirmed COVID-19 Cases (per 1M)");
                chart.getYAxis().setLabel("Cases");
                break;
            case ConfirmedDeaths:
                title.setText("Cumulative Confirmed COVID-19 Deaths (per 1M)");
                chart.getYAxis().setLabel("Deaths");
                break;
            case RateOfVaccination:
                title.setText("Cumulative Rate of Vaccination against COVID-19");
                chart.getYAxis().setLabel("Rate");
                break;
        }
        chart.getXAxis().setLabel("Date");
        chart.setData(allData);
    }

    /**
     * @param isTask1 Check if user chooses Task 1
     */
    void showTaskUI(boolean isTask1) {
        if (isTask1) {
            title.setText("Data Table");
            showPickDateUI();
            stackShow(dataTable);
        } else {
            switch (dataInstance.focusedData) {
                case ConfirmedCases:
                    title.setText("Cumulative Confirmed COVID-19 Cases (per 1M)");
                    break;
                case ConfirmedDeaths:
                    title.setText("Cumulative Confirmed COVID-19 Deaths (per 1M)");
                    break;
                case RateOfVaccination:
                    title.setText("Cumulative Rate of Vaccination against COVID-19");
                    break;
            }
            showPickPeriodUI();
            stackShow(chart);
        }
    }

    /**
     * Hides period UI and display single date picker UI.
     */
    void showPickDateUI() {
        dataRangeTile.setText("Date");
        startDateLabel.setText("Date: ");
        startDatePicker.setVisible(true);
        endDataLabel.setVisible(false);
        endDatePicker.setVisible(false);
    }

    /**
     * Hides single date picker UI and display period UI.
     */
    void showPickPeriodUI() {
        dataRangeTile.setText("Date Range");
        startDateLabel.setText("Start date: ");
        startDatePicker.setVisible(true);
        endDataLabel.setVisible(true);
        endDatePicker.setVisible(true);
    }

    /**
     * Shows report UI.
     *
     * @param type data user is interested in
     */
    void showReportUI(InterestedData type) { // switch the UI of StackPane
        switch (type) {
            case ConfirmedCases:
                showPickPeriodUI();
                stackShow(reportA);
                break;
            case RateOfVaccination:
                showPickPeriodUI();
                countryInstruction.setVisible(false);
                stackShow(reportC);
                break;
            case ConfirmedDeaths:
                dataRangeTile.setText("");
                startDateLabel.setText("");
                endDataLabel.setVisible(false);
                startDatePicker.setVisible(false);
                endDatePicker.setVisible(false);
                countryInstruction.setVisible(false);
                stackShow(reportB);
                break;
        }
    }

    /**
     * Removes a node from stackPane.
     *
     * @param e UI to be removed
     */
    void removeFromStack(Node e) {
        try {
            stack.getChildren().remove(e);
        } catch (Exception exc) {

        }
    }

    /**
     * Shows the UI in stack. This method will ensure only the given is shown on stack.
     *
     * @param e UI to be displayed.
     */
    void stackShow(Node e) {
        removeFromStack(chart);
        removeFromStack(dataTable);
        removeFromStack(reportA);
        removeFromStack(reportB);
        removeFromStack(reportC);

        stack.getChildren().add(e);
        e.setVisible(true);
    }

    /**
     * Error check - to check the number of country selected is one or not
     * @return boolean value that error exist or not
     */
    boolean errorCheckOneCountry() {
        Object[] ISO = dataInstance.getISOList(countryListView.getSelectionModel().getSelectedItems());
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);
        if (ISOStrings.length == 0) {
            PromptHelper.showErrorPrompt(window, "Please select one country");
            return true;
        }
        if (ISOStrings.length > 1) {
            PromptHelper.showErrorPrompt(window, "Please select one country only");
            return true;
        }
        return false;
    }

    /**
     * UI output - Scatter Plot of the death cases and the confirmed rate.
     *
     * @param data user inputed for all controls
     */
    void generateChartB1(final UIDataModel data) {
        chartReportB1.getData().clear();

        Object[] ISO = dataInstance.getISOList(countryListView.getSelectionModel().getSelectedItems());
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);
        double[] regression_result = new double[3];
        String x_data = "new_cases_per_million";
        String y_data = "new_deaths_per_million";

        Series<Float, Float> scatterData = ReportTask.generateChartB(data.dataPath.get(), ISOStrings[0], x_data, y_data, regression_result, 1);
        chartReportB1.getData().addAll(scatterData);
        ResultB1.setText(ReportTask.correlationAnalysisB1(regression_result));
        taskB1correlation.setText("Correlation = " + regression_result[0] + "\nNumber of data = " + Math.round(regression_result[1]) + "\nSlope = " + regression_result[2]);
    }

    /**
     * UI output - Scatter Plot of the death cases and the vaccination rate that could the citizens be immediately vaccinated during a breakout.
     *
     * @param data user inputed for all controls
     */
    void generateChartB2(final UIDataModel data) {
        chartReportB2.getData().clear();

        Object[] ISO = dataInstance.getISOList(countryListView.getSelectionModel().getSelectedItems());
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);
        double[] regression_result = new double[3];
        String x_data = "new_deaths_per_million";
        String y_data = "new_vaccinations_smoothed_per_million";

        Series<Float, Float> scatterData = ReportTask.generateChartB(data.dataPath.get(), ISOStrings[0], x_data, y_data, regression_result, 1);
        chartReportB2.getData().addAll(scatterData);
        ResultB2.setText(ReportTask.correlationAnalysisB2(regression_result));
        taskB2correlation.setText("Correlation = " + regression_result[0] + "\nNumber of data = " + Math.round(regression_result[1]) + "\nSlope = " + regression_result[2]);
    }

    /**
     * UI output - Scatter Plot of the vaccination rate and the death cases to verify the efficiency of vaccines.
     *
     * @param data user inputed for all controls
     */
    void generateChartB3(final UIDataModel data) {
        chartReportB3.getData().clear();

        Object[] ISO = dataInstance.getISOList(countryListView.getSelectionModel().getSelectedItems());
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);
        double[] regression_result = new double[3];
        String x_data = "new_vaccinations_smoothed_per_million";
        String y_data = "new_deaths_per_million";
        int dayChecked = (int) SliderReportB3.getValue();

        Series<Float, Float> scatterData = ReportTask.generateChartB(data.dataPath.get(), ISOStrings[0], x_data, y_data, regression_result, dayChecked);
        chartReportB3.getData().addAll(scatterData);
        ResultB3.setText(ReportTask.correlationAnalysisB3(regression_result, dayChecked));
        taskB3correlation.setText("Correlation = " + regression_result[0] + "\nNumber of data = " + Math.round(regression_result[1]) + "\nSlope = " + regression_result[2]);
    }

    void generateChartC1(final UIDataModel data) {
        // no need to check input, just use the dataset
        chartReportC1.getData().clear();
        tableReportC1.getItems().clear();
        tableReportC1.getColumns().clear();
        String iDataset = data.dataPath.get();
        ObservableList<XYChart.Series<String, Float>> chartData = ReportTask.generateChartC1(iDataset);
        chartReportC1.setData(chartData);
        // generate table of countries in different quartiles
        String[] quartiles = {"Quartile 1", "Quartile 2", "Quartile 3", "Quartile 4"};

        for (int i = 0; i < 4; ++i) {
            TableColumn<Map, String> q = new TableColumn(quartiles[i]);
            q.setCellValueFactory(new MapValueFactory<>(quartiles[i]));
            tableReportC1.getColumns().add(q);
        }
        ObservableList tableData = ReportTask.generateTableC1();
        tableReportC1.getItems().addAll(tableData);
        chartReportC1Title.setVisible(true);
    }

    /**
     * @param data User input for all controls
     */
    void generateChartC2(final UIDataModel data) {
        // no need to check input, just use the dataset
        chartReportC2.getData().clear();
        List<LocalDate> period = DataAnalysis.getValidPeriod(data.dataPath.get());
        LocalDate lastDate = period.get(1);
        ObservableList scatterData = ReportTask.generateChartC2(data.dataPath.get(), lastDate);
        chartReportC2.setData(scatterData);
        System.out.println("Got some data");
        String title = "Figure 2. Scatter plot of vaccination rates against HDI as of "
                + lastDate.toString() + " labelled by continents. While not all countries "
                + "with high HDI have high vaccination rates, most countries with "
                + "low HDI have minimal vaccination rates.";
        chartReportC2Title.setText(title);
        chartReportC2Title.setVisible(true);
    }

    /**
     * @param data User input for all controls
     */
    void generateChartC3(final UIDataModel data) {
        chartReportC3.getData().clear();

        String iDataset = data.dataPath.get();

        ObservableList<String> selectedCountries = countryListView.getSelectionModel().getSelectedItems();
        Object[] ISO = dataInstance.getISOList(selectedCountries);
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        if (ISOStrings.length == 0) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Please select at least one country");
            error.show();
            return;
        }
        if (ISOStrings.length > 1) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Please select one country only");
            error.show();
            return;
        }

        LocalDate iStartDate = data.start, iEndDate = data.end;
        List<String> checkPeriodInput = CheckInput.checkValidPeriod(iStartDate, iEndDate, iDataset);
        if (checkPeriodInput.size() == 1) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Please select a valid date period");
            error.show();
            return;
        }
        if (!checkPeriodInput.get(checkPeriodInput.size() - 1).isEmpty()) {
            Alert info = new Alert(AlertType.INFORMATION);
            info.setContentText(checkPeriodInput.get(checkPeriodInput.size() - 1));
            info.show();
        }
        checkPeriodInput.remove(checkPeriodInput.size() - 1);
        if (checkPeriodInput.get(0).equals(checkPeriodInput.get(1))) chartReportC3.setCreateSymbols(true);
        else chartReportC3.setCreateSymbols(false);
        ObservableList<XYChart.Series<String, Float>> allData = ReportTask.generateChartC3(iDataset, ISOStrings[0], checkPeriodInput);
        if (allData == null) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Insufficient information from selected country");
            error.show();
            return;
        }
        chartReportC3Title.setVisible(true);
        chartReportC3.setData(allData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
    }
}

