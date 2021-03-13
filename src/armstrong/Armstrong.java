package armstrong;

import java.util.concurrent.TimeUnit;

public class Armstrong {

    public static void main(String[] args) throws Exception {

        long before = System.nanoTime();

        if (args.length > 0) {
            int length = Integer.parseInt(args[0]);
            if (length > 0) {
                generateSolutions(length);
            }
        } else {
            for (int length = 1; length <= 60; ++length) {
                generateSolutions(length);
            }
        }

        long after = System.nanoTime();
        long elapsed = TimeUnit.MILLISECONDS.convert(after - before, TimeUnit.NANOSECONDS);
        System.out.println("Elapsed: " + elapsed + "ms");
    }

    private static void generateSolutions(int length) {

        assert length >= 1;

        System.out.println("Length " + length);

        if (length == 1) {
            for (int digit = 0; digit <= 9; ++digit) {
                System.out.println(digit);
            }
        }
        else if (length <= 60) {
            Solver.forLength(length).generateSolutions();
        }
    }
}
