package suotiandata;

import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * @Author: huangJunJie  2021-04-26 19:13
 */
public class InsertToMilvus {
    public static void main(String[] args) throws Exception {

        String HOST = "192.168.136.176";
        int PORT = 19530;
        String collectionName = "suotian_new";
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        for (int i = 1; i < 9; i++) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Users\\Administrator\\Desktop\\milvus前期测试\\milvus准确率和性能测试\\添加新人脸测试\\归一化后的总向量(供milvus插入使用)\\data" + i));
            List<List<Float>> features = (List<List<Float>>) ois.readObject();
            ois.close();
            System.out.println(features.size());
            for (int j = 0; j < features.size(); j += 10000) {
                InsertParam insertParam = new InsertParam.Builder(collectionName).withFloatVectors(features.subList(j, j + 10000)).build();
                client.insert(insertParam);
                client.flush(collectionName);
                System.out.println("插入10000");
            }
//            InsertParam insertParam = new InsertParam.Builder(collectionName).withFloatVectors(features.subList(80000, features.size())).build();
//            client.insert(insertParam);
//            client.flush(collectionName);
//            System.out.println("插入10000");

            System.out.println("插入结束");

//            InsertParam insertParam = new InsertParam.Builder(collectionName).withFloatVectors(features).build();
//
//
//            client.insert(insertParam);
//            client.flush(collectionName);
//
//            System.out.println("插入" + features.size());


        }
        client.close();


//        System.out.println("数据导入结束");

        //创建索引
//        IndexType indexType = IndexType.IVFLAT;
//        JsonObject indexParamsJson = new JsonObject();
//        indexParamsJson.addProperty("nlist", 4096);
//        Index index = new Index.Builder(collectionName, indexType).withParamsInJson(indexParamsJson.toString()).build();
//        client.createIndex(index);
//        client.close();
//
//        System.out.println("索引创建结束");


    }
}
