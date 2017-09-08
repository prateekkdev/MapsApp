package com.akexorcist.library.googledirectionandplace;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void isFlagTest() throws Exception {
        // assertEquals(3, 2 + 2);

        // 100
        // 110
        // 1001
        int value = 13;

        // 0001
        // 0100

        // System.out.println("" + (value & 4));

        // assert ((value & 4) != 0);


//        String original = "Aman Kesarwani";
//        String encrypted = encrypt(original);
//        String decrypted = decrypt(encrypted);

        int original = 8989;
        System.out.println("original: " + original);

        int encrypted = encrypt(original);
        System.out.println("encryped: " + encrypted);

        int decrypted = decrypt(encrypted);
        System.out.println("decrypted: " + decrypted);

        assertEquals(original, decrypted);
    }

    short key = 255;

    int encrypt(int value) {
        return value ^ key;
    }

    int decrypt(int value) {
        return value ^ key;
    }

    String encrypt(String str) {
        StringBuilder builder = new StringBuilder("");
        for (char c : str.toCharArray()) {
            builder.append(c ^ key);
        }

        return builder.toString();
    }

    String decrypt(String str) {
        StringBuilder builder = new StringBuilder("");
        for (char c : str.toCharArray()) {
            builder.append(c ^ key);
        }

        return builder.toString();
    }


    // 6, 4
    // a = 6 = 110
    // b = 4 = 100
    // a ^ b = 010
    // a ^ b = 110

}