import java.io.*;

/**
 * @Author: huangJunJie  2021-03-27 15:47
 */
public class ExtractDataset {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("D:\\facefeatures\\2354W.txt"));
        int count = 23540000;
        while (count > 0) {
            br.readLine();
            count--;
        }
        for (int i = 1; i < 7; i++) {
            int total = 1000;
            BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\facefeatures\\extract_dataset\\"+i));
            while (total > 0) {
                bw.write(br.readLine());
                bw.newLine();
                total--;
            }
            bw.close();
        }
        br.close();
    }
}
