import com.google.gson.JsonObject;
import io.milvus.client.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @Author: huangJunJie  2021-03-25 10:54
 */
public class CreateCollectionScript {
    public static void main(String[] args) throws IOException {
        HashMap<String, String> map = parseRunParam(args);
        String host = map.get("host");
        String collectionName = map.get("cname");
        String filepath = map.get("filepath");
        String logpath = map.get("logpath");
        int port = Integer.parseInt(map.get("port"));
        int indexFileSize = Integer.parseInt(map.get("ifs"));
        int nlist = (int) (4 * Math.sqrt(indexFileSize * 1024 / 2));

        ConnectParam connectParam = new ConnectParam.Builder().withHost(host).withPort(port).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        /**
         * 创建集合
         */
        CollectionMapping collectionMapping =
                new CollectionMapping.Builder(collectionName, 512)
                        .withIndexFileSize(indexFileSize)
                        .withMetricType(MetricType.L2)
                        .build();

        client.createCollection(collectionMapping);
        /**
         * 插入数据(一次插入10000条)
         */

        List<List<Float>> vectors = new LinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        int count = 0;
        String line;
        while ((line = br.readLine()) != null) {
            vectors.add(normalize(parseLine(line)));
            if (++count == 10000) {
                InsertParam insertParam = new InsertParam.Builder(collectionName).withFloatVectors(vectors).build();
                client.insert(insertParam);
                client.flush(collectionName);
                count = 0;
                vectors.clear();
                System.out.println("新增10000");
            }
        }

        /**
         * 创建索引
         */
        IndexType indexType = IndexType.IVFLAT;
        JsonObject indexParamsJson = new JsonObject();
        indexParamsJson.addProperty("nlist", nlist);
        Index index = new Index.Builder(collectionName, indexType).withParamsInJson(indexParamsJson.toString()).build();
        client.createIndex(index);

        /**
         * 执行一次搜索，让数据载入内存
         */
        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", 256);
        SearchParam searchParam =
                new SearchParam.Builder(collectionName)
                        .withFloatVectors(generateVectors(1, 512))
                        .withTopK(10)
                        .withParamsInJson(searchParamsJson.toString())
                        .build();
        client.search(searchParam);
        client.close();
        System.out.println("script run over");
    }

    private static HashMap<String, String> parseRunParam(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("host").required().hasArg(true).type(String.class).build());
        options.addOption(Option.builder("port").required().hasArg(true).type(String.class).build());
        options.addOption(Option.builder("cname").required().hasArg(true).type(String.class).build());
        options.addOption(Option.builder("ifs").required().hasArg(true).type(String.class).build());
        options.addOption(Option.builder("filepath").required().hasArg(true).type(String.class).build());
        options.addOption(Option.builder("logpath").required().hasArg(true).type(String.class).build());


        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cl = parser.parse(options, args);
            HashMap<String, String> map = new HashMap();
            map.put("host", cl.getOptionValue("host"));
            map.put("port", cl.getOptionValue("port"));
            map.put("cname", cl.getOptionValue("cname"));
            map.put("ifs", cl.getOptionValue("ifs"));
            map.put("filepath", cl.getOptionValue("filepath"));
            map.put("logpath", cl.getOptionValue("logpath"));
            return map;
        } catch (ParseException e) {
            System.out.println("运行参数设置有误");
            System.exit(0);
        }
        return null;
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

    /**
     * 生成随机向量
     */
    public static List<List<Float>> generateVectors(int vectorCount, int dimension) {
        SplittableRandom splitcollectionRandom = new SplittableRandom();
        List<List<Float>> vectors = new ArrayList<>(vectorCount);
        for (int i = 0; i < vectorCount; ++i) {
            splitcollectionRandom = splitcollectionRandom.split();
            DoubleStream doubleStream = splitcollectionRandom.doubles(dimension);
            List<Float> vector =
                    doubleStream.boxed().map(Double::floatValue).collect(Collectors.toList());
            vectors.add(vector);
        }
        return vectors;
    }


    /**
     * 解析每行数据
     */
    public static HashMap aa(String line) {
        HashMap map=new HashMap();
        String[] origin = line.split(" ");
        List<Float> list = Arrays.stream(Arrays.copyOfRange(origin, 1, origin.length )).map((itme) -> Float.parseFloat(itme)).collect(Collectors.toList());

        map.put("id",Long.parseLong(origin[0]));
        map.put("vector",list);

        return map;
    }
}
