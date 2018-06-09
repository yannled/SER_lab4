package controllers;

import ch.heigvd.iict.ser.imdb.models.Data;
import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;
import ch.heigvd.iict.ser.rmi.IServerMediaApi;
import models.*;
import views.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;

public class ControleurMedia extends Observable implements IServerMediaApi {

	private ControleurGeneral ctrGeneral;
	private static MainGUI mainGUI;
	private ORMAccess ormAccess;
	
	private GlobalData globalData;

	public ControleurMedia(ControleurGeneral ctrGeneral, MainGUI mainGUI, ORMAccess ormAccess){
		this.ctrGeneral=ctrGeneral;
		ControleurMedia.mainGUI=mainGUI;
		this.ormAccess=ormAccess;

		try {
			Registry rmiRegistry = LocateRegistry.createRegistry(9998);
			IServerMediaApi rmiServiceMedia = (IServerMediaApi) UnicastRemoteObject.exportObject(this, 9998);
			rmiRegistry.bind("Rmimedia", rmiServiceMedia);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendJSONToMedia(){
		new Thread(){
			public void run(){
				mainGUI.setAcknoledgeMessage("Envoi JSON ... WAIT");
				//long currentTime = System.currentTimeMillis();
				try {
					globalData = ormAccess.GET_GLOBAL_DATA();
					//mainGUI.setWarningMessage("Envoi JSON: Fonction non encore implementee");

					JsonCreation json = new JsonCreation(globalData);
					json.create();

					setChanged();
					notifyObservers();
				}
				catch (Exception e){
					mainGUI.setErrorMessage("Construction JSON impossible", e.toString());
				}
			}
		}.start();
	}

	@Override
	public void addObserver(IClientApi client) throws RemoteException {
		WrappedObserver wo = new WrappedObserver(client);
		addObserver(wo);
		System.out.println("Added observer: " + wo);
	}

	@Override
	public boolean isStillConnected() throws RemoteException {
		return true;
	}

	@Override
	public String getData() throws RemoteException {
		String path = "json.json";
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Gson gson = new Gson();
		Object json = gson.fromJson(bufferedReader, Object.class);
		return json.toString();
	}

	private class WrappedObserver implements Observer, Serializable {

		private static final long serialVersionUID = -2067345842536415833L;

		private IClientApi ro = null;

		public WrappedObserver(IClientApi ro) {
			this.ro = ro;
		}

		@Override
		public void update(Observable o, Object arg) {
			try {
				ro.update(o.toString(), IClientApi.Signal.UPDATE_REQUESTED, "updateProjections");
			} catch (RemoteException e) {
				System.out.println("Remote exception removing observer: " + this);
				o.deleteObserver(this);
			}
		}
	}

}