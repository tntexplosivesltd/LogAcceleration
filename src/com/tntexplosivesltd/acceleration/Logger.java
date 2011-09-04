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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.os.Environment;
import android.util.Log;

/**
 * @brief Logger class to handle all logging to the microSD card.
 */
public class Logger {
	private boolean _logging = false;
	private boolean _can_log = false;
	private String _seperator = ",";
	private String _filename = "/LogAcceleration_";
	private String _ext = ".csv";
	private File _log_file;
	private File _root;
	private FileWriter _log_writer;
	private int _log_number = 0;
	
	private boolean _busy = false;

	/**
	 * @brief Sets the logging state of the app.
	 * @param logging Set whether the app is logging to file or not.
	 * @details This is used to disable/enable logging.
	 */
	public void set_logging(boolean logging)
	{
		_logging = logging;
	}
	
	/**
	 * @brief Retrieves the logging status of the app.
	 * @return Returns whether the app is logging to file or not.
	 * @details This is used to disable/enable logging.
	 */
	public boolean is_logging()
	{
		return _logging;
	}
	
	/**
	 * @brief Sets the busy state of the app.
	 * @param busy Set whether the logger is busy, i.e. has a scheduled task waiting.
	 * @details This is used to determine whether or not to start another scheduled logging task. 
	 */
	public void set_busy(boolean busy)
	{
		_busy = busy;
	}
	
	/**
	 * @brief Retrieves the busy status of the logger.
	 * @return Returns whether the logger is busy, i.e. has a scheduled task waiting.
	 * @details This is used to determine whether or not to start another scheduled logging task.
	 */
	public boolean is_busy()
	{
		return _busy;
	}
	
	/**
	 * @brief Sets the seperator character/string for the log entries.
	 * @param seperator The seperator to use between the x, y, and z values in the log.
	 */
	public void set_seperator(String seperator)
	{
		_seperator = seperator;
	}
	
	/**
	 * @brief Retrieves the seperator character/string for the log entries.
	 * @return Returns the seperator.
	 */
	public String get_seperator()
	{
		return _seperator;
	}

	/**
	 * @brief Initialises the logger.
	 * @return The result of the initialisation. This is a suitable value for a message telling the user what happened, good or bad.
	 * @details If logging is on, this is where the logger decides the filename for the log. If there are any errors, it automatically turns logging off.
	 */
	public String initialize()
	{
		if (_logging)
		{
			_root = Environment.getExternalStorageDirectory();
			if (_root.canWrite())
			{
				_log_file = new File(_root + _filename + _log_number + _ext);
				while (_log_file.exists())
				{
					_log_number++;
					_log_file = new File(_root + _filename + _log_number + _ext);
				}
				_can_log = true;
				return "Logging to " + _root + _filename + _log_number + _ext;
			}
			else
			{
				_logging = false;
				return "Can not write to SD card";
			}
		}
		else
		{
			return "Logging Disabled";
		}
	}
	
	/**
	 * @brief Writes log header to file.
	 * @return Returns whether or not the write succeeded.
	 * @details The header is just has the title for each column (Time (ms), X, Y, Z).
	 */
	public boolean log_header()
	{
		if (_logging)
		{
			if (_can_log)
			{
				try
				{
					_log_writer = new FileWriter(_root + _filename + _log_number + _ext, true);
					BufferedWriter out = new BufferedWriter(_log_writer);
					out.append("Time (ms)" + _seperator + "X" + _seperator + "Y" + _seperator + "Z" + "\n");
					out.close();
				}
				catch(IOException e)
				{
					Log.e("LogAggeleration", "Can not write data: " + e.getMessage());
					_logging = false;
					_can_log = false;
				}
			}
		}
		else
		{
			return false;
		}
		return true;
	}
	
	/**
	 * @brief Writes entry to file determined earlier.
	 * @param entry An array of 4 floats to write to the log file - [number,x,y,z].
	 * @return Returns whether or not the write succeeded.
	 * @details Automatically turns logging off it something goes wrong.
	 */
	public boolean log(float[] entry)
	{
		if (_logging)
		{
			if (_can_log)
			{
				if (entry.length != 4)
				{
					return false;
				}
				try
				{
					_log_writer = new FileWriter(_root + _filename + _log_number + _ext, true);
					BufferedWriter out = new BufferedWriter(_log_writer);
					out.append((int)entry[0] + _seperator + entry[1] + _seperator + entry[2] + _seperator + entry[3] + "\n");
					out.close();
				}
				catch(IOException e)
				{
					Log.e("LogAggeleration", "Can not write data: " + e.getMessage());
					_logging = false;
					_can_log = false;
				}
			}
		}
		else
		{
			return false;
		}
		return true;
	}
}