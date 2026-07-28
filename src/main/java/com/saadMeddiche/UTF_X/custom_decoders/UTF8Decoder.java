package com.saadMeddiche.UTF_X.custom_decoders;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class UTF8Decoder  {

    public String readString(Path filePath) {

        try (SeekableByteChannel ch = Files.newByteChannel(filePath, StandardOpenOption.READ)) {

            ByteBuffer bf = ByteBuffer.allocate(1_024); // 1Kio

            while(ch.read(bf) > 0) {

                bf.flip();

                byte b = bf.get();

                bf.compact();

            }

            return "";


        } catch (IOException e) {
            return "FAILED TO DECODE";
        }

    }

}