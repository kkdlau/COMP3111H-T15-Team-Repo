package comp3111.covid;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.ScatterChart;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.*;
import java.time.LocalDate;

/**
 * Building on the sample skeleton for 'ui.fxml' Controller Class generated by SceneBuilder
 */
public class Controller {
	
	@FXML
	private Tab tabTaskZero;
    @FXML
    private TextField textfieldDataset;

    @FXML
    private Tab tabTask12;

    @FXML
    private Tab a3Tab;

    @FXML
    private CheckBox acumulativeCheckButton;

    @FXML
    private Label countryInstruction;
    
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

    // Report B
    @FXML
    private Tab b3Tab;
    @FXML
    private ScrollPane reportB;
    @FXML
    private ScatterChart chartReportB1,chartReportB2,chartReportB3;
    @FXML
    private Button buttonReportB1;
    @FXML
    private Label taskB1correlation,taskB2correlation,taskB3correlation, LabelSliderReportB3;
    @FXML
    private Label ResultB1,ResultB2,ResultB3;
    @FXML
    private Slider SliderReportB3;
    
    // Report C
    @FXML
    private Tab c3Tab;
    @FXML
    private ScrollPane reportC;
    @FXML
    private Button buttonReportC1, buttonReportC3;
    @FXML
    private LineChart chartReportC1, chartReportC3;
    @FXML
    private ScatterChart chartReportC2;
    @FXML
    private TableView tableReportC1;

    @FXML
    private HBox rootUI;
    @FXML
    private VBox rightUI;

    @FXML
    private TabPane tabGroup;


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

        /**
         * Let list view listens to changes of getAvailableCountries().
         * So that when a new dataset is loaded, the list will update also.
         */
        countryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.listViewSubscribe(dataInstance.getAvailableCountries());

