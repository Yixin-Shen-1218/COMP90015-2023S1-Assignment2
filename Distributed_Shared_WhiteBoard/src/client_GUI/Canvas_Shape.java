/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package client_GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Canvas_Shape {
	private int x1, y1, x2, y2;
	
	private Color color = Color.BLACK;
	
	private String shape_type;
	
	private String text;
	
	// Basic shapes
	public Canvas_Shape(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	// text shape
	public Canvas_Shape(int x1, int y1, String text) {
		this.x1 = x1;
		this.y1 = y1;
		this.text = text;
	}
	
	public void draw_shapes(Graphics2D graph) {
		// set the shape color
		graph.setColor(color);
		
		// set the line width
		graph.setStroke(new BasicStroke(3));
		
		switch (shape_type) {
		case "text":
			graph.drawString(text, x1, y1);
			break;
		case "line":
			graph.drawLine(x1, y1, x2, y2);
			break;
		case "circle":
			Integer radius = Math.min(Math.abs(x2 - x1), Math.abs(y2 - y1));
			graph.drawOval(Math.min(x2, x1), Math.min(y2, y1), radius, radius);
			break;
		case "oval":
			graph.drawOval(Math.min(x2, x1), Math.min(y2, y1), Math.abs(x2 - x1), Math.abs(y2 - y1));
			break;
		case "rectangle":
			graph.drawRect(Math.min(x2, x1), Math.min(y2, y1), Math.abs(x2 - x1), Math.abs(y2 - y1));
			break;
		default:
			System.out.println("The shape is unknown");
			break;
		}
	}
	
	public String getType() {
		return this.shape_type;
	}
	
	public void setType(String shape_type) {
		this.shape_type = shape_type;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
}