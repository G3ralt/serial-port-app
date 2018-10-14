package portLogic;

import com.fazecast.jSerialComm.*;
import java.util.concurrent.Callable;

/**
 * This task is responsible waiting and identifying Inspector connection. The
 * task checks for Inspector connection every 1 second and returns the port
 * where Inspector is connected or null if not connected.
 *
 * @author aosenov
 */
public class WaitConnectionTask implements Callable  {

    @Override
    public SerialPort call() throws InterruptedException {
        SerialPort port = findInspector();
        while (port == null) {
            System.out.println("Port Not found");
            Thread.sleep(1000);
            port = findInspector();
        }        
        return port;

    }

    /**
     * Gets all Serial Ports and iterates them in order to find the correct one.
     *
     * @return null if Inspector not found, port if found
     */
    private SerialPort findInspector() {
        SerialPort[] ports = SerialPort.getCommPorts();
        SerialPort toReturn = null;
        for (SerialPort port : ports) {
            if (port.getDescriptivePortName().contains("CP210x")
                    || port.getDescriptivePortName().contains("CH340")) {
                toReturn = port;
            }
        }
        return toReturn;
    }

}
