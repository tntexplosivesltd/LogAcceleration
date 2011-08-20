package com.tntexplosivesltd.acceleration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.os.Environment;
import android.util.Log;

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
	
	public boolean busy = false;

	
	public void set_logging(boolean logging)
	{
		_logging = logging;
	}
	
	public boolean is_logging()
	{
		return _logging;
	}
	
	public void set_seperator(String seperator)
	{
		_seperator = seperator;
	}
	
	public String get_seperator()
	{
		return _seperator;
	}

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
					GraphData.logged_values = (int)entry[0];
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