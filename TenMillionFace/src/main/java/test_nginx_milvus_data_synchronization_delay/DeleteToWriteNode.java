package test_nginx_milvus_data_synchronization_delay;

import io.milvus.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @Author: huangJunJie  2021-04-10 17:01
 */
public class DeleteToWriteNode {
    public static void main(String[] args) {
        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.198").withPort(19539).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        List<Long> entityIds = new ArrayList<>();
        entityIds.add(1000002L);

        Response response = client.deleteEntityByID("TestDelay", entityIds);
        if (response.ok()){
            System.out.println(System.currentTimeMillis());
        }
        client.close();
    }

    /**
     * 生成随机向量
     */
    public static List<List<Float>> generateVectors(int vectorCount, int dimension) {
        SplittableRandom splitcollectionRandom = new SplittableRandom();
        List<List<Float>> vectors = new ArrayList<>(vectorCount);
        for (int i = 0; i < vectorCount; ++i) {
            splitcollectionRandom = splitcollectionRandom.split();
            DoubleStream doubleStream = splitcollectionRandom.doubles(dimension);
            List<Float> vector =
                    doubleStream.boxed().map(Double::floatValue).collect(Collectors.toList());
            vectors.add(vector);
        }
        return vectors;
    }
}
