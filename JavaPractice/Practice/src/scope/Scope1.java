package scope;

public class Scope1 {
    public static void main(String[] args) {
        int m = 10;
        if (true) {
            int x = 20;
            System.out.println("if m = " + m);
            System.out.println("if x = " + x);
        }

        // System.out.println("main x = " + x); //오류, 변수 x에 접근 불가
        System.out.println("main m = " + m);
    }
}
