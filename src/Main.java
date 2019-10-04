import java.io.*;
import java.nio.file.Files;

public class Main {

    public static void main ( String[] args ) throws IOException, InterruptedException {

        /*
         * при извлечении из interbase db iridium
         * записей не надо делать переворот бит в байте
         */

        String currentDirectory = System.getProperty("user.dir");

        FileOutputStream fos = new FileOutputStream(currentDirectory + "/src/output.wav");

        DataOutputStream dos = new DataOutputStream(fos);
        File file = new File(currentDirectory + "/src/test_rev.bin");
        byte[] ar = Files.readAllBytes(file.toPath());

        String[] cmd = {"ffmpeg", "-f", "s16le", "-ar", "8000", "-ac", "1", "-i", "pipe:0", "-f", "wav", "-ar", "8000", "-ac", "1", "pipe:1"};

        int outputsize = ar.length - ar.length / 40;
        int sizeres = outputsize / 39 * 720 * 2;
        byte[] aa = new byte[sizeres];
        l2m.lissen2me.Iridium ir = new l2m.lissen2me.Iridium();

        ir.to_decode(ar, outputsize, aa, sizeres);

        dos.write(wav8kToWavNk(cmd, aa));

        dos.close();

        System.out.println("");
    }

    public static byte[] wav8kToWavNk ( String[] cmd, byte[] input ) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectInput();
        builder.redirectOutput();
        Process process = builder.start();
        OutputStream stdin = process.getOutputStream();
        byteArrayToOs(stdin, input);

        InputStream is = process.getInputStream();
        byte[] outputArray = isToByteAray(is);
        process.waitFor();
        return outputArray;
    }

    public static void byteArrayToOs ( OutputStream os, byte[] array ) {
        Thread t = new Thread(() -> {
            BufferedOutputStream writer = new BufferedOutputStream(os);
            try {
                writer.write(array);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public static byte[] isToByteAray ( InputStream is ) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c = bis.read();
        while (c != -1) {
            baos.write(c);
            c = bis.read();
        }
        is.close();
        return baos.toByteArray();
    }
}
