package array;

import java.util.Scanner;

public class EnhancedFor1 {
    public static int add(int a, int b) {
        int sumValue = a + b;
        return sumValue;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int[][] numbers = new int[5][5];

        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                int number = input.nextInt();
                numbers[i][j] = number;
            }
        }

        int res = 0;
        for (int i = 0; i < numbers.length; i++) {
            for (int number : numbers[i]) {
                res = add(res, number);
            }
        }
        System.out.println(res);
    }
}
