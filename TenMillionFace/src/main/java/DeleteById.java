import io.milvus.client.ConnectParam;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-04-30 09:06
 */
public class DeleteById {
    public static void main(String[] args) {
        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.203").withPort(19531).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        List<Long> id=new LinkedList<>();
        id.add(1600000026048999601L);
        long l = System.currentTimeMillis();
        client.deleteEntityByID("FACE_1599613749000",id);
        System.out.println("删除耗时："+(System.currentTimeMillis()-l));
        client.close();
    }
}
