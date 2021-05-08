import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @Author: huangJunJie  2021-04-06 13:46
 */
public class Search {
    public static void main(String[] args) {
        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.2.119").withPort(19530).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", 256);
        long totaltime = 0;
        for (int i = 0; i < 1000; i++) {
            SearchParam searchParam =
                    new SearchParam.Builder("FACE_1599613749000")
                            .withFloatVectors(generateVectors(1, 512))
                            .withTopK(10)
                            .withParamsInJson(searchParamsJson.toString())
                            .build();
            long l = System.currentTimeMillis();
            SearchResponse search = client.search(searchParam);
            totaltime+=(System.currentTimeMillis()-l);
        }
        System.out.println(totaltime);
        client.close();
        System.out.println("script run over");
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
