import comp3111.covid.UIDataModel;
import comp3111.covid.UIDataModelUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class UIDataModelUtilsTester {

	UIDataModel dataInstance;
	String newValue;
	
    @Before
    public void setup() {
    	dataInstance = new UIDataModel();
    	newValue = "";
    }
    
    @Test
    public void checkDataPathSetting() {
    	UIDataModelUtils.setDataPath(dataInstance, newValue);
    }
    
    @Test
    public void checkAvailableCountriesSetting() {
    	UIDataModelUtils.setAvailableCountries(dataInstance);
    }
}