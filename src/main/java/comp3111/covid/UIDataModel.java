package comp3111.covid;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class UIDataModel {
    /**
     * Selected data.
     * <p>
     * It should be updated only if the user click the group of ratio button.
     */
    public InterestedData focusedData = InterestedData.ConfirmedCases;
    /**
     * Path where the dataset locates at.
     */
    public StringProperty dataPath;
    /**
     * If it is set to true, the calculation should be acumulative.
     */
    public BooleanProperty acumulativeData;
    /**
     * The range of date for searching data.
     */
    public LocalDate start = null, end = null;
    public Map<String, String> ISORepresentation = null;
    private ObservableList<String> availableCountries = FXCollections.observableArrayList();

    public ObservableList<String> getAvailableCountries() {
        return availableCountries;
    }

    public Object[] getISOList(ObservableList<String> countries) {
        ArrayList<String> iso = new ArrayList<>();
        for (var c : countries) {
            iso.add(ISORepresentation.get(c));
        }

        return iso.toArray();
    }

    @Override
    public String toString() {
        return "UIDataModel{" +
                ", focusedData=" + focusedData +
                ", dataPath='" + dataPath + '\'' +
                ", acumulativeData=" + acumulativeData +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
