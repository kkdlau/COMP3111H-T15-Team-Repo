import static org.junit.Assert.*;

import comp3111.covid.DataPicker;
import org.junit.Before;
import org.junit.Test;

public class DataPickerTester {
    String[] countries;
    DataPicker<String> picker;

    @Before
    public void setup() {
        countries = new String[]{"China", "USA", "Canada"};
        picker = new DataPicker<>(countries);
    }

    @Test
    public void pickCorrectCountry() {
        assertEquals(picker.pick(0), countries[0]);
    }

    @Test
    public void getAllPossibleOptions() {
        assertArrayEquals(picker.possibleOptions(), countries);
    }

}
