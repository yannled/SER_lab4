
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;
import db.MySQLAccess;
import ch.heigvd.iict.ser.imdb.models.Data;

public class Main extends Observable implements IServerApi{

	static {
		// this will load the MySQL driver, each DB has its own driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("MySQL drivers not found !");
			System.exit(1);
		}

		//database configuration
		MySQLAccess.MYSQL_URL 		= "docr.iict.ch";
		MySQLAccess.MYSQL_DBNAME 	= "imdb";
		MySQLAccess.MYSQL_USER 		= "imdb";
		MySQLAccess.MYSQL_PASSWORD 	= "imdb";
	}

	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		Main main = new Main();
		try {
			Registry rmiRegistry = LocateRegistry.createRegistry(9999);
			IServerApi rmiService = (IServerApi) UnicastRemoteObject.exportObject(main, 9999);
			rmiRegistry.bind("RmiService", rmiService);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		main.run();
	}

	private Data lastData = null;

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
	public Data getData() throws RemoteException {
		return this.lastData;
	}

	private void run() {

		boolean continuer = true;		
		while(continuer) {
			System.out.print("Select the data version to download [1/2/3/0=quit]: ");
			int choice = -1;
			try {
				choice = scanner.nextInt();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(choice == 0) continuer = false;
			else if(choice >= 1 && choice <= 3) {
				Worker worker = new Worker(choice);
				this.lastData = worker.run();


				//TODO notify client
				setChanged();
				notifyObservers();
			}
		}
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
				ro.update(o.toString(), IClientApi.Signal.UPDATE_REQUESTED, arg.toString());
			} catch (RemoteException e) {
				System.out.println("Remote exception removing observer: " + this);
				o.deleteObserver(this);
			}
		}
	}

}
