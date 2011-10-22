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
	private boolean _has_changed = false;
	private int _delay = 0;
	private int _log_number = 0;
	private String _ext = ".csv";
	private String _filename = "LogAcceleration_";
	private String _seperator = ",";
	
	private File _log_file;
	private File _root;
	private FileWriter _log_writer;
	private BufferedWriter out;
	
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
	 * @brief Retrieves whether or not the logging preferences have changed.
	 * @return Whether or not the logging preferences have changed.
	 */
	public boolean has_changed()
	{
		return _has_changed;
	}
	
	/**
	 * @brief Clears whether the preferences have been changed.
	 */
	public void clear_changed()
	{
		_has_changed = false;
	}
	
	/**
	 * @brief Sets the logging delay of this instance of Logger
	 * @param delay The delay between each entry (ms)
	 */
	public void set_delay(Integer delay)
	{
		if (delay != _delay)
		{
			_has_changed = true;
			_delay = delay;
		}
	}
	
	/**
	 * @brief Retrieves the logging delay of this instance of Logger
	 * @return The delay between each entry (ms)
	 */
	public int delay()
	{
		return _delay;
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
		if (!seperator.equals(_seperator))
		{
			_seperator = seperator;
			_has_changed = true;
		}
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
	 * @brief Flushes the output buffer.
	 * @return Whether or not the flush was successful.
	 * @details Should be called when the app is paused.
	 */
	public String flush()
	{
		try
		{
			if (out != null)
			{
				out.flush();
			}
			if (_log_writer != null)
			{
				_log_writer.flush();
			}
		}
		catch(IOException e)
		{
			Log.e("LogAggeleration", "Can not flush: " + e.getMessage());
			return "Could not flush buffer.";
		}
		return "Buffer Flushed.";
	}
	
	/**
	 * @brief Closes file writers, flushing them.
	 * @return Whether or not the closing was successful.
	 * @details Should be called when the app closes, and when logging is turned off.
	 */
	public String close_logs()
	{
		try
		{
			if (out != null)
			{
				out.close();
			}
			if (_log_writer != null)
			{
				_log_writer.close();
			}
		}
		catch(IOException e)
		{
			Log.e("LogAggeleration", "Can not flush: " + e.getMessage());
			return "Could not flush buffer.";
		}
		return "Buffer Flushed.";
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
			_root = new File(Environment.getExternalStorageDirectory() + "/LogAcceleration/");
			if (!_root.exists())
			{
				if (! _root.mkdir())
					return "Could not create directory for logs. Logging turned off";
			}
			
			if (_root.canWrite())
			{
				_log_file = new File(_root + "/" + _filename + _log_number + _ext);
				while (_log_file.exists())
				{
					_log_number++;
					_log_file = new File(_root + "/" + _filename + _log_number + _ext);
				}

				try
				{
					_log_writer = new FileWriter(_log_file, true);
					out = new BufferedWriter(_log_writer);
				}
				catch (IOException e)
				{
					Log.e("LogAggeleration", "Can not create filewriter: " + e.getMessage());
					_logging = false;
					return "Can not create file writer.";
				}
				_can_log = true;
				return "Logging to " + _root + "/" + _filename + _log_number + _ext;
			}
			else
			{
				_logging = false;
				return "Can not write to SD card. Logging is off.";
			}
		}
		else
		{
			return "Logging is disabled.";
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
					out.append("Time (ms)" + _seperator + "X" + _seperator + "Y" + _seperator + "Z" + "\n");
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
					out.append((int)entry[0] + _seperator + entry[1] + _seperator + entry[2] + _seperator + entry[3] + "\n");
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