package nettysrc;

import java.net.Socket;

public class SocketClient {

    public static void main(String[] args) throws Exception {
        new Socket("127.0.0.1",8000)
                .getOutputStream().write("hello".getBytes());
    }
}
