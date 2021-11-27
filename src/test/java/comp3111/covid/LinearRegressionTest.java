import comp3111.covid.LinearRegression;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.LocalDate;


public class LinearRegressionTest {
	int array1Size;
	double[] x1;
	double[] y1;
	int array2Size;
	double[] x2;
	double[] y2;
	
	@Before
	public void setup() {
		array1Size = 4;
		array2Size = 6;
		y1 = x1 = new double[array1Size];
		y2 = x2 = new double[array2Size];
		for(double d:x1) {d = Math.random();}
		for(double d:y1) {d = Math.random();}
		for(double d:x2) {d = Math.random();}
		for(double d:y2) {d = Math.random();}
	}
	@Test
	public void LinearRegressionConstructorNull() {
		LinearRegression model1 = new LinearRegression(null,y1);
		LinearRegression model2 = new LinearRegression(x1,null);
	}
	@Test
	public void LinearRegressionConstructorUnmatch() {
		LinearRegression model = new LinearRegression(x2,y1);
	}
	@Test
	public void LinearRegressionConstructorValid() {
		LinearRegression model1 = new LinearRegression(x1,y1);
		LinearRegression model2 = new LinearRegression(x2,y2);
	}
	@Test
	public void LinearRegressionAccessorMethod() {
		LinearRegression model = new LinearRegression(x1,y1);
		model.intercept();
		model.slope();
		model.R2();
		model.interceptStdErr();
		model.slopeStdErr();
		model.toString();
		model.generateMockData();
	}
	@Test
	public void LinearRegressionPredict() {
		LinearRegression model = new LinearRegression(x1,y1);
		model.predict(-1);
		model.predict(0);
		model.predict(1);
	}
}