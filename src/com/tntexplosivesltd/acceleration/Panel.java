package com.tntexplosivesltd.acceleration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

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
		// TODO Auto-generated method stub
		canvas_thread.set_running(true);
		canvas_thread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
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
		// Draw stuff in here
		Paint paint = new Paint();
		Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(logo, 10, 10, null);
	}

}
