package org.hooli;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.in;
import static java.lang.System.out;

public class Master implements MasterInterface {
    ArrayList<WorkerInterface> workers;
    private Random random = new Random();

    public Master() {
        workers = new ArrayList<>();
    }

    /**
     * Registers a worker
     * @param worker
     * @return true if register was successful
     * @throws RemoteException
     */
    @Override
    public boolean register(WorkerInterface worker) throws RemoteException {
        if (worker != null) {
            this.workers.add(worker);
            System.out.println(this.workers.size() + "/3 workers registered.");
            return true;
        }
        return false;
    }

    /**
     * Returns N random workers. If N >= # workers, returns a shallow copy of the actual array of workers
     * @param n quantity of workers
     * @return N random workers
     */
    @SuppressWarnings("unchecked")
    private List<WorkerInterface> getWorkers(int n){
        System.out.printf("Choosing %d workers of %d available\n", n, this.workers.size());
        if(n >= this.workers.size()){
            return (List<WorkerInterface>) this.workers.clone();
        }
        IntStream indexes = random.ints(0, this.workers.size() - 1);
        return indexes.mapToObj(i -> this.workers.get(i)).collect(Collectors.toList());
    }

    private List<WorkerInterface> getWorkers(){
        return getWorkers(3);
    }

    private void removeWorker(WorkerInterface worker){
        this.workers.remove(worker);
    }


    private static Master master;

    /**
     * Creates a random vector of size n
     * @param n
     * @return int array of size n
     */
    private static int[] randomVector(int n) {
        int[] vector = new int[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            vector[i] = random.nextInt();
        }
        return vector;
    }

    private static int compute(int n) throws IllegalStateException {
        System.out.println("Preparing to send data...");
        int[] v1 = randomVector(n);
        int[] v2 = randomVector(n);
        Integer finalResult = null;

        while (finalResult == null) {
            HashMap<Integer, Integer> results = new HashMap<>();
            for (WorkerInterface worker : master.getWorkers()) {
                try{
                    int result = worker.dotProduct(v1, v2);
                    System.out.println("A worker returned " + result);
                    Integer count = results.get(result);
                    results.put(result, count != null ? count + 1 : 1);
                }
                catch(RemoteException e){
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

    private static void userLoop(){
        Scanner in = new Scanner(System.in);
        while(true){
            try{
                System.out.println("Enter a number:");
                int n = in.nextInt();
                int result = compute(n);
                System.out.printf("Result: %d\n", result);
            }
            catch(Exception e){
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
