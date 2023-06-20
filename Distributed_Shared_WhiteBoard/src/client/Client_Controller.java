/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package client;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import client_GUI.Client_GUI;
import server.IRemoteWhiteBoard;
import server.JoinType;

public class Client_Controller extends UnicastRemoteObject implements IRemoteClient{
	private String ip_address = "localhost";
	private int port_num = 1444;
	private String user_name;
	
	private Client_GUI client_GUI;
	private boolean IsManager = false;
	
	private IRemoteWhiteBoard remoteWB; 
	private String prompt_msg;
	
	
//	public Client_Controller(String user_name) throws RemoteException{
//		this.user_name = user_name;
////		this.client_GUI = new Client_GUI(IsManager, this);
//	}

	public Client_Controller(String ip_address, int port, String user_name) throws RemoteException{
		this.ip_address = ip_address;
		this.port_num = port;
		this.user_name = user_name;
	}
	
	public String getUserName() {
		return this.user_name;
	}
	
	
	public Client_GUI getClient_GUI() {
		return this.client_GUI;
	}
	
	
	// indicates whether it is a manager 
	public void setIsManager(boolean IsManager) {
		this.IsManager = IsManager;
		if (IsManager) {
			System.out.println("This controller is a manager!");
		}else {
			System.out.println("This controller is a common user!");
		}
	}


	// send the message to the chat window
	public void sendMessageClick(String message) {
		if (message == null || message.trim().equals("")) {
			return;
		}else {
			try {
				this.remoteWB.chat_broadcast(this.user_name, message);
			} catch (RemoteException e) {
				prompt_Dialog("Remote Exception, some remote problems occured.");
				e.printStackTrace();
			}
		}
	}
	
	
	// initialize the client GUI
	@Override
	public void GUI_init() throws RemoteException {
		this.client_GUI = new Client_GUI(IsManager, this);
		client_GUI.initialize();
		
//		System.out.println("GUI initialization");
	}


	// function to connect the client controller to server
	public boolean join_Server() {
		//Connect to the rmiregistry that is running
		try {
			Registry registry = LocateRegistry.getRegistry(this.ip_address, this.port_num);
			
			//Retrieve the stub/proxy for the remote WB object from the registry
			this.remoteWB = (IRemoteWhiteBoard) registry.lookup("WhiteBoard");
			
			JoinType joinType = this.remoteWB.join_server(this, user_name, IsManager);
			System.out.println(joinType);
			
			if (joinType == joinType.Join_Success) {
				prompt_msg = "Welcome to the whiteboard game!\nYour username is " + user_name + ".";
				prompt_Dialog(prompt_msg);
				return true;
			}
			
			switch (joinType) {
			case Manager_Exist:
				prompt_Dialog("There is a manager, you can not join as a manager.");
				break;
			case No_Manager:
				prompt_Dialog("Sorry, there is no manager. Please create a manager by running CreateWhiteBoard command.");
				break;
			case Duplicate_Username:
				prompt_Dialog("Sorry, the user name is duplicate, please enter a new user name.");
				break;
			case Manager_Refused:
				prompt_Dialog("Sorry, the manager rejects your request.");
				break;
			default:
				prompt_Dialog("Sorry, some thing went wrong, you can not join the whiteboard.");
				break;
			}
			
		} catch (RemoteException e) {
			prompt_Dialog("Remote error occurs. The ip address or the port num might be wrong.");
		} catch (Exception e) {
			e.printStackTrace();
			prompt_Dialog("some exception occurs!");
		}

		return false;
	}


	private void prompt_Dialog(String prompt_msg) {
		if (this.client_GUI == null) {
			JOptionPane.showMessageDialog(null, prompt_msg);
		} else {
			JOptionPane.showMessageDialog(this.client_GUI.getFrame(), prompt_msg);
		}
	}


	// update the user list in the GUI when the user list changes
	@Override
	public void update_userList(ArrayList<String> username_List) throws RemoteException {
		// set the area to blank
		this.client_GUI.get_userListTextArea().setText(null);
		
		// update the user list area
		StringBuilder usernames = new StringBuilder();
		
		for (String username : username_List) {
			usernames.append(username + "\n");
		}
		
		this.client_GUI.get_userListTextArea().setText(usernames.toString());
	}


	// get the approval from the manager
	@Override
	public boolean Manager_Approve(String user_name) throws RemoteException {
		String message = user_name + " wants to share your whiteboard\nDo you want to approve?";
		String title = "New Player Request";
		int input = JOptionPane.showConfirmDialog(client_GUI.getFrame(), 
				message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
//		System.out.println(input);
		if (input == JOptionPane.YES_OPTION) {
			return true;
		}else {
			return false;
		}
	}


	// Close the white board game
	public void Close_Window(WindowEvent windowEvent) {
		try {
			if (IsManager) {
				remoteWB.Close_ALL();
			} else {
				remoteWB.user_exit(this.user_name);
			}
		} catch (RemoteException e) {
			prompt_Dialog("Remote Exception, some remote problems occured.");
		}
	}
	
	
	// transfer  the BufferdImange to Byte array
	private byte[] image_process() {
		byte[] bytes = null;
		
		try {
			BufferedImage image = client_GUI.getCanvas_Panel().getBufferedImage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			baos.flush();
			bytes = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			prompt_Dialog("Sorry, some IO exception occurs!");
			e.printStackTrace();
		}
		
		return bytes;
	}
	
	
	// Synchronize the client white board, update the image on the server, and update all the images of the other clients
	public void synchronizeImage() {
		byte[] bytes = image_process();
		
		// use server to save and broadcast the image
		try {
			this.remoteWB.synchronizeImage(bytes, user_name);
		} catch (RemoteException e) {
			prompt_Dialog("Remote Exception, some remote problems occured.");
		}
	}

	
	// close the window as the manager exit
	@Override
	public void manager_exit_close() throws RemoteException {
		new Thread(() -> {
			prompt_msg = "Sorry, your game is terminated as the manager exit!";
			prompt_Dialog(prompt_msg);
			System.exit(0);
		}).start();
	}


	// update the chat window
	@Override
	public void update_chatWindow(String user_name, String msg) throws RemoteException {
		this.client_GUI.get_chatTextArea().append(user_name + ": " + msg + "\n");
	}

	@Override
	public void updateByteImage(byte[] bytes) throws RemoteException{
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			BufferedImage image = ImageIO.read(bais);
			this.client_GUI.getCanvas_Panel().load_Image(image);
		} catch (IOException e) {
			prompt_Dialog("Sorry, some IO exception occurs!");
			e.printStackTrace();
		}
	}

