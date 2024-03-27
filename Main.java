import java.io.PrintWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class Main{

    public static boolean isPrime(long number){
        int i;
        double s;
        boolean prime = true;
        if(number%2 == 0 && number != 2){
            return false;
        }

        if(prime){
            s = Math.sqrt(number);
            for (i = 3; prime && i <= s; i +=2){
                if(number % i == 0){
                    return false;
                }
            }
        }
        return prime;
    }

    public static void main(String args[]){
        try {
            List<String> lines = Files.readAllLines(Paths.get("entrada.txt"));
            List<Long> numbers = new ArrayList<>();
            for (String line : lines) {
                numbers.add(Long.parseLong(line.trim()));
            }
            List<Long> primeNumbers = new ArrayList<>();

            long startTime = System.nanoTime();
            runThreads(1, numbers, primeNumbers);
            PrintWriter writer = new PrintWriter(new File("numeros_primos_1.txt"));
            for (Long prime : primeNumbers) {
                writer.println(prime);
            }
            long endTime = System.nanoTime();
            System.out.println("Tempo com 1 thread: " + (endTime - startTime) + " ns"+ " Numero de primos: " + primeNumbers.size());

            startTime = System.nanoTime();
            runThreads(5, numbers, primeNumbers);
            writer = new PrintWriter(new File("numeros_primos_5.txt"));
            for (Long prime : primeNumbers) {
                writer.println(prime);
            }
            endTime = System.nanoTime();
            System.out.println("Tempo com 5 threads: " + (endTime - startTime) + " ns"+ " Numero de primos: " + primeNumbers.size());

            startTime = System.nanoTime();
            runThreads(10, numbers, primeNumbers);
            writer = new PrintWriter(new File("numeros_primos_10.txt"));
            for (Long prime : primeNumbers) {
                writer.println(prime);
            }
            endTime = System.nanoTime();
            System.out.println("Tempo com 10 threads: " + (endTime - startTime) + " ns"+ " Numero de primos: " + primeNumbers.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void runThreads(int numberOfThreads, List<Long> numbers, List<Long> primeNumbers) {
        List<Thread> threads = new ArrayList<>();
        int size = numbers.size();
        int chunkSize = size / numberOfThreads;
        primeNumbers.clear(); // Limpa a lista de primos para a nova execução

        for (int i = 0; i < numberOfThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numberOfThreads - 1) ? size : (start + chunkSize);
            Thread thread = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    long number = numbers.get(j);
                    if (isPrime(number)) {
                        synchronized (primeNumbers) {
                            primeNumbers.add(number);
                        }
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
