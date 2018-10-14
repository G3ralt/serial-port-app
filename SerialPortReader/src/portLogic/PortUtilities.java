package portLogic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class PortUtilities {

    //registers offset
    public static final short _MRDRX = 0x00;
    public static final short _MWRRX = 0x04;
    public static final short _CPLRX = 0x08;
    public static final short _RX = 0x0c;
    public static final short _MRDTX = 0x10;
    public static final short _MWRTX = 0x14;
    public static final short _CPLTX = 0x18;
    public static final short _TX = 0x1c;
    public static final short _PEAKRX = 0x20;
    public static final short _PEAKTX = 0x24;

    public static final short addrSpyVersion = 0x30;
    public static final short addrMaxPlLtssm = 0x34;
    public static final short addrL0LinkSpd = 0x35;
    public static final short addrEquStatus = 0x36;
    public static final int addrInspCap = 0x37;
    public static final short addrRxPolarity = 0x38;
    public static final short addrActiveLanes = 0x39;
    public static final short addrDetectLanes = 0x3a;
    public static final short addrPolCplPhase = 0x3b;
    public static final short addrErrorCnt = 0x3c;
    
    public PortUtilities() {
        System.out.println("CREATE");
    }

    public static int getPerf(byte[] byteArray, int addr) {
        long perfReg = byteArray[addr] + (byteArray[addr + 1] << 8) + (byteArray[addr + 2] << 16) + (byteArray[addr + 3] << 24);
        return (int) (perfReg / 12496);
    }

    public static String byteToBinaryString(byte b1) {
        return String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');    
    }
    
    public static ArrayList<Field> getAllOffsets()  {
        Field[] fields = PortUtilities.class.getDeclaredFields();
        ArrayList<Field> toReturn = new ArrayList<>();
        for (Field field : fields) {
            if(field.getName().startsWith("_")) {
                toReturn.add(field);
            }
            
        }
        return toReturn;
    }
}
