package org.hooli;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static java.lang.System.in;

public class Master implements MasterInterface {
    ArrayList<WorkerInterface> workers;
    public Master() {
        workers = new ArrayList<>();
    }

    // Registers a worker
    @Override
    public boolean register(WorkerInterface worker) throws RemoteException {
        if (worker != null) {
            workers.add(worker);
            System.out.println(workers.size() + "/3 workers registered.");
            return true;
        }
        return false;
    }

    // Creates a random vector of size n
    private static int[] randomVector(int n) {
        int[] vector = new int[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            vector[i] = random.nextInt();
        }
        return vector;
    }

    public static void main(String args[]) {
        // TODO: Should it be only once?
        // TODO: Block the user input if a worker dies and there are not three workers

        try {
            Master obj = new Master();
            MasterInterface stub = (MasterInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Master", stub);
            System.out.println("Server ready.");
            System.out.println("Waiting for workers...");

            // TODO: Wait for three workers
            // System.out.println("At least 3 workers must be connected.");
            System.out.println("Enter a number:");
            int n = System.in.read();

            int[] v1 = randomVector(n);
            int[] v2 = randomVector(n);
            Integer finalResult = null;

            // Repeat until workers agree
            while (finalResult == null) {
                HashMap<Integer, Integer> results = new HashMap<>();
                // TODO: Should select only three workers
                for (WorkerInterface worker : obj.workers) {
                    int result = worker.dotProduct(v1, v2);
                    Integer count = results.get(result);
                    results.put(result, count != null ? count + 1 : 1);
                }

                // Get most frequent result
                Map.Entry<Integer, Integer> mostFrequent = Collections.max(results.entrySet(),
                        (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

                // Only accept if 2 or more agree
                if (mostFrequent.getValue() > 1) {
                    finalResult = mostFrequent.getKey();
                }
            }

            System.out.println("Result: " + finalResult);

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
