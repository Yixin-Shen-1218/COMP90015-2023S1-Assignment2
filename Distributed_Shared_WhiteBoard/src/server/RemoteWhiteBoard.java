/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import client.IRemoteClient;

public class RemoteWhiteBoard extends UnicastRemoteObject implements IRemoteWhiteBoard{
	// server stores the clients
	private ConcurrentHashMap<String, IRemoteClient> client_list;
	
	// indicates whether there is a manager or not
	private boolean No_Manager = true;
	
	// save the name of manager
	private String manager_name;
	
	// store the current image
	private byte[] byteImage;
	
	protected RemoteWhiteBoard() throws RemoteException {
		this.client_list = new ConcurrentHashMap<String, IRemoteClient>();
		this.No_Manager = true;
	}

	
	private ArrayList<IRemoteClient> getUsers() {
		return new ArrayList<IRemoteClient>(client_list.values());
	}
	
	private ArrayList<String> getUserNames() {
		return new ArrayList<String>(client_list.keySet());
	}
	
	@Override
	public JoinType join_server(IRemoteClient remoteClient, String user_name, boolean IsManager) throws RemoteException {
		// if the new player is manager
		if (IsManager) {
			if (!No_Manager) {
				return JoinType.Manager_Exist;
			}else {
				No_Manager = false;
				manager_name = user_name;
				client_list.put(user_name, remoteClient);
				
				// initiate the GUI
				remoteClient.GUI_init();
				
				// update the user list in all the GUIs
				Update_All_Userlists();
				chat_broadcast(this.manager_name + " (manager)", "Hello World!");
				
				return JoinType.Join_Success;
			}
		}
		
		// if there is no manager and you run the common user first
		if (No_Manager) {
			return JoinType.No_Manager;
		}
		
		// if the user name is duplicate
		if (client_list.containsKey(user_name)) {
			return JoinType.Duplicate_Username;
		} else {
			if (client_list.get(manager_name).Manager_Approve(user_name)) {
				client_list.put(user_name, remoteClient);
				
				// initiate the GUI
				remoteClient.GUI_init();
				
				if (byteImage != null) {
					remoteClient.updateByteImage(byteImage);
				}
				
				// update the user list in all the GUIs
				Update_All_Userlists();
				chat_broadcast(this.manager_name + " (manager)", "Welcome " + user_name + " join the game!");
				
				return JoinType.Join_Success;
			}else {
				return JoinType.Manager_Refused;
			}
		}
	}

	// update all the gui's user list
	private void Update_All_Userlists() {
		ArrayList<IRemoteClient> users = getUsers();
		ArrayList<String> username_List = getUserNames();

		for (int i = 0; i < users.size(); i++) {
			try {
				users.get(i).update_userList(username_List);
			} catch (RemoteException e) {
				System.out.println("Remote Exception, " + username_List.get(i) + "failed, please check the connection situation.");
			}
		}
	}

	@Override
	public void Close_ALL() throws RemoteException {
		// set the no manager boolean value to true
		this.No_Manager = true;
		
		// set the manager name to null and remove the manager from client_list
		client_list.remove(manager_name);
		this.manager_name = null;
		
		ArrayList<IRemoteClient> users = getUsers();
		
		for (int i = 0; i < users.size(); i++) {
			users.get(i).manager_exit_close();
		}
		
		client_list.clear();
		
		// clear the byteImage in the server
		byteImage = null;
	}
	
	
	// single user exit
	@Override
	public void user_exit(String user_name) throws RemoteException {
		client_list.remove(user_name);
		Update_All_Userlists();
		
		// send a exit message
		chat_broadcast(manager_name + " (manager)", user_name + " has left.");
	}

	// broadcast the chat message to all the users
	@Override
	public void chat_broadcast(String user_name, String msg) throws RemoteException {
		ArrayList<IRemoteClient> users = getUsers();
		
		for (int i = 0; i < users.size(); i++) {
			users.get(i).update_chatWindow(user_name, msg);
		}
	}

	// synchronize the image to other users
	@Override
	public void synchronizeImage(byte[] bytes, String user_name) throws RemoteException {
		ArrayList<IRemoteClient> users = getUsers();
		ArrayList<String> username_List = getUserNames();
		
		this.byteImage = bytes;
		
		for (int i = 0; i < username_List.size(); i++) {
			if (username_List.get(i) != user_name) {
				users.get(i).updateByteImage(bytes);
			}
		}
	}


	@Override
	public void kickout_user(String selected_username) throws RemoteException{
		client_list.get(selected_username).kickout_close();
		
		client_list.remove(selected_username);
		Update_All_Userlists();
		
		// send a exit message
		chat_broadcast(manager_name + " (manager)", selected_username + " has been kicked out.");
	}


	@Override
	public boolean user_exist(String selected_username) throws RemoteException{
		ArrayList<String> username_List = getUserNames();
		
		if (username_List.contains(selected_username)) {
			return true;
		}
		
		return false;
	}
}
