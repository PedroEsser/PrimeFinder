package PrimeLists;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import utils.BitGrid;
import utils.MathUtils;
import utils.TwoWayHashmap;

public class BigPrimeList implements Iterable<Long>{

	private BitGrid flags;
	private PrimeList primesUpToSQRT;
	private TwoWayHashmap<Integer, Integer> rrs;
	private final int magicNumber;
	private int primesForMagicNumber;
	private final long upperbound;
	
	public BigPrimeList(long upperbound) {
		this(upperbound, 6);
	}
	
	public BigPrimeList(long upperBound, int primesForMagicNumber) {
		long timestamp = System.currentTimeMillis();
		this.upperbound = upperBound;
		this.primesForMagicNumber = primesForMagicNumber;
		primesUpToSQRT = new PrimeList((long)Math.sqrt(upperBound));
		magicNumber = calculateMagicNumber(primesForMagicNumber);
		calculateReducedResidueSystem();
		System.out.println("Magic number: " + magicNumber);
		int gridHeight = (int)Math.ceil((double)upperBound / magicNumber);
		flags = new BitGrid(rrs.size(), gridHeight);
		sieveAll(0);
		
		
		double seconds = (double)(System.currentTimeMillis() - timestamp) / 1000;
		System.out.println("Primes up to " + upperBound + " calculated, total time ellapsed: " + seconds + " seconds");
	}
	
	private void sieveAll(int nWorkers) {
		/*PrimeBlockingQueue queue = new PrimeBlockingQueue(primesUpToSQRT, primesForMagicNumber);
		
		Sieve[] sieves = new Sieve[nWorkers];
		for(int i = 0 ; i < nWorkers ; i++) {
			sieves[i] = new Sieve(queue, upperbound, magicNumber, rrs, flags);
			sieves[i].start();
		}
		
		for(Sieve s : sieves)
			try {
				s.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		*/
		final long nPrimes = primesUpToSQRT.getNumberOfPrimes();
		long nPrimesForFeedback = 1;//(long)(Math.pow(nPrimes, .1));
		
		
		long i = 0;
		long j = 0;
		long currentPrime = primesUpToSQRT.getNthPrime(primesForMagicNumber);
		for(; currentPrime != -1 ; currentPrime = primesUpToSQRT.getNextPrime(currentPrime)) {
			sieve2(currentPrime);
			i++;
			if(j++ == nPrimesForFeedback) {
				j = 0;
				//nPrimesForFeedback = (long)(nPrimesForFeedback * 1.5);
				nPrimesForFeedback++;
				System.out.println(String.format("%.2f", 100d * i / nPrimes) + "%");
				
			}
		}
		flags.setBit(0, 0, true);
	}
	
	private void sieve(long currentPrime) {
		long primeMultiple = currentPrime * currentPrime;
		long inc = currentPrime << 1;
		
		int currentMultiple = (int)(primeMultiple / magicNumber);
		int currentMod = (int)(primeMultiple % magicNumber);
		int modInc = (int)(inc % magicNumber);
		int multipleInc = (int)(inc / magicNumber);
		
		for( ; primeMultiple <= upperbound ; primeMultiple += inc) {
			if(rrs.containsKey(currentMod))
				//sieve(primeMultiple);
				flags.setBit(rrs.getValue(currentMod),  currentMultiple, true);
			currentMod += modInc;
			currentMultiple += multipleInc;
			if(currentMod >= magicNumber) {
				currentMod -= magicNumber;
				currentMultiple++;
			}
		}
	}
	
	private void sieve2(long currentPrime) {
		long current = currentPrime * currentPrime;
		Point c = getPointFor(currentPrime);
		int x = c.x;
		int y = c.y;
		
		while(current < upperbound) {
			Point p = getPointFor(current);
			flags.setBit(p, true);
			if(++x == flags.getWidth()) {
				x = 0;
				y++;
			}
			current = currentPrime * getValueAt(x, y);
		}
	}
	
	private void sieve3(long currentPrime) {
		long current = currentPrime * currentPrime;
		Point c = getPointFor(currentPrime);
		int x = c.x;
		int y = c.y;
		
		while(current < upperbound) {
			Point p = getPointFor(current);
			flags.setBit(p, true);
			if(++x == flags.getWidth()) {
				x = 0;
				y++;
			}
			current = currentPrime * getValueAt(x, y);
		}
	}
	
	public Point getPointFor(long n) {
		int i = (int)(n % magicNumber);
		if(rrs.containsKey(i)) {
			i = rrs.getValue(i);
			int j = (int)(n / magicNumber);
			return new Point(i, j);
		}
		return null;
	}
	
