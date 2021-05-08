import io.milvus.client.ConnectParam;
import io.milvus.client.InsertParam;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huangJunJie  2021-03-24 08:57
 */
public class Insert {
    public static void main(String[] args) throws IOException {
        new Insert().insert(args[0], Integer.parseInt(args[1]), args[2], args[3]);
    }

    public void insert(String HOST, int PORT, String collectionName, String filePath) throws IOException {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

//        List<List<Float>> vectors = new LinkedList<>();
//        BufferedReader br = new BufferedReader(new FileReader(filePath));
//        int count = 0;
//        String line;
//        long l = System.currentTimeMillis();
//        int times=0;
//        while ((line = br.readLine()) != null) {
//            vectors.add(normalize(parseLine(line)));
//            if (++count == 20000) {
//                InsertParam insertParam = new InsertParam.Builder(collectionName).withFloatVectors(vectors).build();
//                client.insert(insertParam);
//                count = 0;
//                vectors.clear();
//                System.out.println("新增20000");
//                times+=1;
//                if (times==25){
//                    break;
//                }
//            }
//        }
//        client.flush(collectionName);
//        client.close();
//        br.close();
//        System.out.println("数据导入结束");

//        BufferedWriter bw=new BufferedWriter(new FileWriter("/home/root/milvus0106_for_accuracytest/time.txt"));
//        bw.write(String.format("导入数据耗时%d毫秒", System.currentTimeMillis() - l));
//        bw.newLine();
//        System.out.println(String.format("导入数据耗时%d毫秒", System.currentTimeMillis() - l));


        List<List<Float>> vectors = new LinkedList<>();
        List<Long> ids = new LinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader("/data/testdata/features.log"));
        int count = 0;
        String line;
        int frequency = 0;
        while ((line = br.readLine()) != null) {
            HashMap aa = aa(line);
            vectors.add(normalize((List<Float>) aa.get("vector")));
            ids.add((Long) aa.get("id"));
            if (++count == 20000) {
                InsertParam insertParam = new InsertParam.Builder(collectionName).withFloatVectors(vectors).withVectorIds(ids).build();
                client.insert(insertParam);
                client.flush(collectionName);
                count = 0;
                vectors.clear();
                ids.clear();
                System.out.println("新增20000");
                frequency += 1;
                if (frequency == 50) {
                    break;
                }
            }
        }

        client.close();
        br.close();
        System.out.println("数据导入结束");

//        InsertParam insertParam = new InsertParam.Builder(collectionName).withFloatVectors(vectors).withVectorIds(ids).build();
//        client.insert(insertParam);
//        client.flush(collectionName);
//        System.out.println(String.format("新增%d", vectors.size()));

//        /**
//         * 创建索引
//         */
//        IndexType indexType = IndexType.IVFLAT;
//        JsonObject indexParamsJson = new JsonObject();
//        indexParamsJson.addProperty("nlist", 2895);
//        Index index = new Index.Builder(collectionName, indexType).withParamsInJson(indexParamsJson.toString()).build();
//        long startTime = System.currentTimeMillis();
//        client.createIndex(index);
////        bw.write(String.format("创建索引耗时%d毫秒", System.currentTimeMillis() - startTime));
////        bw.newLine();
//        System.out.println(String.format("创建索引耗时%d毫秒", System.currentTimeMillis() - startTime));
//
////        bw.close();
//        client.close();
//
//
//        System.out.println("script run over");


    }

    /**
     * 解析每行数据
     */
    public List<Float> parseLine(String line) {
        String[] origin = line.split(" ");
        List<Float> list = Arrays.stream(Arrays.copyOfRange(origin, 1, origin.length - 1)).map((itme) -> Float.parseFloat(itme)).collect(Collectors.toList());
        return list;
    }

    /**
     * 归一化
     */
    private List<Float> normalize(List<Float> beforeNormalize) {
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

    /**
     * 解析每行数据
     */
    public static HashMap aa(String line) {
        HashMap map = new HashMap();
        String[] origin = line.split(" ");
        List<Float> list = Arrays.stream(Arrays.copyOfRange(origin, 1, origin.length)).map((itme) -> Float.parseFloat(itme)).collect(Collectors.toList());

        map.put("id", Long.parseLong(origin[0]));
        map.put("vector", list);

        return map;
    }
}
