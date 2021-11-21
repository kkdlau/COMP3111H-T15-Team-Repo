package comp3111.covid;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.collections.ObservableList;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Building on the sample skeleton for 'ui.fxml' Controller Class generated by SceneBuilder
 */
public class Controller {

    @FXML
    private Tab tabTaskZero;

    @FXML
    private TextField textfieldISO;

    @FXML
    private Button buttonConfirmedDeaths;

    @FXML
    private TextField textfieldDataset;

    @FXML
    private Button buttonRateOfVaccination;

    @FXML
    private Button buttonConfirmedCases;

    @FXML
    private Tab tabReport1;

    @FXML
    private Tab tabReport2;

    @FXML
    private Label labelTableTitle;
    @FXML
    private Label labelTabTitle;
    @FXML
    private MenuButton menuButtonSelectTable;
    @FXML
    private MenuButton menuButtonSelectCountry;

    @FXML
    private Tab tabReport3;

    @FXML
    private DatePicker oneDatePickerC1;
    @FXML
    private Button buttonRateOfVacTable;
    @FXML
    private Label rateOfVacTableLabel;
    @FXML
    private TextArea textAreaCountryNamesC1;
    @FXML
    private TableView rateOfVacTable;
    @FXML
    private TableColumn vacTableColCountry;
    @FXML
    private TableColumn vacTableColFullyVac;
    @FXML
    private TableColumn vacTableColRateOfVac;


    @FXML
    private Tab tabApp1;

    @FXML
    private Tab tabApp2;

    @FXML
    private Tab tabApp3;
    @FXML
    private DatePicker startDatePickerC2;
    @FXML
    private DatePicker endDatePickerC2;
    @FXML
    private TextArea textAreaCountryNamesC2;
    @FXML
    private Button buttonRateOfVacChart;
    @FXML
    private LineChart rateOfVacChart;


    @FXML
    private CheckBox acumulativeCheckButton;

    @FXML
    private TitledPane countryFilter;

    @FXML
    private ListView<String> countryListView;

    @FXML
    private RadioButton dataCaseButton;

    @FXML
    private RadioButton dataDeathButton;

    @FXML
    private TableView<String> dataTable;

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
    private LineChart chart;


    ToggleGroup ratioButtonGroups = new ToggleGroup();

    UIDataModel dataInstance = new UIDataModel();


    public void initialize() {
        // default data for data pickers
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());

        this.updateUIDataModel();

        /**
         * Let list view listens to changes of getAvailableCountries().
         * So that when a new dataset is loaded, the list will update also.
         */
        countryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        countryListView.setItems(dataInstance.getAvailableCountries());

