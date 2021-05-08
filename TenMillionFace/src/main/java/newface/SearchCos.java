package newface;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huangJunJie  2021-04-26 10:47
 */
public class SearchCos {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //load data
        BufferedReader br = new BufferedReader(new FileReader("/home/root/accuracyTestFor100wData/100W.txt"));
        LinkedList<float[]> list = new LinkedList<>();
        String line;
        while ((line = br.readLine()) != null) {
            list.add(parseLine(line));
        }
        br.close();

        //load new face
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/home/root/accuracyTestFor100wData/group1中每张人脸对应的特征向量.txt"));
        String line1;
        while ((line1 = bufferedReader.readLine()) != null) {
            String strFeature = line1.split(":")[1];
            String[] dimValue = strFeature.replace(" ", "").replace("[", "").replace("]", "").split(",");
            float[] vector = new float[dimValue.length];
            for (int i = 0; i < vector.length; i++) {
                vector[i] = Float.parseFloat(dimValue[i]);
            }
            list.add(normalize(vector));
        }
        bufferedReader.close();

        System.out.println(String.format("数据总量 %d 条", list.size()));

        //load test_dataset and calculate top10
        for (int i = 0; i < 5; i++) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(String.format("/home/root/accuracyTestFor100wData/测试集/testDataset%d", i)));
            LinkedList<Float[]> testDataset = (LinkedList<Float[]>) ois.readObject();
            ois.close();

            LinkedList<float[]> searchVectors=new LinkedList<>();
            for (Float[] temp : testDataset) {
                searchVectors.add(toPrimitive(temp));
            }

            //cos计算方式得出每个向量在全部向量中的topK向量
            LinkedList<float[]> topKByCos = calcTopKByCos(list, searchVectors, 10);

            //保存查询结果
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format("/home/root/accuracyTestFor100wData/res/%d", i)));
            oos.writeObject(topKByCos);
            oos.close();
            System.out.println("第" + i + "次计算结束");
        }
    }

    /**
     * 解析每行数据
     */
    public static float[] parseLine(String line) {
        String[] origin = line.split(" ");
        List<Float> list = Arrays.stream(Arrays.copyOfRange(origin, 1, origin.length - 1)).map((itme) -> Float.parseFloat(itme)).collect(Collectors.toList());
        List<Float> normalize = normalize(list);
        float[] vector = new float[512];
        int i = 0;
        for (Float f : normalize) {
            vector[i++] = f;
        }
        return vector;
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
     * 归一化
     */
    private static float[] normalize(float[] beforeNormalize) {
        double v = 0;
        for (float element : beforeNormalize) {
            v += Math.pow(element, 2);
        }
        double sqrtV = Math.sqrt(v);

        float[] afterNormalize = new float[beforeNormalize.length];
        for (int i = 0; i < afterNormalize.length; i++) {
            afterNormalize[i] = (float) (beforeNormalize[i] / sqrtV);
        }
        return afterNormalize;
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

}
