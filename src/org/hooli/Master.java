package org.hooli;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Master implements MasterInterface {
    private final int WORKERS_PER_REQUEST = 3;

    private final ArrayList<WorkerInterface> workers;
    private static Random random = new Random();

    public Master() {
        workers = new ArrayList<>();
    }

    /**
     * Registers a worker
     *
     * @param worker
     * @return true if register was successful
     * @throws RemoteException
     */
    @Override
    public boolean register(WorkerInterface worker) throws RemoteException {
        if (worker != null) {
            this.workers.add(worker);
            System.out.printf("%d/%d workers registered.\n",
                    this.workers.size(), WORKERS_PER_REQUEST);
            synchronized (this){
                if (this.workers.size() >= WORKERS_PER_REQUEST) {
                    this.notifyAll();
                }
            }
            return true;
        }
        return false;
    }

    private boolean enoughWorkers() {
        return this.workers.size() >= WORKERS_PER_REQUEST;
    }


    /**
     * Returns @WORKERS_PER_REQUEST random workers.
     *
     * @return @WORKERS_PER_REQUEST random workers
     * @throws IllegalStateException
     */
    @SuppressWarnings("unchecked")
    private List<WorkerInterface> getWorkers() throws IllegalStateException {
        System.out.printf("Choosing %d workers of %d available\n",
                WORKERS_PER_REQUEST, this.workers.size());
        if (!enoughWorkers()) {
            throw new IllegalStateException("Not enough workers");
        }

        IntStream indexes = random.ints(WORKERS_PER_REQUEST, 0, this.workers.size() - 1);
        return indexes.mapToObj(this.workers::get).collect(Collectors.toList());
    }

    private void removeWorker(WorkerInterface worker) {
        this.workers.remove(worker);
    }

    private int compute(int n) throws IllegalStateException {
        int[] v1 = randomVector(n);
        int[] v2 = randomVector(n);
        Integer finalResult = null;

        while (finalResult == null) {
            HashMap<Integer, Integer> results = new HashMap<>();
            for (WorkerInterface worker : getWorkers()) {
                try {
                    int result = worker.dotProduct(v1, v2);
                    Integer count = results.get(result);
                    results.put(result, count != null ? count + 1 : 1);
                } catch (RemoteException e) {
                    master.removeWorker(worker);
                    throw new IllegalStateException("One of the selected workers has died");
                }
            }

            // Get most frequent result
            Map.Entry<Integer, Integer> mostFrequent = Collections.max(results.entrySet(),
                    (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

            // Only accept if 2 or more agree
            if (mostFrequent.getValue() > 1) {
                finalResult = mostFrequent.getKey();
            }
        }

        return finalResult;
    }

    private static Master master;

    /**
     * Creates a random vector of size n
     *
     * @param n
     * @return int array of size n
     */
    private static int[] randomVector(int n) {
        int[] vector = new int[n];
        for (int i = 0; i < n; i++) {
            vector[i] = random.nextInt();
        }
        return vector;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static void userLoop() {
        Scanner in = new Scanner(System.in);
        while (true) {
            try {
                synchronized (master){
                    if (!master.enoughWorkers()) {
                        master.wait();
                    }
                }
                System.out.println("Please, enter a number:");
                int n = in.nextInt();
                int result = master.compute(n);
                System.out.printf("Result: %d\n", result);
            } catch (Exception e) {
                System.err.println("Server exception: " + e);
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        try {
            master = new Master();
            MasterInterface stub = (MasterInterface) UnicastRemoteObject.exportObject(master, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Master", stub);

            System.out.println("Server ready.");
            System.out.println("Waiting for workers...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        userLoop();
    }
}
