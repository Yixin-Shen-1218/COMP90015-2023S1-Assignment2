/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.IRemoteClient;

public interface IRemoteWhiteBoard extends Remote{
	// create server remote object function that allows users to join
	public JoinType join_server(IRemoteClient remoteClient, String user_name, boolean IsManager) throws RemoteException;

	// close all the running windows
	public void Close_ALL() throws RemoteException;

	// close the user window who chooses to exit
	public void user_exit(String user_name) throws RemoteException;
	
	// broadcast the chat message to all the users
	public void chat_broadcast(String user_name, String msg) throws RemoteException;

	// synchronizeImage to the other users
	public void synchronizeImage(byte[] bytes, String user_name) throws RemoteException;

	// kick the selected user
	public void kickout_user(String selected_username) throws RemoteException;

	// check whether the user is exist or not
	public boolean user_exist(String selected_username) throws RemoteException;
}
