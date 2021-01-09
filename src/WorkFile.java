import java.io.*;
import java.util.*;

public abstract class WorkFile {

    private static  void check(String filename) throws Exception {

        java.io.File file = new java.io.File(filename);
        Scanner in = new Scanner(file);
        String data;

        while (in.hasNext()){
            data = new String(in.nextLine());
            if(!data.trim().equals("")){
                return;
            }
        }

        in.close();
        throw new Exception("Файл состоит из пустых строк.");
    }

    public static String readFile( String fileName) throws Exception {

        WorkFile.check(fileName);
        Scanner in = new Scanner(new File(fileName));

        StringBuilder data;
        StringBuilder buffText = new StringBuilder();
        while (in.hasNextLine()) {
            data = new StringBuilder(in.nextLine());

            if ((new String(data.toString())).trim().equals(""))
                continue;
            buffText.append(data).append(" ");
        }

        return buffText.toString();
    }

    public static void writeFile( String fileName, String text) throws Exception{

        FileOutputStream ps = new FileOutputStream(fileName);
        ps.write(text.getBytes());
        ps.close();
    }
}