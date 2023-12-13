package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote{
    void setPosition(int x, int y, int color)      throws RemoteException;
    int getColor()                              throws RemoteException;
    int[] getOpponent()                        throws RemoteException;
    int getWinner()                                throws RemoteException;
}