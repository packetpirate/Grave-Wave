package com.grave.gfx;

import org.newdawn.slick.Color;

import com.grave.Globals;

public class ColorGenerator {
	private Color start, end;
	
	public ColorGenerator(Color start_, Color end_) {
		this.start = start_;
		this.end = end_;
	}
	
	public Color generate() {
		int hA = Math.max(start.getAlpha(), end.getAlpha());
		int lA = (start.getAlpha() + end.getAlpha()) - hA;
		int dA = ((hA - lA) > 0) ? (Globals.rand.nextInt(hA - lA) + lA) : lA;
		
		int hR = Math.max(start.getRed(), end.getRed());
		int lR = (start.getRed() + end.getRed()) - hR;
		int dR = ((hR - lR) > 0) ? (Globals.rand.nextInt(hR - lR) + lR) : lR;
		
		int hG = Math.max(start.getGreen(), end.getGreen());
		int lG = (start.getGreen() + end.getGreen()) - hG;
		int dG = ((hG - lG) > 0) ? (Globals.rand.nextInt(hG - lG) + lG) : lG;
		
		int hB = Math.max(start.getBlue(), end.getBlue());
		int lB = (start.getBlue() + end.getBlue()) - hB;
		int dB = ((hB - lG) > 0) ? (Globals.rand.nextInt(hB - lB) + lB) : lG;
		
		return new Color(dR, dG, dB, dA);
	}
}
