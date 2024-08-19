package operator;

public class OperatorAdd1 {
    public static void main(String[] args) {
        int a = 10;

        a = a + 1;
        System.out.println("a = " + a);
        a = a + 1;
        System.out.println("a = " + a);

        ++a;
        System.out.println("a = " + a);
        ++a;
        System.out.println("a = " + a);
    }
}
