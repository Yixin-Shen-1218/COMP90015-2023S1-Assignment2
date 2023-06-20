/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package client_GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import client.Client_Controller;

public class Client_GUI {
	private JFrame frame;
	private Client_Controller client_controller;
	private boolean isManager;
	private Canvas_Panel canvas_Panel;
	private Mouse_listener mouse_listener;
	
	private JTextArea userListTextArea;
	private JTextArea chatTextArea;
	private JTextField enterField;
	
	private String command;
	
	/**
	 * Create the application.
	 */
	public Client_GUI(boolean isManager, Client_Controller client_controller) {
		this.isManager = isManager;
//		System.out.println(isManager);
		this.client_controller = client_controller;
		
//		initialize();
	}
	
	public Client_Controller getClient_Controller() {
		return this.client_controller;
	}
	
	public Frame getFrame() {
		return this.frame;
	}
	
	public JTextArea get_userListTextArea() {
		return this.userListTextArea;
	}
	
	public Canvas_Panel getCanvas_Panel() {
		return this.canvas_Panel;
	}
	
	public JTextArea get_chatTextArea() {
		return this.chatTextArea;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 550);
		frame.setTitle("WhiteBoard Game, username: " + client_controller.getUserName());
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		// create the menu bar
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		if (isManager) {
			JMenuItem new_item = new JMenuItem("new");
			new_item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					client_controller.newAction();
				}
			});
			fileMenu.add(new_item);
			
			JMenuItem open_item = new JMenuItem("open");
			open_item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					client_controller.openAction();
				}
			});
			fileMenu.add(open_item);
			
			JMenuItem save_item = new JMenuItem("save");
			save_item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						client_controller.saveAction();
					} catch (IOException e1) {
						System.out.println("IO exception occurs.");
						e1.printStackTrace();
					}
				}
			});
			fileMenu.add(save_item);
			
			JMenuItem saveAs_item = new JMenuItem("saveAs");
			saveAs_item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					client_controller.saveAsAction();
				}
			});
			fileMenu.add(saveAs_item);
			
			JMenuItem close_item = new JMenuItem("close");
			close_item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					client_controller.closeAction();
				}
			});
			fileMenu.add(close_item);
			
			JMenuItem kickout_item = new JMenuItem("kickout");
			kickout_item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					client_controller.kickoutAction();
				}
			});
			fileMenu.add(kickout_item);
		}
		
		// create the tool panel
		JPanel toolJPanel = ToolPanel();
		frame.getContentPane().add(toolJPanel, BorderLayout.WEST);
		
		
		// create the draw panel for mouse listener
		JPanel drawJPanel = new JPanel();
		drawJPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		drawJPanel.setBackground(Color.GRAY);
		
		canvas_Panel = new Canvas_Panel();
		canvas_Panel.setBackground(Color.WHITE);
		canvas_Panel.setPreferredSize(new Dimension(545, 480));
		
		drawJPanel.add(canvas_Panel);
		
		frame.getContentPane().add(drawJPanel, BorderLayout.CENTER);
		
