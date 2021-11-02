package PrimeLists;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import utils.BitArray;
import utils.MathUtils;

public class PrimeList implements Iterable<Long>{

	private final BitArray flags;
	
	public PrimeList(long upperBound) {
		flags = new BitArray(upperBound+1);
		long timestamp = System.currentTimeMillis();
		primesUpTo(upperBound);
		double seconds = (double)(System.currentTimeMillis() - timestamp) / 1000;
		System.out.println("Primes up to " + upperBound + " calculated, total time ellapsed: " + seconds + " seconds");
	}
	
	private void primesUpTo(long upperBound) {
		long sqrt = (long)Math.sqrt(upperBound);
		for(long i = 5 ; i <= sqrt ; i += i%6 == 5 ? 2 : 4) 
			if(!flags.getBit(i)) 
				for(long j = i*i ; j <= upperBound ; j += i << 1) 
					flags.setBit(j, true);
	}
	
	public ArrayList<Long> getAllPrimes(){
		ArrayList<Long> list = new ArrayList<>();
		list.add((long)2);
		list.add((long)3);
		
		for(long i = 5 ; i <= flags.size() ; i += i%6 == 5 ? 2 : 4)
			if(!flags.getBit(i))
				list.add(i);
		return list;
	}
	
	public long getNthPrime(long index) {
		if(index == 0) return 2;
		if(index == 1) return 3;
		long count = 2;
		for(long i = 5 ; i <= flags.size() ; i += i%6 == 5 ? 2 : 4)
			if(!flags.getBit(i)) 
				if(count++ == index) return i;
		
		return -1;
	}
	
	public long getLastNthPrime(long index) {
		long count = 0;
		long i = flags.size() - 1;
		
		for(i = MathUtils.myFloor(i) ; i > 3 ; i -= i%6 == 1 ? 2 : 4)
			if(!flags.getBit(i)) 
				if(count++ == index) return i;
		
		index -= count;
		if(index == 0) return 3;
		if(index == 1) return 2;
		return -1;
	}
	
	public long getNextPrime(long n) {
		if(n < 2) return 2;
		if(n == 2) return 3;
		for(long i = MathUtils.myCeiling(n+1); i < flags.size() ; i += i%6 == 5 ? 2 : 4)
			if(!flags.getBit(i)) 
				return i;
		return -1;
	}
	
	public long getNumberOfPrimes() {
		long count = 2;
		for(long i = 5 ; i < flags.size() ; i += i%6 == 5 ? 2 : 4)
			if(!flags.getBit(i))
				count ++;
		return count;
	}
	
	public boolean isPrime(long x) {
		if(x < 2) return false;
		if(x < 4) return true;
		if(x % 2 == 0 || x % 3 == 0) return false;
		return !flags.getBit(x);
	}
	
	public void writeToFile(String path) {
		try {
			PrintWriter writer = new PrintWriter(new File(path));
			int currentPercent = 0;
			int percentInc = 1;
			long incr = flags.size() * percentInc / 100;
			long next = incr;
			for(long p : this) {
				writer.write(p + ", ");
				if(p >= next) {
					next += incr;
					currentPercent += percentInc;
					System.out.println(currentPercent + "% of the primes written.");
				}
			}
			writer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isPrimeStatic(long x) {
		if(x < 2) return false;									//no primes below 2
		if(x < 4) return true;									//2 and 3 prime
		if(x % 2 == 0 || x % 3 == 0) return false;
		
		int sqrt = (int)(Math.sqrt((double)x));
		for(long i = 5 ; i <= sqrt ; i += i%6 == 5 ? 2 : 4) 	//every prime after 3 is either 1 or 5 (mod 6)
			if(x % i == 0)			
				return false;
		
		return true;
	}

	@Override
	public Iterator<Long> iterator() {
		return new MyIterator();
	}
	
	private class MyIterator implements Iterator<Long>{

		long currentPrime = 1;
		final long lastPrime = getLastNthPrime(0);
		
		@Override
		public boolean hasNext() {
			return currentPrime != lastPrime;
		}

		@Override
		public Long next() {
			currentPrime = getNextPrime(currentPrime);
			return currentPrime;
		}
		
	}
	
}
