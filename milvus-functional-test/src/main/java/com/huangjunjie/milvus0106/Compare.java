package com.huangjunjie.milvus0106;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;


/**
 * @Author: huangJunJie  2021-03-16 09:52
 *
 * 比较cos搜索和milvus搜索的结果
 */
public class Compare {
    private final int NPROBE = 2048;

    public void runCompare() throws IOException, ClassNotFoundException {
        for (int i = 0; i < 5; i++) {
            ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\vectors\\milvus_search\\nprobe%d\\%d", NPROBE, i)));
            LinkedList<float[]> milvusVectors = (LinkedList<float[]>) ois1.readObject();
            ois1.close();
            System.out.println(milvusVectors.size());

            ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\vectors\\cos_search\\结果向量\\forTestDataset%d", i)));
            LinkedList<float[]> cosVectors = (LinkedList<float[]>) ois2.readObject();
            ois2.close();
            System.out.println(cosVectors.size());

//            for (int j = 0; j < milvusVectors.size(); j++) {
//                if (!Arrays.equals(milvusVectors.get(i), cosVectors.get(i))) {
//                    System.out.println(j);
//                }
//            }
//            System.out.println(String.format("第%d次匹配结束",i));
            System.out.println(Arrays.toString(milvusVectors.get(2106)));
            System.out.println(Arrays.toString(cosVectors.get(2106)));

        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Compare().runCompare();
    }
}
