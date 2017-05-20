//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package socketio;

import java.io.IOException;

/**
 *
 * @author Benedikt
 */
public class ServerSocket {
    private int localPort;
    private java.net.ServerSocket serverSocket;

    public ServerSocket(int localPort) throws IOException {
        this.serverSocket = new java.net.ServerSocket(localPort);
        this.localPort = this.serverSocket.getLocalPort();
    }

    public Socket accept() throws IOException {
        return new Socket(this.serverSocket.accept());
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }
}
