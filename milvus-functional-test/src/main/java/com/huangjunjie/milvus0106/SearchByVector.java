package com.huangjunjie.milvus0106;

import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-03-15 13:53
 * <p>
 * 以向量搜索向量
 */
public class SearchByVector {

    private final String HOST = "192.168.136.174";
    private final int PORT = 32519;

    private final int TOPK = 10;
    private final int NPROBE = 256;

    private final String COLLECTION = "face";

    public void runSearch() throws IOException, ClassNotFoundException {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        for (int i = 0; i < 5; i++) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus准确率和性能测试\\最新\\从归一化后的group2中抽取测试数据集\\测试集\\testDataset%d", i)));
            LinkedList<Float[]> dataset = (LinkedList<Float[]>) ois.readObject();
            ois.close();
            System.out.println(String.format("数据集向量个数：%d", dataset.size()));

            List<List<Float>> resultTopkVectors = new LinkedList<>();

            List<List<Float>> vectorsToSearch = new LinkedList<>();
            for (Float[] a : dataset) {
                LinkedList<Float> list = new LinkedList<>();
                for (Float b : a) {
                    list.add(b);
                }
                vectorsToSearch.add(list);
                //开始查询
                JsonObject searchParamsJson = new JsonObject();
                searchParamsJson.addProperty("nprobe", NPROBE);
                SearchParam searchParam =
                        new SearchParam.Builder(COLLECTION)
                                .withFloatVectors(vectorsToSearch)
                                .withTopK(TOPK)
                                .withParamsInJson(searchParamsJson.toString())
                                .build();
                SearchResponse searchResponse = client.search(searchParam);
                if (searchResponse.ok()) {
                    List<List<Long>> resultIds = searchResponse.getResultIdsList();
                    List<List<Float>> floatVectors = client.getEntityByID(COLLECTION, resultIds.get(0)).getFloatVectors();
                    for (List<Float> c : floatVectors) {
                        resultTopkVectors.add(c);
                    }
                } else {
                    System.out.println("搜索失败");
                }
                vectorsToSearch.clear();
            }
            System.out.println(resultTopkVectors.size());

            LinkedList<float[]> list = new LinkedList<>();
            for (List<Float> temp : resultTopkVectors) {
                int index = 0;
                float[] arr = new float[512];
                for (Float f : temp) {
                    arr[index++] = f;
                }
                list.add(arr);
            }

            System.out.println(list.size());
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format("C:\\Users\\Administrator\\Desktop\\vectors\\milvus_search\\nprobe%d\\%d",NPROBE, i)));
            oos.writeObject(list);
            oos.close();

        }

        client.close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new SearchByVector().runSearch();
    }

}
