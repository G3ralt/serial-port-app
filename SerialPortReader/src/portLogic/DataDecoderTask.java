package portLogic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import javafx.concurrent.Task;
import static portLogic.PortUtilities.*;

public class DataDecoderTask extends Task<Void> {

    private final BlockingQueue<LabelData> labelData;
    private final BlockingQueue<byte[]> portData;

    public DataDecoderTask(BlockingQueue<LabelData> labelData, BlockingQueue<byte[]> portData) {
        this.labelData = labelData;
        this.portData = portData;
    }

    @Override
    protected Void call() {
        while (true) {
            try {
                byte[] byteArray = this.portData.take();
                ArrayList<LabelData> list = decodePortData(byteArray);
                for (LabelData labelData : list) {
                    this.labelData.put(labelData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<LabelData> decodePortData(byte[] byteArray) throws IllegalArgumentException, IllegalAccessException {
        ArrayList<LabelData> toReturn = new ArrayList<>();
        short addr;
        for (Field f : getAllOffsets()) {
            addr = (short) f.get(f);
            //Throughput values
            if (addr < 40) {
                toReturn.add(new LabelData(f.getName(), String.valueOf(getPerf(byteArray, addr))));
            }
        }
        return toReturn;
    }

}
