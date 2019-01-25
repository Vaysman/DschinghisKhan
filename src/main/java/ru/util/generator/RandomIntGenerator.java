package ru.util.generator;

public class RandomIntGenerator {
    private static final String ALPHA_NUMERIC_STRING = "012345789";

    /**
     * @param count amount of numbers that will be generated
     * @return sequence of random numbers
     */
    public static Integer randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return Integer.valueOf(builder.toString());
    }
}
