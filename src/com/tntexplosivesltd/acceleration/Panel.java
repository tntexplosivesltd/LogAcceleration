package com.tntexplosivesltd.acceleration;

import java.util.ListIterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

	
	float dip = getContext().getResources().getDisplayMetrics().density;
	CanvasThread canvas_thread;
	// Draw stuff in here
	Paint circle_paint = new Paint();
	Paint line_paint = new Paint();
	Paint block_paint = new Paint();
	Paint block_text = new Paint();
	Paint x_paint = new Paint();
	Paint y_paint = new Paint();
	Paint z_paint = new Paint();
	Paint extremes = new Paint();
	float graph_width = 2f;
	float circle_diameter = 10f;

	public Panel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		getHolder().addCallback(this);
		setFocusable(true);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		canvas_thread = new CanvasThread(getHolder(), this);
		canvas_thread.set_running(true);
		canvas_thread.start();
		circle_paint.setColor(Color.BLUE);
		line_paint.setColor(Color.GRAY);
		block_paint.setColor(Color.LTGRAY);
		block_text.setColor(Color.BLACK);
		x_paint.setColor(Color.RED);
		y_paint.setColor(Color.rgb(0, 127, 0));
		z_paint.setColor(Color.BLUE);
		extremes.setColor(Color.YELLOW);
		x_paint.setStrokeWidth(graph_width);
		y_paint.setStrokeWidth(graph_width);
		z_paint.setStrokeWidth(graph_width);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		canvas_thread.set_running(false);
		while (retry)
		{
			try
			{
				canvas_thread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		float view_height = canvas.getHeight()*dip;
		float view_width = canvas.getWidth()*dip; 
		float ninth = view_height / 9;
		float g_width = view_width / GraphData.max_data;
		
		canvas.drawColor(Color.WHITE);
		
			if (GraphData.mode == 0)
			{
				// We're drawing circles
				int lines = 20;
				for (int i = 0; i <= lines; i++)
				{
					// Draw Gridlines
					canvas.drawLine(i*view_width/lines, 0, i*view_width/lines, view_height, line_paint);
					canvas.drawLine(0, i*view_height/lines, view_width, i*view_height/lines, line_paint);
					if ((i % 2) == 0)
					{
						// Draw value
						canvas.drawText(String.format("%.1f", i*2/10f-2f), view_width/2, i*view_height/lines, line_paint);
						canvas.drawText(String.format("%.1f", i*2/10f-2f), i*view_width/lines, view_height/2, line_paint);
					}
				}
				/*
				if (GraphData.orientation == 0)
				{
					canvas.drawCircle(view_width/2 - GraphData.x*10, view_height/2 + GraphData.y*10, circle_diameter*dip, circle_paint);
				}
				else
				{
					canvas.drawCircle(view_width/2 - GraphData.x*10, view_height/2 + GraphData.z*10, circle_diameter*dip, circle_paint);
				}
				*/
				canvas.drawText(""+GraphData.logged_values, view_width/2 - GraphData.x*10, view_height/2 + GraphData.y*10, circle_paint);
			}
			else
			{
				// We're drawing graphs
				
				// Draw gridlines
				int lines = 18;
				for (int i = 0; i < lines; i++)
				{
					canvas.drawLine(i*view_width/lines, 0, i*view_width/lines, view_height, line_paint);
					canvas.drawLine(0, i*view_height/lines, view_width, i*view_height/lines, line_paint);
				}

				// Draw x
				// Dividing block
				canvas.drawRect(0f, 0f, view_width, ninth, block_paint);
				// Values
				canvas.drawText("X values (" + String.format("%.2f", GraphData.x) + ", min: " + String.format("%.2f", GraphData.min_x) + ", max: " + String.format("%.2f", GraphData.max_x) + ")", 20*dip, 0.75f*ninth, block_text);
				int num = 0;
				Float prev = 0f;
				Float current;
				float offset = ninth*2;
				float g_scale = ninth/20;
				synchronized(GraphData.data_x)
				{
					canvas.drawLine(0f, offset - GraphData.max_x*g_scale, view_width, offset - GraphData.max_x*g_scale, extremes);
					canvas.drawLine(0f, offset - GraphData.min_x*g_scale, view_width, offset - GraphData.min_x*g_scale, extremes);
					ListIterator<Float> x_itr = GraphData.data_x.listIterator();
					while (x_itr.hasNext())
					{
						current = -x_itr.next();
						if (num != 0)
						{
							canvas.drawLine((num-1)*g_width, offset + prev*g_scale, num*g_width, offset + current*g_scale, x_paint);
						}
						num++;
						prev = current;
					}
				}
				
				//Draw y
				canvas.drawRect(0f, 3*ninth, view_width, 4*ninth, block_paint);
				canvas.drawText("Y values (" + String.format("%.2f", GraphData.y) + ", min: " + String.format("%.2f", GraphData.min_y) + ", max: " + String.format("%.2f", GraphData.max_y) + ")", 20*dip, 3.75f*ninth, block_text);
				offset = ninth * 5;
				num = 0;
				synchronized(GraphData.data_y)
				{
					canvas.drawLine(0f, offset - GraphData.max_y*(g_scale), view_width, offset - GraphData.max_y*(g_scale), extremes);
					canvas.drawLine(0f, offset - GraphData.min_y*(g_scale), view_width, offset - GraphData.min_y*(g_scale), extremes);
					ListIterator<Float> itr = GraphData.data_y.listIterator();
					while (itr.hasNext())
					{
						current = -itr.next();
						if (num != 0)
						{
							canvas.drawLine((num-1)*g_width, offset + prev*(g_scale), num*g_width, offset + current*(g_scale), y_paint);
						}
						num++;
						prev = current;
					}
				}
				
				// Draw z
				canvas.drawRect(0f, 6*ninth, view_width, 7*ninth, block_paint);
				canvas.drawText("Z values (" + String.format("%.2f", GraphData.z) + ", min: " + String.format("%.2f", GraphData.min_z) + ", max: " + String.format("%.2f", GraphData.max_z) + ")", 20*dip, 6.75f*ninth, block_text);
				offset = ninth * 8f;
				num = 0;
				synchronized(GraphData.data_z)
				{
					canvas.drawLine(0f, offset - GraphData.max_z*(g_scale), view_width, offset - GraphData.max_z*(g_scale), extremes);
					canvas.drawLine(0f, offset - GraphData.min_z*(g_scale), view_width, offset - GraphData.min_z*(g_scale), extremes);
					ListIterator<Float> itr = GraphData.data_z.listIterator();
					while (itr.hasNext())
					{
						current = -itr.next();
						if (num != 0)
						{
							canvas.drawLine((num-1)*g_width, offset + prev*(g_scale), num*g_width, offset + current*(g_scale), z_paint);
						}
						num++;
						prev = current;
					}
				}
			}
	}
}
