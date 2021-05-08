package com.huangjunjie.milvuspressuretest.service;

import com.google.gson.JsonObject;
import com.huangjunjie.milvuspressuretest.common.Dataset;
import io.milvus.client.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @Author: huangJunJie  2021-03-19 16:17
 */
@Service
public class MilvusService {

    private final String HOST = "192.168.136.176";
    private final int PORT = 19530;


//    private final int TOPK = 10;
//    private final int NPROBE = 256;
//
//    private final String COLLECTION = "face_ifs1024";

    public int search(String COLLECTION, int NPROBE, int TOPK) {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        Random random = new Random();
        int index = random.nextInt(1000);

        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", NPROBE);
        SearchParam searchParam =
                new SearchParam.Builder(COLLECTION)
                        .withFloatVectors(Dataset.vectors.subList(index, index + 1))
//                        .withFloatVectors(generateVectors(1,512))
                        .withTopK(TOPK)
                        .withParamsInJson(searchParamsJson.toString())
                        .build();
        SearchResponse searchResponse = client.search(searchParam);
        client.close();
        return searchResponse.getResultIdsList().get(0).size();
    }

//    public int searchInCluster(){
//        ConnectParam connectParam = new ConnectParam.Builder().withHost(CLUSTER_HOST).withPort(CLUSTER_PORT).build();
//        MilvusClient client = new MilvusGrpcClient(connectParam);
//
//        JsonObject searchParamsJson = new JsonObject();
//        searchParamsJson.addProperty("nprobe", NPROBE);
//        SearchParam searchParam =
//                new SearchParam.Builder(COLLECTION)
//                        .withFloatVectors(generateVectors(1,512))
//                        .withTopK(TOPK)
//                        .withParamsInJson(searchParamsJson.toString())
//                        .build();
//        SearchResponse searchResponse = client.search(searchParam);
//        client.close();
//        return searchResponse.getResultIdsList().get(0).size();
//    }


    public void insert() {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        InsertParam insertParam = new InsertParam.Builder("FACE_1599613749000").withFloatVectors(generateVectors(1, 512)).build();
        client.insert(insertParam);
        client.flush("FACE_1599613749000");
        client.close();
    }

    public void delete() {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", 1);
        SearchParam searchParam =
                new SearchParam.Builder("FACE_1599613749000")
                        .withFloatVectors(generateVectors(1, 512))
                        .withTopK(1)
                        .withParamsInJson(searchParamsJson.toString())
                        .build();
        SearchResponse searchResponse = client.search(searchParam);
        List<Long> id = searchResponse.getResultIdsList().get(0);
        client.deleteEntityByID("FACE_1599613749000", id);
        client.close();
    }

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
