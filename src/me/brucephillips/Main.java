package me.brucephillips;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Main {

    private static int MAP_SIZE = 104857600;

    public static void main(String[] args) {
	// write your code here
    }

    private String findFast(Path path, String text) throws IOException {

        if (path == null || text == null) {

            throw new IllegalArgumentException("Path/text cannot be null");

        }

        if (text.isBlank()) {

            return "";

        }

        final byte[] texttofind = text.getBytes(StandardCharsets.UTF_8);

        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            int position = 0;

            long length = fileChannel.size();

            while (position < length) {

                long remaining = length - position;

                int bytestomap = (int) Math.min(MAP_SIZE, remaining);

                MappedByteBuffer mbBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, position, bytestomap);

                int limit = mbBuffer.limit();

                int lastSpace = -1;

                int firstChar = -1;

                while (mbBuffer.hasRemaining()) {

                    boolean isFirstChar = false;

                    while (firstChar != 0 && mbBuffer.hasRemaining()) {

                        byte currentByte = mbBuffer.get();

                        if (Character.isWhitespace((char) currentByte)) {
                            lastSpace = mbBuffer.position();
                        }

                        if (texttofind[0] == currentByte) {
                            isFirstChar = true;
                            break;
                        }

                    }

                    if (isFirstChar) {
                        boolean isRestOfChars = true;

                        int j;

                        for (j = 1; j < texttofind.length; j++) {

                            if (!mbBuffer.hasRemaining() || texttofind[j] != mbBuffer.get()) {
                                isRestOfChars = false;
                                break;
                            }

                        }

                        if (isRestOfChars) {
                            return "Found " + text;
                        }

                        firstChar = -1;

                    }

                }

                if (lastSpace > -1) {

                    position = position - (limit - lastSpace);

                }

                position += bytestomap;

            }

        }

        return "Did not find " + text;

    }
}
