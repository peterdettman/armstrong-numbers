package armstrong;

public class Base10 {

    static final char[] DIGIT_CHARS = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    public static void add(int n, byte[] x, byte[] y, byte[] z) {
        int c = 0;
        for (int i = 0; i < n; ++i) {
            c += x[i] + y[i];

            int condCarry = (9 - c) >> 31;
            z[i] = (byte)(c - (10 & condCarry)); 
            c = -condCarry;
        }
        assert c == 0;
    }

    public static byte[] create(int n) {
        return new byte[n];
    }

    public static boolean exceedsAnyDigitCount(int n, int[] digitCounts, byte[] x) {
        int[] availCounts = digitCounts.clone();
        for (int i = 0; i < n; ++i) {
            if (--availCounts[x[i]] < 0)
                return true;
        }
        return false;
    }

    public static byte getDigit(int pos, byte[] x) {
        return x[pos];
    }

    public static void mulDigit(int n, byte[] x, byte y, byte[] z) {
        int c = 0;
        for (int i = 0; i < n; ++i) {
            c += (int)x[i] * y;

            int hi = c / 10;
            z[i] = (byte)(c - 10 * hi);
            c = hi;
        }
        assert c == 0;
    }

//    public static void printDigitCounts(int[] digitCounts) {
//        StringBuilder sb = new StringBuilder("[");
//        for (int digit = 9; digit >= 0; --digit) {
//            char digitChar = DIGIT_CHARS[digit];
//            int count = digitCounts[digit];
//            while (--count >= 0) {
//                sb.append(digitChar);
//            }
//        }
//        sb.append("]");
//        System.out.println(sb);
//    }

    public static void printSum(int n, byte[] x) {
        StringBuilder sb = new StringBuilder(n);
        int i = n;
        while (--i >= 0) {
            sb.append(DIGIT_CHARS[x[i]]);
        }
        System.out.println(sb);
    }

    public static int updatePrefixCounts(int n, int prefixLength, int[] prefixCounts, byte[] x, byte[] y) {
        int i = n - prefixLength;
        while (--i >= 0) {
            byte xi = x[i], yi = y[i];
            if (xi != yi)
                break;

            ++prefixCounts[xi];
        }
        return n - (i + 1);
    }
}
