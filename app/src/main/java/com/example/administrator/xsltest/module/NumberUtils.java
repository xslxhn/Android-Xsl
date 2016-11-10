package com.example.administrator.xsltest.module;

import java.text.SimpleDateFormat;

public class NumberUtils {
    private static String[] binaryArray = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
    private static String hexStr = "0123456789ABCDEF";

    public static byte[] binaryStr2Bytes(String paramString) {
        String[] arrayOfString = paramString.split(",");
        byte[] arrayOfByte = new byte[arrayOfString.length];
        for (int i = 0; ; i++) {
            if (i >= arrayOfByte.length)
                return arrayOfByte;
            arrayOfByte[i] = ((byte) parse(arrayOfString[i]));
        }
    }

//    public static String binaryString2hexString(String paramString)
//    {
//      if ((paramString == null) || (paramString.equals("")) || (paramString.length() % 8 != 0))
//        return null;
//      StringBuffer localStringBuffer = new StringBuffer();
//      int i = 0;
//      if (i >= paramString.length())
//        return localStringBuffer.toString();
//      int j = 0;
//      for (int k = 0; ; k++)
//      {
//        if (k >= 4)
//        {
//          localStringBuffer.append(Integer.toHexString(j).toUpperCase());
//          i += 4;
//          break;
//        }
//        j += (Integer.parseInt(paramString.substring(i + k, 1 + (i + k))) << -1 + (4 - k));
//      }
//    }

    public static String binaryToHexString(byte[] paramArrayOfByte) {
        String str1 = "";
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length)
                return str1;
            String str2 = String.valueOf(hexStr.charAt((0xF0 & paramArrayOfByte[i]) >> 4)) + String.valueOf(hexStr.charAt(0xF & paramArrayOfByte[i]));
            str1 = str1 + str2 + " ";
        }
    }

//    public static byte[] byteArrayReverse(byte[] paramArrayOfByte)
//    {
//      for (int i = 0; ; i++)
//      {
//        if (i >= paramArrayOfByte.length / 2)
//          return paramArrayOfByte;
//        int j = paramArrayOfByte[i];
//        paramArrayOfByte[i] = paramArrayOfByte[(-1 + paramArrayOfByte.length - i)];
//        paramArrayOfByte[(-1 + paramArrayOfByte.length - i)] = j;
//      }
//    }

    public static int byteReverseToInt(byte[] paramArrayOfByte) {
        int i = 0;
        for (int j = -1 + paramArrayOfByte.length; ; j--) {
            if (j <= -1)
                return i;
            i = i << 8 | 0xFF & paramArrayOfByte[j];
        }
    }

    public static int byteToInt(byte[] paramArrayOfByte) {
        int i = 0;
        for (int j = 0; ; j++) {
            if (j >= paramArrayOfByte.length)
                return i;
            i = i << 8 | 0xFF & paramArrayOfByte[j];
        }
    }

    public static String bytes2BinaryStr(byte[] paramArrayOfByte) {
        String str1 = "";
        int i = paramArrayOfByte.length;
        for (int j = 0; ; j++) {
            if (j >= i)
                return str1;
            int k = paramArrayOfByte[j];
            int m = (k & 0xF0) >> 4;
            String str2 = str1 + binaryArray[m];
            int n = k & 0xF;
            str1 = str2 + binaryArray[n];
        }
    }

    public static String bytes2HexString(byte[] paramArrayOfByte) {
        String str1 = "";
        if (paramArrayOfByte == null)
            return str1;
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length)
                return str1;
            String str2 = Integer.toHexString(0xFF & paramArrayOfByte[i]);
            if (str2.length() == 1)
                str2 = '0' + str2;
            str1 = str1 + str2.toUpperCase();
        }
    }

    public static int bytes2int(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            temp = b[b.length - i - 1] & mask;
            if (i > 0) {
                temp = temp << i * 8;
            }
            res += temp;
        }
        return res;
    }

    public static String[] bytes2HexStringComma(byte[] paramArrayOfByte) {
        String str1[] = null;
        String str = "";
        if (paramArrayOfByte != null) {
            for (int i = 0; i < paramArrayOfByte.length; i++) {
                String str2 = Integer.toHexString(0xFF & paramArrayOfByte[i]);
                if (str2.length() == 1) {
                    str2 = '0' + str2;
                }
                if (i != paramArrayOfByte.length) {
                    str2 = str2 + ",";
                }
                str = str + str2.toUpperCase();
            }
            str1 = str.split(",");
        }
        return str1;
    }

//    public static String hexString2binaryString(String paramString)
//    {
//      String str1;
//      if ((paramString == null) || (paramString.length() % 2 != 0))
//        str1 = null;
//      while (true)
//      {
//        return str1;
//        str1 = "";
//        for (int i = 0; i < paramString.length(); i++)
//        {
//          String str2 = "0000" + Integer.toBinaryString(Integer.parseInt(paramString.substring(i, i + 1), 16));
//          str1 = str1 + str2.substring(-4 + str2.length());
//        }
//      }
//    }

    public static byte[] hexStringToBinary(String paramString) {
        int i = paramString.length() / 2;
        byte[] arrayOfByte = new byte[i];
        for (int j = 0; ; j++) {
            if (j >= i)
                return arrayOfByte;
            arrayOfByte[j] = ((byte) ((byte) (hexStr.indexOf(paramString.charAt(j * 2)) << 4) | (byte) hexStr.indexOf(paramString.charAt(1 + j * 2))));
        }
    }

    public static byte[] intToByteArray(int paramInt) {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = ((byte) (0xFF & paramInt >> 24));
        arrayOfByte[1] = ((byte) (0xFF & paramInt >> 16));
        arrayOfByte[2] = ((byte) (0xFF & paramInt >> 8));
        arrayOfByte[3] = ((byte) (paramInt & 0xFF));
        return arrayOfByte;
    }

    public static int parse(String paramString) {
        if (32 == paramString.length())
            return -(1 + (2147483647 + Integer.parseInt("-" + paramString.substring(1), 2)));
        return Integer.parseInt(paramString, 2);
    }

    public static String timeStamp2format(long paramLong) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(1000L * paramLong));
    }
}
