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
import java.util.LinkedList;

/**
 * @brief Helper class of static values for sharing between other classes
 */
public class GraphData {
	
	/**
	 * @brief Current X-acceleration value
	 */
	public static float x;
	
	/**
	 * @brief Current Y-acceleration value
	 */
	public static float y;
	
	/**
	 * @brief Current Z-acceleration value
	 */
	public static float z;
	
	/**
	 * @brief Minimum X-acceleration value seen so far
	 */
	public static float min_x = 20.f;
	
	/**
	 * @brief Minimum Y-acceleration value seen so far
	 */
	public static float min_y = 20.f;
	
	/**
	 * @brief Minimum X-acceleration value seen so far
	 */
	public static float min_z = 20.f;
	
	/**
	 * @brief Maximum X-acceleration value seen so far
	 */
	public static float max_x = -20.f;
	
	/**
	 * @brief Maximum Y-acceleration value seen so far
	 */
	public static float max_y = -20.f;
	
	/**
	 * @brief Maximum Z-acceleration value seen so far
	 */
	public static float max_z = -20.f;
	
	/**
	 * @brief Maximum number of data points to record for graph
	 */
	public static int max_data = 100;
	
	/**
	 * @brief What mode the graph drawing is in. Might add more
	 * @details 0 = circle, 1 = graphs
	 * */
	public static int mode = 0;
	
	/**
	 * @brief Phone orientation. Might add more
	 * @details 0 = phone is lying flat, 1 = phone upright
	 */
	public static int orientation = 0;
	
	/**
	 * @brief List of latest [max data] X-acceleration data points
	 */
	public static  LinkedList<Float> data_x = new LinkedList<Float>();
	
	/**
	 * @brief List of latest [max data] Y-acceleration data points
	 */
	public static  LinkedList<Float> data_y = new LinkedList<Float>();
	
	/**
	 * @brief List of latest [max data] Z-acceleration data points
	 */
	public static  LinkedList<Float> data_z = new LinkedList<Float>();
	
	/**
	 * @brief Resets all maximum and minimum values
	 * @details Maximums go to -20, minimums go to 20
	 */
	public static void reset()
	{
		min_x = 20.f; 
		min_y = 20.f; 
		min_z = 20.f; 
		max_x = -20.f; 
		max_y = -20.f; 
		max_z = -20.f;
	}
}
