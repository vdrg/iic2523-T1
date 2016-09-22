package org.hooli;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by vdrg on 9/22/16.
 */
public interface WorkerInterface extends Remote {

    int dotProduct(int[] v1, int[] v2) throws RemoteException;
}
