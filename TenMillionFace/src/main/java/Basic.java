import com.google.gson.JsonObject;
import io.milvus.client.*;
import org.junit.Test;


/**
 * @Author: huangJunJie  2021-03-24 09:15
 */
public class Basic {

    @Test
    public void a() {
        createCollection();
    }

    @Test
    public void b() {
        getColletionInfo();
    }

    @Test
    public void c() {
        dropCollection();
    }

    @Test
    public void d() {
        createIndex();
    }


//    private static String HOST = "192.168.136.198";
//    private static int PORT = 19531;
//    private static String collectionName = "FACE_1599613749000";

    private static String HOST = "192.168.136.203";
    private static int PORT = 19531;
    private static String collectionName = "FACE_1599613749000";

    public static void createCollection() {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        int dimension = 512;
        int indexFileSize = 512;
        MetricType metricType = MetricType.L2;
        CollectionMapping collectionMapping =
                new CollectionMapping.Builder(collectionName, dimension)
                        .withIndexFileSize(indexFileSize)
                        .withMetricType(metricType)
                        .build();

        client.createCollection(collectionMapping);
        client.close();
    }

    public static void getColletionInfo() {
        long l = System.currentTimeMillis();
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        System.out.println(System.currentTimeMillis()-l);

        Response getCollectionStatsResponse = client.getCollectionStats(collectionName);
        if (getCollectionStatsResponse.ok()) {
            String jsonString = getCollectionStatsResponse.getMessage();
            System.out.format("Collection Stats: %s\n", jsonString);
        }
        client.close();
    }

    public static void createIndex() {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);

        IndexType indexType = IndexType.IVFLAT;
        JsonObject indexParamsJson = new JsonObject();
        indexParamsJson.addProperty("nlist", 2048);
        Index index = new Index.Builder(collectionName, indexType).withParamsInJson(indexParamsJson.toString()).build();
        long l = System.currentTimeMillis();
        client.createIndex(index);
        System.out.println(String.format("创建索引耗时：%d 秒",System.currentTimeMillis()-l));
        client.close();
    }

    public static void dropCollection() {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        client.dropCollection(collectionName);
        client.close();
    }

    public static void dropIndex() {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(HOST).withPort(PORT).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);
        client.dropIndex(collectionName);
        client.close();
    }
    @Test
    public void testDropIndex(){
        dropIndex();
    }


}
