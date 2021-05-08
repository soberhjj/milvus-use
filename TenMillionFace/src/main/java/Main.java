import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huangJunJie  2021-04-12 16:05
 */
public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("/data/testdata/features.log"));
        LinkedList<float[]> list = new LinkedList<>();
        int count = 0;
        String line;
        for (int i = 0; i < 11; i++) {
            while (true) {
                list.add(parseLine(br.readLine()));
                count += 1;
                System.out.println(count);
                if (count == 2000000) {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format("/data/testdata/allvectors/%d", i)));
                    oos.writeObject(list);
                    oos.close();
                    count = 0;
                    list.clear();
                    System.out.println("生成文件");
                    break;
                }
            }
        }

        list.clear();
        int a = 0;
        while (a < 1540000 && (line = br.readLine()) != null) {
            list.add(parseLine(line));
            a++;
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format("/data/testdata/allvectors/%d", 12)));
        oos.writeObject(list);
        oos.close();
        br.close();


    }

    /**
     * 解析每行数据
     */
    public static float[] parseLine(String line) {
        String[] origin = line.split(" ");
        List<Float> list = Arrays.stream(Arrays.copyOfRange(origin, 1, origin.length)).map((itme) -> Float.parseFloat(itme)).collect(Collectors.toList());
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
}
