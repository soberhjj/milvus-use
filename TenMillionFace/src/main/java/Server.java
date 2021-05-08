import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: huangJunJie  2021-04-15 09:29
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(20210);
        Socket accept = serverSocket.accept();
        System.out.println("客户端连接成功");
    }
}
