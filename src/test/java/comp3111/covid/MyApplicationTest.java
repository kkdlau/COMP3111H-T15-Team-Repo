package comp3111.covid;

import javafx.fxml.FXMLLoader;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MyApplicationTest extends ApplicationTest {

    private static final String UI_FILE = "/ui.fxml";  //file in the folder of src/main/resources/

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     */

    private Controller controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(UI_FILE));
        HBox root = (HBox) loader.load();

        Controller c = loader.getController();
        controller = c;

        c.setStage(stage);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("COMP3111H - COVID Data Visualization");
    }

    @Test
    public void regressionReort() {
        LinearRegression tmp = new LinearRegression(new double[]{0.0, 1.0}, new double[]{0.0, 1.0});
        LinearRegression tmp2 = new LinearRegression(new double[]{0.0, 1.0}, new double[]{0.0, 50.0});

        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.generateRegressionReport(tmp, tmp, "C1", "C2");
            controller.generateRegressionReport(tmp, tmp2, "C1", "C2");
        }));

        Assertions.assertThat(controller.regressionReport.getText()).contains("C1");
        Assertions.assertThat(controller.regressionReport.getText()).contains("C2");
    }

    @Test
    public void generateChartC() {
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.generateChartC1(controller.dataInstance);
            controller.generateChartC2(controller.dataInstance);
            controller.generateChartC3(controller.dataInstance);
        }));


        Assertions.assertThat(controller.chartReportC1Title.isVisible()).isEqualTo(true);
        Assertions.assertThat(controller.chartReportC2Title.isVisible()).isEqualTo(true);
    }

    @Test
    public void errorCheckOneCountryTest() {
        AtomicBoolean b = new AtomicBoolean(false);

        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            b.set(controller.errorCheckOneCountry());
        }));
        Assertions.assertThat(b.get()).isEqualTo(true);
    }

    @Test
    public void stackAddTest() {
        Button tmp = new Button();
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.stackShow(tmp);
        }));

        Assertions.assertThat(tmp.isVisible()).isEqualTo(true);
    }

    @Test
    public void showReportUITest() {
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.showReportUI(InterestedData.ConfirmedCases);
        }));
        Assertions.assertThat(controller.reportA.isVisible()).isEqualTo(true);


        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.showReportUI(InterestedData.ConfirmedDeaths);
        }));
        Assertions.assertThat(controller.reportB.isVisible()).isEqualTo(true);

        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.showReportUI(InterestedData.RateOfVaccination);
        }));
        Assertions.assertThat(controller.reportC.isVisible()).isEqualTo(true);
    }

    @Test
    public void sliderResetTest() {
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.resetSliderRange(10);
        }));

        Assertions.assertThat(controller.countryASlider.getMax()).isEqualTo(10.0);
    }

    @Test
    public void generateRegressionChartTest() {
        AtomicReference<Exception> e = new AtomicReference<>();
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            try {
                controller.generateRegressionChart(controller.dataInstance);
            } catch (Exception exc) {
                e.set(exc);
            }
        }));

        Assertions.assertThat(e.get().getMessage()).contains("Please");
    }

    @Test
    public void tabInitializeTest() {
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            try {
                controller.initialize();
                controller.tabTaskA3Initialize();
                controller.tabTaskB3Initialize();
                controller.tabTaskC3Initialize();
            } catch (Exception exc) {
                Assertions.fail("fail to initialize tabs.");
            }
        }));
    }

    @Test
    public void tabClickingTest() {
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.tabGroup.getSelectionModel().select(1);
        }));
        Assertions.assertThat(controller.reportA.isVisible()).isEqualTo(true);


        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.tabGroup.getSelectionModel().select(2);
        }));
        Assertions.assertThat(controller.reportB.isVisible()).isEqualTo(true);


        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.tabGroup.getSelectionModel().select(3);
        }));
        Assertions.assertThat(controller.reportC.isVisible()).isEqualTo(true);
    }

    @Test
    public void shiftCountryDataTest() {
        XYChart.Series<String, Float> d = new XYChart.Series<>();
        d.getData().add(new XYChart.Data<>("2020-01-01", 1.0F));
        d.getData().add(new XYChart.Data<>("2020-01-02", 1.0F));

        XYChart.Series<String, Float> d2 = DeepCopyUtils.copySeries(d);

        controller.shiftCountryData(d, d2, 10);

        Assertions.assertThat(d.getData().size()).isEqualTo(d2.getData().size());
    }

    @Test public void chartTableTest() {
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.generateChart(controller.dataInstance);
            controller.generateTable(controller.dataInstance);
        }));

        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.countryListView.getSelectionModel().select(0);
            controller.generateTable(controller.dataInstance);
        }));
    }

    @Test
    public void ratioButtonTest() {
        WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx(() -> {
            controller.ratioButtonInitialize();
            controller.ratioButtonGroups.getSelectedToggle().selectedProperty().set(true);
            controller.getFocusedData();
        }));

        Assertions.assertThat(controller.buttonDataMapping(null)).isEqualTo(InterestedData.RateOfVaccination);
    }
}
