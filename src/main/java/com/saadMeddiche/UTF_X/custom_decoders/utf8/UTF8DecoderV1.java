package com.saadMeddiche.UTF_X.custom_decoders.utf8;

import com.saadMeddiche.UTF_X.custom_decoders.UTF8CustomDecoder;
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
public class UTF8DecoderV1 implements UTF8CustomDecoder {

    public static void main(String[] args) {

        UTF8DecoderV1 decoder = new UTF8DecoderV1();

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

                while (bf.hasRemaining()) {

                    int headByte = Byte.toUnsignedInt(bf.get());

                    // One byte
                    if(0x0 <= headByte && headByte <= 0x7F) {

                        int codePoint = extractCodePoint(headByte);

                        char[] characters = Character.toChars(codePoint);

                        stringBuilder.append(characters);

                        continue;

                    }

                    // Two bytes
                    if(0xC2 <= headByte && headByte <= 0xDF) {

                        if(bf.remaining() < 1) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        int b2 = Byte.toUnsignedInt(bf.get());

                        if(!isTail(b2)) {
                            stringBuilder.append('\uFFFD');
                            continue;
                        }

                        int codePoint = extractCodePoint(headByte, b2);

                        char[] characters = Character.toChars(codePoint);

                        stringBuilder.append(characters);

                        continue;

                    }

                    // Three bytes
                    if(0xE0 <= headByte && headByte <= 0xEF) {

                        if(bf.remaining() < 2) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        int b2 = Byte.toUnsignedInt(bf.get());

                        int b3 =  Byte.toUnsignedInt(bf.get());

                        boolean possibility_1 = headByte == 0xE0
                                                && (0xA0 <= b2 && b2 <= 0xBF)
                                                && isTail(b3);

                        boolean possibility_2 = (0xE1 <= headByte && headByte <= 0xEC)
                                                && isTail(b2)
                                                && isTail(b3);

                        boolean possibility_3 = headByte == 0xED
                                                && (0x80 <= b2 && b2 <= 0x9F)
                                                && isTail(b3);

                        boolean possibility_4 = (0xEE <= headByte && headByte <= 0xEF)
                                                && isTail(b2)
                                                && isTail(b3);

                        boolean isSequenceValid = possibility_1 || possibility_2 || possibility_3 || possibility_4;

                        if(!isSequenceValid) {
                            stringBuilder.append('\uFFFD');
                            continue;
                        }

                        int codePoint = extractCodePoint(headByte, b2, b3);

                        char[] characters = Character.toChars(codePoint);

                        stringBuilder.append(characters);

                        continue;

                    }

                    // Four bytes
                    if(0xF0 <= headByte && headByte <= 0xF4) {

                        if(bf.remaining() < 3) {
                            bf.position(bf.position() - 1);
                            break;
                        }

                        int b2 = Byte.toUnsignedInt(bf.get());

                        int b3 = Byte.toUnsignedInt(bf.get());

                        int b4 = Byte.toUnsignedInt(bf.get());

                        boolean possibility_1 = headByte == 0xF0
                                                && (0x90 <= b2 && b2 <= 0xBF)
                                                && isTail(b3)
                                                && isTail(b4);

                        boolean possibility_2 = (0xF1 <= headByte && headByte <= 0xF2)
                                                && isTail(b2)
                                                && isTail(b3)
                                                && isTail(b4);

                        boolean possibility_3 = headByte == 0xF4
                                                && (0x80 <= b2 && b2 <= 0x8F)
                                                && isTail(b3)
                                                && isTail(b4);

                        boolean isSequenceValid = possibility_1 || possibility_2 || possibility_3;

                        if(!isSequenceValid) {
                            stringBuilder.append('\uFFFD');
                            continue;
                        }

                        int codePoint = extractCodePoint(headByte, b2, b3, b4);

                        char[] characters = Character.toChars(codePoint);

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

    private int extractCodePoint(int b1) {
        return b1 & 0b0111_1111;
    }

    private int extractCodePoint(int b1, int b2) {

        int e1 = b1 & 0b0001_1111;

        int e2 = b2 & 0b0011_1111;

        return (e1 << 6) + e2;

    }

    private int extractCodePoint(int b1, int b2, int b3) {

        int e1 = b1 & 0b0000_1111;

        int e2 = b2 & 0b0011_1111;

        int e3 = b3 & 0b0011_1111;

        return (e1 << 12) + (e2 << 6) + e3;

    }

    private int extractCodePoint(int b1, int b2, int b3, int b4) {

        int e1 = b1 & 0b0000_0111;

        int e2 = b2 & 0b0011_1111;

        int e3 = b3 & 0b0011_1111;

        int e4 = b4 & 0b0011_1111;

        return (e1 << 18) + (e2 << 12) + (e3 << 6) + e4;

    }

    private boolean isTail(int b) {
        return 0x80 <= b && b <= 0xBF;
    }

}