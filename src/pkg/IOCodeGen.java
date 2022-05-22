package pkg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IOCodeGen {

    public static List<String> input(String path) {
        List<String> strs = new ArrayList<>();
        try {
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            while (line != null) {
                strs.add(line);
                line = br.readLine();
            }
            reader.close();
        } catch (IOException ioe) {
            System.err.println("Error: " + ioe);
        } finally {
            return strs;
        }
    }

    public static void output(List<String> lines, String path) {
        try {
            FileWriter writer = new FileWriter(path, false);
            writer.write("\n");
            int i = 1;
            for (String line : lines) {
                writer.write("    " + line.trim());
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
