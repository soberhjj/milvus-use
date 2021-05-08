import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huangJunJie  2020-12-18 11:25
 */
public class SearchByCos {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //加载总向量
        LinkedList<float[]> allFeature=new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(String.format("/home/root/AccuracyTest/allvectors/%d",i))));
            allFeature.addAll((LinkedList<float[]>) objectInputStream.readObject());
            objectInputStream.close();
        }
        System.out.println(allFeature.size());


        for (int i = 1; i < 7; i++) {
            BufferedReader br = new BufferedReader(new FileReader(String.format("/home/root/AccuracyTest/testdataset/%d", i)));

            LinkedList<float[]> testSetFeature = new LinkedList<>();
            String line;
            while ((line = br.readLine()) != null) {
                List<Float> normalize = normalize(parseLine(line));
                Float[] floats = new Float[normalize.size()];
                normalize.toArray(floats);
                testSetFeature.add(toPrimitive(floats));
            }
            br.close();

            //cos计算方式得出每个向量在全部向量中的topK向量
            LinkedList<float[]> topKByCos = calcTopKByCos(allFeature, testSetFeature, 10);

            //保存查询结果
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format("/home/root/AccuracyTest/res/%d",i)));
            oos.writeObject(topKByCos);
            oos.close();
            System.out.println("第" + i + "次计算结束");
        }

    }

    public static LinkedList<float[]> calcTopKByCos(LinkedList<float[]> allFeature, LinkedList<float[]> testSetFeature, int topk) {
        LinkedList<float[]> allFeatureTopK = new LinkedList<>();
        for (float[] f : testSetFeature) {
            LinkedList<float[]> topKForOneFeature = new LinkedList();
            LinkedList<Float> resultDistance = new LinkedList();

            //计算该向量的psvm
            float psum = 0;
            for (int l = 0; l < 512; l++) {
                psum += f[l] * f[l];
            }
            psum = (float) Math.sqrt(psum);

            //在全部向量即allFeature中搜索该向量的topK向量
            for (float[] feature : allFeature) {
                float distance = getDistance(f, feature, psum);

                if (resultDistance.size() < topk) {
                    if (resultDistance.size() == 0) {
                        resultDistance.add(distance);
                        topKForOneFeature.add(feature);
                    } else {
                        for (int j = 0; j < resultDistance.size(); j++) {
                            if (resultDistance.get(j) > distance) {
                                resultDistance.add(j, distance);
                                topKForOneFeature.add(j, feature);
                                break;
                            }
                            if (j == resultDistance.size() - 1 && resultDistance.get(j) <= distance) {
                                resultDistance.add(j + 1, distance);
                                topKForOneFeature.add(j + 1, feature);
                                break;
                            }
                        }
                    }
                } else {
                    if (resultDistance.get(0) >= distance) {
                        continue;
                    }
                    for (int m = 0; m < topk - 1; m++) {
                        if (resultDistance.get(m + 1) >= distance && resultDistance.get(m) < distance) {
                            resultDistance.add(m + 1, distance);
                            resultDistance.removeFirst();
                            topKForOneFeature.add(m + 1, feature);
                            topKForOneFeature.removeFirst();
                            break;
                        }
                    }
                    if (resultDistance.get(topk - 1) < distance) {
                        resultDistance.add(topk, distance);
                        resultDistance.removeFirst();
                        topKForOneFeature.add(topk, feature);
                        topKForOneFeature.removeFirst();
                    }
                }
            }
            for (int n = topKForOneFeature.size() - 1; n >= 0; n--) {
                allFeatureTopK.add(topKForOneFeature.get(n));
            }
        }
        return allFeatureTopK;
    }

    /**
     * cos方式计算两个向量的distance
     *
     * @param var1
     * @param var2
     * @param psum
     * @return
     */
    public static float getDistance(float[] var1, float[] var2, float psum) {
        float tsum = 0;

        for (int i = 0; i < 512; i++) {
            tsum += var1[i] * var2[i];
        }
        double v = tsum / psum;
        v = (v + 1) / 2;
        return (float) v;
    }

    /**
     * 将 Float[] 转为 float[]
     *
     * @param original
     * @return
     */
    public static float[] toPrimitive(Float[] original) {
        int length = original.length;
        float[] dest = new float[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
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


}
