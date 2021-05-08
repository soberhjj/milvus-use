package com.huangjunjie.milvus0106.testdelay;


import io.milvus.client.ConnectParam;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-03-16 14:38
 */
public class Delete {
    public static void main(String[] args){
        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.174").withPort(32519).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        List<Long> id=new ArrayList<>();
        id.add(1615878529606309000L);
        client.deleteEntityByID("face", id);
        client.flush("face");
        System.out.println(System.currentTimeMillis());
        client.close();
    }
}
