import java.io.IOException;
import java.net.Socket;

/**
 * @Author: huangJunJie  2021-04-15 09:29
 */
public class Client {
    public static void main(String[] args) throws IOException {
        long l = System.currentTimeMillis();
        long l1 = System.nanoTime();
        Socket socket=new Socket("192.168.136.222",20210);
        System.out.println(String.format("连接耗时:%d ns",System.nanoTime()-l1));

    }
}
