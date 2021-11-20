package comp3111.covid;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class UIDataModel {
    private ObservableList<String> availableCountries = FXCollections.observableArrayList();

    /**
     * Selected data.
     *
     * It should be updated only if the user click the group of ratio button.
     */
    public InterestedData focusedData = InterestedData.ConfirmedCases;

    /**
     * Path where the dataset locates at.
     */
    public String dataPath = null;

    /**
     * If it is set to true, the calculation should be acumulative.
     */
    public boolean acumulativeData = false;

    /**
     * The range of date for searching data.
     */
    public LocalDate start = null, end = null;


    public Map<String, String> ISORepresentation = null;

    public ObservableList<String> getAvailableCountries() {
        return availableCountries;
    }
}
