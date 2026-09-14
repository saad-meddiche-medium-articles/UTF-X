package com.saadMeddiche.UTF_X.custom_decoders;

import java.nio.file.Path;

public interface UTF8CustomDecoder {

    String readString(Path filePath);

}