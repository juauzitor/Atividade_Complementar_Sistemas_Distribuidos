import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class PrimeFinder implements Runnable {
    private final int number;
    private static final List<Integer> primes = Collections.synchronizedList(new ArrayList<>());

    public PrimeFinder(int number) {
        this.number = number;
    }

    @Override
    public void run() {
        if (isPrime(this.number)) {
            primes.add(this.number);
        }
    }

    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;

        if (n % 2 == 0 || n % 3 == 0) return false;

        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public static void saveListToFile(List<Integer> primes, String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (Integer prime : primes) {
                writer.write(prime + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("entrada.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                executor.execute(new PrimeFinder(Integer.parseInt(line)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
        }

        saveListToFile(primes, "lista.txt");
        System.out.println("Total de números primos encontrados: " + primes.size());
    }
}
