package armstrong;

public class Solver {

    public static Solver forLength(int length)
    {
        assert length >= 2;

        int dimension = length + 1;

        byte[][][] table = new byte[10][][];
        for (byte digit = 0; digit <= 9; ++digit)
        {
            byte[][] digitTable = new byte[dimension][];
            table[digit] = digitTable;

            digitTable[0] = new byte[dimension];

            byte[] first = new byte[dimension];
            {
                first[0] = digit;
                for (int power = 1; power < length; ++power)
                {
                    Base10.mulDigit(dimension, first, digit, first);
                }
            }
            digitTable[1] = first;

            byte[] previous = first;
            for (int multiple = 2; multiple <= length; ++multiple)
            {
                byte[] current = new byte[dimension];
                Base10.add(dimension, first, previous, current);
                digitTable[multiple] = current;
                previous = current;
            }
        }

        return new Solver(length, table);
    }

    private final int length;
    private final byte[][][] table;

    private Solver(int length, byte[][][] table) {
        this.length = length;
        this.table = table;
    }

    public boolean generateSolutions() {

        int[] digitCounts = new int[10];

        byte[][] levelSums = new byte[11][];
        for (int i = 0; i <= 10; ++i) {
            levelSums[i] = new byte[length + 1];
        }

        int[][] levelPrefixCounts = new int[11][];
        for (int i = 0; i <= 10; ++i) {
            levelPrefixCounts[i] = new int[10];
        }

        return generateSolutions(9, digitCounts, length, levelSums, 0, levelPrefixCounts);
    }

    private boolean generateSolutions(int level, int[] digitCounts, int freeDigits, byte[][] levelSums,
        int prefixLength, int[][] levelPrefixCounts) {

        assert 0 < level && level <= 9;
        assert 0 < freeDigits && freeDigits <= length;

        byte[] higherLevelSum = levelSums[level + 1];
        byte[] levelSum = levelSums[level];
        byte[][] levelTable = table[level];

        assert 0 == higherLevelSum[length];

        int maxLevelDigits = freeDigits;

        for (;;) {
            Base10.add(length + 1, higherLevelSum, levelTable[maxLevelDigits], levelSum);
            if (0 == levelSum[length])
                break;
            --maxLevelDigits;
        }

        int[] prefixCounts = levelPrefixCounts[level];
        System.arraycopy(levelPrefixCounts[level + 1], 0, prefixCounts, 0, 10);

        int reservedDigits = 0;

        if (maxLevelDigits == freeDigits) {

            if (0 == levelSum[length - 1])
                return false;

            prefixLength = Base10.updatePrefixCounts(length, prefixLength, prefixCounts, higherLevelSum, levelSum);

//            for (int higherLevel = 9; higherLevel > level; --higherLevel) {
//                if (prefixCounts[higherLevel] > digitCounts[higherLevel])
//                    return true;
//            }

            int pendingDigitCounts = 0, underflow = 0;
            for (int higherLevel = 9; higherLevel > level; --higherLevel) {
                int diff = digitCounts[higherLevel] - prefixCounts[higherLevel];
                pendingDigitCounts += diff;
                underflow |= diff;
            }

            int availableLength = length - prefixLength;
            int afterPrefix = availableLength - 1;
            availableLength += (levelSum[afterPrefix] - (level + 1)) >> 31;

            underflow |= availableLength - pendingDigitCounts;

            if (underflow < 0)
                return true;

            reservedDigits -= (levelSum[afterPrefix] - level) >> 31;
        }

        for (int lowerLevel = 0; lowerLevel < level; ++lowerLevel) {
            reservedDigits += prefixCounts[lowerLevel];
        }

        maxLevelDigits = Math.min(maxLevelDigits, freeDigits - reservedDigits);

        int minLevelDigits = prefixCounts[level];
        if (minLevelDigits > maxLevelDigits)
            return true;

        if (maxLevelDigits == freeDigits) {
            digitCounts[level] = maxLevelDigits--;
            checkSolution(digitCounts, levelSum);
        }

        int suffixLength = length - prefixLength;

        if (level == 1) {

            for (int levelDigits = maxLevelDigits; levelDigits >= minLevelDigits; --levelDigits) {

                Base10.add(suffixLength, higherLevelSum, levelTable[levelDigits], levelSum);

                if (0 == levelSum[length - 1])
                    break;

                digitCounts[0] = freeDigits - levelDigits;
                digitCounts[1] = levelDigits;

                checkSolution(digitCounts, levelSum);
            }

            digitCounts[0] = 0;

        } else {

            for (int levelDigits = maxLevelDigits; levelDigits >= minLevelDigits; --levelDigits) {

                Base10.add(suffixLength, higherLevelSum, levelTable[levelDigits], levelSum);

                digitCounts[level] = levelDigits;

                if (!generateSolutions(level - 1, digitCounts, freeDigits - levelDigits, levelSums, prefixLength,
                    levelPrefixCounts))
                    break;
            }
        }

        digitCounts[level] = 0;

        return true;
    }

    private void checkSolution(int[] digitCounts, byte[] sum)
    {
        assert sumOfDigitCounts(digitCounts) == length;
        assert 0 == sum[length] && 0 != sum[length - 1];

        if (!Base10.exceedsAnyDigitCount(length, digitCounts, sum)) {
            Base10.printSum(length, sum);
        }
    }

    private static int sumOfDigitCounts(int[] digitCounts) {
        int sum = 0;
        for (int i = 0; i <= 9; ++i) {
            sum += digitCounts[i];
        }
        return sum;
    }
}
