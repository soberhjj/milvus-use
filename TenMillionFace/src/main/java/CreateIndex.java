import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @Author: huangJunJie  2021-04-28 14:47
 */
public class CreateIndex {
    public static void main(String[] args) {
        String HOST=args[0];
        int port=Integer.parseInt(args[1]);
        String COLLECTION=args[2];
        int nlist=Integer.parseInt(args[3]);

        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(port).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        IndexType indexType = IndexType.IVFLAT;
        JsonObject indexParamsJson = new JsonObject();
        indexParamsJson.addProperty("nlist", nlist);
        Index index = new Index.Builder(COLLECTION, indexType).withParamsInJson(indexParamsJson.toString()).build();
        long l = System.currentTimeMillis();
        client.createIndex(index);
        System.out.println(String.format("创建索引耗时：%d 秒",System.currentTimeMillis()-l));

        //执行一次搜索（目的是载入数据到内存）
        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", 16);
        SearchParam searchParam =
                new SearchParam.Builder(COLLECTION)
                        .withFloatVectors(generateVectors(1, 512))
                        .withTopK(1)
                        .withParamsInJson(searchParamsJson.toString())
                        .build();
        client.search(searchParam);
        client.close();
        System.out.println("数据载入内存结束");
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
