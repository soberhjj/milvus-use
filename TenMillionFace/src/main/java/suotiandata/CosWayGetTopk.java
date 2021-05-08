package suotiandata;

import java.io.*;
import java.util.LinkedList;

/**
 * @Author: huangJunJie  2020-12-18 11:25
 */
public class CosWayGetTopk {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //加载总向量
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("/home/root/accuracyTestSuotianData/归一化后总向量")));
        // ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\抠出测试集\\抠出测试集后的总向量\\总向量")));
        LinkedList<float[]> allFeature = (LinkedList<float[]>) objectInputStream.readObject();
        objectInputStream.close();
        System.out.println(allFeature.size());

        for (int i = 0; i < 5; i++) {
            if (i==3){
                continue;
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("/home/root/accuracyTestSuotianData/testDataset"+i)));
            LinkedList<Float[]> testDataset = (LinkedList<Float[]>) ois.readObject();
            ois.close();
            LinkedList<float[]> testSetFeature = new LinkedList<>();
            for (Float[] f : testDataset) {
                testSetFeature.add(toPrimitive(f));
            }

            //cos计算方式得出每个向量在全部向量中的topK向量

            LinkedList<float[]> topKByCos = calcTopKByCos(allFeature, testSetFeature, 10);
            System.out.println("结果向量个数：" + topKByCos.size());


            //保存查询结果
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/home/root/accuracyTestSuotianData/res/"+i));
            oos.writeObject(topKByCos);
            oos.close();
            System.out.println("计算结束");

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


}
