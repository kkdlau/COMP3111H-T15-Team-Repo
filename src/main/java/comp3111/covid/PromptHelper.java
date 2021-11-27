package comp3111.covid;

import javafx.scene.control.Alert;
import javafx.stage.Window;

import java.util.Objects;

public class PromptHelper {
    private PromptHelper() {

    }

    /**
     * Show a alert dialog.
     *
     * @param owner the stage associated with the dialog.
     * @param msg message to be displayed.
     * @param type alert type.
     */
    static public void showPrompt(Window owner, String msg, Alert.AlertType type) {
        if (owner == null) return;
        Alert prompt = new Alert(type);
        prompt.initOwner(owner);
        prompt.setContentText(msg);
        prompt.show();
    }

    /**
     * Factory method for showing a error dialog.
     *
     * @param owner the stage associated with the dialog.
     * @param msg message to be displayed.
     */
    static public void showErrorPrompt(Window owner, String msg) {
        showPrompt(owner, msg, Alert.AlertType.ERROR);
    }

    /**
     * Factory method for showing a information dialog.
     *
     * @param owner the stage associated with the dialog.
     * @param msg message to be displayed.
     */
    static public void showInfoPrompt(Window owner, String msg) {
        showPrompt(owner, msg, Alert.AlertType.INFORMATION);
    }
}
