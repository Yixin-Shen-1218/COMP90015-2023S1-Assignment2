/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class WhiteBoardServer {
	public static void main(String[] args) {
		try {
			
			IRemoteWhiteBoard remoteWhiteBoard = new RemoteWhiteBoard();
			
			int port_num = Integer.parseInt(args[0]);
			
            //Publish the remote object's stub in the registry under the name "WhiteBoard"
            Registry registry = LocateRegistry.createRegistry(port_num);
            registry.bind("WhiteBoard", remoteWhiteBoard);
            
            System.out.println("WhiteBoard server ready, the port num is " + port_num);
			
		} catch (AlreadyBoundException e) {
			System.out.println("Already Bound Exception. The port is alreadt bounded, please change to another port");
		} catch (RemoteException e) {
			System.out.println("Remote Exception, the registry fail to be created");
		} catch(NumberFormatException e) {
			System.out.println("Number Format Exception, you should enter one and only one integer argument to run the server");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Array Index Out Of Bounds Exception, you should enter one and only one integer argument to run the server");
		}
	}
}
