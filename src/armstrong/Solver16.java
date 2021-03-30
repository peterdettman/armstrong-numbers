package armstrong;

public class Solver16 {

    public static Solver16 forLength(int length)
    {
        assert length >= 2;

        int dimension = length + 1;

        int[][][] table = new int[16][][];
        for (byte digit = 0; digit <= 15; ++digit)
        {
            int[][] digitTable = new int[dimension][];
            table[digit] = digitTable;

            digitTable[0] = Base16.create(dimension);

            int[] first = Base16.create(dimension);
            {
                first[0] = digit;
                for (int power = 1; power < length; ++power)
                {
                    Base16.mulDigit(dimension, first, digit, first);
                }
            }
            digitTable[1] = first;

            int[] previous = first;
            for (int multiple = 2; multiple <= length; ++multiple)
            {
                int[] current = Base16.create(dimension);
                Base16.add(dimension, first, previous, current);
                digitTable[multiple] = current;
                previous = current;
            }
        }

        return new Solver16(length, table);
    }

    private final int length;
    private final int[][][] table;

    private Solver16(int length, int[][][] table) {
        this.length = length;
        this.table = table;
    }

    public boolean generateSolutions() {

        int[] digitCounts = new int[16];

        int[][] levelSums = new int[17][];
        for (int i = 0; i <= 16; ++i) {
            levelSums[i] = Base16.create(length + 1);
        }

        int[][] levelPrefixCounts = new int[17][];
        for (int i = 0; i <= 16; ++i) {
            levelPrefixCounts[i] = new int[16];
        }

        return generateSolutions(15, digitCounts, length, levelSums, 0, levelPrefixCounts);
    }

    private boolean generateSolutions(int level, int[] digitCounts, int freeDigits, int[][] levelSums,
        int prefixLength, int[][] levelPrefixCounts) {

        assert 0 < level && level <= 15;
        assert 0 < freeDigits && freeDigits <= length;

        int[] higherLevelSum = levelSums[level + 1];
        int[] levelSum = levelSums[level];
        int[][] levelTable = table[level];

        assert 0 == Base16.getDigit(length, levelSum);

        int maxLevelDigits = freeDigits;

        for (;;) {
            Base16.add(length + 1, higherLevelSum, levelTable[maxLevelDigits], levelSum);
            if (0 == Base16.getDigit(length, levelSum))
                break;
            --maxLevelDigits;
        }

        int[] prefixCounts = levelPrefixCounts[level];
        System.arraycopy(levelPrefixCounts[level + 1], 0, prefixCounts, 0, 16);

        int reservedDigits = 0;

        if (maxLevelDigits == freeDigits) {

            if (0 == Base16.getDigit(length - 1, levelSum))
                return false;

            prefixLength = Base16.updatePrefixCounts(length, prefixLength, prefixCounts, higherLevelSum, levelSum);

//            for (int higherLevel = 15; higherLevel > level; --higherLevel) {
//                if (prefixCounts[higherLevel] > digitCounts[higherLevel])
//                    return true;
//            }

            int pendingDigitCounts = 0, underflow = 0;
            for (int higherLevel = 15; higherLevel > level; --higherLevel) {
                int diff = digitCounts[higherLevel] - prefixCounts[higherLevel];
                pendingDigitCounts += diff;
                underflow |= diff;
            }

            int availableLength = length - prefixLength;
            int afterPrefix = Base16.getDigit(availableLength - 1, levelSum);
            availableLength += (afterPrefix - (level + 1)) >> 31;

            underflow |= availableLength - pendingDigitCounts;

            if (underflow < 0)
                return true;

            reservedDigits -= (afterPrefix - level) >> 31;
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

                Base16.add(suffixLength, higherLevelSum, levelTable[levelDigits], levelSum);

                if (0 == Base16.getDigit(length - 1, levelSum))
                    break;

                digitCounts[0] = freeDigits - levelDigits;
                digitCounts[1] = levelDigits;

                checkSolution(digitCounts, levelSum);
            }

            digitCounts[0] = 0;

        } else {

            for (int levelDigits = maxLevelDigits; levelDigits >= minLevelDigits; --levelDigits) {

                Base16.add(suffixLength, higherLevelSum, levelTable[levelDigits], levelSum);

                digitCounts[level] = levelDigits;

                if (!generateSolutions(level - 1, digitCounts, freeDigits - levelDigits, levelSums, prefixLength,
                    levelPrefixCounts))
                    break;
            }
        }

        digitCounts[level] = 0;

        return true;
    }

    private void checkSolution(int[] digitCounts, int[] sum)
    {
        assert sumOfDigitCounts(digitCounts) == length;
        assert 0 == Base16.getDigit(length, sum) && 0 != Base16.getDigit(length - 1, sum);

        if (!Base16.exceedsAnyDigitCount(length, digitCounts, sum)) {
            Base16.printSum(length, sum);
        }
    }

    private static int sumOfDigitCounts(int[] digitCounts) {
        int sum = 0;
        for (int i = 0; i <= 15; ++i) {
            sum += digitCounts[i];
        }
        return sum;
    }
}
