package com.tntexplosivesltd.acceleration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

	float dip = getContext().getResources().getDisplayMetrics().density;
	float width = getContext().getResources().getDisplayMetrics().widthPixels;
	CanvasThread canvas_thread;
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
		float view_height = canvas.getHeight();
		float view_width = canvas.getWidth(); 
		// Draw stuff in here
		Paint circle_paint = new Paint();
		circle_paint.setColor(Color.RED);
		Paint line_paint = new Paint();
		line_paint.setColor(Color.GRAY);
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
					canvas.drawCircle(view_width/2 + -GraphData.x * 10 * dip, view_height/2 + GraphData.y * 10 * dip, 10 * dip, circle_paint);
				}
				else
				{
					canvas.drawCircle(view_width/2 + -GraphData.x * 10 * dip, view_height/2 + GraphData.z * 10 * dip, 10 * dip, circle_paint);
				}
			}
			else
			{
				int lines = 16;
				for (int i = 0; i < lines; i++)
				{
					canvas.drawLine(i*view_width/lines, 0, i*view_width/lines, view_height, line_paint);
					canvas.drawLine(0, i*view_height/lines, view_width, i*view_height/lines, line_paint);
				}
				// We're drawing graphs
				
			}
		}
	}

}
