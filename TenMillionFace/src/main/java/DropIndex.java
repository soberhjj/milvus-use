import io.milvus.client.ConnectParam;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;

/**
 * @Author: huangJunJie  2021-04-28 14:47
 */
public class DropIndex {
    public static void main(String[] args) {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(args[0]).withPort(Integer.parseInt(args[1])).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        client.dropIndex(args[2]);
        client.close();
    }
}
