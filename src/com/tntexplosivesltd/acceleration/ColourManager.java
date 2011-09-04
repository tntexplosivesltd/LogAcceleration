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
import android.graphics.Color;

/**
 * @brief Manages colour setting from the preferences, and gives colours for drawing.
 */
public class ColourManager {
	
	/**
	 * @brief Array of colours to use in drawing.
	 * @details Only built-in android colours.
	 */
	public static int[] colours = {Color.WHITE, Color.LTGRAY, Color.BLUE, Color.GRAY, Color.YELLOW, Color.BLACK, Color.RED, Color.GREEN, Color.BLUE};

	/**
	 * @brief Available colour palette.
	 * @details Might add to later, at the moment just basic colours,
	 */
	public static int[] palette = {Color.BLACK, Color.rgb(127, 127, 255), Color.BLUE,Color.rgb(0, 0, 127), Color.CYAN, Color.LTGRAY, Color.GRAY, Color.DKGRAY, Color.rgb(127,255,127), Color.GREEN, Color.rgb(0, 127, 0), Color.rgb(255, 127, 127), Color.RED, Color.rgb(127, 0, 0), Color.WHITE, Color.YELLOW};
	
	/**
	 * @brief Sets the colours to use for drawing.
	 * @details All colours are passes as integers, obtained through android.graphics.Color.
	 * @param background Background graph colour.
	 * @param boxes Colour of the text boxes.
	 * @param circle Colour of the circle on the XY-plane graph.
	 * @param grid Colour of the lines and their associated text.
	 * @param min_max Colour of the min/max lines.
	 * @param text Colour of the text in the text boxes.
	 * @param x Colour of the x-axis graph.
	 * @param y Colour of the y-axis graph.
	 * @param z Colour of the z-axis graph.
	 */
	public static void set_colours(int background, int boxes, int circle, int grid, int min_max, int text, int x, int y, int z)
	{
		colours[0] = background;
		colours[1] = boxes;
		colours[2] = circle;
		colours[3] = grid;
		colours[4] = min_max;
		colours[5] = text;
		colours[6] = x;
		colours[7] = y;
		colours[8] = z;
	}
}
