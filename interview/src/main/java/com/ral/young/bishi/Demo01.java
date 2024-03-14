package com.ral.young.bishi;

/**
 * 给两个各超过50位的数字字符串，需要将两个字符串进行相加输出新的数字字符串，要求：
 * 传入的两个字符串都不能转为整型或长整型直接相加。
 *
 * @author renyunhui
 * @date 2024-03-11 15:18
 * @since 1.0.0
 */
public class Demo01 {

    public static void main(String[] args) {
        System.out.println(doAdd("222", "435888"));
    }

    public static long doAdd(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();
        int i = m - 1;
        int j = n - 1;
        int carry = 0;
        StringBuilder builder = new StringBuilder();
        while (i >= 0 || j >= 0) {
            int one = i < 0 ? 0 : str1.charAt(i) - '0';
            int two = j < 0 ? 0 : str2.charAt(j) - '0';
            int sum = one + two;
            int tmp = sum + carry;
            builder.append(tmp % 10);
            carry = tmp / 10;
            i--;
            j--;
        }
        return carry > 0 ? Long.parseLong(builder.append(carry).reverse().toString()) : Long.parseLong(builder.reverse().toString());
    }
}
