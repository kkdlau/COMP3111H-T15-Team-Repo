package comp3111.covid;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;

public class TaskA3Controller {
    private Button chartGenerateButton;

    private ListView<String> countryAListView;

    private Slider countryASlider;

    private ListView<String> countryBListView;

    private Slider countryBSlider;

    TaskA3Controller(Button chartGenerateButton, ListView<String> countryAListView, ListView<String> countryBListView, Slider countryASlider, Slider countryBSlider) {
        this.chartGenerateButton = chartGenerateButton;
        this.countryAListView = countryAListView;
        this.countryBListView = countryBListView;
        this.countryASlider = countryASlider;
        this.countryBSlider = countryBSlider;
    }
}
