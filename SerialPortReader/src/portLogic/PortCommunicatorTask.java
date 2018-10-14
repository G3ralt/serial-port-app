package portLogic;

import com.fazecast.jSerialComm.*;
import java.io.*;
import java.util.concurrent.*;
import static portLogic.PortUtilities.*;

public class PortCommunicatorTask implements Callable<Object> {

    private final int baudRate = 128000;
    private final int newDatabits = 8;
    private final int newStopBits = 1;
    private final BlockingQueue<byte[]> portData;
    private final SerialPort port;

    public PortCommunicatorTask(BlockingQueue<byte[]> portData, SerialPort port) {
        this.port = port;
        this.port.setComPortParameters(baudRate, newDatabits, newStopBits, SerialPort.ODD_PARITY);
        this.port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        this.port.openPort();
        this.portData = portData;
    }

    @Override
    public Object call() throws Exception {
        try {
            synchPhase();
            while (port.isOpen()) {
                writeSettings();
                this.portData.put(readRegisters());
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION v portcomunicator THREAD");
        } finally {
            if(port == null && port.isOpen()) {
                this.port.closePort();
            }
        }
        return null;
    }

    public void synchPhase() throws IOException {
        byte[] synchData = {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFC};
        byte[] buffer = new byte[1];

        for (int i = 0; i < synchData.length; i++) {
            buffer[0] = synchData[i];
            port.writeBytes(buffer, 1);
            port.readBytes(buffer, 1);
            if (byteToBinaryString(buffer[0]).substring(0, 2).contains("1")) {
                writeExceptionLog(buffer[0]); //Write exception in log
                i = -1; //Reset the loop
            }

        }

    }

    private void writeExceptionLog(int result) {
        System.out.println("EXCEPTION! AT SYCNCH");
        //TO DO
    }

    private void writeSettings() {
        //System.out.println("WRITE SETTINGS");
    }

    private byte[] readRegisters() throws IOException, InterruptedException {
        byte[] regList = new byte[256];
        for (byte i = 0x00; i <= 0x52; i++) {

            if ((i >= 0x00 && i < 0x28) // Performance values
             || (i >= 0x30 && i < 0x40)
             || (i >= 0x50 && i < 0x52)) // Control and Core Command 
            {
                regList[i] = readByte(i);
            }

        }
        return regList;
    }     
    
    private byte readByte(Byte address) throws IOException, InterruptedException {
        Thread.sleep(100);
        System.out.println("==================================");
        byte toReturn = 0;
        byte[] buffer = new byte[1];

        //send 0x01 for read
        buffer[0] = 0x01;
        port.writeBytes(buffer, 1);
        System.out.println("WRITTEN1: " + buffer[0] + " binary " + byteToBinaryString(buffer[0]));

        // read completion data
        port.readBytes(buffer, 1);
        System.out.println("READ1:" + buffer[0] + " binary " + byteToBinaryString(buffer[0]));
        
        Thread.sleep(100);
        //send address
        buffer[0] = address;
        port.writeBytes(buffer, 1);
        System.out.println("WRITTEN2: " + buffer[0] + " binary " + byteToBinaryString(buffer[0]));

        // read completion data
        port.readBytes(buffer, 1);
        System.out.println("READ2:" + buffer[0] + " binary " + byteToBinaryString(buffer[0]));
        
        //read data
        port.readBytes(buffer, 1);
        System.out.println("Data " + buffer[0] + " binary " + byteToBinaryString(buffer[0]));
        
        
        toReturn = (byte) buffer[0];

        return toReturn;
    }

}
