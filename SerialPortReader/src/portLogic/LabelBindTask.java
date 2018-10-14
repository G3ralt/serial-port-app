package portLogic;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

/**
 * This class is used to bind and set a Label from the GUI to a specific
 * StringProperty of the class. The class goes to a READY state and waits for
 * another Thread to call the setStr method and thus to update the Label in the
 * GUI.
 */
public class LabelBindTask extends Task<Void> {

    private StringProperty str;
    private final Label label;

    public LabelBindTask(Label label) {
        this.label = label;
        this.str = new SimpleStringProperty("DEFAULT");
    }

    /**
     * Only binds the Label to the property.
     *
     * @return
     * @throws Exception
     */
    @Override
    protected Void call() throws Exception {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label.textProperty().bind(str);

            }
        });

        return null;
    }

    /**
     * Used to update the Label in the GUI.
     *
     * @param s The new value of the label.
     */
    public void setStr(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                str.set(s);
            }
        });
    }
}
