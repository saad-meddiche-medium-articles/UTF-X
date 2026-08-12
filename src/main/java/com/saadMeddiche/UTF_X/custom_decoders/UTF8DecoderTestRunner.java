package com.saadMeddiche.UTF_X.custom_decoders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class UTF8DecoderTestRunner {

    public static void main(String[] args) throws IOException {
        UTF8Decoder decoder = new UTF8Decoder();
        int passed = 0;
        int failed = 0;

        System.out.println("=== Starting UTF8Decoder Verification Tests ===\n");

        // Test 1: ASCII (1-byte characters)
        if (runTest("1-Byte (ASCII)", decoder, "Hello, World! 123", "Hello, World! 123")) passed++; else failed++;

        // Test 2: 2-Byte Characters (Latin / French / Spanish)
        if (runTest("2-Byte Characters", decoder, "Café, niño, über", "Café, niño, über")) passed++; else failed++;

        // Test 3: 3-Byte Characters (Arabic / CJK)
        if (runTest("3-Byte Characters", decoder, "مرحبا - 日本語 - 漢字", "مرحبا - 日本語 - 漢字")) passed++; else failed++;

        // Test 4: 4-Byte Characters (Emojis / Surrogates)
        if (runTest("4-Byte (Emojis)", decoder, "Java ☕ 😀 🚀 🔥", "Java ☕ 😀 🚀 🔥")) passed++; else failed++;

        // Test 5: Mixed Multibyte Content & Buffer Boundary Test
        String mixedText = "ASCII | 2-byte: éàç | 3-byte: ￦ € | 4-byte: 𐍈 🌍 ".repeat(200); // Exceeds 8KB buffer
        if (runTest("Buffer Boundary (>8KB Mixed)", decoder, mixedText, mixedText)) passed++; else failed++;

        // Test 6: Invalid Byte Sequence Handling
        byte[] invalidBytes = new byte[] { (byte) 0xC3, (byte) 0x28, 'A', 'B' }; // Bad 2nd byte in 2-byte sequence
        if (runRawByteTest("Invalid Continuation Byte", decoder, invalidBytes, "\uFFFDAB")) passed++; else failed++;

        // Test 7: Standalone / Orphan Continuation Byte (0x80 - 0xBF)
        // Flaw: Unmatched header check causes the byte to be silently dropped without emitting \uFFFD.
        byte[] orphanContinuation = new byte[] { 'A', (byte) 0x80, 'B' };
        if (runRawByteTest("Orphan Continuation Byte", decoder, orphanContinuation, "A\uFFFDB")) passed++; else failed++;

        // Test 8: Truncated Multibyte Sequence at EOF
        // Flaw: Breaking out of inner loop on EOF loses trailing bytes in buffer without emitting replacement chars.
        byte[] truncatedEof = new byte[] { 'A', (byte) 0xC3 }; // 0xC3 needs 1 more byte, but file ends
        if (runRawByteTest("Truncated Sequence at EOF", decoder, truncatedEof, "A\uFFFD")) passed++; else failed++;

        // Test 9: Overlong Encoding (Security Flaw)
        // Flaw: Decoder accepts non-minimal encodings like 0xC0 0xAF (overlong ASCII '/'), allowing security bypasses.
        byte[] overlongSlash = new byte[] { (byte) 0xC0, (byte) 0xAF };
        if (runRawByteTest("Overlong Encoding (0xC0 0xAF)", decoder, overlongSlash, "\uFFFD\uFFFD")) passed++; else failed++;

        // Test 10: Out-of-Range Codepoint (> U+10FFFF)
        // Flaw: 0xF5 0x80 0x80 0x80 produces U+140000, causing Character.toChars() to throw uncaught IllegalArgumentException.
        byte[] outOfRangeCodePoint = new byte[] { (byte) 0xF5, (byte) 0x80, (byte) 0x80, (byte) 0x80 };
        if (runRawByteTest("Out-of-Range (> U+10FFFF)", decoder, outOfRangeCodePoint, "\uFFFD\uFFFD\uFFFD\uFFFD")) passed++; else failed++;

        // Test 11: Invalid Lead Byte (0xFF / 0xF8+)
        // Flaw: 0xFF matches (headByte & 0xF0) == 0xF0 and attempts to decode a 4-byte sequence on invalid headers.
        byte[] invalidLeadByte = new byte[] { (byte) 0xFF, 'A' };
        if (runRawByteTest("Invalid Lead Byte (0xFF)", decoder, invalidLeadByte, "\uFFFDA")) passed++; else failed++;

        System.out.println("\n===============================================");
        System.out.printf("Test Summary: %d PASSED | %d FAILED%n", passed, failed);
        System.out.println("===============================================");
    }

    private static boolean runTest(String testName, UTF8Decoder decoder, String input, String expected) throws IOException {
        Path tempFile = Files.createTempFile("utf8_test_", ".txt");
        try {
            Files.writeString(tempFile, input, StandardCharsets.UTF_8);

            String result = decoder.readString(tempFile);
            boolean pass = expected.equals(result);

            printResult(testName, expected, result, pass);
            return pass;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static boolean runRawByteTest(String testName, UTF8Decoder decoder, byte[] rawBytes, String expected) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("utf8_raw_test_", ".bin");
            Files.write(tempFile, rawBytes);

            String result = decoder.readString(tempFile);
            boolean pass = expected.equals(result);

            printResult(testName, expected, result, pass);
            return pass;
        } catch (Exception e) {
            printResult(testName, expected, "CRASHED (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")", false);
            return false;
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {}
            }
        }
    }

    private static void printResult(String testName, String expected, String actual, boolean pass) {
        String status = pass ? "[PASS]" : "[FAIL]";
        System.out.printf("%-32s %s%n", testName + ":", status);
        if (!pass) {
            System.out.println("   Expected: " + expected);
            System.out.println("   Actual:   " + actual);
        }
    }

}