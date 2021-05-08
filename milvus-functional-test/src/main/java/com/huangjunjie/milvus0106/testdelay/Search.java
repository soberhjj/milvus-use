package com.huangjunjie.milvus0106.testdelay;

import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * @Author: huangJunJie  2021-03-16 14:38
 */
public class Search {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("E:\\IDEA\\Projects\\milvus-use\\src\\main\\java\\com\\hjj\\milvus\\version0106\\testdelay\\vector"));
        List<List<Float>> vector = (List<List<Float>>) ois.readObject();
        ois.close();

        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.174").withPort(32519).build();
//        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.203").withPort(19540).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        //开始查询
        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", 256);
        SearchParam searchParam =
                new SearchParam.Builder("face")
                        .withFloatVectors(vector)
                        .withTopK(1)
                        .withParamsInJson(searchParamsJson.toString())
                        .build();
        while (true) {
            SearchResponse searchResponse = client.search(searchParam);
            System.out.println(System.currentTimeMillis());
            System.out.println(searchResponse.getResultIdsList().get(0).get(0));
        }
    }
}
