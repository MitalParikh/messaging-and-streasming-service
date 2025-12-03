package com.example.queues.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubsetArray {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4};
        List<int[]> subsetArrList = new ArrayList<>();
        // List<int[]> interimSubsetArrList = new ArrayList<>();
        for(int i=0; i < arr.length; i++) {
            System.out.println("i: " +  i);
                recurse(0, Arrays.copyOfRange(arr, 0, arr.length-i), subsetArrList);
        }
        subsetArrList.forEach(elArr -> System.out.println("Subset array: " + Arrays.toString(elArr )));

        for(int i=1; i < arr.length-1; i++) {
            System.out.println("i: " +  i);
            for(int j=i; j < arr.length-1; j++) {
                int[] permutedArr = new int[j];
                System.arraycopy(arr, i, permutedArr, 0, j);
                recurseArr(Arrays.copyOfRange(arr, 0, i), j, Arrays.copyOfRange(arr, j, arr.length), subsetArrList);
        }
            // for(int j=i; j < arr.length-1; j++) {
            //     // int[] arrtoprocess = Stream.of(arr).flatMapToInt(Arrays::stream).filter(x -> x != Arrays.stream(Arrays.copyOfRange(arr, j, arr.length-i)).findFirst().orElse(-1)).toArray();
                
            //     // int[] arrToProcess = Arrays.copyOf(arr, i+1, )
            //     recurse(j, Arrays.copyOfRange(arr, j, arr.length-(j+1)), subsetArrList);
            //     // subsetArrList.addAll(interimSubsetArrList);
            // }
        }
        
    }

    private static void recurse(int remainingArrIndex, int[] subsetArr, List<int[]> resultArrList) {
           
        if(remainingArrIndex < subsetArr.length) {
            // resultArrList.forEach(elArr -> System.out.println("initial array: " + Arrays.toString(elArr)));
            resultArrList.add(Arrays.copyOfRange(subsetArr, remainingArrIndex, subsetArr.length));  
            resultArrList.forEach(elArr -> System.out.println("result array after addition: " + Arrays.toString(elArr)));
       // } else {   
            recurse(remainingArrIndex+1, subsetArr, resultArrList);
            // resultArrList.forEach(elArr -> System.out.println("result array: " + Arrays.toString(elArr)));
        }
    }

    private static void recurseArr(int[] firstSubset, int nextSubsetIndex, int[] nextSubset, List<int[]> resultArrList) {
        
        if(nextSubsetIndex < nextSubset.length) {
            int[] newSubset = new int[firstSubset.length + nextSubset.length - nextSubsetIndex];
            System.arraycopy(newSubset, 0, firstSubset, 0, firstSubset.length);
            System.arraycopy(nextSubset, firstSubset.length, nextSubset, nextSubsetIndex, (nextSubset.length));
            resultArrList.add(newSubset);
            resultArrList.forEach(elArr -> System.out.println("result array after addition: " + Arrays.toString(elArr)));
       // } else {   
            recurseArr(firstSubset, nextSubsetIndex+1, nextSubset, resultArrList);
            // resultArrList.forEach(elArr -> System.out.println("result array: " + Arrays.toString(elArr)));
        }
    }
}
