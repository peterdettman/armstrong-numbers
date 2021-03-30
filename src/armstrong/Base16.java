package armstrong;

public class Base16 {

    private static final long M32L = 0xFFFFFFFFL;

    static final char[] DIGIT_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F' };

    public static void add(int n, int[] x, int[] y, int[] z) {
        int n1 = n & 7, n8 = n >>> 3;
        long cc = 0L;
        for (int i8 = 0; i8 < n8; ++i8) {
            cc += x[i8] & M32L;
            cc += y[i8] & M32L;
            z[i8] = (int)cc;
            cc >>>= 32;
        }
        if (0 == n1) {
            assert 0L == cc;
            return;
        }

        int maskHigh = -1 << (n1 << 2), maskLow = ~maskHigh;
        cc += x[n8] & maskLow;
        cc += y[n8] & maskLow;
        assert 0 == ((int)cc & maskHigh);
        z[n8] &= maskHigh;
        z[n8] += (int)cc;
    }

    public static int[] create(int n) {
        return new int[(n + 7) >>> 3];
    }

    public static boolean exceedsAnyDigitCount(int n, int[] digitCounts, int[] x) {
        int[] availCounts = digitCounts.clone();
        int n1 = n & 7, n8 = n >>> 3;
        for (int i8 = 0; i8 < n8; ++i8) {
            int xi = x[i8];  
            for (int s = 0; s < 32; s += 4) {
                if (--availCounts[(xi >>> s) & 0xF] < 0)
                    return true;
            }
        }
        if (n1 > 0) {
            int xi = x[n8];  
            for (int s = 0; s < (n1 << 2); s += 4) {
                if (--availCounts[(xi >>> s) & 0xF] < 0)
                    return true;
            }
        }
        return false;
    }

    public static int getDigit(int pos, int[] x) {
        int n1 = pos & 7, n8 = pos >>> 3;
        return (x[n8] >>> (n1 << 2)) & 0xF;
    }

    public static void mulDigit(int n, int[] x, int y, int[] z) {
        // NOTE: Doesn't attempt unnecessary partial final word handling
        int n8 = (n + 7) >>> 3;
        long cc = 0L;
        for (int i8 = 0; i8 < n8; ++i8) {
            cc += (x[i8] & M32L) * y;
            z[i8] = (int)cc;
            cc >>>= 32;
        }
        assert 0L == cc;
    }

//    public static void printDigitCounts(int[] digitCounts) {
//        StringBuilder sb = new StringBuilder("[");
//        for (int digit = 15; digit >= 0; --digit) {
//            char digitChar = DIGIT_CHARS[digit];
//            int count = digitCounts[digit];
//            while (--count >= 0) {
//                sb.append(digitChar);
//            }
//        }
//        sb.append("]");
//        System.out.println(sb);
//    }

    public static void printSum(int n, int[] x) {
        StringBuilder sb = new StringBuilder(n);
        int n1 = n & 7, n8 = n >>> 3;
        if (n1 > 0) {
            int xi = x[n8];
            int s = n1 << 2;
            while ((s -= 4) >= 0) {
                sb.append(DIGIT_CHARS[(xi >>> s) & 0xF]);
            }
        }
        int i8 = n8;
        while (--i8 >= 0) {
            int xi = x[i8];
            int s = 32;
            while ((s -= 4) >= 0) {
                sb.append(DIGIT_CHARS[(xi >>> s) & 0xF]);
            }
        }
        System.out.println(sb);
    }

    public static int updatePrefixCounts(int n, int prefixLength, int[] prefixCounts, int[] x, int[] y) {
        int i = n - prefixLength;
        while (--i >= 0) {
//            int xi = getDigit(i, x), yi = getDigit(i, y);
            int n1 = i & 7, n8 = i >>> 3, s = n1 << 2;
            int xi = (x[n8] >>> s) & 0xF;
            int yi = (y[n8] >>> s) & 0xF;

            if (xi != yi)
                break;

            ++prefixCounts[xi];
        }
        return n - (i + 1);
    }
}
