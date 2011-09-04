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

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * @brief Handles thread for drawing on the canvas.
 */
public class CanvasThread extends Thread {
	private SurfaceHolder _surface_holder;
	private Panel _panel;
	private boolean _run = false;
	
	/**
	 * @brief Constructor for the CanvasThread class.
	 * @param holder SurfaceHolder that we can get a canvas from.
	 * @param panel Instance of Panel class to pass the canvas to.
	 */
	public CanvasThread(SurfaceHolder holder, Panel panel)
	{
		_surface_holder = holder;
		_panel = panel;
	}
	
	/**
	 * @brief Sets whether the canvas drawing thread is running or not.
	 * @param run Whether the thread is running.
	 */
	public void set_running(boolean run)
	{
		_run = run;
	}
	
	/**
	 * @brief Retrieves whether or not the drawing thread is running.
	 * @return Set whether or not to draw the graphs on the canvas.
	 */
	public boolean is_running()
	{
		return _run;
	}
	
	/**
	 * @brief Override method that is used my the thread.
	 * @details What to do when the thread runs. While the thread is running, it draws on the canvas. Otherwise, it unlocks and tidies up.
	 */
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
