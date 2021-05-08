package com.huangjunjie.milvus0106.testdelay;

import com.huangjunjie.milvus0106.MilvusClientExample;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @Author: huangJunJie  2021-03-16 14:56
 */
public class data {
    public static void main(String[] args) throws IOException {
        List<List<Float>> vector = MilvusClientExample.generateVectors(1, 512);
        ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream("E:\\IDEA\\Projects\\milvus-use\\src\\main\\java\\com\\hjj\\milvus\\version0106\\testdelay\\vector"));
        oos.writeObject(vector);
        oos.close();



    }
}
