package controllers;

import ch.heigvd.iict.ser.imdb.models.Data;
import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;
import views.*;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ControleurWFC extends UnicastRemoteObject implements IClientApi {

	private ControleurGeneral ctrGeneral;
	private static MainGUI mainGUI;
	private IServerApi remoteService = null;
	private Data data;

	public ControleurWFC(ControleurGeneral ctrGeneral, MainGUI mainGUI, IServerApi remoteService) throws RemoteException {
		super();
		this.ctrGeneral=ctrGeneral;
		ControleurWFC.mainGUI=mainGUI;
		data  = null;
		this.remoteService = remoteService;
	}

	protected void initialConnection() throws RemoteException {
		//we register to server
		remoteService.addObserver(this);

		//initial request, we download all existing data
		data = remoteService.getData();

		//display
		System.out.println("Initial request: ");
		System.out.println(data);
	}

	protected void startCheckingThread() {
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

	@Override
	public void update(Object observable, Signal signalType, String updateMsg) throws RemoteException {
		//we log the received signal
		System.out.println("got signal [" + signalType.name() + "]: " + updateMsg);

		//we request for new data
		//local data in dataBase are sorted, we pick the most recent at the first position

		Data newDataDownloaded = remoteService.getData();

		//we add them to the local database
		if(newDataDownloaded != null) {
			;this.data = newDataDownloaded;
			ctrGeneral.initBaseDeDonneesAvecNouvelleVersion(data);
		}

		System.out.println("Update request: ");
		System.out.println(data);

	}


}