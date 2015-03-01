package com.util;

/**
 * Utility class fo decoding words
 */
public final class NumberDecoder {

    private NumberDecoder() {
        throw new AssertionError("Trying to instantiate utility class");
    }

    /**
     * Decodes each character by the next schema
     * E | J N Q | R W X | D S Y | F T | A M | C I V | B K U | L O P | G H Z
     * e | j n q | r w x | d s y | f t | a m | c i v | b k u | l o p | g h z
     * 0 |   1   |   2   |   3   |  4  |  5  |   6   |   7   |   8   |   9
     *
     * @param word word to decode
     * @return decoded representation of string
     */
    public static String decode(String word) {
        StringBuilder sb = new StringBuilder(word.length());

        for (char c : word.toCharArray()) {
            sb.append(decode(c));
        }

        return sb.toString();
    }

    private static int decode(char c) {
        switch (c) {
            case 'E':
            case 'e':
                return 0;
            case 'J':
            case 'j':
            case 'N':
            case 'n':
            case 'Q':
            case 'q':
                return 1;
            case 'R':
            case 'r':
            case 'W':
            case 'w':
            case 'X':
            case 'x':
                return 2;
            case 'D':
            case 'd':
            case 'S':
            case 's':
            case 'Y':
            case 'y':
                return 3;
            case 'F':
            case 'f':
            case 'T':
            case 't':
                return 4;
            case 'A':
            case 'a':
            case 'M':
            case 'm':
                return 5;
            case 'C':
            case 'c':
            case 'I':
            case 'i':
            case 'V':
            case 'v':
                return 6;
            case 'B':
            case 'b':
            case 'K':
            case 'k':
            case 'U':
            case 'u':
                return 7;
            case 'L':
            case 'l':
            case 'O':
            case 'o':
            case 'P':
            case 'p':
                return 8;
            case 'G':
            case 'g':
            case 'H':
            case 'h':
            case 'Z':
            case 'z':
                return 9;
            default:
                throw new IllegalArgumentException("Unsupported character " + c);
        }
    }
}