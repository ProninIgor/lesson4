package ru.vtb.learning.exercise1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Program {

    static final int size = 100000000;

    public static void main(String[] args) {

        float[] arr = createArray();

        method1(arr);
        arr = createArray();

        method2(arr, 1);
        arr = createArray();
        method2(arr, 4);
        arr = createArray();
        method2(arr, 8);
        arr = createArray();
        method2(arr, 12);
        arr = createArray();
        method2(arr, 16);
        arr = createArray();
        method2(arr, 50);
        arr = createArray();
        method2(arr, 100);
        arr = createArray();
        method2(arr, 200);

        /*
Time for method1: 10359
Time for 1 thread: 10357
Time for 4 thread: 2762
Time for 8 thread: 1644
Time for 12 thread: 1211 <- количество логических процессоров
Time for 16 thread: 1261
Time for 50 thread: 1411 <- странный скачок, который происходит в виде некой погрешности. При проверке с шагом 1, всё ок
Time for 100 thread: 1236
Time for 200 thread: 1238
        * */
    }

    private static void method1(float[] arr) {
        long time = System.currentTimeMillis();

        for (int i = 0; i < size; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }

        System.out.println("Time for method1: " + (System.currentTimeMillis() - time));
    }

    private static void method2(float[] arr, int threadCount) {
        long time = System.currentTimeMillis();

        int[] parts = getParts(size, threadCount);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < parts.length - 1; i++) {
            Integer index = i;

            Integer start = parts[index]; // почему не могу писать parts[i]?
            Integer finish = parts[index + 1];

            executorService.execute(
                    () -> {
                        for (int j = start; j <= finish; j++)
                            arr[j] = (float) (arr[j] * Math.sin(0.2f + j / 5) * Math.cos(0.2f + j / 5) * Math.cos(0.4f + j / 2));
                    });

        }

        executorService.shutdown();

        // как ещё можно подождать выполнения всех задач?
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Time for " + threadCount + " thread: " + (System.currentTimeMillis() - time));
    }

    private static int[] getParts(int size, int partCount) {

        int[] result = new int[partCount + 1];
        result[0] = 0;

        for (int i = 1; i <= partCount; i++) {
            result[i] = i * (size / partCount) - 1;
        }

        return result;
    }

    private static float[] createArray() {
        float[] arr = new float[size];
        for (int i = 0; i < size; i++) {
            arr[i] = 1;
        }
        return arr;
    }
}
