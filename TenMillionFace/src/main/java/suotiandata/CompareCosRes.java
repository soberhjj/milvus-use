package suotiandata;

import javax.sound.midi.Soundbank;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @Author: huangJunJie  2021-04-27 08:58
 */
public class CompareCosRes {
    public static void main(String[] args) throws Exception {
        ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\suotian\\cos\\res"));
        LinkedList<float[]> topKByCosAfter = (LinkedList<float[]>) ois1.readObject();
        ois1.close();
        System.out.println(topKByCosAfter.size());


        ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream("C:\\Users\\Administrator\\Desktop\\milvus前期测试\\milvus准确率和性能测试\\最新\\从归一化后的group2中抽取测试数据集\\cos计算的top10结果集\\forTestDataset3"));
        LinkedList<float[]> topKByCosBefore = (LinkedList<float[]>) ois2.readObject();
        ois2.close();
        System.out.println(topKByCosBefore.size());

        int same = 0;
        for (int i = 0; i < topKByCosAfter.size(); i++) {
            if (Arrays.toString(topKByCosAfter.get(i)).equals(Arrays.toString(topKByCosBefore.get(i)))){
                same+=1;
            }
        }
        System.out.println(same);
    }
}
