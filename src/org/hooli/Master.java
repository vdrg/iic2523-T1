package org.hooli;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Master implements Compute {

    public Master() {}

    @Override
    public String sayHello() throws RemoteException {
        return "Hello, world!";
    }

    public static void main(String args[]) {

        try {
            Master obj = new Master();
            Compute stub = (Compute) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Master", stub);

            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
