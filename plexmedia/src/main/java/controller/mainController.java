package controller;

import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerMediaApi;
import javafx.fxml.FXML;
import sun.misc.Signal;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import ch.heigvd.iict.ser.rmi.IServerApi;

public class mainController extends UnicastRemoteObject implements IClientApi {

    private IServerMediaApi remoteService = null;
    private String data;

    public mainController() throws RemoteException {
    }

    @FXML
    protected void initialize() {
        try {
            this.remoteService = (IServerMediaApi) Naming.lookup("//localhost:9998/Rmimedia");

            initialConnection();
            startCheckingThread();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void initialConnection() throws RemoteException {
        //we register to server
        remoteService.addObserver(this);
    }

    public void startCheckingThread() {
        //we start a thread to periodically check if the server is available
        Thread thread = new Thread(){
            @Override
            public void run() {
                boolean isStillConnectedToServer = true;
                while(isStillConnectedToServer) {
                    //every 10 seconds
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) { }
                    try {
                        isStillConnectedToServer = remoteService.isStillConnected();
                    } catch (RemoteException e) {
                        isStillConnectedToServer = false;
                    }
                }
                System.err.println("Server is not avalaible anymore, we stop client");
                System.exit(1);
            };
        };
        thread.start();
    }

    @FXML
    public void showJsonFile() {
        System.out.println(data);
    }

    @Override
    public void update(Object observable, Signal signalType, String updateMsg) throws RemoteException {
        //we log the received signal
        System.out.println("got signal [" + signalType.name() + "]: JSON file created by PLEXADMIN");

        //we request for new data
        //local data in dataBase are sorted, we pick the most recent at the first position

        String newDataDownloaded = remoteService.getData();

        //we add them to the local database
        if(newDataDownloaded != null) {
            ;this.data = newDataDownloaded;
            System.out.println(data);
        }
    }
}