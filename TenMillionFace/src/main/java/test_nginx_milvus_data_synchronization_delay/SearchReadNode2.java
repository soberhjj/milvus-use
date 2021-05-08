package test_nginx_milvus_data_synchronization_delay;

import com.google.gson.JsonObject;
import io.milvus.client.ConnectParam;
import io.milvus.client.GetEntityByIDResponse;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-04-10 16:36
 */
public class SearchReadNode2 {
    public static void main(String[] args) {
        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.198").withPort(19538).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        //开始查询
        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", 128);

        List<Long> ids = new LinkedList<>();
        ids.add(1000002L);
        while (true){
            GetEntityByIDResponse response = client.getEntityByID("TestDelay", ids);
            List<Float> resVector = response.getFloatVectors().get(0);
            if (resVector.size()==0){
                System.out.println(System.currentTimeMillis());
                break;
            }
        }
        client.close();
    }
}
