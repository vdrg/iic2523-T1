package org.hooli;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Worker implements WorkerInterface {

    private double failureProbability;
    private Random random = new Random();

    private Worker(double failureProbability) throws IllegalArgumentException{
        if(failureProbability < 0 || failureProbability > 1){
            throw new IllegalArgumentException("Failure probability should be between 0 and 1");
        }
        this.failureProbability = failureProbability;
    }

    // Calculate the dot product between two vectors.
    @Override
    public int dotProduct(int[] v1, int[] v2) throws RemoteException {
        // Check vectors length
        if(v1.length != v2.length){
            throw new RemoteException("Vectors should have the same length");
        }

        int result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        int error = inducedError();
        System.out.printf("Result: %d - Error: %d\n", result, error);
        return result + error;
    }

    private int inducedError(){
        return random.nextDouble() > 1 - this.failureProbability ? random.nextInt() : 0;
    }

    private static Worker worker;
    private static String host;
    private static final String MASTER = "Master";
    private static final int CHECK_INTERVAL = 1000;

    public static void exit(){
        try{
            UnicastRemoteObject.unexportObject(worker, true);
            System.exit(0);
        }
        catch (NoSuchObjectException e){
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void checkMaster(){
        while(true){
            try{
                Registry registry = LocateRegistry.getRegistry(host);
                MasterInterface stub = (MasterInterface) registry.lookup(MASTER);
                if(stub == null){
                    System.out.println("Master disconnected");
                    exit();
                }
                Thread.sleep(CHECK_INTERVAL);
            }
            catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
                exit();
            }
        }
    }


    public static void main(String[] args) {
        host = (args.length < 1) ? null : args[0];
        double failureProbability = (args.length < 2) ? 0 : Double.parseDouble(args[1]);

        worker = new Worker(failureProbability);

        try {
            UnicastRemoteObject.exportObject(worker, 0);

            Registry registry = LocateRegistry.getRegistry(host);
            MasterInterface stub = (MasterInterface) registry.lookup(MASTER);

            if (!stub.register(worker)) {
                System.err.println("Could not reach Master");
                exit();
            }
            System.out.println("Registered to Master");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            exit();
        }
        checkMaster();
    }
}
