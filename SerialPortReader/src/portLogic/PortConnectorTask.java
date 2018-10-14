package portLogic;

import com.fazecast.jSerialComm.SerialPort;
import java.util.ArrayList;
import java.util.concurrent.*;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class PortConnectorTask extends Task<Void> {

    private final ExecutorService executor;
    private final BlockingQueue<byte[]> portData;
    private final ArrayList<Label> labels;
    private final BlockingQueue<LabelData> labelData;

    public PortConnectorTask(BlockingQueue<byte[]> portData, ArrayList<Label> labels, BlockingQueue<LabelData> labelData) {
        this.executor = Executors.newFixedThreadPool(3);
        this.portData = portData;
        this.labels = labels;
        this.labelData = labelData;
    }

    @Override
    protected Void call() {
        while (true) {
            try {
                System.out.println("ITERATION");
                Future<SerialPort> futurePort = executor.submit(new WaitConnectionTask());
                SerialPort port = futurePort.get();
                Future future = executor.submit(new PortCommunicatorTask(portData, port));
                future.get();
            } catch (Exception e) {
                System.out.println("Exception at Port Connector Task");
            } finally {
                //Set all labels to empty Strings
                executor.submit(() -> {
                    for (Label l : labels) {
                        labelData.offer(new LabelData(l.getId(), ""));
                    }
                });
            }
        }
    }

}
