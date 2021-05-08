import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huangJunJie  2021-04-12 16:59
 */
public class searchForAccuracyFor500w {
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
        nprobe.add(4096);

        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.175").withPort(19530).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        for (int i : nprobe) {
            System.out.println(i);
            for (int j = 1; j < 7; j++) {
                BufferedReader br = new BufferedReader(new FileReader(String.format("D:\\facefeatures\\extract_dataset\\%d", j)));
                List<List<Float>> searchVectors = new LinkedList<>();
                String line;
                while ((line = br.readLine()) != null) {
                    searchVectors.add(normalize((parseLine(line))));
                }
                System.out.println(searchVectors.size());
                br.close();

                //开始查询
                JsonObject searchParamsJson = new JsonObject();
                searchParamsJson.addProperty("nprobe", i);
                SearchParam searchParam =
                        new SearchParam.Builder("face_500w_ifs2048")
                                .withFloatVectors(searchVectors)
                                .withTopK(10)
                                .withParamsInJson(searchParamsJson.toString())
                                .build();
                SearchResponse searchResponse = client.search(searchParam);
                List<List<Long>> resultIdsList = searchResponse.getResultIdsList();
                List<String> allVectors = new LinkedList<>();
                for (List<Long> ids : resultIdsList) {
                    List<List<Float>> resVectors = client.getEntityByID("face_500w_ifs2048", ids).getFloatVectors();
                    for (List temp : resVectors) {
                        allVectors.add(listToString(temp));
                    }
                }
                System.out.println(allVectors.size());
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\500w\\milvus\\%d-%d", i, j)));
                oos.writeObject(allVectors);
                oos.close();
            }

        }


    }

    /**
     * 解析每行数据
     */
    public static List<Float> parseLine(String line) {
        String[] origin = line.split(" ");
        List<Float> list = Arrays.stream(Arrays.copyOfRange(origin, 1, origin.length - 1)).map((itme) -> Float.parseFloat(itme)).collect(Collectors.toList());
        return list;
    }

    /**
     * 归一化
     */
    private static List<Float> normalize(List<Float> beforeNormalize) {
        double v = 0;
        for (Float element : beforeNormalize) {
            v += Math.pow(element, 2);
        }
        double sqrtV = Math.sqrt(v);

        List<Float> afterNormalize = new LinkedList<>();
        for (Float element : beforeNormalize) {
            afterNormalize.add((float) (element / sqrtV));
        }
        return afterNormalize;
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
