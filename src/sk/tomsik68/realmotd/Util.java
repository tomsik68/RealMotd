package sk.tomsik68.realmotd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class Util {

    public static boolean isInt(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getInt(String string) {
        try {
            return Integer.valueOf(string);
        } catch (Exception e) {
            return -1;
        }
    }

    public static String readFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
                result = result.append(line).append("/n");
            }
            br.close();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static void writeFile(File file, String... lines) {
        try {
            PrintWriter pw = new PrintWriter(file);
            for (String s : lines) {
                pw.println(s);
            }
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
