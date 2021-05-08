import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: huangJunJie  2021-04-12 16:59
 */
public class searchForAccuracy {
    public static void main(String[] args) throws Exception {
        String HOST=args[0];
        int port=Integer.parseInt(args[1]);
        String COLLECTION=args[2];
        String resultPath=args[3];

        List<Integer> nprobe = new LinkedList();
        nprobe.add(256);
        nprobe.add(512);
        nprobe.add(1024);

        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(port).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        for (int i : nprobe) {
            System.out.println(i);
            for (int j = 1; j < 3; j++) {
                BufferedReader br = new BufferedReader(new FileReader(String.format("/home/root/scripts/accuracy_testdataset/%d", j)));
                List<List<Float>> searchVectors = new LinkedList<>();
                String line;
                while ((line = br.readLine()) != null) {
                    searchVectors.add(normalize((parseLine(line))));
                }
                System.out.println(searchVectors.size());
                br.close();

                //开始查询，每次查一个向量的top10
                List<String> resultVectors = new LinkedList<>();
                List<List<Float>> searchVector = new LinkedList<>();
                for (List<Float> list : searchVectors){
                    searchVector.add(list);
                    JsonObject searchParamsJson = new JsonObject();
                    searchParamsJson.addProperty("nprobe", i);
                    SearchParam searchParam =
                            new SearchParam.Builder(COLLECTION)
                                    .withFloatVectors(searchVector)
                                    .withTopK(10)
                                    .withParamsInJson(searchParamsJson.toString())
                                    .build();
                    SearchResponse searchResponse = client.search(searchParam);
                    List<List<Long>> resultIdsList = searchResponse.getResultIdsList();
                    for (List<Long> ids : resultIdsList) {
                        List<List<Float>> resVectors = client.getEntityByID(COLLECTION, ids).getFloatVectors();
                        for (List temp : resVectors) {
                            resultVectors.add(listToString(temp));
                        }
                    }
                    searchVector.clear();
                }
                System.out.println(resultVectors.size());
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format(resultPath+"/%d-%d", i, j)));
                oos.writeObject(resultVectors);
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
