import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

public class imgprocs {
    static int storeValues(BufferedReader FILE_READER, char delimiter) throws IOException {
        char temp;
        String store = null;

        do {
            temp = (char) FILE_READER.read();
            if (temp != delimiter)
                store = (store == null) ? Character.toString(temp) : store + temp;
        } while (temp != delimiter);

        return Integer.parseInt(store);
    }

    static boolean updateMap(BufferedReader FILE_READER, TreeMap<Integer, Integer> map) throws IOException {
        String currentLine = FILE_READER.readLine();
        if (currentLine == null)
            return false;
        int val = Integer.parseInt(currentLine);
        if (map.containsKey(val))
            map.replace(val, map.get(val) + 1);
        else
            map.put(val, 1);
        return true;
    }

    // text-based display function
    static void display(TreeMap<Integer, Integer> map) {
        for (Integer x : map.keySet())
            System.out.println(x + ": " + map.get(x));
    }

    // graph display function
    static void displayGraph(TreeMap<Integer, Integer> map) {
        int size;
        for (Integer x : map.keySet()) {
            System.out.print(x + " ");
            for (size = 1; size <= map.get(x) / 1000; size++)
                System.out.print(".");
            System.out.println(" " + size);
        }
    }

    public static void main(String[] args) {
        BufferedReader pgm = null;
        int height, width;
        TreeMap<Integer, Integer> store = new TreeMap<>();

        try {
            pgm = new BufferedReader(new FileReader("img_procs.pgm"));

            try {
                // skip first two lines
                pgm.readLine();
                pgm.readLine();

                width = storeValues(pgm, ' ');
                height = storeValues(pgm, '\n');

                while (updateMap(pgm, store))
                    ;

                // display(store);
                displayGraph(store);

            } catch (IOException e) {
                System.err.println("Error reading file");
            }

        } catch (FileNotFoundException e) {
            System.err.println("File cannot be opened");
        } finally {
            if (pgm != null) {
                try {
                    pgm.close();
                } catch (IOException e) {
                    System.err.println("File cannot be closed");
                }
            }
        }
    }
}
