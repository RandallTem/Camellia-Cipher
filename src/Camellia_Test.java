public class Camellia_Test {

    public void EncryptFile(String name, String key){
        long time = System.nanoTime();
        Camellia camellia = new Camellia();
        camellia.Encrypt(name, key);
        System.out.println("Encryption done for "
                +String.format("%.3f", ((System.nanoTime() - time) * 0.000000001))+" seconds");
    }

    public void DecryptFile(String name, String key){
        long time = System.nanoTime();
        Camellia camellia = new Camellia();
        camellia.Decrypt(name, key);
        System.out.println("Decryption done for "
                +String.format("%.3f", ((System.nanoTime() - time) * 0.000000001))+" seconds");
    }

    public void checkValuesTest(){
        Camellia camellia = new Camellia();
        long D1, D2;
        long[] res;
        byte[] byte_key128 = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, 0x76, 0x54, 0x32, 0x10};
        long[] subkeys = camellia.keySchedule(byte_key128);
        D1 = 0x0123456789abcdefL;
        D2 = 0xfedcba9876543210L;
        res = camellia.crypt(D1, D2, subkeys);
        System.out.println("Key 128 bits: 0123456789abcdeffedcba9876543210");
        System.out.println("Message: 0123456789abcdeffedcba9876543210");
        System.out.println("Result: "+Long.toHexString(res[0])+Long.toHexString(res[1]));
        byte[] byte_key192 = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, 0x76, 0x54, 0x32, 0x10,
                0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
        subkeys = camellia.keySchedule(byte_key192);
        D1 = 0x0123456789abcdefL;
        D2 = 0xfedcba9876543210L;
        res = camellia.crypt(D1, D2, subkeys);
        System.out.println("\nKey 192 bits: 0123456789abcdeffedcba98765432100011223344556677");
        System.out.println("Message: 0123456789abcdeffedcba9876543210");
        System.out.println("Result: "+Long.toHexString(res[0])+Long.toHexString(res[1]));
        byte[] byte_key256 = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, 0x76, 0x54, 0x32, 0x10,
                0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77,
                (byte)0x88, (byte)0x99, (byte)0xaa, (byte)0xbb, (byte)0xcc, (byte)0xdd, (byte)0xee, (byte)0xff};
        subkeys = camellia.keySchedule(byte_key256);
        D1 = 0x0123456789abcdefL;
        D2 = 0xfedcba9876543210L;
        res = camellia.crypt(D1, D2, subkeys);
        System.out.println("\nKey 256 bit: 0123456789abcdeffedcba987654321000112233445566778899aabbccddeeff");
        System.out.println("Message: 0123456789abcdeffedcba9876543210");
        System.out.println("Result: "+Long.toHexString(res[0])+Long.toHexString(res[1]));

    }

    public static void main(String[] args){
        Camellia_Test test = new Camellia_Test();
        test.checkValuesTest();
        System.out.println("\n");
        test.EncryptFile("resources/File_5MB.txt", "1234567887654321");
        test.DecryptFile("resources/File_5MB.txt.crptd", "1234567887654321");
    }
}
