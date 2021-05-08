package newface;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-04-16 09:34
 */
public class CompareCosAndMilvus {
    public static void main(String[] args) throws Exception {
        List<Integer> nprobes = new LinkedList<>();
        String nprobe_number = "1,16,32,64,128,256,512,1024,2048,2895";
        for (String s : nprobe_number.split(",")) {
            nprobes.add(Integer.parseInt(s));
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\100w_addnewface\\测试结果.txt"));
        for (int i = 1; i < 2; i++) {
            for (int nprobe : nprobes) {
                ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\100w_addnewface\\cos\\%d", i)));
                LinkedList<float[]> cosRes = (LinkedList<float[]>) ois1.readObject();
                System.out.println(cosRes.size());

                ois1.close();
                ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(String.format("C:\\Users\\Administrator\\Desktop\\milvus后期测试20210421\\准确性比较\\100w_addnewface\\milvus_new\\%d-%d", nprobe, i)));
                List<String> milvusRes = (List<String>) ois2.readObject();
                System.out.println(milvusRes.size());

                ois2.close();

                //计算top1相等的向量占总向量的比例
                int same_top1 = 0;
                for (int j = 0; j < cosRes.size(); j += 10) {
                    String s1 = Arrays.toString(cosRes.get(j)).replaceAll("[\\[\\],]", "");
                    String s2 = milvusRes.get(j);

                    if (s1.equals(s2)) {
                        same_top1 += 1;
                    }
                }

//                for (int i1=420;i1<430;i1++){
//                    System.out.println(Arrays.toString(cosRes.get(i1)).replaceAll("[\\[\\],]", ""));
//                }
//                System.out.println("===========================");
//                for (int i1=420;i1<430;i1++){
//                    System.out.println(milvusRes.get(i1));
//                }


                bw.write(String.format("测试数据集%d，nprobe %d，top1相等的向量个数 %d", i, nprobe, same_top1));
                bw.newLine();

                //计算top1到top10两两对应相等
                int same_top1_to_top10 = 0;
                for (int j = 0; j < cosRes.size(); j += 10) {
                    int m = j;
                    for (; m < j + 10; m++) {
                        String s1 = Arrays.toString(cosRes.get(m)).replaceAll("[\\[\\],]", "");
                        String s2 = milvusRes.get(m);
                        if (!s1.equals(s2)) {
                            break;
                        }
                    }
                    if (m == (j + 10)) {
                        same_top1_to_top10 += 1;
                    }
                }
                bw.write(String.format("测试数据集%d，nprobe %d，top1到top10两两对应相等的向量个数 %d", i, nprobe, same_top1_to_top10));
                bw.newLine();

                //计算milvus搜索的top1在余弦计算的top10中
                int milvusTop1_in_cosTop10 = 0;
                for (int j = 0; j < cosRes.size(); j += 10) {
                    String s2 = milvusRes.get(j);
                    for (int m = j; m < j + 10; m++) {
                        String s1 = Arrays.toString(cosRes.get(m)).replaceAll("[\\[\\],]", "");
                        if (s1.equals(s2)) {
                            milvusTop1_in_cosTop10 += 1;
                            break;
                        }
                    }
                }
                bw.write(String.format("测试数据集%d，nprobe %d，milvus搜索的top1在余弦计算的top10中的向量个数 %d", i, nprobe, milvusTop1_in_cosTop10));
                bw.newLine();

                //计算milvus搜索的top10都在余弦计算的top10中
                int milvusTop10_in_cosTop10 = 0;
                for (int j = 0; j < cosRes.size(); ) {
                    List<String> milvus = milvusRes.subList(j, j + 10);
                    List<String> cos = new LinkedList<>();
                    for (int m = 0; m < 10; m++) {
                        cos.add(Arrays.toString(cosRes.get(j++)).replaceAll("[\\[\\],]", ""));
                    }
                    if (inclusionRate(cos, milvus) == 10) {
                        milvusTop10_in_cosTop10 += 1;
                    }
                }
                bw.write(String.format("测试数据集%d，nprobe %d，milvus搜索的top10都在余弦计算的top10中的向量个数 %d", i, nprobe, milvusTop10_in_cosTop10));
                bw.newLine();
            }
        }
        bw.close();
    }


    /**
     * list2中的元素在list1中占的比例
     *
     * @param list1
     * @param list2
     */
    public static int inclusionRate(List<String> list1, List<String> list2) {
        int include = 0;
        for (String s2 : list2) {
            for (String s1 : list1) {
                if (s2.equals(s1)) {
                    include += 1;
                    break;
                }
            }
        }
        return include;
    }

}
