import comp3111.covid.PromptHelper;
import org.junit.Before;
import org.junit.Test;
import javafx.scene.control.Alert;
import javafx.stage.Window;

import static org.junit.Assert.*;

import java.time.LocalDate;


public class PromptHelperTest {
	Window owner;
	Alert.AlertType type;
	String msg;
	
	@Before
	public void setup() {
		msg = "Select a country only!";
	}
	@Test
	public void ShowPromptNull() {
		PromptHelper.showPrompt(owner,null,null);
		PromptHelper.showPrompt(null,msg,null);
		PromptHelper.showPrompt(null,null,type);
		PromptHelper.showPrompt(null,null,null);
	}
	@Test
	public void ShowPromptValid() {
		PromptHelper.showPrompt(owner,msg,type);
	}
}