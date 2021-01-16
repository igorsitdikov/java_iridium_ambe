package l2m.decoder.iridium;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {

        /*
         * при извлечении из interbase db iridium
         * записей не надо делать переворот бит в байте
         */

        FileOutputStream fos = new FileOutputStream("output.wav");

        DataOutputStream dos = new DataOutputStream(fos);
        File file = new File("test_rev.bin");
        byte[] input = Files.readAllBytes(file.toPath());

        int inSize = input.length - input.length / 40;
        int outSize = inSize / 39 * 720 * 2;
        byte[] result = new byte[outSize];

        Iridium ir = new Iridium();
        ir.to_decode(input, inSize, result, outSize);

        byte[] bytes = PCMHelper.PCM2Wave(result, 1, 8000, 16);
        dos.write(bytes);
        dos.close();

        System.out.println("converted");
    }
}
