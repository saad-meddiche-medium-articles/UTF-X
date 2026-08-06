package com.saadMeddiche.UTF_X.custom_decoders;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.UTF8Sample;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class UTF8Decoder  {

    public static void main(String[] args) {

        UTF8Decoder decoder = new UTF8Decoder();

        decoder.readString(new UTF8Sample());

    }

    public String readString(FileSample fileSample) {

        File file = fileSample.getFilePath().toFile();

        try (SeekableByteChannel ch = Files.newByteChannel(file.toPath(), StandardOpenOption.READ)) {

            ByteBuffer bf = ByteBuffer.allocate((int) Math.min(1_024, file.length())); // 1Kio

            long characterCount = 1;

            while(ch.read(bf) > 0) {

                bf.flip();

                byte[] array = bf.array();

                for(int index = 0; index < array.length;) {

                    byte headByte = array[index++];

                    if((headByte & 0b1111_0000) == 0b1111_0000) {

                        byte b2 = array[index++];

                        byte b3 = array[index++];

                        byte b4 = array[index++];

                        char[] characters = characters(headByte, b2, b3, b4);

                        System.out.println("Character #" + characterCount++  + ": " +  new String(characters));

                        continue;

                    }

                    if((headByte & 0b1110_0000) == 0b1110_0000) {

                        byte b2 = array[index++];

                        byte b3 = array[index++];

                        char character = character(headByte, b2, b3);

                        System.out.println("Character #" + characterCount++  + ": " +  character);

                        continue;

                    }

                    if((headByte & 0b1100_0000) == 0b1100_0000) {

                        byte b2 = array[index++];

                        char character = character(headByte, b2);

                        System.out.println("Character #" + characterCount++  + ": " +  character);

                        continue;

                    }

                    if((headByte & 0b1000_0000) == 0) {

                        char character = character(headByte);

                        System.out.println("Character #" + characterCount++  + ": " +  character);

                        continue;

                    }

                }

                bf.compact();

            }

            return "";


        } catch (IOException e) {
            return "FAILED TO DECODE";
        }

    }

    private char character(byte b1) {

        byte e1 = (byte) (b1 & 0b0111_1111);

        return (char) e1;

    }

    private char character(byte b1, byte b2) {

        byte e1 = (byte) (b1 & 0b0001_1111);

        byte e2 = (byte) (b2 & 0b0011_1111);

        int merged = (e1 << 6) + e2;

        return (char) merged;

    }

    private char character(byte b1, byte b2, byte b3) {

        byte e1 = (byte) (b1 & 0b0000_1111);

        byte e2 = (byte) (b2 & 0b0011_1111);

        byte e3 = (byte) (b3 & 0b0011_1111);

        int merged = (e1 << 12) + (e2 << 6) + e3;

        return (char) merged;

    }

    private char[] characters(byte b1, byte b2, byte b3, byte b4) {

        byte e1 = (byte) (b1 & 0b0000_0111);

        byte e2 = (byte) (b2 & 0b0011_1111);

        byte e3 = (byte) (b3 & 0b0011_1111);

        byte e4 = (byte) (b4 & 0b0011_1111);

        int merged = (e1 << 18) + (e2 << 12) + (e3 << 6) + e4;

        return Character.toChars(merged);

    }

}