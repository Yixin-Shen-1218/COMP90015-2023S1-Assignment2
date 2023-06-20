/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package client_GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Mouse_listener  extends MouseAdapter{
	private Color color = Color.BLACK;
	private Graphics2D graphics2D;
	private Client_GUI client_GUI;
	private Canvas_Panel canvas_Panel;
	private String shapeName = "line";
	private int x1, y1, x2, y2;
	
	
	public Mouse_listener(JPanel drawJPanel, Canvas_Panel canvas_Panel, Client_GUI client_GUI) {
		this.canvas_Panel = canvas_Panel;
		this.client_GUI = client_GUI;
//		System.out.println("this" + drawJPanel.getAlignmentX());
		this.graphics2D = (Graphics2D) drawJPanel.getGraphics();
//		System.out.println(pen.getClass());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		x1 = e.getX();
		y1 = e.getY();
		
		graphics2D.setStroke(new BasicStroke(3));
		
		// draw text
		if ("text".equals(shapeName)) {
			String text_input = JOptionPane.showInputDialog("Please enter the text:");
			if (text_input != null) {
				graphics2D.setColor(color);
				graphics2D.drawString(text_input, x1, y1);
				
				// generate the shape element to canvas panel/image data
				Canvas_Shape text_shape = new Canvas_Shape(x1, y1, text_input);
				text_shape.setColor(color);
				text_shape.setType("text");
				
				// add the generated shape to canvas_Panel
				canvas_Panel.add_Shape(text_shape);
				
				client_GUI.getClient_Controller().synchronizeImage();
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		x2 = e.getX();
		y2 = e.getY();
		
		graphics2D.setStroke(new BasicStroke(3));
		graphics2D.setColor(color);
		
		// draw the shapes
		switch (shapeName) {
		case "line":
			graphics2D.drawLine(x1, y1, x2, y2);
			break;
		case "circle":
			Integer radius = Math.min(Math.abs(x2 - x1), Math.abs(y2 - y1));
			graphics2D.drawOval(Math.min(x2, x1), Math.min(y2, y1), radius, radius);
			break;
		case "oval":
			graphics2D.drawOval(Math.min(x2, x1), Math.min(y2, y1), Math.abs(x2 - x1), Math.abs(y2 - y1));
			break;
		case "rectangle":
			graphics2D.drawRect(Math.min(x2, x1), Math.min(y2, y1), Math.abs(x2 - x1), Math.abs(y2 - y1));
			break;
		default:
			System.out.println("The shape is unknown");
			break;
		}
		
		Canvas_Shape new_shape = new Canvas_Shape(x1, y1, x2, y2);
		new_shape.setColor(color);
		new_shape.setType(shapeName);
		canvas_Panel.add_Shape(new_shape);
		
		client_GUI.getClient_Controller().synchronizeImage();
	}
	
	
	
	public void setShape(String command) {
		this.shapeName = command;
//		System.out.println(command);
	}


	public void setColor(Color color) {
		this.color = color;
//		System.out.println(color);
	}
	
}