        textfieldDataset.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield changed from " + oldValue + " to " + newValue);
            UIDataModelUtils.setDataPath(dataInstance, newValue);
        });
        UIDataModelUtils.setDataPath(dataInstance, textfieldDataset.getText());

        dataCaseButton.setToggleGroup(ratioButtonGroups);
        dataDeathButton.setToggleGroup(ratioButtonGroups);
        dataVaccinButton.setToggleGroup(ratioButtonGroups);

        ratioButtonGroups.selectedToggleProperty().addListener((ob, oldVal, newVal) -> {
            dataInstance.focusedData = buttonDataMapping((RadioButton) ratioButtonGroups.getSelectedToggle());
            System.out.println("update focused data: " + dataInstance.focusedData.toString());
        });

        startDatePicker.valueProperty().addListener((ob, oldV, newV) -> {
            dataInstance.start = newV;
        });

        endDatePicker.valueProperty().addListener((ob, oldV, newV) -> {
            dataInstance.end = newV;
        });

        generateButton.setOnAction((e) -> {
            if (dataInstance.acumulativeData)
                this.generateChart(dataInstance);
            else
                this.generateTable(dataInstance);
        });

        acumulativeCheckButton.setOnAction(e -> {
            dataInstance.acumulativeData = acumulativeCheckButton.isSelected();
            showTaskUI(!dataInstance.acumulativeData);
        });

        showTaskUI(!dataInstance.acumulativeData);

    }

    public void updateUIDataModel() {
        dataInstance.dataPath = textfieldDataset.getText();
        dataInstance.focusedData = this.getFocusedData();
        dataInstance.acumulativeData = this.acumulativeCheckButton.isSelected();
        dataInstance.start = startDatePicker.getValue();
        dataInstance.end = endDatePicker.getValue();

        UIDataModelUtils.setAvailableCountries(this.dataInstance);
    }

    InterestedData getFocusedData() {
        if (dataCaseButton.isSelected())
            return buttonDataMapping(dataCaseButton);
        else if (dataDeathButton.isSelected())
            return buttonDataMapping(dataDeathButton);
        else
            return buttonDataMapping(dataVaccinButton);
    }

    InterestedData buttonDataMapping(RadioButton btn) {
        if (dataCaseButton == btn)
            return InterestedData.ConfirmedCases;
        else if (dataDeathButton == btn)
            return InterestedData.ConfirmedDeaths;
        else
            return InterestedData.RateOfVaccination;
    }

    void generateTable(final UIDataModel data) {
        dataTable.getItems().clear();
        dataTable.getColumns().clear();
        TableColumn<String, String> country = new TableColumn("country");
        TableColumn<String, String> vaccinated = new TableColumn("fully_vaccinated");
        TableColumn<String, String> rate = new TableColumn("rate_of_vaccination");
        dataTable.getColumns().addAll(country, vaccinated, rate);

        String[] validDate = CheckInput.checkValidDate(dataInstance.start, dataInstance.dataPath);
        System.out.println(validDate[0]);

        // todo: make it support any interest of data
        ObservableList<String> selectedCountries = countryListView.getSelectionModel().getSelectedItems();
        Object[] ISO = dataInstance.getISOList(selectedCountries);
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        // for debugging purpose
        System.out.println(dataInstance.dataPath);
        System.out.println(Arrays.toString(ISOStrings));
        System.out.println(validDate[1]);

        ObservableList tableData = VaccinationRate.generateVacTable(dataInstance.dataPath, Arrays.asList(ISOStrings), validDate[1]);


        dataTable.getItems().addAll(tableData);
    }

    void generateChart(final UIDataModel data) {
        System.out.println(data);

        chart.getData().clear();

        String iDataset = data.dataPath;

        ObservableList<String> selectedCountries = countryListView.getSelectionModel().getSelectedItems();
        Object[] ISO = dataInstance.getISOList(selectedCountries);
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        if (ISOStrings.length == 0) {
            // todo: show "no countries are selected"
            return;
        }

        LocalDate iStartDate = data.start, iEndDate = data.end;
        List<String> checkPeriodInput = CheckInput.checkValidPeriod(iStartDate, iEndDate, iDataset);
        if (checkPeriodInput.size() == 1) {
            // todo: show "invalid date period is selected"
            return;
        }
        checkPeriodInput.remove(checkPeriodInput.size() - 1);
        if (checkPeriodInput.get(0).equals(checkPeriodInput.get(1))) rateOfVacChart.setCreateSymbols(true);
        else chart.setCreateSymbols(false);
        ObservableList<XYChart.Series<String, Float>> allData = VaccinationRate.generateVacChart(Arrays.asList(ISOStrings), checkPeriodInput, iDataset);
        chart.setData(allData);
    }

    void showTaskUI(Boolean isTask1) {
        if (isTask1) {
            dataRangeTile.setText("Date");
            startDateLabel.setText("Date: ");
            endDataLabel.setVisible(false);
            endDatePicker.setVisible(false);
            chart.setVisible(false);
            dataTable.setVisible(true);
        } else {
            dataRangeTile.setText("Date Range");
            startDateLabel.setText("Start date: ");
            endDataLabel.setVisible(true);
            endDatePicker.setVisible(true);
            chart.setVisible(true);
            dataTable.setVisible(false);
        }
    }


    void selectTable(String type) {
        menuButtonSelectTable.setText(type);
        labelTabTitle.setText("Rate of " + type + " for COVID-19 by Country");
        labelTableTitle.setText("Rate of " + type + " against COVID-19 as of ...");
    }

    /**
     * Table C
     * To be triggered by "Get Vaccination Rate" button on the Table C tab.
     */
    @FXML
    void doRateOfVacTable(ActionEvent event) {
        rateOfVacTable.getItems().clear();

        String iDataset = textfieldDataset.getText(); // assume dataset specified in Task Zero

        String iLocations = textAreaCountryNamesC1.getText();
        if (iLocations.isEmpty()) {
//    		textAreaConsole.setText("Please input at least one country of interest!\n");
            return;
        }
        String errorConsole = "";

        LocalDate iDate = oneDatePickerC1.getValue();

        // parse valid IsoCodes from location
        List<String> checkLocOutput = CheckInput.checkValidLocations(iLocations, iDataset);
        errorConsole += checkLocOutput.get(checkLocOutput.size() - 1);
        if (checkLocOutput.size() == 1) {
            // todo: show error
            return;
        }
        checkLocOutput.remove(checkLocOutput.size() - 1); // valid IsoCodes

        String[] validDate = CheckInput.checkValidDate(iDate, iDataset);
        errorConsole += validDate[0];

//    	textAreaConsole.setText(errorConsole);
        DateTimeFormatter tempformatter = DateTimeFormatter.ofPattern("uuuu-M-d");
        LocalDate temp = LocalDate.parse(validDate[1], tempformatter);
        String labelDate = temp.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        rateOfVacTableLabel.setText("Rate of Vaccination against COVID-19 as of " + labelDate);
        // pass valid inputs to VaccinationRate methods
        ObservableList tableData = VaccinationRate.generateVacTable(iDataset, checkLocOutput, validDate[1]);
        vacTableColCountry.setCellValueFactory(new MapValueFactory<>("country"));
        vacTableColFullyVac.setCellValueFactory(new MapValueFactory<>("fully_vaccinated"));
        vacTableColRateOfVac.setCellValueFactory(new MapValueFactory<>("rate_of_vaccination"));

        rateOfVacTable.getItems().addAll(tableData);
    }

    @FXML
    void doRateOfVacChart(ActionEvent event) {
//    	textAreaConsole.setText(""); // clear previous output
        rateOfVacChart.getData().clear();
        String iDataset = textfieldDataset.getText(); // assume dataset specified in Task Zero
        String iLocations = textAreaCountryNamesC2.getText();
        if (iLocations.isEmpty()) {
//    		textAreaConsole.setText("Please input at least one country of interest!\n");
            return;
        }
        String errorConsole = "";
        List<String> checkLocInput = CheckInput.checkValidLocations(iLocations, iDataset);
        errorConsole += checkLocInput.get(checkLocInput.size() - 1);
        if (checkLocInput.size() == 1) {
//    		textAreaConsole.setText(errorConsole);
            return;
        }
        checkLocInput.remove(checkLocInput.size() - 1); // valid IsoCodes

        LocalDate iStartDate = startDatePickerC2.getValue(), iEndDate = endDatePickerC2.getValue();
        List<String> checkPeriodInput = CheckInput.checkValidPeriod(iStartDate, iEndDate, iDataset);
        errorConsole += checkPeriodInput.get(checkPeriodInput.size() - 1);
        if (checkPeriodInput.size() == 1) {
//    		textAreaConsole.setText(errorConsole);
            return;
        }
        checkPeriodInput.remove(checkPeriodInput.size() - 1);
        if (checkPeriodInput.get(0).equals(checkPeriodInput.get(1))) rateOfVacChart.setCreateSymbols(true);
        else rateOfVacChart.setCreateSymbols(false);
        ObservableList<XYChart.Series<String, Float>> allData = VaccinationRate.generateVacChart(checkLocInput, checkPeriodInput, iDataset);
        rateOfVacChart.setData(allData);
//    	textAreaConsole.setText(errorConsole);

    }
}

