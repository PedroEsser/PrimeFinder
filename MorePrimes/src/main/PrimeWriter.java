package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import PrimeLists.PrimeList;

public class PrimeWriter {

	private final long upperLimit;
	private final long upperLimitSqrt;
	private final PrimeList finder;
	
	public PrimeWriter(long upperLimit) {
		this.upperLimit = upperLimit;
		this.upperLimitSqrt = (long)Math.sqrt(upperLimit);
		this.finder = new PrimeList(upperLimitSqrt);
	}
	
	public void writeToFile(String path) {
		try {
			PrintWriter writer = new PrintWriter(new File(path));
			for(long p : finder)
				writer.write(p + ", ");
			
			long currentIndex = upperLimitSqrt;
			long currentIndexMod6 = currentIndex % 6;
			currentIndex += currentIndexMod6 < 2 ? 1 - currentIndexMod6 : 5 - currentIndexMod6;
			
			int currentPercent = 0;
			int percentInc = 1;
			long incr = upperLimit * percentInc / 100;
			long next = incr;
			
			for(; currentIndex < upperLimit ; currentIndex += currentIndex % 6 == 1 ? 4 : 2)
				if(isBigPrime(currentIndex)) {
					writer.write(currentIndex + ", ");
					if(currentIndex >= next) {
						next += incr;
						currentPercent += percentInc;
						System.out.println(currentPercent + "% of the primes written.");
					}
				}
			
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isBigPrime(long x) {
		for(long p : finder)
			if(x % p == 0)
				return false;
		return true;
	}
	
}
