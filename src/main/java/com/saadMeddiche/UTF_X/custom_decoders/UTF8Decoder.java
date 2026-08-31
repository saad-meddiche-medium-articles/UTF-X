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

import static com.saadMeddiche.UTF_X.utils.ByteUtil.*;

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

            long treatedBytes = 0;
            long fileSize = ch.size();
            StringBuilder stringBuilder = new StringBuilder();
            ByteBuffer bf = ByteBuffer.allocate((int) Math.min(1_024, file.length())); // 1Kio

            while(ch.read(bf) > 0) {

                bf.flip();

                while (bf.position() < bf.limit()) {

                    byte headByte = bf.get();

                    if((headByte & 0b1111_1000) == 0b1111_0000) {

                        if(bf.remaining() < 3) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        byte b2 = bf.get();

                        byte b3 = bf.get();

                        byte b4 = bf.get();

                        if(!isContinuation(b2) || !isContinuation(b3) || !isContinuation(b4)) {
                            stringBuilder.append('\uFFFD');
                            continue;
                        }

                        int codePoint = extractCodePoint(headByte, b2, b3, b4);

                        char[] characters;
                        if(0x10000 <= codePoint && codePoint < 0x10FFFF)
                            characters = Character.toChars(codePoint);
                        else
                            characters = new char[] {'\uFFFD', '\uFFFD', '\uFFFD', '\uFFFD'};

                        stringBuilder.append(characters);

                        continue;

                    }

                    if((headByte & 0b1111_0000) == 0b1110_0000) {

                        if(bf.remaining() < 2) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        byte b2 = bf.get();

                        byte b3 = bf.get();

                        if(!isContinuation(b2) || !isContinuation(b3)) {
                            stringBuilder.append('\uFFFD');
                            continue;
                        }

                        int codePoint = extractCodePoint(headByte, b2, b3);

                        char[] characters;
                        if(0x800 <= codePoint && codePoint < 0xFFFF)
                            characters = Character.toChars(codePoint);
                        else
                            characters = new char[] {'\uFFFD', '\uFFFD', '\uFFFD'};

                        stringBuilder.append(characters);

                        continue;

                    }

                    if((headByte & 0b1110_0000) == 0b1100_0000) {

                        if(bf.remaining() < 1) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        byte b2 = bf.get();

                        if(!isContinuation(b2)) {
                            stringBuilder.append('\uFFFD');
                            continue;
                        }

                        int codePoint = extractCodePoint(headByte, b2);

                        char[] characters;
                        if(0x80 <= codePoint && codePoint < 0x7FF)
                            characters = Character.toChars(codePoint);
                        else
                            characters = new char[] {'\uFFFD', '\uFFFD'};

                        stringBuilder.append(characters);

                        continue;

                    }

                    if((headByte & 0b1000_0000) == 0b0000_0000) {

                        int codePoint = extractCodePoint(headByte);

                        char[] characters;
                        if(0 <= codePoint && codePoint < 0x7F)
                            characters = Character.toChars(codePoint);
                        else
                            characters = new char[] {'\uFFFD'};

                        stringBuilder.append(characters);

                        continue;

                    }

                    stringBuilder.append('\uFFFD');

                }

                treatedBytes += bf.position();

                bf.compact();

            }

            if(treatedBytes != fileSize) {
                stringBuilder.append('\uFFFD');
            }

            return stringBuilder.toString();


        } catch (IOException e) {
            log.error("Error while decoding", e);
            return "FAILED TO DECODE";
        }

    }

    private int extractCodePoint(byte b1) {

        return b1 & 0b0111_1111;

    }

    private int extractCodePoint(byte b1, byte b2) {

        int e1 = b1 & 0b0001_1111;

        int e2 = b2 & 0b0011_1111;

        return (e1 << 6) + e2;

    }

    private int extractCodePoint(byte b1, byte b2, byte b3) {

        int e1 = b1 & 0b0000_1111;

        int e2 = b2 & 0b0011_1111;

        int e3 = b3 & 0b0011_1111;

        return (e1 << 12) + (e2 << 6) + e3;

    }

    private int extractCodePoint(byte b1, byte b2, byte b3, byte b4) {

        int e1 = b1 & 0b0000_0111;

        int e2 = b2 & 0b0011_1111;

        int e3 = b3 & 0b0011_1111;

        int e4 = b4 & 0b0011_1111;

        return (e1 << 18) + (e2 << 12) + (e3 << 6) + e4;

    }

    private boolean isContinuation(byte b) {

        return isBitSet(b , 7)
               &&
               isBitNoSet(b, 6);

    }

}