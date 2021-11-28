package comp3111.covid;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.*;

import java.time.temporal.ChronoUnit;

public class ReportTaskTester {
	String mockData = "COVID_Dataset_v1.0small.csv";
	String realDataset = "COVID_Dataset_v1.0.csv";
	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern( "uuuu-M-d" );
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void generateChartC3Valid() {		
		// date range no empty 
		LocalDate start = LocalDate.parse("2020-9-6", inputFormatter);
		LocalDate end = LocalDate.parse("2020-9-6", inputFormatter);
		List<String> period = Arrays.asList(start.toString(), end.toString());
		
		ObservableList<XYChart.Series<String, Float>> output =
				ReportTask.generateChartC3(realDataset, "ISR", period);
		XYChart.Series<String, Float> vacData = output.get(0);
		XYChart.Series<String, Float> icuData = output.get(1);
		XYChart.Series<String, Float> hospData = output.get(2);
		
		XYChart.Series<String, Float> vacPred = new XYChart.Series<String, Float>();
		vacPred.getData().add(new XYChart.Data(start.toString(), 0.0f));
		XYChart.Series<String, Float> icuPred = new XYChart.Series<String, Float>();
		icuPred.getData().add(new XYChart.Data(start.toString(), Float.parseFloat("15.366") / 10));
		XYChart.Series<String, Float> hospPred = new XYChart.Series<String, Float>();
		hospPred.getData().add(new XYChart.Data(start.toString(), Float.parseFloat("130.783") / 10));
		
		boolean vac = vacData.getData().toString().equals(vacPred.getData().toString());
		boolean icu = icuData.getData().toString().equals(icuPred.getData().toString());
		boolean hosp = hospData.getData().toString().equals(hospPred.getData().toString());
		
		System.out.println(vacData.getData().toString() + " " + vacPred.getData().toString());
		System.out.println(icuData.getData().toString() + " " + icuPred.getData().toString());
		System.out.println(hospData.getData().toString() + " " + hospPred.getData().toString());
		
		assertTrue(vac && icu && hosp);
	}
	
	@Test 
	public void generateChartC3ICUEmpty() {
		// date range empty for icu
		// August 3 2020, hosp 106.521, Israel >> null
		LocalDate start = LocalDate.parse("2020-8-3", inputFormatter);
		LocalDate end = LocalDate.parse("2020-8-3", inputFormatter);
		List<String> period = Arrays.asList(start.toString(), end.toString());
		
		ObservableList<XYChart.Series<String, Float>> output =
				ReportTask.generateChartC3(realDataset, "ISR", period);
		
		assertNull(output);
	}
	
	@Test 
	public void generateChartC3HospEmpty() {
		// Algeria DZA 2020 Dec 11, null because dont have 
		LocalDate start = LocalDate.parse("2020-12-11", inputFormatter);
		LocalDate end = LocalDate.parse("2020-12-11", inputFormatter);
		List<String> period = Arrays.asList(start.toString(), end.toString());
		
		ObservableList<XYChart.Series<String, Float>> output =
				ReportTask.generateChartC3(realDataset, "DZA", period);
		
		assertNull(output);			
	}

}