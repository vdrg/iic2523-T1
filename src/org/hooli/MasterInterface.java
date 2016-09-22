package org.hooli;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterInterface extends Remote {
    boolean register(WorkerInterface worker) throws RemoteException;
}
