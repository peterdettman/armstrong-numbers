package armstrong;

import java.util.concurrent.TimeUnit;

public class Armstrong {

    public static void main(String[] args) throws Exception {

        long startTime = System.nanoTime();

        if (args.length > 0) {
            int length = Integer.parseInt(args[0]);
            if (length > 0) {
                generateSolutions(startTime, length);
            }
        } else {
            for (int length = 1; length <= 60; ++length) {
                generateSolutions(startTime, length);
            }
        }

        System.out.println("Elapsed: " + getElapsed(startTime) + "ms");
    }

    private static void generateSolutions(long startTime, int length) {

        assert length >= 1;

        System.out.println("Length " + length + " [@" + getElapsed(startTime) + "ms]");

        if (length == 1) {
            for (int digit = 0; digit <= 9; ++digit) {
                System.out.println(Base10.DIGIT_CHARS[digit]);
            }
        }
        else if (length <= 60) {
            Solver.forLength(length).generateSolutions();
        }
    }

    private static long getElapsed(long startTime) {
        return TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    }
}
