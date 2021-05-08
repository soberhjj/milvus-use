package suotiandata;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-04-27 08:58
 */
public class CompareMilvusRes {
    public static void main(String[] args) throws Exception {
        List<Integer> nprobe = new LinkedList();
        nprobe.add(1);
        nprobe.add(16);
        nprobe.add(32);
        nprobe.add(64);
        nprobe.add(128);
        nprobe.add(256);
        nprobe.add(512);
        nprobe.add(1024);
        nprobe.add(2048);
        nprobe.add(2895);

        for (int i : nprobe) {
            for (int j = 3; j < 4; j++) {
                ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\suotian\\milvus\\%d-%d", i, j)));
                List<String> topKByMilvusAfter = (List<String>) ois1.readObject();
                ois1.close();
                System.out.println(topKByMilvusAfter.size());


                ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\suotian\\milvus\\%d-%d", i, j)));
                List<String> topKByMilvusBefore = (List<String>) ois2.readObject();
                ois2.close();
                System.out.println(topKByMilvusBefore.size());

            }

        }
    }
}
