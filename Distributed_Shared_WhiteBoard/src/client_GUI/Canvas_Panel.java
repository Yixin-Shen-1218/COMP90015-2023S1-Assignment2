/*
 * Author: Yixin SHEN <yixishen1@student.unimelb.edu.au>
 * Student ID: 1336242
 */

package client_GUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Canvas_Panel extends JPanel{
	private ArrayList<Canvas_Shape> canvas_shapes;
	
	private BufferedImage canvas_data;
	
	public Canvas_Panel() {
		canvas_shapes = new ArrayList<Canvas_Shape>();
	}

	public void add_Shape(Canvas_Shape shape) {
		canvas_shapes.add(shape);
	}

	// get the current image
	public BufferedImage getBufferedImage() {
		// get the size of image
		Dimension dimension = this.getSize();
		BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_BGR);
		
		// paint on the image
		Graphics2D graphics2d = image.createGraphics();
		this.paintComponent(graphics2d);
		
		// release the system resources
		graphics2d.dispose();
		return image;
	}
	
	// function to draw the components
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		if (canvas_data != null) {
			graphics.drawImage(canvas_data, 0, 0, null);
		}
		
		// draw the shapes
		for (int i = 0; i < canvas_shapes.size(); i++) {
			Graphics2D graphics2d = (Graphics2D) graphics;
			canvas_shapes.get(i).draw_shapes(graphics2d);
		}
	}
	

	// delete all the private attributes in the class
	public void clear_Canvas() {
		this.canvas_shapes = new ArrayList<Canvas_Shape>();
		this.canvas_data = null;
		repaint();
	}
	
	// load the certain image to the canvas_panel
	public void load_Image(BufferedImage image) {
		clear_Canvas();
		this.canvas_data = image;
	}
}
