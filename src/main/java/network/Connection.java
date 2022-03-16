package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Parent class: network.Connection, which has two children: DefaultConnection and network.LossyConnection
 */
public class Connection implements Receiver, Sender {

    private Socket socket;
    private boolean isOpen;



    /**
     * Constructor.
     * @param socket
     */
    public Connection(Socket socket) {
        this.socket = socket;
        if(this.socket.isConnected()){
            this.isOpen = true;
        } else {
            this.isOpen = false;
        }

    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    // For receive and send method, use reference https://stackoverflow.com/questions/1176135/socket-send-and-receive-byte-array
    // and reference https://www.devzoneoriginal.com/2020/07/java-socket-example-for-sending-and.html

    /**
     * Method for receiving data from the wrapped socket of default connection
     * @return received bytes
     */
    public byte[] receive() {
        if (this.isOpen()) {
            try {
                DataInputStream inPutStream = new DataInputStream(this.socket.getInputStream());
                int len = inPutStream.readInt();
                byte[] message = new byte[len];
                inPutStream.readFully(message);
                return message;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * Method for sending data using the wrapped socket of default connection, return true if sending successfully, otherwise false
     * @param message
     * @return
     */
    public boolean send(byte[] message) {
        if(this.isOpen()){
            try{
                DataOutputStream outPutStream = new DataOutputStream(this.socket.getOutputStream());
                outPutStream.writeInt(message.length);
                outPutStream.write(message);
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



    public void close(){
        try {
            this.socket.close();
            this.setOpen(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

