package com.gzsr.math;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.misc.Pair;

public class Calculate {
	/**
	 * Calculates the hypotenuse of the triangle between two points.
	 * @param src The source position.
	 * @param target The target position.
	 * @return The theta value representing the hypotenuse between the two points.
	 */
	public static float Hypotenuse(Pair<Float> src, Pair<Float> target) {
		return (float)Math.atan2((target.y - src.y), (target.x - src.x));
	}
	
	/**
	 * Calculates the distance between two points.
	 * @param src The source position.
	 * @param target The target position.
	 * @return The distance between the two points.
	 */
	public static float Distance(Pair<Float> src, Pair<Float> target) {
		float xD = target.x - src.x;
		float yD = target.y - src.y;
		return (float)Math.sqrt((xD * xD) + (yD * yD));
	}
	
	public static Pair<Float> rotateAboutPoint(Pair<Float> pivot, Pair<Float> point, float theta) {
		float x = (pivot.x + ((point.x - pivot.x) * (float)Math.cos(theta)) - ((point.y - pivot.y) * (float)Math.sin(theta)));
		float y = (pivot.y + ((point.x - pivot.x) * (float)Math.sin(theta)) + ((point.y - pivot.y) * (float)Math.cos(theta)));
		
		return new Pair<Float>(x, y);
	}
	
	/**
	 * Draws the given text using the given font to the graphics context and wraps
	 * the text according to the specified max width.
	 * @param g The graphics context to draw the text on.
	 * @param text The text to draw on the screen.
	 * @param font The font to render the text with.
	 * @param x The x-coordinate at which to draw the text.
	 * @param y The y-coordinate at which to draw the text.
	 * @param maxWidth The max width for the line before text wraps.
	 * @param center Whether or not to draw the text centered.
	 */
	public static void TextWrap(Graphics g, String text, Font font, float x, float y, float maxWidth, boolean center) {
		TextWrap(g, text, font, x, y, maxWidth, center, Color.white);
	}
	
	/**
	 * Draws the given text using the given font to the graphics context and wraps
	 * the text according to the specified max width. Draws the text with the specified color.
	 * @param g The graphics context to draw the text on.
	 * @param text The text to draw on the screen.
	 * @param font The font to render the text with.
	 * @param x The top-left x-coordinate at which to draw the text.
	 * @param y The top-left y-coordinate at which to draw the text.
	 * @param maxWidth The max width for the line before text wraps.
	 * @param center Whether or not to draw the text centered.
	 * @param color The color to use when rendering the text.
	 */
	public static void TextWrap(Graphics g, String text, Font font, float x, float y, float maxWidth, boolean center, Color color) {
		float charWidth = font.getWidth("A"); // How wide are characters in this font?
		
		g.setColor(color);
		g.setFont(font);
		
		int i = 0; // The current beginning index of the line substring from the text.
		int line = 0; // The current line - 1.
		int charsPerLine = (int)(maxWidth / charWidth); // How many characters can fit on each line?
		while(i < (text.length() - 1)) {
			// Choose which characters will be drawn on this line.
			String substr = "";
			
			int nextBreak = Calculate.nextSpace(text, i, Math.min((i + charsPerLine), (text.length() - 1)));
			int charsToGrab = charsPerLine;
			if((nextBreak != -1) && (nextBreak - i) < charsPerLine) {
				charsToGrab = (nextBreak - i);
			}
			
			if((i + charsToGrab) <= text.length()) {
				substr = text.substring(i, (i + charsToGrab));
			} else {
				substr = text.substring(i);
			}
			substr = substr.trim(); // Trim leading and trailing whitespace to avoid ruining text centering.
			
			// Get the y-coordinate to draw this line on.
			float cy = y + (line * font.getLineHeight());
			
			if(center) FontUtils.drawCenter(font, substr, (int)x, (int)cy, (int)maxWidth, color);
			else g.drawString(substr, x, cy);
			
			i += charsToGrab;
			line++;
		}
	}
	
	private static int nextSpace(String text, int start, int end) {
		int next = -1;
		
		if(end == (text.length() - 1)) {
			return text.length();
		}
		
		for(int c = end; c >= start; c--) {
			if(text.charAt(c) == ' ') {
				next = c;
				break;
			}
		}
		
		return next;
	}
}
