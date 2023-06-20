/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package client;

import java.rmi.RemoteException;

public class CreateWhiteBoard {
	public static void main(String[] args) {
		try {
			String ip_address = args[0];
			int port = Integer.parseInt(args[1]);
			String user_name = args[2];
			
			Client_Controller client_controller = new Client_Controller(ip_address, port, user_name);
			
			client_controller.setIsManager(true);
			
			if (client_controller.join_Server()) {
				System.out.println("WB launchs successfully! " + user_name + " (manager) joined.");
			}else {
				System.exit(0);
			}
			
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("The number of arguments is wrong.");
		} catch (RemoteException e) {
			System.out.println("Remote Exception, some remote problems occured.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
