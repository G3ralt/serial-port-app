package portLogic;

import java.util.ArrayList;
import java.util.concurrent.*;
import javafx.scene.control.Label;

/**
 * Class used for handling the connection with the port and manipulation of port
 * data. 
 * One task for connection with the port. 
 * One task for decoding the bytes received from port.
 * One task for updating the labels in the GUI. *
 * @author aosenov
 */
public class PortHandler {

    private final ArrayList<Label> labels;
    private final BlockingQueue<LabelData> labelData;
    private final BlockingQueue<byte[]> portData;
    private final ExecutorService executor;

    public PortHandler(ArrayList<Label> labels) {
                this.labels = labels;
        this.portData = new ArrayBlockingQueue<>(1000);
        this.labelData = new ArrayBlockingQueue<>(1000);
        this.executor = Executors.newFixedThreadPool(100);
        PortUtilities.getAllOffsets();
    }

    /**
     * Starts three independent tasks.
     */
    public void start() {
        executor.submit(new DataDecoderTask(this.labelData, this.portData));
        executor.submit(new LabelUpdaterTask(this.labels, this.labelData));
        executor.submit(new PortConnectorTask(this.portData, this.labels, this.labelData));
    }

    /**
     * This method tries to close the Threads on application shutdown.
     */
    public void close() {
        executor.shutdown();
        executor.shutdownNow();
    }
}
