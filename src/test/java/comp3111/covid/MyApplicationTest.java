package comp3111.covid;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.Semaphore;

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
        Scene scene =  new Scene(root);
        stage.setScene(scene);
        stage.setTitle("COMP3111H - COVID Data Visualization");
    }

    @Test
    public void regressionReort() {
        LinearRegression tmp = new LinearRegression(new double[]{0.0, 1.0}, new double[]{0.0, 1.0});

        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            controller.generateRegressionReport(tmp, tmp, "C1", "C2");
        });

        Assertions.assertThat(controller.regressionReport.getText()).contains("C1");
        Assertions.assertThat(controller.regressionReport.getText()).contains("C2");
    }

    @Test void generateChartC() {
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            controller.generateChartC1(controller.dataInstance);
            controller.generateChartC2(controller.dataInstance);
            controller.generateChartC3(controller.dataInstance);
        });

        
    }
}
