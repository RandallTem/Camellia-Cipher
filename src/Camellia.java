import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Camellia {

    final private int[][] SBOX1 =  {{112, 130, 44, 236, 179, 39, 192, 229, 228, 133, 87, 53, 234, 12, 174, 65},
                                    {35, 239, 107, 147, 69, 25, 165, 33, 237, 14, 79, 78, 29, 101, 146, 189},
                                    {134, 184, 175, 143, 124, 235, 31, 206, 62, 48, 220, 95, 94, 197, 11, 26},
                                    {166, 225, 57, 202, 213, 71, 93, 61, 217, 1, 90, 214, 81, 86, 108, 77},
                                    {139, 13, 154, 102, 251, 204, 176, 45, 116, 18, 43, 32, 240, 177, 132, 153},
                                    {223, 76, 203, 194, 52, 126, 118, 5, 109, 183, 169, 49, 209, 23, 4, 215},
                                    {20, 88, 58, 97, 222, 27, 17, 28, 50, 15,156, 22, 83, 24, 242, 34},
                                    {254, 68, 207, 178, 195, 181, 122, 145, 36, 8, 232, 168, 96, 252, 105, 80},
                                    {170, 208, 160, 125, 161, 137, 98, 151, 84, 91, 30, 149, 224, 255, 100, 210},
                                    {16, 196, 0, 72, 163, 247, 117, 219, 138, 3, 230, 218, 9, 63, 221, 148},
                                    {135, 92, 131, 2, 205, 74, 144, 51, 115, 103, 246, 243, 157, 127, 191, 226},
                                    {82, 155, 216, 38, 200, 55, 198, 59, 129, 150, 111, 75, 19, 190, 99, 46},
                                    {233, 121, 167, 140, 159, 110, 188, 142, 41, 245, 249, 182, 47, 253, 180, 89},
                                    {120, 152, 6, 106, 231, 70, 113, 186, 212, 37, 171, 66, 136, 162, 141, 250},
                                    {114, 7, 185, 85, 248, 238, 172, 10, 54, 73, 42, 104, 60, 56, 241, 164},
                                    {64, 40, 211, 123, 187, 201, 67, 193, 21, 227, 173, 244, 119, 199, 128, 158}};

    final private long[] SIGMA = {0xA09E667F3BCC908BL, 0xB67AE8584CAA73B2L, 0xC6EF372FE94F82BEL,
                                  0x54FF53A5F1D36F1CL, 0x10E527FADE682D1DL, 0xB05688C2B3E6C1FDL};

    private byte cycleShift(byte value,  int count){
        int temp = (value & 0xFF) >> (8 - count);
        return (byte)((value << count) + temp);
    }

    private int cycleShift(int value,  int count){
        int temp = (value) >>> (32 - count);
        int show = ((value << count) + temp);
        return ((value << count) + temp);
    }

    private long[] cycleShiftForPair(long[] values, int count) {
        long[] temp = new long[2];
        long[] new_values = new long[2];
        if (count <= 64) {
            temp[1] = (values[0] >>> (64-count));
            new_values[0] = (values[0] << count) + (values[1] >>> (64 - count));
            new_values[1] = (values[1] << count) + temp[1];
        } else {
            temp[0] = values[0] >>> 64-(count-64);
            temp[1] = (values[0] << (count-64)) + (values[1] >>> 64-(count-64));
            new_values[0] = (values[1] << 64-(128-count)) + temp[0];
            new_values[1] = temp[1];
        }
        return new_values;
    }

    private long FFunc(long data, long subkey){
        long x = data ^ subkey, result = 0;
        byte[] t = new byte[8];
        byte[] y = new byte[8];
        t[0] = (byte)(x >> 56);
        t[1] = (byte)(x >> 48);
        t[2] = (byte)(x >> 40);
        t[3] = (byte)(x >> 32);
        t[4] = (byte)(x >> 24);
        t[5] = (byte)(x >> 16);
        t[6] = (byte)(x >> 8);
        t[7] = (byte)x;
        t[0] = (byte)SBOX1[(t[0]>>4)&0x0F][t[0]&0x0F];
        t[1] = cycleShift((byte)SBOX1[(t[1]>>4)&0x0F][t[1]&0x0F], 1);
        t[2] = cycleShift((byte)SBOX1[(t[2]>>4)&0x0F][t[2]&0x0F], 7);
        t[3] = (byte)SBOX1[(cycleShift(t[3], 1)>>4)&0x0F][cycleShift(t[3], 1)&0x0F];
        t[4] = cycleShift((byte)SBOX1[(t[4]>>4)&0x0F][t[4]&0x0F], 1);
        t[5] = cycleShift((byte)SBOX1[(t[5]>>4)&0x0F][t[5]&0x0F], 7);
        t[6] = (byte)SBOX1[(cycleShift(t[6], 1)>>4)&0x0F][cycleShift(t[6], 1)&0x0F];
        t[7] = (byte)SBOX1[(t[7]>>4)&0x0F][t[7]&0x0F];
        y[0] = (byte)(t[0] ^ t[2] ^ t[3] ^ t[5] ^ t[6] ^ t[7]);
        y[1] = (byte)(t[0] ^ t[1] ^ t[3] ^ t[4] ^ t[6] ^ t[7]);
        y[2] = (byte)(t[0] ^ t[1] ^ t[2] ^ t[4] ^ t[5] ^ t[7]);
        y[3] = (byte)(t[1] ^ t[2] ^ t[3] ^ t[4] ^ t[5] ^ t[6]);
        y[4] = (byte)(t[0] ^ t[1] ^ t[5] ^ t[6] ^ t[7]);
        y[5] = (byte)(t[1] ^ t[2] ^ t[4] ^ t[6] ^ t[7]);
        y[6] = (byte)(t[2] ^ t[3] ^ t[4] ^ t[5] ^ t[7]);
        y[7] = (byte)(t[0] ^ t[3] ^ t[4] ^ t[5] ^ t[6]);
        for (int i = 0; i<8; i++){
            result <<= 8;
            result += y[i]&0xFF;
        }
        return result;
    }

    private long[] getKLKR(String string_key){
        long[] key = new long[4];
        byte[] byte_key = string_key.getBytes();
        int c = -1;
        for (int i = 0; i < (byte_key.length > 32 ? 32 : byte_key.length); i++){
            if (i%8 == 0) c++;
            key[c] <<= 8;
            key[c] += byte_key[i]&0xFF;
        }
        if (byte_key.length <= 24 && byte_key.length > 16)
            key[3] = ~key[2];
        return key;
    }

    private long[] getKLKRByte(byte[] byte_key){
        long[] key = new long[4];
        int c = -1;
        for (int i = 0; i < (byte_key.length > 32 ? 32 : byte_key.length); i++){
            if (i%8 == 0) c++;
            key[c] <<= 8;
            key[c] += byte_key[i]&0xFF;
        }
        if (byte_key.length <= 24 && byte_key.length > 16)
            key[3] = ~key[2];
        return key;
    }

    private long[] getKAKB(long[] KL_KR) {
        long[] KA_KB = new long[4];
        long D1, D2;
        D1 = KL_KR[0] ^ KL_KR[2];
        D2 = KL_KR[1] ^ KL_KR[3];
        D2 = D2 ^ FFunc(D1, SIGMA[0]);
        D1 = D1 ^ FFunc(D2, SIGMA[1]);
        D1 = D1 ^ KL_KR[0];
        D2 = D2 ^ KL_KR[1];
        D2 = D2 ^ FFunc(D1, SIGMA[2]);
        D1 = D1 ^ FFunc(D2, SIGMA[3]);
        KA_KB[0] = D1;
        KA_KB[1] = D2;
        D1 = KA_KB[0] ^ KL_KR[2];
        D2 = KA_KB[1] ^ KL_KR[3];
        D2 = D2 ^ FFunc(D1, SIGMA[4]);
        D1 = D1 ^ FFunc(D2, SIGMA[5]);
        KA_KB[2] = D1;
        KA_KB[3] = D2;
        return KA_KB;
    }

    private long[] getSubkeys128(long[] KL_KR, long[] KA_KB){
        long[] subkeys = new long[26];
        long[] KL = {KL_KR[0], KL_KR[1]}, KA = {KA_KB[0], KA_KB[1]};
        subkeys[0] = KL[0];
        subkeys[1] = KL[1];
        subkeys[2] = KA[0];
        subkeys[3] = KA[1];
        subkeys[4] = cycleShiftForPair(KL, 15)[0];
        subkeys[5] = cycleShiftForPair(KL, 15)[1];
        subkeys[6] = cycleShiftForPair(KA, 15)[0];
        subkeys[7] = cycleShiftForPair(KA, 15)[1];
        subkeys[8] = cycleShiftForPair(KA, 30)[0];
        subkeys[9] = cycleShiftForPair(KA, 30)[1];
        subkeys[10] = cycleShiftForPair(KL, 45)[0];
        subkeys[11] = cycleShiftForPair(KL, 45)[1];
        subkeys[12] = cycleShiftForPair(KA, 45)[0];
        subkeys[13] = cycleShiftForPair(KL, 60)[1];
        subkeys[14] = cycleShiftForPair(KA, 60)[0];
        subkeys[15] = cycleShiftForPair(KA, 60)[1];
        subkeys[16] = cycleShiftForPair(KL, 77)[0];
        subkeys[17] = cycleShiftForPair(KL, 77)[1];
        subkeys[18] = cycleShiftForPair(KL, 94)[0];
        subkeys[19] = cycleShiftForPair(KL, 94)[1];
        subkeys[20] = cycleShiftForPair(KA, 94)[0];
        subkeys[21] = cycleShiftForPair(KA, 94)[1];
        subkeys[22] = cycleShiftForPair(KL, 111)[0];
        subkeys[23] = cycleShiftForPair(KL, 111)[1];
        subkeys[24] = cycleShiftForPair(KA, 111)[0];
        subkeys[25] = cycleShiftForPair(KA, 111)[1];
        return subkeys;
    }

    private long[] getSubkeys192_256(long[] KL_KR, long[] KA_KB){
        long[] subkeys = new long[34];
        long[] KL = {KL_KR[0], KL_KR[1]}, KR = {KL_KR[2], KL_KR[3]}, KA = {KA_KB[0], KA_KB[1]}, KB = {KA_KB[2], KA_KB[3]};
        subkeys[0] = KL[0];
        subkeys[1] = KL[1];
        subkeys[2] = KB[0];
        subkeys[3] = KB[1];
        subkeys[4] = cycleShiftForPair(KR, 15)[0];
        subkeys[5] = cycleShiftForPair(KR, 15)[1];
        subkeys[6] = cycleShiftForPair(KA, 15)[0];
        subkeys[7] = cycleShiftForPair(KA, 15)[1];
        subkeys[8] = cycleShiftForPair(KR, 30)[0];
        subkeys[9] = cycleShiftForPair(KR, 30)[1];
        subkeys[10] = cycleShiftForPair(KB, 30)[0];
        subkeys[11] = cycleShiftForPair(KB, 30)[1];
        subkeys[12] = cycleShiftForPair(KL, 45)[0];
        subkeys[13] = cycleShiftForPair(KL, 45)[1];
        subkeys[14] = cycleShiftForPair(KA, 45)[0];
        subkeys[15] = cycleShiftForPair(KA, 45)[1];
        subkeys[16] = cycleShiftForPair(KL, 60)[0];
        subkeys[17] = cycleShiftForPair(KL, 60)[1];
        subkeys[18] = cycleShiftForPair(KR, 60)[0];
        subkeys[19] = cycleShiftForPair(KR, 60)[1];
        subkeys[20] = cycleShiftForPair(KB, 60)[0];
        subkeys[21] = cycleShiftForPair(KB, 60)[1];
        subkeys[22] = cycleShiftForPair(KL, 77)[0];
        subkeys[23] = cycleShiftForPair(KL, 77)[1];
        subkeys[24] = cycleShiftForPair(KA, 77)[0];
        subkeys[25] = cycleShiftForPair(KA, 77)[1];
        subkeys[26] = cycleShiftForPair(KR, 94)[0];
        subkeys[27] = cycleShiftForPair(KR, 94)[1];
        subkeys[28] = cycleShiftForPair(KA, 94)[0];
        subkeys[29] = cycleShiftForPair(KA, 94)[1];
        subkeys[30] = cycleShiftForPair(KL, 111)[0];
        subkeys[31] = cycleShiftForPair(KL, 111)[1];
        subkeys[32] = cycleShiftForPair(KB, 111)[0];
        subkeys[33] = cycleShiftForPair(KB, 111)[1];
        return subkeys;
    }

    private long[] transformKeys128(long[] subkeys){
        long[] new_subkeys = new long[subkeys.length];
        new_subkeys[0] = subkeys[24];
        new_subkeys[1] = subkeys[25];
        new_subkeys[24] = subkeys[0];
        new_subkeys[25] = subkeys[1];
        for (int i = 2; i <= 12; i++){
            new_subkeys[i] = subkeys[25-i];
            new_subkeys[25-i] = subkeys[i];
        }
        return new_subkeys;
    }

    private long[] transformKeys192_256(long[] subkeys){
        long[] new_subkeys = new long[subkeys.length];
        new_subkeys[0] = subkeys[32];
        new_subkeys[1] = subkeys[33];
        new_subkeys[32] = subkeys[0];
        new_subkeys[33] = subkeys[1];
        for (int i = 2; i <= 16; i++){
            new_subkeys[i] = subkeys[33-i];
            new_subkeys[33-i] = subkeys[i];
        }
        return new_subkeys;
    }

    protected long[] keySchedule(String string_key){
        long[] KL_KR = getKLKR(string_key);
        long[] KA_KB = getKAKB(KL_KR);
        long[] subkeys = (string_key.length() <= 16) ? getSubkeys128(KL_KR, KA_KB) : getSubkeys192_256(KL_KR, KA_KB);
        return subkeys;
    }

    protected long[] keySchedule(byte[] byte_key){
        long[] KL_KR = getKLKRByte(byte_key);
        long[] KA_KB = getKAKB(KL_KR);
        long[] subkeys = (byte_key.length <= 16) ? getSubkeys128(KL_KR, KA_KB) : getSubkeys192_256(KL_KR, KA_KB);
        return subkeys;
    }

    private long FLFunc(long data, long subkey){
        int x1, x2, k1, k2;
        x1 = (int)(data >>> 32);
        x2 = (int)(data & 0xFFFFFFFFL);
        k1 = (int)(subkey >>> 32);
        k2 = (int)(subkey & 0xFFFFFFFFL);
        x2 = x2 ^ (cycleShift((x1 & k1), 1));
        x1 = x1 ^ (x2 | k2);
        return ((long)x1 << 32) | (long)x2 & 0xFFFFFFFFL;
    }

    private long FLINVFunc(long data, long subkey){
        int y1, y2, k1, k2;
        y1 = (int)(data >>> 32);
        y2 = (int)(data & 0xFFFFFFFFL);
        k1 = (int)(subkey >>> 32);
        k2 = (int)(subkey & 0xFFFFFFFFL);
        y1 = y1 ^ (y2 | k2);
        y2 = y2 ^ (cycleShift((y1 & k1), 1));
        return ((long)y1 << 32) | (long)y2 & 0xFFFFFFFFL;
    }

    protected long[] crypt(long D1, long D2, long[] subkeys){
        int size = subkeys.length;
        D1 = D1 ^ subkeys[0];
        D2 = D2 ^ subkeys[1];
        for (int i = 2; i < size-2; i+=2){
            if (i%8 == 0){
                D1 = FLFunc(D1, subkeys[i]);
                D2 = FLINVFunc(D2, subkeys[i+1]);
            } else {
                D2 = D2 ^ FFunc(D1, subkeys[i]);
                D1 = D1 ^ FFunc(D2, subkeys[i+1]);
            }
        }
        D2 = D2 ^ subkeys[size-2];
        D1 = D1 ^ subkeys[size-1];
        long[] res = {D2, D1};
        return res;
    }

    private byte[][] longToByte(long D1, long D2){
        byte[][] bytes = new byte[2][8];
        for (int i = 7; i >=0 ; i--){
            bytes[0][i]= (byte)D1;
            bytes[1][i] = (byte)D2;
            D1 >>>= 8;
            D2 >>>= 8;
        }
        return bytes;
    }

    private int countExtraBytes(long[] res){
        int counter = 0, index = 1;
        long mask = 0xFFL, comp = 0x80L;
        for (int i = 0; i < 16; i++){
            if (i == 8) {
                mask = 0xFFL;
                comp = 0x80L;
                index = 0;
            }
            if ((res[index] & mask) == 0) {
                counter++;
                mask <<= 8;
                comp <<= 8;
            } else if ((res[index] & mask) == comp) {
                return ++counter;
            } else {
                return 0;
            }
        }
        return 0;
    }

    public void Encrypt(String path, String key){
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(path), 16);
            BufferedOutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(path+".crptd"));
            byte[] bytes = new byte[16];
            long D1 = 0, D2 = 0;
            int i;
            long[] subkeys = keySchedule(key);
            while ((i = (reader.read(bytes))) == 16) {
                D1 = ByteBuffer.wrap(Arrays.copyOfRange(bytes,0,8)).getLong();
                D2 = ByteBuffer.wrap(Arrays.copyOfRange(bytes,8,16)).getLong();
                long[] res = crypt(D1, D2, subkeys);
                byte[][] res_b = longToByte(res[0], res[1]);
                writer.write(res_b[0]);
                writer.write(res_b[1]);
                writer.flush();
            }
            if (i > 0 && i < 7){
                D1 = 0;
                D2 = 0;
                for (int j = 0; j < i; j++){
                    D1 <<= 8;
                    D1 += bytes[j];
                }
                D1 <<= 1;
                D1 += 1;
                D1 <<= 64-(i*8+1);
            } else if (i >= 8){
                D1 = ByteBuffer.wrap(Arrays.copyOfRange(bytes,0,8)).getLong();
                D2 = 0;
                for (int j = 8; j < i; j++){
                    D2 <<= 8;
                    D2 += bytes[j];
                }
                D2 <<= 1;
                D2 += 1;
                D2 <<= 64-((i-8)*8+1);
            }
            if (i != -1) {
                long[] res = crypt(D1, D2, subkeys);
                byte[][] res_b = longToByte(res[0], res[1]);
                writer.write(res_b[0]);
                writer.write(res_b[1]);
                writer.flush();
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            System.out.println("Error occurred");
            System.out.println(e);
            return;
        }
    }

    public void Decrypt(String path, String key){
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(path), 16);
            BufferedOutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(path.substring(0, path.lastIndexOf("."))));
            byte[] bytes = new byte[16];
            long D1 = 0, D2 = 0;
            int i;
            long[] subkeys = keySchedule(key);
            subkeys = subkeys.length == 26 ? transformKeys128(subkeys) : transformKeys192_256(subkeys);
            while ((i = (reader.read(bytes))) == 16) {
                D1 = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, 8)).getLong();
                D2 = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 8, 16)).getLong();
                long[] res = crypt(D1, D2, subkeys);
                byte[][] res_b = longToByte(res[0], res[1]);
                if (reader.available() == 0) {
                    int counter = countExtraBytes(res);
                    if (counter <= 8) {
                        writer.write(res_b[0]);
                        writer.write(res_b[1], 0, 8-counter);
                    } else {
                        writer.write(res_b[0], 0, 16-counter);
                    }
                    writer.flush();
                } else {
                    writer.write(res_b[0]);
                    writer.write(res_b[1]);
                    writer.flush();
                }
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            System.out.println("Error occurred");
            System.out.println(e);
            return;
        }
    }
}
