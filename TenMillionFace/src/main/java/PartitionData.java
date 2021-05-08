import java.io.*;

/**
 * @Author: huangJunJie  2021-03-24 14:28
 */
public class PartitionData {
    public static void main(String[] args) throws IOException{
        new PartitionData().partitionData();
    }

    public void partitionData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("D:\\facefeatures\\2354W.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\facefeatures\\500W.txt"));
        int count = 5000000;
        while (count > 0) {
            bw.write(br.readLine());
            bw.newLine();
            count--;
        }
        br.close();
        bw.close();
    }
}
