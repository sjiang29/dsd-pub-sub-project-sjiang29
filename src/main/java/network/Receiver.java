package network;

/**
 * network.Receiver interface: can receive message
 */
public interface Receiver {
    /**
     * Interface method
     * @return
     */
    public byte[] receive();
}
