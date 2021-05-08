package com.huangjunjie.milvus0106.testdelay;


import io.milvus.client.*;

import java.io.*;
import java.util.List;

/**
 * @Author: huangJunJie  2021-03-16 14:38
 */
public class Inser {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectInputStream ois=new ObjectInputStream(new FileInputStream("E:\\IDEA\\Projects\\milvus-use\\src\\main\\java\\com\\hjj\\milvus\\version0106\\testdelay\\vector"));
        List<List<Float>> vector= (List<List<Float>>) ois.readObject();
        ois.close();

        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.174").withPort(32519).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        InsertParam insertParam = new InsertParam.Builder("face").withFloatVectors(vector).build();
        InsertResponse insertResponse = client.insert(insertParam);
        client.flush("face");
        System.out.println(System.currentTimeMillis());
        System.out.println(insertResponse.getVectorIds().get(0));

        client.close();
    }
}
