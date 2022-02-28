package network;

/**
 * network.Receiver interface: can send message
 */
public interface Sender {
    /**
     * Interface method, return true is message is sent successfully, otherwise return false
     * @param message
     * @return
     */
    public boolean send(byte[] message);
}
