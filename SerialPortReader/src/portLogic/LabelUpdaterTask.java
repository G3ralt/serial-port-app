package portLogic;

import java.util.*;
import java.util.concurrent.*;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

/**
 * This method controls the updates to the Labels in the GUI. It starts by
 * creating a collection of Tasks for each label. The tasks bind a label to a
 * specific StringProperty. Later, when data is available the LabelUpdaterClass
 * updates the label value by calling the setStr() method. An executor service
 * is used to submit/start/ all the tasks and a Thread for the consumer of
 * LabelData.
 *
 * @author aosenov
 */
public class LabelUpdaterTask extends Task<Void> {

    private final HashMap<String, LabelBindTask> tasks;
    private final BlockingQueue<LabelData> labelData;

    public LabelUpdaterTask(ArrayList<Label> labels, BlockingQueue<LabelData> labelData) {
        this.tasks = createTasks(labels);
        this.labelData = labelData;
    }

    /**
     * Creates executor for all tasks and starts the consumer method which
     * updates the values of the properties and respectively the Labels.
     *
     * @return null
     * @throws Exception
     */
    @Override
    protected Void call() throws Exception {
        //Start all Binding tasks
        ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
        for (LabelBindTask t : tasks.values()) {
            executor.submit(t);
        }
        //Consume the data from the port and set respective values
        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    LabelData data = this.labelData.take(); //Blocking call
                    LabelBindTask t = tasks.get(data.getAddress());
                    t.setStr(data.getData());

                } catch (InterruptedException ex) {
                    for (int i = 0; i < 100; i++) {
                        System.out.print("=");
                    }
                    System.out.println(ex.getMessage());

                }
            }

        });
        consumer.setDaemon(true);
        consumer.start();

        return null;
    }

    /**
     * This method creates a LabelBindTask for each updatable Label and puts
     * them in a collection.
     *
     * @param labels All labels from the FXML file that are going to be updated.
     * @return A collection of tasks.
     */
    private HashMap<String, LabelBindTask> createTasks(ArrayList<Label> labels) {
        HashMap<String, LabelBindTask> toReturn = new HashMap<>();
        for (Label l : labels) {
            LabelBindTask task = new LabelBindTask(l);
            toReturn.put(l.getId(), task);
        }
        return toReturn;
    }
}
