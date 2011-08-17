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
		canvas_thread = new CanvasThread(getHolder(), this);
		setFocusable(true);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		canvas_thread.set_running(true);
		canvas_thread.start();
		circle_paint.setColor(Color.BLUE);
		line_paint.setColor(Color.GRAY);
		x_paint.setColor(Color.RED);
		y_paint.setColor(Color.rgb(0, 127, 0));
		z_paint.setColor(Color.BLUE);
		extremes.setColor(Color.DKGRAY);
		x_paint.setStrokeWidth(graph_width);
		y_paint.setStrokeWidth(graph_width);
		z_paint.setStrokeWidth(graph_width);
		line_paint.setTextSize(20);
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
		float eighth = view_height / 8;
		float g_width = view_width / GraphData.max_data;
		
		canvas.drawColor(Color.WHITE);
		if (GraphData.show)
		{
			if (GraphData.mode == 0)
			{
				int lines = 20;
				for (int i = 0; i < lines; i++)
				{
					canvas.drawLine(i*view_width/lines, 0, i*view_width/lines, view_height, line_paint);
					canvas.drawLine(0, i*view_height/lines, view_width, i*view_height/lines, line_paint);
				}
				// We're drawing circles
				if (GraphData.orientation == 0)
				{
					canvas.drawCircle(view_width/2 - GraphData.x*10, view_height/2 + GraphData.y*10, circle_diameter*dip, circle_paint);
				}
				else
				{
					canvas.drawCircle(view_width/2 - GraphData.x*10, view_height/2 + GraphData.z*10, circle_diameter*dip, circle_paint);
				}
			}
			else
			{
				// We're drawing graphs
				
				// Draw gridlines
				int lines = 16;
				for (int i = 0; i < lines; i++)
				{
					canvas.drawLine(i*view_width/lines, 0, i*view_width/lines, view_height, line_paint);
					canvas.drawLine(0, i*view_height/lines, view_width, i*view_height/lines, line_paint);
				}

				// Draw x
				int num = 0;
				Float prev = 0f;
				Float current;
				float offset = eighth;
				canvas.drawText("X", 25, offset-10, line_paint);
				synchronized(GraphData.data_x)
				{
					ListIterator<Float> x_itr = GraphData.data_x.listIterator();
					while (x_itr.hasNext())
					{
						current = -x_itr.next();
						if (num != 0)
						{
							canvas.drawLine((num-1)*g_width, offset + prev*(eighth/20), num*g_width, offset + current*(eighth/20), x_paint);
						}
						num++;
						prev = current;
					}
				}
				canvas.drawLine(0f, offset - GraphData.max_x*(eighth/20), view_width, offset - GraphData.max_x*(eighth/20), extremes);
				canvas.drawLine(0f, offset - GraphData.min_x*(eighth/20), view_width, offset - GraphData.min_x*(eighth/20), extremes);
				
				//Draw y
				canvas.drawRect(0f, 2*eighth, view_width, 3f*eighth, line_paint);
				offset = eighth * 4;
				canvas.drawText("Y", 25, offset-10, line_paint);
				num = 0;
				synchronized(GraphData.data_y)
				{
					ListIterator<Float> itr = GraphData.data_y.listIterator();
					while (itr.hasNext())
					{
						current = -itr.next();
						if (num != 0)
						{
							canvas.drawLine((num-1)*g_width, offset + prev*(eighth/20), num*g_width, offset + current*(eighth/20), y_paint);
						}
						num++;
						prev = current;
					}
				}
				canvas.drawLine(0f, offset - GraphData.max_y*(eighth/20), view_width, offset - GraphData.max_y*(eighth/20), extremes);
				canvas.drawLine(0f, offset - GraphData.min_y*(eighth/20), view_width, offset - GraphData.min_y*(eighth/20), extremes);
				
				// Draw z
				canvas.drawRect(0f, 5*eighth, view_width, 6f*eighth, line_paint);
				offset = eighth * 7f;
				canvas.drawText("Z", 25, offset-10, line_paint);
				num = 0;
				synchronized(GraphData.data_z)
				{
					ListIterator<Float> itr = GraphData.data_z.listIterator();
					while (itr.hasNext())
					{
						current = -itr.next();
						if (num != 0)
						{
							canvas.drawLine((num-1)*g_width, offset + prev*(eighth/20), num*g_width, offset + current*(eighth/20), z_paint);
						}
						num++;
						prev = current;
					}
				}
				canvas.drawLine(0f, offset - GraphData.max_z*(eighth/20), view_width, offset - GraphData.max_z*(eighth/20), extremes);
				canvas.drawLine(0f, offset - GraphData.min_z*(eighth/20), view_width, offset - GraphData.min_z*(eighth/20), extremes);
			}
		}
	}
}
