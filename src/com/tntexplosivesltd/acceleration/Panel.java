/*
	This file is part of LogAcceleration.

    LogAcceleration is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    LogAcceleration is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with LogAcceleration.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.tntexplosivesltd.acceleration;

import java.util.ListIterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * @brief Panel for handling canvas drawing, etc.
 */
public class Panel extends SurfaceView implements SurfaceHolder.Callback {

	private CanvasThread _canvas_thread;
	// Draw stuff in here
	private static Paint _box_paint = new Paint();
	private static Paint _circle_paint = new Paint();
	private static Paint _grid_paint = new Paint();
	private static Paint _minmax = new Paint();
	private static Paint _text = new Paint();
	private static Paint _x_paint = new Paint();
	private static Paint _y_paint = new Paint();
	private static Paint _z_paint = new Paint();
	private float _graph_thickness = 2f;
	private float _circle_diameter = 10f;

	/**
	 * @brief Public constructor.
	 * @param context Current context - used for superclass.
	 * @param attrs Attribute set - used for superclass.
	 */
	public Panel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		getHolder().addCallback(this);
		setFocusable(true);
	}
	
	/**
	 * @brief SurfaceChanged called when the surface is changed. Needed for SurfaceView.
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	/**
	 * @brief Called when the surface is created. Sets itinial variables like paints, and starts canvas thread.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_canvas_thread = new CanvasThread(getHolder(), this);
		_canvas_thread.set_running(true);
		_canvas_thread.start();
		_box_paint.setColor(ColourManager.colours[1]);
		_circle_paint.setColor(ColourManager.colours[2]);
		_grid_paint.setColor(ColourManager.colours[3]);
		_minmax.setColor(ColourManager.colours[4]);
		_text.setColor(ColourManager.colours[5]);
		_x_paint.setColor(ColourManager.colours[6]);
		_y_paint.setColor(ColourManager.colours[7]);
		_z_paint.setColor(ColourManager.colours[8]);
		_x_paint.setStrokeWidth(_graph_thickness);
		_y_paint.setStrokeWidth(_graph_thickness);
		_z_paint.setStrokeWidth(_graph_thickness);
	}

	public static void refresh_colours()
	{
		_box_paint.setColor(ColourManager.colours[1]);
		_circle_paint.setColor(ColourManager.colours[2]);
		_grid_paint.setColor(ColourManager.colours[3]);
		_minmax.setColor(ColourManager.colours[4]);
		_text.setColor(ColourManager.colours[5]);
		_x_paint.setColor(ColourManager.colours[6]);
		_y_paint.setColor(ColourManager.colours[7]);
		_z_paint.setColor(ColourManager.colours[8]);
	}
	
	/**
	 * @brief Called when the surface is destroyed. This is where the canvas thread is joined, so it can exit nicely.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		_canvas_thread.set_running(false);
		while (retry)
		{
			try
			{
				_canvas_thread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	/**
	 * @brief Called by the CanvasThread class. Draws the graphs with current values.
	 * @param canvas The canvas to draw on. 
	 */
	@Override
	public void onDraw(Canvas canvas)
	{
		float view_height = canvas.getHeight();
		float view_width = canvas.getWidth(); 
		float half_height = view_height / 2;
		float half_width = view_width / 2;
		float ninth = view_height / 9;
		float g_width = view_width / GraphData.max_data;
		
		canvas.drawColor(ColourManager.colours[0]);
		
		if (GraphData.mode == 0)
		{
			// We're drawing circles
			int lines = 20;
			for (int i = 0; i <= lines; i++)
			{
				// Draw Gridlines
				canvas.drawLine(i*view_width/lines, 0, i*view_width/lines, view_height, _grid_paint);
				canvas.drawLine(0, i*view_height/lines, view_width, i*view_height/lines, _grid_paint);
				if ((i % 2) == 0)
				{
					// Draw value
					canvas.drawText(String.format("%.0f", i*2-20f), view_width/2, i*view_height/lines, _grid_paint);
					canvas.drawText(String.format("%.0f", i*2-20f), i*view_width/lines, view_height/2, _grid_paint);
				}
			}
			if (GraphData.orientation == 0)
				canvas.drawCircle(half_width - GraphData.x*half_width/20, half_height + GraphData.y*half_height/20, (20+GraphData.z)/10*_circle_diameter, _circle_paint);
			else
				canvas.drawCircle(half_width - GraphData.x*half_width/20, half_height + GraphData.z*half_height/20, (20+GraphData.y)/10*_circle_diameter, _circle_paint);
		}
		else
		{
			// We're drawing graphs
			
			// Draw gridlines
			int lines = 18;
			for (int i = 0; i < lines; i++)
			{
				canvas.drawLine(i*view_width/lines, 0, i*view_width/lines, view_height, _grid_paint);
				canvas.drawLine(0, i*view_height/lines, view_width, i*view_height/lines, _grid_paint);
			}

			// Draw x
			// Dividing block
			canvas.drawRect(0f, 0f, view_width, ninth, _box_paint);
			// Values
			canvas.drawText("X values (" + String.format("%.2f", GraphData.x) + ", min: " + String.format("%.2f", GraphData.min_x) + ", max: " + String.format("%.2f", GraphData.max_x) + ")", 20, 0.75f*ninth, _text);
			int num = 0;
			Float prev = 0f;
			Float current;
			float offset = ninth*2;
			float g_scale = ninth/20;
			canvas.drawLine(0f, offset - GraphData.max_x*g_scale, view_width, offset - GraphData.max_x*g_scale, _minmax);
			canvas.drawLine(0f, offset - GraphData.min_x*g_scale, view_width, offset - GraphData.min_x*g_scale, _minmax);
			synchronized(GraphData.data_x)
			{
				// Max/min lines
				ListIterator<Float> itr = GraphData.data_x.listIterator();
				while (itr.hasNext())
				{
					current = -itr.next();
					if (num != 0)
						canvas.drawLine((num-1)*g_width, offset + prev*g_scale, num*g_width, offset + current*g_scale, _x_paint);
					num++;
					prev = current;
				}
			}
			
			// Draw y
			canvas.drawRect(0f, 3*ninth, view_width, 4*ninth, _box_paint);
			canvas.drawText("Y values (" + String.format("%.2f", GraphData.y) + ", min: " + String.format("%.2f", GraphData.min_y) + ", max: " + String.format("%.2f", GraphData.max_y) + ")", 20, 3.75f*ninth, _text);
			offset = ninth * 5;
			num = 0;
			canvas.drawLine(0f, offset - GraphData.max_y*(g_scale), view_width, offset - GraphData.max_y*(g_scale), _minmax);
			canvas.drawLine(0f, offset - GraphData.min_y*(g_scale), view_width, offset - GraphData.min_y*(g_scale), _minmax);
			synchronized(GraphData.data_y)
			{
				ListIterator<Float> itr = GraphData.data_y.listIterator();
				while (itr.hasNext())
				{
					current = -itr.next();
					if (num != 0)
						canvas.drawLine((num-1)*g_width, offset + prev*(g_scale), num*g_width, offset + current*(g_scale), _y_paint);
					num++;
					prev = current;
				}
			}
			
			// Draw z
			canvas.drawRect(0f, 6*ninth, view_width, 7*ninth, _box_paint);
			canvas.drawText("Z values (" + String.format("%.2f", GraphData.z) + ", min: " + String.format("%.2f", GraphData.min_z) + ", max: " + String.format("%.2f", GraphData.max_z) + ")", 20, 6.75f*ninth, _text);
			offset = ninth * 8f;
			num = 0;
			canvas.drawLine(0f, offset - GraphData.max_z*(g_scale), view_width, offset - GraphData.max_z*(g_scale), _minmax);
			canvas.drawLine(0f, offset - GraphData.min_z*(g_scale), view_width, offset - GraphData.min_z*(g_scale), _minmax);
			synchronized(GraphData.data_z)
			{
				ListIterator<Float> itr = GraphData.data_z.listIterator();
				while (itr.hasNext())
				{
					current = -itr.next();
					if (num != 0)
						canvas.drawLine((num-1)*g_width, offset + prev*(g_scale), num*g_width, offset + current*(g_scale), _z_paint);
					num++;
					prev = current;
				}
			}
		}
	}
}