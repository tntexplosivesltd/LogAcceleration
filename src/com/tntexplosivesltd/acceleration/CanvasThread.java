package com.tntexplosivesltd.acceleration;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class CanvasThread extends Thread {
	private SurfaceHolder _surface_holder;
	private Panel _panel;
	private boolean _run = false;
	
	public CanvasThread(SurfaceHolder holder, Panel panel)
	{
		_surface_holder = holder;
		_panel = panel;
	}
	
	public void set_running(boolean run)
	{
		_run = run;
	}
	
	public boolean is_running()
	{
		return _run;
	}
	
	@Override
	public void run()
	{
		Canvas c;
		while (_run)
		{
			c = null;
			try
			{
				c = _surface_holder.lockCanvas(null);
				synchronized (_surface_holder)
				{
					_panel.onDraw(c);
				}
			}
			finally
			{
				if (c != null)
				{
					_surface_holder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}
