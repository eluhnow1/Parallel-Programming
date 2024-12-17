import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelArrayInitialization {
    private static final int ARRAY_SIZE = 200000;
    private static int[] array = new int[ARRAY_SIZE];
    
    static class ArrayInitializer implements Runnable {
        private final int startIndex;
        private final int endIndex;
        
        public ArrayInitializer(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
        
        @Override
        public void run() {
            for (int i = startIndex; i < endIndex; i++) {
                array[i] = i;
            }
        }
    }
    
    public static void initializeArray(int numThreads) throws InterruptedException {
        // Calculate the size of each chunk
        int chunkSize = ARRAY_SIZE / numThreads;
        
        // Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        // Submit tasks
        for (int i = 0; i < numThreads; i++) {
            int startIndex = i * chunkSize;
            int endIndex = (i == numThreads - 1) ? ARRAY_SIZE : (i + 1) * chunkSize;
            executor.submit(new ArrayInitializer(startIndex, endIndex));
        }
        
        // Shutdown executor and wait for all tasks to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
    
    public static void main(String[] args) {
        // Test with different numbers of threads
        int[] threadCounts = {1, 2, 4, 8};
        
        for (int numThreads : threadCounts) {
            try {
                // Reset array
                array = new int[ARRAY_SIZE];
                
                // Start timer
                long startTime = System.nanoTime();
                
                // Initialize array
                initializeArray(numThreads);
                
                // End timer
                long endTime = System.nanoTime();
                
                // Calculate duration
                long duration = endTime - startTime;
                
                // Verify array (optional)
                boolean correct = verifyArray();
                
                // Print results
                System.out.printf("Number of threads: %d%n", numThreads);
                System.out.printf("Time taken: %d nanoseconds%n", duration);
                System.out.printf("Array verification: %s%n%n", correct ? "PASSED" : "FAILED");
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static boolean verifyArray() {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            if (array[i] != i) {
                return false;
            }
        }
        return true;
    }
}