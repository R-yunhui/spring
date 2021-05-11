package com.ral.admin.webflux.controller;

import java.util.Arrays;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-12 8:43
 * @Describe: leetcode 每日一题  1310. 子数组异或查询
 * 有一个正整数数组arr，现给你一个对应的查询数组queries，其中queries[i] = [Li,Ri]。
 * 对于每个查询i，请你计算从Li到Ri的XOR值（即arr[Li] xor arr[Li+1] xor ... xor arr[Ri]）作为本次查询的结果。
 * 并返回一个包含给定查询queries所有结果的数组。
 * @Modify:
 */
public class Day12 {

    public static void main(String[] args) {
        int[] arr = new int[]{1, 3, 4, 8};
        int[][] queries = new int[][]{{0, 1}, {1, 2}, {0, 3}, {3, 3}};
        System.err.println(Arrays.toString(new Day12().xorQueries(arr, queries)));
    }

    public int[] xorQueries(int[] arr, int[][] queries) {
        int[] result = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int[] temp = queries[i];
            for (int j = temp[0]; j <= temp[1]; j++) {
                result[i] ^= arr[j];
            }
        }
        return result;
    }

    public int[] xorQueriesTwo(int[] arr, int[][] queries) {
        int n = arr.length;
        int[] xors = new int[n + 1];
        for (int i = 0; i < n; i++) {
            xors[i + 1] = xors[i] ^ arr[i];
        }
        int m = queries.length;
        int[] ans = new int[m];
        for (int i = 0; i < m; i++) {
            ans[i] = xors[queries[i][0]] ^ xors[queries[i][1] + 1];
        }
        return ans;
    }
}
