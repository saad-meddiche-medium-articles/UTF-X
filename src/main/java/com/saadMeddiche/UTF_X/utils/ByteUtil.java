package com.saadMeddiche.UTF_X.utils;

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

}