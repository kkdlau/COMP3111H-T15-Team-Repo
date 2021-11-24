package comp3111.covid;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Building on the sample skeleton for 'ui.fxml' Controller Class generated by SceneBuilder
 */
public class Controller {

    @FXML
    private TextField textfieldDataset;

    @FXML
    private Tab c3Tab;

    @FXML
    private Tab tabTask12;

    @FXML
    private Tab a3Tab;

    @FXML
    private Tab b3Tab;

    @FXML
    private CheckBox acumulativeCheckButton;

    @FXML
    private ListView<String> countryListView;

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
    private LineChart chart;

    @FXML
    private NumberAxis chartXAxis;

    @FXML
    private CategoryAxis chartYAxis;

    @FXML
    private StackPane stack;

    @FXML
    private VBox rightUI;

    @FXML
    private HBox rootUI;

    @FXML
    private TabPane tabGroup;

    @FXML
    private Label title;


    ToggleGroup ratioButtonGroups = new ToggleGroup();

    UIDataModel dataInstance = new UIDataModel();


    public void initialize() {
        // default data for data pickers
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        stack.getChildren().remove(chart);
        stack.getChildren().remove(dataTable);
        chartXAxis.setAutoRanging(true);
        chartYAxis.setAutoRanging(true);


        this.updateUIDataModel();

        this.ratioButtonInitialize();
        
        // Let list view listens to changes of getAvailableCountries().
        // So that when a new dataset is loaded, the list will update also.
        countryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.listViewSubscribe(dataInstance.getAvailableCountries());

        textfieldDataset.textProperty().addListener((observable, oldValue, newValue) -> {
            UIDataModelUtils.setDataPath(dataInstance, newValue);
        });

        startDatePicker.valueProperty().addListener((ob, oldV, newV) -> dataInstance.start = newV);

        endDatePicker.valueProperty().addListener((ob, oldV, newV) -> dataInstance.end = newV);

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

        tabGroup.getSelectionModel().selectedItemProperty().addListener(onTabChanged);

        UIDataModelUtils.setDataPath(dataInstance, textfieldDataset.getText());
        showTaskUI(!dataInstance.acumulativeData);
    }

    ChangeListener<Tab> onTabChanged = (ov, disSelected, selected) -> {
        if (disSelected == tabTask12) {
            rootUI.getChildren().remove(rightUI);
        } else if (disSelected == a3Tab) {
            rootUI.getChildren().remove(rightUI);
            rightUI.getChildren().add(stack);
        } else if (disSelected == b3Tab) {

        } else if (disSelected == c3Tab) {

        }

        if (selected == tabTask12) {
            rootUI.getChildren().add(rightUI);
        } else if (selected == a3Tab) {
            rootUI.getChildren().add(rightUI); // reuse the right UI
            rightUI.getChildren().remove(stack); // but don't keep the stack
            title.setText("COVID-19 Confirmed Cases Report"); // update title
        } else if (selected == b3Tab) {

        } else if (selected == c3Tab) {

        }
    };

    public void ratioButtonInitialize() {
        dataCaseButton.setToggleGroup(ratioButtonGroups);
        dataDeathButton.setToggleGroup(ratioButtonGroups);
        dataVaccinButton.setToggleGroup(ratioButtonGroups);

        ratioButtonGroups.selectedToggleProperty().addListener((ob, oldVal, newVal) -> {
            dataInstance.focusedData = buttonDataMapping((RadioButton) ratioButtonGroups.getSelectedToggle());
            showTaskUI(!dataInstance.acumulativeData);
        });
    }

    public void listViewSubscribe(ObservableList src) {
        countryListView.setItems(src);
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
        String[] validDate = CheckInput.checkValidDate(dataInstance.start, dataInstance.dataPath);
        System.out.println(validDate[0]);

        // todo: make it support any interest of data
        ObservableList<String> selectedCountries = countryListView.getSelectionModel().getSelectedItems();
        Object[] ISO = dataInstance.getISOList(selectedCountries);
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        if (ISOStrings.length == 0) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Please select at least one country");
            error.show();
            return;
        }
        // for debugging purpose
//        System.out.println(dataInstance.dataPath);
//        System.out.println(Arrays.toString(ISOStrings));
//        System.out.println(validDate[1]);

        ObservableList tableData = VaccinationRate.generateVacTable(dataInstance.dataPath, Arrays.asList(ISOStrings),
                validDate[1], getFocusedData());
        country.setCellValueFactory(new MapValueFactory<>("country"));
        col1.setCellValueFactory(new MapValueFactory<>("col1data"));
        col2.setCellValueFactory(new MapValueFactory<>("col2data"));

        dataTable.getItems().addAll(tableData);
    }

    void generateChart(final UIDataModel data) {
        chart.getData().clear();

        String iDataset = data.dataPath;

        ObservableList<String> selectedCountries = countryListView.getSelectionModel().getSelectedItems();
        Object[] ISO = dataInstance.getISOList(selectedCountries);
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);

        if (ISOStrings.length == 0) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Please select at least one country");
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
        if (checkPeriodInput.get(0).equals(checkPeriodInput.get(1))) chart.setCreateSymbols(true);
        else chart.setCreateSymbols(false);
        ObservableList<XYChart.Series<String, Float>> allData = VaccinationRate.generateVacChart(iDataset, Arrays.asList(ISOStrings), checkPeriodInput, getFocusedData());
        chart.setData(allData);
    }

    void showTaskUI(Boolean isTask1) {
        if (isTask1) {
            title.setText("Data Table");
            dataRangeTile.setText("Date");
            startDateLabel.setText("Date: ");
            endDataLabel.setVisible(false);
            endDatePicker.setVisible(false);
            stack.getChildren().remove(chart);
            stack.getChildren().add(dataTable);
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
            dataRangeTile.setText("Date Range");
            startDateLabel.setText("Start date: ");
            endDataLabel.setVisible(true);
            endDatePicker.setVisible(true);
            stack.getChildren().remove(dataTable);
            stack.getChildren().add(chart);
        }
    }


    /**
     * Table C
     * To be triggered by "Get Vaccination Rate" button on the Table C tab.
     */
    /**
     @FXML void doRateOfVacTable(ActionEvent event) {
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
     **/
/**
 @FXML void doRateOfVacChart(ActionEvent event) {
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
 **/
}