//		System.out.println("!!!!!" + drawJPanel.getAlignmentX());
		
		// create user panel
		JPanel userJPanel = userPanel();
		frame.getContentPane().add(userJPanel, BorderLayout.EAST);
		
		frame.setVisible(true);
		
		// add mouse listener to the drawJPanel
		mouse_listener = new Mouse_listener(drawJPanel, canvas_Panel, this);
		drawJPanel.addMouseListener(mouse_listener);
		drawJPanel.addMouseMotionListener(mouse_listener);
		
		setWindowCloseEvent();
	}
	
	
	// set the close event
	private void setWindowCloseEvent() {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				String message = "Are you sure you want to exit the whiteboard?";
				String title = "Window Closing";
				
				int input = JOptionPane.showConfirmDialog(frame, 
						message, title, JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (input == JOptionPane.YES_OPTION) {
					client_controller.Close_Window(windowEvent);
					windowEvent.getWindow().dispose();
					System.exit(0);
				}
			}
		});
	}

	
	// construct the user panel
	private JPanel userPanel() {
		// create the user list and chat panel part
		
		JPanel user_Panel = new JPanel();
		user_Panel.setBackground(Color.lightGray);
		user_Panel.setLayout(null);
		user_Panel.setPreferredSize(new Dimension(180, 500));

		// create the user list label
		JLabel userJLabel = new JLabel("USER LIST: ");
		userJLabel.setFont(new Font("TimesRoman", Font.PLAIN, 13));
		userJLabel.setBounds(5, 0, 170, 25);
		user_Panel.add(userJLabel);
		
		// create the user list window
		userListTextArea = new JTextArea();
		userListTextArea.setLineWrap(true);
		userListTextArea.setEditable(false);
		userListTextArea.setBounds(5, 25, 170, 120);
		JScrollPane userListScroll = new JScrollPane(userListTextArea);
		userListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		userListScroll.setBounds(5, 25, 170, 120);
		
		user_Panel.add(userListScroll);
		
		// create the chat window label
		JLabel chatJLabel = new JLabel("CHAT HISTORY: ");
		chatJLabel.setFont(new Font("TimesRoman", Font.PLAIN, 13));
		chatJLabel.setBounds(5, 150, 170, 25);
		user_Panel.add(chatJLabel);
		
		// create the user list window
		chatTextArea = new JTextArea();
		chatTextArea.setLineWrap(true);
		chatTextArea.setEditable(false);
		chatTextArea.setBounds(5, 175, 170, 200);
		JScrollPane chatTextScroll = new JScrollPane(chatTextArea);
		chatTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatTextScroll.setBounds(5, 175, 170, 200);
		
		user_Panel.add(chatTextScroll);
		
		// create enter label
		JLabel MassageJLabel = new JLabel("ENTER MESSAGE: ");
		MassageJLabel.setFont(new Font("TimesRoman", Font.PLAIN, 13));
		MassageJLabel.setBounds(5, 375, 170, 25);
		user_Panel.add(MassageJLabel);
		
		// create enter text are
		enterField = new JTextField();
		enterField.setBounds(5, 400, 170, 25);
		user_Panel.add(enterField);
		
		// create send button
		JButton sendButton = new JButton("send");
		sendButton.setBounds(5, 430, 80, 40);
		sendButton.setForeground(Color.GREEN);
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = enterField.getText();
				client_controller.sendMessageClick(message);
				enterField.setText(null);
			}
		});
		user_Panel.add(sendButton);
		
		
		// create clear button
		JButton clearButton = new JButton("clear");
		clearButton.setBounds(95, 430, 80, 40);
		clearButton.setForeground(Color.DARK_GRAY);
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enterField.setText(null);
			}
		});
		
		user_Panel.add(clearButton);
		
		
		
		return user_Panel;
	}
	

	// construct the tool panel
	private JPanel ToolPanel() {
		// initialize the tool panel
		JPanel toolJPanel = new JPanel();
		toolJPanel.setLayout(new BorderLayout());
		toolJPanel.setPreferredSize(new Dimension(50, 500));
		
		JPanel Shape_Image_Panel = new JPanel();
		Shape_Image_Panel.setLayout(new GridLayout(5, 1, 1, 1));
		
		String[] shape_list = { "/images/line.png", "/images/circle.png", "/images/oval.png", "/images/rectangle.png", "/images/text.png"};
		
		for (int i = 0; i < shape_list.length; i++) {
			ImageIcon icon = new ImageIcon(getClass().getResource(shape_list[i]));
			JButton shape_button = new JButton();
			shape_button.setBounds(0, 0, 35, 35);
			shape_button.setPreferredSize(new Dimension(50, 50));
			
			// scale the icon image
			@SuppressWarnings("static-access")
			Image temp = icon.getImage().getScaledInstance(shape_button.getWidth(), shape_button.getHeight(), icon.getImage().SCALE_DEFAULT);  
			ImageIcon scaled_icon = new ImageIcon(temp);
			
			shape_button.setIcon(scaled_icon);
			
			// add the button actions, set the current shape
			String shapeName = shape_list[i].substring(8, shape_list[i].lastIndexOf("."));
			shape_button.setActionCommand(shapeName);
			
			//			System.out.println(nameString);
			shape_button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					command = e.getActionCommand();
					mouse_listener.setShape(command);
				}
			});
			
			
			Shape_Image_Panel.add(shape_button);
		}
		toolJPanel.add(Shape_Image_Panel, BorderLayout.NORTH);
		
		
		// create 16 colors panel
		JPanel color_Panel = new JPanel();
		color_Panel.setLayout(new GridLayout(8, 2, 2, 2));
		
		Color[] color_Array = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.MAGENTA,
				Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW, new Color(148,0,211), new Color(25,25,112), new Color(50,205,50),
				new Color(255,248,220), new Color(210,105,30)
				};
		
		for (int i = 0; i < color_Array.length; i++) {
			JButton color_button = new JButton();
			color_button.setPreferredSize(new Dimension(25, 25));
			color_button.setBackground(color_Array[i]);
			color_button.setBorderPainted(false);
			color_button.setOpaque(true);
			
			final Integer color_idx = i;
			
			// add the actions, set the current color
			color_button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					mouse_listener.setColor(color_Array[color_idx]);
				}
			});
			
			color_Panel.add(color_button);	
		}
		
		toolJPanel.add(color_Panel, BorderLayout.SOUTH);
		
		return toolJPanel;
	}
}
