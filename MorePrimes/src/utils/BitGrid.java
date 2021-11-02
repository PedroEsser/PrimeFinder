package utils;

import java.awt.Point;

public class BitGrid {

	private final BitArray bitArray;
	private final int width;
	private final int height;

	public BitGrid(int width, int height) {
		this.width = width;
		this.height = height;
		long nBits = (long)width * height;
		bitArray = new BitArray(nBits);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public boolean getBit(int x, int y) {
		return bitArray.getBit(convertToPos(x, y));
	}

	public void setBit(int x, int y, boolean b) {
		bitArray.setBit(convertToPos(x, y), b);
	}
	
	public boolean getBit(Point p) {
		return bitArray.getBit(convertToPos(p));
	}

	public void setBit(Point p, boolean b) {
		bitArray.setBit(convertToPos(p), b);
	}
	
	public BitArray getBitArray() {
		return bitArray;
	}

	public Point convertToPoint(int pos) {
		return new Point(pos % width, pos / width);
	}

	public long convertToPos(int x, int y) {
		return x + (long)y * width;
	}
	
	public long convertToPos(Point p) {
		return p.x + (long)p.y * width;
	}
}
