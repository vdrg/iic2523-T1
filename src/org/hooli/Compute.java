package org.hooli;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Compute extends Remote {
   String sayHello() throws RemoteException;
}
