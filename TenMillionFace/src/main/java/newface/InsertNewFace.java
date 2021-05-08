package newface;

import io.milvus.client.ConnectParam;
import io.milvus.client.InsertParam;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-04-26 09:34
 */
public class InsertNewFace {
    public static void main(String[] args) throws Exception{
        //载入新增的向量，并且也将每个向量转换为float[]
        BufferedReader br = new BufferedReader(new FileReader("D:\\人脸图片分两组\\group1中每张人脸对应的特征向量.txt"));
        String line;
        List<List<Float>> vectors=new LinkedList<>();
        while ((line = br.readLine()) != null) {
            String strFeature = line.split(":")[1];
            List<Float> vector=new LinkedList<>();
            String[] dimValue = strFeature.replace(" ", "").replace("[", "").replace("]", "").split(",");
            for (int i = 0; i < dimValue.length; i++) {
                vector.add(Float.parseFloat(dimValue[i]));
            }
            vectors.add(normalize(vector));
        }
        br.close();

        ConnectParam connectParam = new ConnectParam.Builder().withHost("192.168.136.176").withPort(19530).build();
        MilvusClient client = new MilvusGrpcClient(connectParam);



        InsertParam insertParam = new InsertParam.Builder("suotian").withFloatVectors(vectors).build();
        client.insert(insertParam);
        client.flush("suotian");



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
}
