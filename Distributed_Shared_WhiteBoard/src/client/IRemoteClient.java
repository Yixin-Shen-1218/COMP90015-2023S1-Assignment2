/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteClient extends Remote{
	// Initialize the GUI window
	public void GUI_init() throws RemoteException;
	
	// when a new user join, get the approve from the manager
	public boolean Manager_Approve(String user_name) throws RemoteException;
	
	// update the user list
	public void update_userList(ArrayList<String> username_List) throws RemoteException;

	// close the window as the manager exit
	public void manager_exit_close() throws RemoteException;

	// update the chat windows
	public void update_chatWindow(String user_name, String msg) throws RemoteException;

	// update the image of the canvas panel
	public void updateByteImage(byte[] bytes) throws RemoteException;

	// close the user window when the user is kicked out by the manager
	public void kickout_close() throws RemoteException;



}
