package com.saadMeddiche.UTF_X.custom_decoders;

import com.saadMeddiche.UTF_X.file_samples.UTF8Sample;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
public class UTF8Decoder  {

    public static void main(String[] args) {

        UTF8Decoder decoder = new UTF8Decoder();

        String result = decoder.readString(new UTF8Sample().getFilePath());

        System.out.println(result);

    }

    public String readString(Path filePath) {


        File file = filePath.toFile();

        try (SeekableByteChannel ch = Files.newByteChannel(filePath, StandardOpenOption.READ)) {

            StringBuilder stringBuilder = new StringBuilder();
            ByteBuffer bf = ByteBuffer.allocate((int) Math.min(4, file.length())); // 1Kio

            while(ch.read(bf) > 0) {

                bf.flip();

                while (bf.position() < bf.limit()) {

                    byte headByte = bf.get();

                    if((headByte & 0b1111_0000) == 0b1111_0000) {

                        if(bf.remaining() < 3) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        byte b2 = bf.get();

                        byte b3 = bf.get();

                        byte b4 = bf.get();

                        char[] characters = characters(headByte, b2, b3, b4);

                        stringBuilder.append(characters);

                        continue;

                    }

                    if((headByte & 0b1110_0000) == 0b1110_0000) {

                        if(bf.remaining() < 2) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        byte b2 = bf.get();

                        byte b3 = bf.get();

                        char character = character(headByte, b2, b3);

                        stringBuilder.append(character);

                        continue;

                    }

                    if((headByte & 0b1100_0000) == 0b1100_0000) {

                        if(bf.remaining() < 1) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        byte b2 = bf.get();

                        char character = character(headByte, b2);

                        stringBuilder.append(character);

                        continue;

                    }

                    if((headByte & 0b1000_0000) == 0) {

                        char character = character(headByte);

                        stringBuilder.append(character);

                        continue;

                    }

                }

                bf.compact();

            }

            return stringBuilder.toString();


        } catch (IOException e) {
            log.error("Error while decoding", e);
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