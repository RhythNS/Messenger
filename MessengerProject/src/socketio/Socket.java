//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package socketio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

public class Socket {
    private String hostname;
    private int port;
    private java.net.Socket socket;

    public Socket(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public Socket(java.net.Socket socket) throws IOException {
        this.socket = socket;
        this.port = socket.getPort();
        this.hostname = socket.getRemoteSocketAddress().toString();
    }

    public void close() throws IOException {
        this.socket.close();
    }

    public boolean connect() {
        try {
            this.socket = new java.net.Socket(this.hostname, this.port);
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    public int dataAvailable() throws IOException {
        return this.socket.getInputStream().available();
    }

    public InetAddress getInetAddress() {
        return this.socket.getInetAddress();
    }

    public int getPort() {
        return this.socket.getPort();
    }

    public int read() throws IOException {
        return this.socket.getInputStream().read();
    }

    public int read(byte[] b, int len) throws IOException {
        return this.socket.getInputStream().read(b, 0, len);
    }

    public String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        int ch = this.socket.getInputStream().read();
        if(ch == -1) {
            return null;
        } else {
            for(; ch != -1 && ch != 10; ch = this.socket.getInputStream().read()) {
                if(ch != 13) {
                    line.append((char)ch);
                }
            }
            return line.toString();
        }
    }

    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    public void write(int b) throws IOException {
        this.socket.getOutputStream().write(b);
    }

    public void write(byte[] b, int len) throws IOException {
        this.socket.getOutputStream().write(b, 0, len);
    }

    public void write(String s) throws IOException {
        this.socket.getOutputStream().write(s.getBytes());
    }
}
