package comp3111.covid;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ReportTaskTest {
	
	String dataset;
	String ISO_code;
	String x_axis,y_axis;
	final String blank="";
	double[][] resultArrayReportB = new double[10][3];
	
	@Before
	public void setup() {
		dataset = "COVID_Dataset_V1.0.csv";
		ISO_code = "AFG";
		x_axis = "new_cases_per_million";
		y_axis = "new_deaths_per_million";
		for(int i=0;i<10;i++) {
			resultArrayReportB[i][0] = 0.8-(i%5)*0.4; //The correlation set is {0.8,0.4,0.0,-0.4,-0.8}
			resultArrayReportB[i][1] = (i>4)?10:2; //The set of the number of data is {10,2}
			resultArrayReportB[i][2] = Math.random();//The slope is a random number
		}
	}
	@Test
	public void generateChartBValid() {
		double[] result = new double[3];
		ReportTask.generateChartB(dataset,ISO_code,x_axis,y_axis,result,1);
		ReportTask.generateChartB(dataset,ISO_code,x_axis,y_axis,result,14);
	}
	@Test
	public void generateChartBNoData() {
		double[] result = new double[3];
		ReportTask.generateChartB(dataset,"AIA",x_axis,y_axis,result,1);
		ReportTask.generateChartB(dataset,"AIA",x_axis,y_axis,result,14);
	}
	@Test
	public void correlationAnalysisB1() {
		for(double[] result: resultArrayReportB) {
			ReportTask.correlationAnalysisB1(result);
		}
	}
	@Test
	public void correlationAnalysisB2() {
		for(double[] result: resultArrayReportB) {
			ReportTask.correlationAnalysisB2(result);
		}
	}
	@Test
	public void correlationAnalysisB3() {
		for(double[] result: resultArrayReportB) {
			int dayChecked = 14+ ((int)(Math.random()*56)%7)*7;
			ReportTask.correlationAnalysisB3(result,dayChecked);
		}
	}
}