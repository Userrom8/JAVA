// package JAVA.college_assignments;

import java.util.ArrayList;

public class anagram {
    static String word = new String();
    static ArrayList<String> permutations = new ArrayList<>();

    static void swap(int index1, int index2) {
        char[] ch = word.toCharArray();
        char temp = ch[index1];
        ch[index1] = ch[index2];
        ch[index2] = temp;
        word = new String(ch);
    }

    static void perm(int size) {
        if (size == 1) {
            permutations.add(word); 
            return;
        }
        for (int i = 0; i < size; i++) {
            perm(size - 1);
            if (size % 2 == 1)
                swap(0, size - 1);
            else
                swap(i, size - 1);
        }
    }

    public static void main(String args[]) {
        word = "fork";
        perm(word.length());
        for (String x : permutations)
            System.out.println(x);
    }
}
