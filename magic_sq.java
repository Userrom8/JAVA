// package JAVA;

public class magic_sq {
    int[][] square;
    int order;

    magic_sq(int val) {
        square = new int[val][val];
        order = val;
    }

    static void generateSquare(magic_sq object) {
        int iter = 1, row = object.order / 2, col = object.order - 1;
        if (object.order % 2 == 0)
            return;
        while (iter <= object.order * object.order) {
            if (row == -1 && col == object.order) {
                row = 0;
                col = object.order - 2;
            } else {
                row = row < 0 ? object.order - 1 : row;
                col = col == object.order ? 0 : col;
            }
            if (object.square[row][col] != 0) {
                row += 1;
                col -= 2;
                continue;
            }
            object.square[row][col] = iter++;
            row -= 1;
            col += 1;
        }
    }

    public static void main(String[] args) {
        int i, j, order = 3;
        magic_sq magic_Square = new magic_sq(order);
        generateSquare(magic_Square);
        for (i = 0; i < order; i++) {
            for (j = 0; j < order; j++)
                System.out.print(magic_Square.square[i][j] + "\t");
            System.out.println();
        }
    }
}
