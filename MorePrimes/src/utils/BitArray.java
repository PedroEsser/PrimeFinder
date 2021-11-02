package utils;

import java.math.BigInteger;
import java.util.Iterator;

public class BitArray implements Iterable<Boolean> {

	private static final byte SHIFT = 6;
	private static final int BITS_PER_LONG = 64;
	private static final byte AND_MASK = BITS_PER_LONG - 1;
	private final long size;
	private final long[] bits;

	public BitArray(long size) {
		this.size = size;
		long dataLength = (size & AND_MASK) == 0 ? size >> SHIFT : (size >> SHIFT) + 1;	//ceiling
		if(dataLength > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Size:" + size + " is too big, max size:" + ((long)Integer.MAX_VALUE << SHIFT));
		bits = new long[(int)dataLength];
	}

	public boolean getBit(long pos) {
		if (!validPosition(pos))
			throw new ArrayIndexOutOfBoundsException("Index " + pos + " out of bounds for length " + size);
		return (bits[(int)(pos >> SHIFT)] & ((long)1 << (pos & AND_MASK))) != 0;
	}

	public void setBit(long pos, boolean b) {
		if (!validPosition(pos))
			throw new ArrayIndexOutOfBoundsException("Index " + pos + " out of bounds for length " + size);
		long word = bits[(int)(pos >> SHIFT)];
		long posBit = (long)1 << (pos & AND_MASK);
		if (b)
			word |= posBit;
		else
			word &= ~posBit;
		bits[(int)(pos >> SHIFT)] = word;
	}
	
	public void not() {
		for(int i = 0 ; i < bits.length ; i++)
			bits[i] = ~bits[i];
	}

	public boolean validPosition(long position) {
		return position >= 0 && position < size;
	}

	public long size() {
		return size;
	}

	public BigInteger getValue() {
		BigInteger result = BigInteger.ZERO;
		for (int i = bits.length - 1; i >= 0; i--) {
			result = result.shiftLeft(BITS_PER_LONG);
			result = result.add(new BigInteger(bits[i] + ""));
		}
		return result;
	}
	
	@Override
	public String toString() {
		String s = "";
		for(int i = 0 ; i < bits.length - 1 ; i++)
			s = String.format("%64s", 
					Long.toBinaryString(bits[i])).replaceAll(" ", "0") + s;
		s = String.format("%64s", 
				Long.toBinaryString(bits[bits.length-1])).replaceAll(" ", "0").substring((int)(BITS_PER_LONG - size & AND_MASK)) + s;
		return s;
	}

	@Override
	public Iterator<Boolean> iterator() {
		return new BitIterator();
	}

	private class BitIterator implements Iterator<Boolean> {

		private int current;

		@Override
		public boolean hasNext() {
			return current != size;
		}

		@Override
		public Boolean next() {
			return getBit(current++);
		}

	}

}
