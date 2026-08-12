package com.saadMeddiche.UTF_X.utils;

import java.util.StringJoiner;

public class ByteUtil {

    private ByteUtil() {}

    public static boolean isBitSet(byte b, int position) {

        assert 0 <= position && position <= 7;

        return (b & 1 << position) != 0;

    }

    public static boolean isBitNoSet(byte b, int position) {

        assert 0 <= position && position <= 7;

        return (b & 1 << position) == 0;

    }

    public static void printBytes(byte[] bytes) {
        System.out.println(asString(bytes));
    }

    public static void printByte(byte b) {
        System.out.println(asString(b));
    }

    public static void printByte(byte b, int number) {
        System.out.println(asString(b, number));
    }

    public static String asString(byte[] bytes) {

        StringJoiner sb = new StringJoiner(",");

        for(byte b : bytes) {
            sb.add(asString(b));
        }

        return "[" + sb.toString() + "]";

    }

    public static String asString(byte b) {
        // b & 0xFF prevents sign extension for negative bytes
        String bits = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
        return "0b" + bits.substring(0, 4) + "_" + bits.substring(4);
    }

    public static String asString(byte b, int number) {
        // b & 0xFF prevents sign extension for negative bytes
        String bits = String.format("%" + number +"s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
        return "0b" + bits.substring(0, 4) + "_" + bits.substring(4);
    }

}