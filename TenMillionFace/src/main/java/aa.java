import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huangJunJie  2021-04-25 15:43
 */
public class aa {
    public static void main(String[] args) throws Exception {

        for (int i = 2; i < 3; i++) {
            ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\100w\\cos\\%d", i)));
            LinkedList<float[]> cosRes = (LinkedList<float[]>) ois1.readObject();
            ois1.close();
            ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\100w\\milvus\\%d-%d", 1024, i)));
            List<String> milvusRes = (List<String>) ois2.readObject();
            ois2.close();

            //计算top1相等的向量占总向量的比例
//            int same_top1 = 0;
//            for (int j = 0; j < cosRes.size(); j += 10) {
//                String s1 = Arrays.toString(cosRes.get(j)).replaceAll("[\\[\\],]", "");
//                String s2 = milvusRes.get(j);
//                if (s1.equals(s2)) {
//                    same_top1 += 1;
//                }else {
//                    System.out.println(j);
//                }
//            }
//            System.out.println("-------------");
//            System.out.println(same_top1);
            for (int i1 = 640; i1 < 650; i1++) {
                System.out.println(Arrays.toString(cosRes.get(i1)));
            }
            System.out.println("=================");
            for (int i1 = 640; i1 < 650; i1++) {
                System.out.println(milvusRes.get(i1));
            }


            BufferedReader br = new BufferedReader(new FileReader("D:\\facefeatures\\extract_dataset\\2"));
            LinkedList<float[]> testSetFeature = new LinkedList<>();
            String line;
            while ((line = br.readLine()) != null) {
                List<Float> normalize = normalize(parseLine(line));
                Float[] floats = new Float[normalize.size()];
                normalize.toArray(floats);
                testSetFeature.add(toPrimitive(floats));
            }
            br.close();
            System.out.println(Arrays.toString(testSetFeature.get(64)));




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
