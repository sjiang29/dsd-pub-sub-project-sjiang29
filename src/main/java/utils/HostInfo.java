package utils;

/**
 * utils.HostInfo Class: class for storing a host's information
 */
public class HostInfo {
    private String hostName;
    private String hostAddress;
    private int port;

    /**
     * Constructor.
     * @param hostName
     * @param hostAddress
     * @param port
     */
    public HostInfo(String hostName, String hostAddress, int port) {
        this.hostName = hostName;
        this.hostAddress = hostAddress;
        this.port = port;
    }

    /**
     * Getter to get hostName
     * @return
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Getter to get hostAddress
     * @return
     */
    public String getHostAddress() {
        return hostAddress;
    }

    /**
     * Getter to get port
     * @return
     */
    public int getPort() {
        return port;
    }
}
