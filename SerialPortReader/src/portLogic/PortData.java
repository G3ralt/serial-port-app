package portLogic;

public class PortData {

    private final int addr;
    private final byte[] data;

    public PortData(int addr, byte[] data) {
        this.addr = addr;
        this.data = data;
    }

    public int getAddr() {
        return addr;
    }

    public byte[] getData() {
        return data;
    }

    

}