	// new a blank panel
	public void newAction() {
		this.client_GUI.getCanvas_Panel().clear_Canvas();
		synchronizeImage();
	}

	public void openAction(){
		// if the archive dir not exist, create a dir named archive
//		System.out.println("11111");
		
		// if the dir is not exist, create a new dir
		File dir = new File("./archive");
		if (dir.exists()) {
//			System.out.println("dir exist");
		} else {
			System.out.println("dir not exist, create a new dir");
			dir.mkdir();
		}
		
//		System.out.println("22222");
		
		JFileChooser chooser = new JFileChooser("./archive");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CanvasData", "out");
		chooser.setFileFilter(filter);
		int flag = chooser.showOpenDialog(this.client_GUI.getFrame());
		
		if (flag == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			byte[] bytes;
			try {
				bytes = Files.readAllBytes(file.toPath());
				
				// load the image to the current manager GUI
				updateByteImage(bytes);
				
				// synchronize the image
				synchronizeImage();
			} catch (RemoteException e) {
				prompt_Dialog("Remote Exception, some remote problems occured.");
			} catch (IOException e) {
				prompt_Dialog("Sorry, some IO exception occurs!");
			}
		}else {
			return;
		}
	}

	// save the current canvas
	public void saveAction() throws IOException {
		byte[] bytes = image_process();
		
		String msg = "Please enter the archive name";
		String fileName = JOptionPane.showInputDialog(this.client_GUI.getFrame(), msg);
		
		if (fileName == null) {
			return;
		}
		
		if (fileName == "" || fileName.trim().equals("")) {
			prompt_Dialog("File name can not be empty. Please try again.");
			return;
		}
		
		String filepath = "archive/" + fileName + ".out";
		File file = new File(filepath);
		
        if (!file.getParentFile().exists()) {
            boolean result = file.getParentFile().mkdirs();
            if (!result) {
                System.out.println("fail to create the dir.");
            }
        }
		
		if (file.exists()) {
			file.delete();
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		
		fos.write(bytes, 0, bytes.length);
		fos.flush();
		fos.close();
		
		prompt_Dialog("Save successfully!");
	}

	// save the current canvas into jpg/png
	public void saveAsAction() {
		BufferedImage current_img = client_GUI.getCanvas_Panel().getBufferedImage();
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.png", "png");
		
		JFileChooser fileChooser = new JFileChooser(".");
		
		fileChooser.setFileFilter(filter);
		
		fileChooser.setMultiSelectionEnabled(false);
		
		int result = fileChooser.showSaveDialog(this.client_GUI.getFrame());
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getPath().endsWith(".png")) {
				file = new File(file.getPath() + ".png");
			}
			try {
				ImageIO.write(current_img, "png", file);
			} catch (IOException e) {
				prompt_Dialog("Sorry, some IO exception occurs!");
				e.printStackTrace();
			}
		}
	}

	// close action in file menu
	public void closeAction() {
		String message = "Are you sure you want to close the whiteboard?";
		String title = "Close White Board";
		
		int input = JOptionPane.showConfirmDialog(this.client_GUI.getFrame(), 
				message, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (input == JOptionPane.YES_OPTION) {
			try {
				remoteWB.Close_ALL();
				System.exit(0);
			} catch (RemoteException e) {
				prompt_Dialog("Remote Exception, some remote problems occured.");
				e.printStackTrace();
			}
		}
	}
	
	// kick out a user
	public void kickoutAction() {
		String msg = "Please enter a user name";
		String selected_username = JOptionPane.showInputDialog(this.client_GUI.getFrame(), msg);
		
//		System.out.println(selected_username);
//		System.out.println(this.user_name);
		
		if (selected_username == null) {
			return;
		}
		
		if (selected_username.equals(this.user_name)) {
			prompt_Dialog("Sorry, as a manager, you can not kick out yourself.");
			return;
		}
		
		try {
			if (this.remoteWB.user_exist(selected_username)) {
				this.remoteWB.kickout_user(selected_username);
			} else {
				prompt_Dialog("Sorry, the input user name is not exist, please try again.");
				return;
			}
		} catch (RemoteException e) {
			prompt_Dialog("Remote Exception, some remote problems occured.");
		}		
		return;
	}

	// close the kicked out user's window
	@Override
	public void kickout_close() throws RemoteException {
		new Thread(() -> {
			prompt_msg = "Sorry, your game is terminated as the manager kicked you out!";
			prompt_Dialog(prompt_msg);
			System.exit(0);
		}).start();
	}
}