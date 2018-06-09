
import controllers.*;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

class Amorce {

	public static void main(String [] args) throws RemoteException, NotBoundException, MalformedURLException {
		//we start client
		Amorce amorce = new Amorce();
		amorce.run();
	}
	
	private ORMAccess 			ormAccess 			= null;
	private ControleurGeneral	controleurGeneral	= null;

	private Amorce() throws RemoteException, NotBoundException, MalformedURLException {
		this.ormAccess = new ORMAccess();
		this.controleurGeneral = new ControleurGeneral(ormAccess);
	}

	private void run() {

		while (controleurGeneral.isAlive()) {
			try {Thread.sleep(1000);} 
			catch(InterruptedException e) {}
		}
	}

}