	private long getValueAt(Point p) {
		return getValueAt(p.x, p.y);
	}
	
	private long getValueAt(int i, int j) {
		return (long)j * magicNumber + rrs.getKey(i);
	}
	
	public void printAllPrimes() {
		for(int j = 0 ; j < flags.getHeight() ; j++)
			for(int i = 0 ; i < flags.getWidth() ; i++)
				if(!flags.getBit(i, j)) {
					long value = getValueAt(i, j);
					if(value <= upperbound)
						System.out.println(value);
				}
	}
	
	public int getNumberOfPrimes() {
		int count = primesForMagicNumber;
		
		for(int j = 0 ; j < flags.getHeight() ; j++)
			for(int i = 0 ; i < flags.getWidth() ; i++)
				if(!flags.getBit(i, j) && getValueAt(i, j) <= upperbound)
					count++;
		
		return count;
	}
	
	public long getNumberOfConsideredNumbers() {
		return (long)flags.getHeight() * flags.getWidth();
	}
	
	public double getRatioOfConsideredNumbers() {
		return (double)getNumberOfConsideredNumbers() / upperbound;
	}
	
	private int calculateMagicNumber(int numberOfPrimesForMagicNumber) {
		int magicN = 1;
		long current = 1;
		for(int i = 0 ; i < numberOfPrimesForMagicNumber ; i++) {
			current = primesUpToSQRT.getNextPrime(current);
			if(current == -1) {
				this.primesForMagicNumber = i + 1;
				return magicN;
			}
			magicN *= current;
		}
		
		return magicN;
	}
	
	private void calculateReducedResidueSystem() {
		rrs = new TwoWayHashmap<>();
		int counter = 0;
		for(int i = 1 ; i < magicNumber ; i++)
			if(MathUtils.areCoPrime(i, magicNumber)) 
				rrs.put(i, counter++);
				
	}
	
	public long getNthPrime(long index) {
		if(index < primesForMagicNumber)
			return primesUpToSQRT.getNthPrime(index);
		long count = primesForMagicNumber;
		for(int j = 0 ; j < flags.getHeight() ; j ++)
			for(int i = 0 ; i < flags.getWidth() ; i ++) 
				if(!flags.getBit(i, j))
					if(count++ == index) return getValueAt(i, j);
		
		return -1;
	}
	
	public long getLastNthPrime(long index) {
		int count = 0;
		for(int j = flags.getHeight() - 1 ; j >= 0 ; j --)
			for(int i = flags.getWidth() - 1 ; i >= 0 ; i --) 
				if(!flags.getBit(i, j) && getValueAt(i, j) <= upperbound)
					if(count++ == index) return getValueAt(i, j);
		
		if(index - count >= primesForMagicNumber)
			return -1;
		return primesUpToSQRT.getNthPrime(index - count);
	}
	
	public long getNextPrime(long n) {
		n++;
		if(n > upperbound)
			return -1;
		
		int nModMN = (int)(n % magicNumber);
		int y = (int)(n / magicNumber);
		Point p = null;
		for(int i = nModMN ; i < magicNumber ; i++)
			if(rrs.containsKey(i)) {
				p = new Point(rrs.getValue(i), y);
				break;
			}
				
		if(p == null)
			p = new Point(0, y+1);
		
		for(int j = p.y ; j < flags.getHeight() ; j ++)
			for(int i = j == p.y ? p.x : 0 ; i < flags.getWidth() ; i ++) 
				if(!flags.getBit(i, j)) {
					long value = getValueAt(i, j);
					if(value <= upperbound)
						return value;
				}
		
		return -1;
	}
	
	@Override
	public Iterator<Long> iterator() {
		return new MyIterator();
	}
	
	private class MyIterator implements Iterator<Long>{

		Point currentPoint = new Point(-primesForMagicNumber, 0);
		final Point lastPoint = getPointFor(getLastNthPrime(0));
		
		private void nextPoint() {
			int currentY = currentPoint.y;
			for(; currentPoint.y < flags.getHeight() ; currentPoint.y ++)
				for(currentPoint.x = currentY == currentPoint.y ? currentPoint.x + 1 : 0 ; currentPoint.x < flags.getWidth() ; currentPoint.x ++) 
					if(!flags.getBit(currentPoint)) return;
		}
		
		@Override
		public boolean hasNext() {
			return !currentPoint.equals(lastPoint);
		}

		@Override
		public Long next() {
			if(currentPoint.x < 0)
				return primesUpToSQRT.getNthPrime(primesForMagicNumber + currentPoint.x++);
			
			nextPoint();
			return getValueAt(currentPoint);
		}
		
	}
	
}