        textfieldDataset.textProperty().addListener((observable, oldValue, newValue) -> {
            UIDataModelUtils.setDataPath(dataInstance, newValue);
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

        tabGroup.getSelectionModel().selectedItemProperty().addListener(onTabChanged);

        UIDataModelUtils.setDataPath(dataInstance, textfieldDataset.getText());
        showTaskUI(!dataInstance.acumulativeData);
        
        buttonReportB1.setOnAction((e) -> {
        	errorCheck_oneCountry();
        	this.generateChartB1(dataInstance);
        	this.generateChartB2(dataInstance);
        	this.generateChartB3(dataInstance);
        });
        
        SliderReportB3.setOnMouseReleased((e) -> {
        	LabelSliderReportB3.setText((int) SliderReportB3.getValue() + "-days death cases are observed after vaccination ");
        	errorCheck_oneCountry();
        	this.generateChartB3(dataInstance);
        });
        
        buttonReportC1.setOnAction((e) -> {
        	this.generateChartC1(dataInstance);
        	this.generateChartC2(dataInstance);
        });
        buttonReportC3.setOnAction((e) -> {
        	this.generateChartC3(dataInstance);
        });
    }

    ChangeListener<Tab> onTabChanged = (ov, disSelected, selected) -> {
   
        if (disSelected == tabTaskZero) {
            //rootUI.getChildren().remove(rightUI);
        } else if (disSelected == a3Tab) {

        } else if (disSelected == b3Tab) {

        } else if (disSelected == c3Tab) {
        	//rootUI.getChildren().remove(reportC);
        	countryInstruction.setVisible(true);
        }
        if (selected == tabTaskZero) {
            //rootUI.getChildren().add(rightUI);
            //rootUI.getChildren().remove(reportC);
        	this.showTaskUI(!dataInstance.acumulativeData);
        } else if (selected == a3Tab) {

        } else if (selected == b3Tab) {
        	this.showReportUI(InterestedData.ConfirmedDeaths);

        } else if (selected == c3Tab) {
        	//rootUI.getChildren().remove(rightUI)
        	//rootUI.getChildren().add(reportC);
        	this.showReportUI(InterestedData.RateOfVaccination);
        }
    };

    public void ratioButtonInitialize() {
        dataCaseButton.setToggleGroup(ratioButtonGroups);
        dataDeathButton.setToggleGroup(ratioButtonGroups);
        dataVaccinButton.setToggleGroup(ratioButtonGroups);

        ratioButtonGroups.selectedToggleProperty().addListener((ob, oldVal, newVal) -> {
            dataInstance.focusedData = buttonDataMapping((RadioButton) ratioButtonGroups.getSelectedToggle());
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

        ObservableList tableData = TableChartTask.generateTable(dataInstance.dataPath, Arrays.asList(ISOStrings), 
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
        ObservableList<XYChart.Series<String, Float>> allData = TableChartTask.generateChart(iDataset, Arrays.asList(ISOStrings), checkPeriodInput, getFocusedData());
        chart.setData(allData);
    }

    void showTaskUI(Boolean isTask1) {
        if (isTask1) {
            dataRangeTile.setText("Date");
            startDateLabel.setText("Date: ");
            endDataLabel.setVisible(false);
            startDatePicker.setVisible(true);
            endDatePicker.setVisible(false);
            stackShow(dataTable);
        } else {
            dataRangeTile.setText("Date Range");
            startDateLabel.setText("Start date: ");
            endDataLabel.setVisible(true);
            startDatePicker.setVisible(true);
            endDatePicker.setVisible(true);
            stackShow(chart);
        }
    }
    
    void showReportUI(InterestedData type) { // switch the UI of StackPane
    	switch(type) {
    	case RateOfVaccination:
    		dataRangeTile.setText("Date Range"); 
            startDateLabel.setText("Start date: ");
            endDataLabel.setVisible(true);
            startDatePicker.setVisible(true);
            endDatePicker.setVisible(true);
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
    
    void stackShow(Node e) {
        stack.getChildren().remove(chart);
    	stack.getChildren().remove(dataTable);
    	stack.getChildren().remove(reportC);
    	stack.getChildren().remove(reportB);
        stack.getChildren().add(e);
    	
    }
    /**
     * UI output - Chart for the average cumulative number of vaccinations for countries in different GDP quartiles
     * @param data
     */

    boolean errorCheck_oneCountry() {
    	Object[] ISO = dataInstance.getISOList(countryListView.getSelectionModel().getSelectedItems());
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);
        if (ISOStrings.length == 0) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Please select one country");
            error.show();
            return true;
        }
        if (ISOStrings.length > 1) {
        	Alert error = new Alert(AlertType.ERROR);
        	error.setContentText("Please select one country only");
        	error.show(); 
        	return true;
        }
        return false;
    }
    
	void generateChartB1(final UIDataModel data) {
    	chartReportB1.getData().clear();
    	
        Object[] ISO = dataInstance.getISOList(countryListView.getSelectionModel().getSelectedItems());
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);
        double[] regression_result = new double[3];
        String x_data = "new_cases_per_million";
        String y_data = "new_deaths_per_million";
        
    	Series<Float, Float> scatterData = ReportTask.generateChartB(data.dataPath, ISOStrings[0],x_data,y_data,regression_result,1);
    	chartReportB1.getData().addAll(scatterData);
    	ResultB1.setText(ReportTask.correlation_analysis_B1(regression_result));
    	taskB1correlation.setText("Correlation = " + regression_result[0] +"\nNumber of data = " + Math.round(regression_result[1])+"\nSlope = "+regression_result[2]);
    }
	
	void generateChartB2(final UIDataModel data) {
    	chartReportB2.getData().clear();
    	
        Object[] ISO = dataInstance.getISOList(countryListView.getSelectionModel().getSelectedItems());
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);
        double[] regression_result = new double[3];
        String x_data = "new_deaths_per_million";
        String y_data = "new_vaccinations_smoothed_per_million";
        
        Series<Float, Float> scatterData = ReportTask.generateChartB(data.dataPath, ISOStrings[0],x_data,y_data,regression_result,1);
    	chartReportB2.getData().addAll(scatterData);
    	ResultB2.setText(ReportTask.correlation_analysis_B2(regression_result));
    	taskB2correlation.setText("Correlation = " + regression_result[0] +"\nNumber of data = " + Math.round(regression_result[1])+"\nSlope = "+regression_result[2]);
    }
	
	void generateChartB3(final UIDataModel data) {
    	chartReportB3.getData().clear();
    	
        Object[] ISO = dataInstance.getISOList(countryListView.getSelectionModel().getSelectedItems());
        String[] ISOStrings = Arrays.copyOf(ISO, ISO.length, String[].class);
        double[] regression_result = new double[3];
        String x_data = "new_vaccinations_smoothed_per_million";
        String y_data = "new_deaths_per_million";
        int dayChecked = (int) SliderReportB3.getValue();
        
        Series<Float, Float> scatterData = ReportTask.generateChartB(data.dataPath, ISOStrings[0],x_data,y_data,regression_result, dayChecked);
    	chartReportB3.getData().addAll(scatterData);
    	ResultB3.setText(ReportTask.correlation_analysis_B3(regression_result,dayChecked));
    	taskB3correlation.setText("Correlation = " + regression_result[0] +"\nNumber of data = " + Math.round(regression_result[1])+"\nSlope = "+regression_result[2]);
    }
    
    void generateChartC1(final UIDataModel data) {
    	// no need to check input, just use the dataset
    	chartReportC1.getData().clear();
    	tableReportC1.getItems().clear();
    	tableReportC1.getColumns().clear();
    	String iDataset = data.dataPath;
    	ObservableList<XYChart.Series<String, Float>> chartData = ReportTask.generateChartC1(iDataset);
    	chartReportC1.setData(chartData);
    	// generate table of countries in different quartiles 
    	TableColumn<Map, String> q1 = new TableColumn("Quartile 1");
    	TableColumn<Map, String> q2 = new TableColumn("Quartile 2");
    	TableColumn<Map, String> q3 = new TableColumn("Quartile 3");
    	TableColumn<Map, String> q4 = new TableColumn("Quartile 4");
    	tableReportC1.getColumns().addAll(q1, q2, q3, q4);
    	ObservableList tableData = ReportTask.generateTableC1();
    	q1.setCellValueFactory(new MapValueFactory<>("q1"));
    	q2.setCellValueFactory(new MapValueFactory<>("q2"));
    	q3.setCellValueFactory(new MapValueFactory<>("q3"));
    	q4.setCellValueFactory(new MapValueFactory<>("q4"));
    	tableReportC1.getItems().addAll(tableData);
    }
    void generateChartC2(final UIDataModel data) {
    	// no need to check input, just use the dataset 
    	chartReportC2.getData().clear();
    	ObservableList scatterData = ReportTask.generateChartC2(data.dataPath);
    	chartReportC2.setData(scatterData);
    	System.out.println("Got some data");
    }
    void generateChartC3(final UIDataModel data) {
    	chartReportC3.getData().clear();

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
        chartReportC3.setData(allData);
    }
}

