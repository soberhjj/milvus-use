package suotiandata;

import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-04-26 10:21
 */
public class SearchMilvus {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
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

        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.176").withPort(19530).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        for (int i : nprobe) {
            System.out.println(i);
            for (int j = 0; j < 5; j++) {
                if (j==3){
                    continue;
                }
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus前期测试\\milvus准确率和性能测试\\最新\\从归一化后的group2中抽取测试数据集\\测试集\\testDataset%d", j)));
                LinkedList<Float[]> testDataset = (LinkedList<Float[]>) ois.readObject();
                ois.close();

                List<List<Float>> searchVectors = new LinkedList<>();
                for (Float[] temp : testDataset) {
                    searchVectors.add(Arrays.asList(temp));
                }
                System.out.println(searchVectors.size());

                List<String> allVectors = new LinkedList<>();
                List<List<Float>> searchVector = new LinkedList<>();
                for (List<Float> list : searchVectors) {
                    searchVector.add(list);
                    //开始查询
                    JsonObject searchParamsJson = new JsonObject();
                    searchParamsJson.addProperty("nprobe", i);
                    SearchParam searchParam =
                            new SearchParam.Builder("suotian_new")
                                    .withFloatVectors(searchVector)
                                    .withTopK(10)
                                    .withParamsInJson(searchParamsJson.toString())
                                    .build();
                    SearchResponse searchResponse = client.search(searchParam);
                    List<Long> ids = searchResponse.getResultIdsList().get(0);
                    List<List<Float>> resVectors = client.getEntityByID("suotian_new", ids).getFloatVectors();
                    for (List<Float> temp : resVectors) {
                        allVectors.add(listToString(temp));
                    }
                    searchVector.clear();
                }
                System.out.println(allVectors.size());
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\suotian\\milvus\\%d-%d", i, j)));
                oos.writeObject(allVectors);
                oos.close();
            }
        }
    }

    private static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return new String(sb);
    }
}
