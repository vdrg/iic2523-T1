package org.hooli;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Worker implements WorkerInterface, Serializable {
    private Worker() {}

    @Override
    public int dotProduct(int[] v1, int[] v2) throws RemoteException {
        // TODO: check vector lengths
        int result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        System.out.println(result);
        return result;
    }

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {

            Worker worker = new Worker();
            UnicastRemoteObject.exportObject(worker, 0);

            Registry registry = LocateRegistry.getRegistry(host);
            MasterInterface stub = (MasterInterface) registry.lookup("Master");

System.out.println(worker.getClass());
            if (!stub.register(worker)) {
                System.err.println("Could not reach Master");
                return;
            }
            System.out.println("Registered to Master");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
