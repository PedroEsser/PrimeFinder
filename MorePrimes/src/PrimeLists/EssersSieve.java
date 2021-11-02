package PrimeLists;

import java.awt.image.RescaleOp;
import java.util.Comparator;
import java.util.Map.Entry;

import utils.BitArray;
import utils.BitGrid;
import utils.MathUtils;
import utils.TwoWayHashmap;

public class EssersSieve {

	private final BitGrid flags;
	private final TwoWayHashmap<Integer, Integer> reducedResidueSystem;
	private final PrimeList firstNPrimes;
	private final int Pn;
	private final int Tn;
	private int primesForPn;
	private final long upperbound;
	private final Index2D[][] magicMatrix;
	
	public EssersSieve(long upperbound) {
		this(upperbound, 4);
	}
	
	public EssersSieve(long upperBound, int primesForPn) {
		long timestamp = System.currentTimeMillis();
		
		this.upperbound = upperBound;
		this.primesForPn = primesForPn;
		this.firstNPrimes = new PrimeList(upperBoundForNthPrime(primesForPn));
		
		this.Pn = calculatePn();
		this.Tn = totientOfPn();
		this.reducedResidueSystem = calculateReducedResidueSystem();
		this.magicMatrix = calculateMagicMatrix();
		int gridHeight = (int)Math.ceil((double)upperBound / Pn);
		this.flags = new BitGrid(Tn, gridHeight);
		
		sieveAll();
		
		double seconds = (double)(System.currentTimeMillis() - timestamp) / 1000;
		System.out.println("Primes up to " + upperBound + " calculated, total time ellapsed: " + seconds + " seconds");
	}
	
	
	
	private void sieveAll() {
		Index2D lastIndex = getLastIndexToSieve();
		Index2D currentIndex = new Index2D(1, 0);
		int nIndicesToCheck = lastIndex.index1D();
		int feedBackCounter = 1;
		int counter = 0;
		while(currentIndex.isSmaller(lastIndex)) {
			
			if(!flags.getBit(currentIndex.x, currentIndex.y)) {
				sieveMultiples(currentIndex);
				if(++counter == feedBackCounter) {
					System.out.println(String.format("%.2f", 100d * currentIndex.index1D()/nIndicesToCheck) + "%");
					feedBackCounter++;
					counter = 0;
				}
			}
			
			currentIndex.next();
		}
	}
	
	private void sieveMultiples(Index2D index) {
		int i = index.y;
		int j = index.x;
		int p = (int)getValueAt(j, i);
		for(int l = 0 ; l < Tn ; l++) {
			int q = i * reducedResidueSystem.getKey(l) + magicMatrix[j][l].y;
			sieveCollum(magicMatrix[j][l].x, (l == 0 ? p : 0) + q, p);
		}
	}
	
	private void sieveCollum(int collumnStart, int rowStart, int rowInc) {
		int actualInc = Tn * rowInc;
		long arrayIndex = flags.convertToPos(collumnStart, rowStart);
		BitArray array = flags.getBitArray();
		while(arrayIndex < array.size() && arrayIndex > 0) {
			array.setBit(arrayIndex, true);
			arrayIndex += actualInc;
		}
	}
	
	private Index2D getLastIndexToSieve() {
		int upperBoundSQRT = (int)Math.sqrt(upperbound);
		return nextIndexAfter(upperBoundSQRT);
		
	}
	
	private Index2D nextIndexAfter(long x) {
		int y = (int)(x / Pn);
		int mod = (int)(x % Pn);
		for(int i = 0 ; i < Tn ; i++) {
			if(reducedResidueSystem.getKey(i) > mod)
				return new Index2D(i, y);
		}
		return new Index2D(Tn, y);
	}
	
	
	
	public static long upperBoundForNthPrime(long x) {				//https://math.stackexchange.com/questions/1270814/bounds-for-n-th-prime
		return (long)(5 + x*(Math.log(x) + Math.log(Math.log(x))));
	}
	
	
	
	public Index2D getIndex2DFor(long n) {
		int i = (int)(n % Pn);
		if(reducedResidueSystem.containsKey(i)) {
			i = reducedResidueSystem.getValue(i);
			int j = (int)(n / Pn);
			return new Index2D(i, j);
		}
		return null;
	}
	
	private long getValueAt(Index2D i) {
		return getValueAt(i.x, i.y);
	}
	
	private long getValueAt(int i, int j) {
		return (long)j * Pn + reducedResidueSystem.getKey(i);
	}
	
	
	
	public void printAllPrimes() {
		Index2D currentIndex = new Index2D(1, 0);
		Index2D lastIndex = nextIndexAfter(upperbound);
		while(currentIndex.isSmaller(lastIndex)) {
			if(!flags.getBit(currentIndex.x, currentIndex.y)){
				long value = getValueAt(currentIndex);
				if(value <= upperbound)
					System.out.println(value);
			}
			currentIndex.next();
		}
	}
	
	public long getNumberOfPrimes() {
		long count = primesForPn;
		
		Index2D currentIndex = new Index2D(1, 0);
		Index2D lastIndex = nextIndexAfter(upperbound);
		while(currentIndex.isSmaller(lastIndex)) {
			if(!flags.getBit(currentIndex.x, currentIndex.y)) 
				count++;
			
			currentIndex.next();
		}
		return count;
	}
	
	public long getNumberOfConsideredNumbers() {
		return (long)flags.getHeight() * flags.getWidth();
	}
	
	public double getRatioOfConsideredNumbers() {
		return (double)getNumberOfConsideredNumbers() / upperbound;
	}
	
	private int calculatePn() {
		int Pn = 1;
		long current = 1;
		for(int i = 0 ; i < primesForPn ; i++) {
			current = firstNPrimes.getNextPrime(current);
			if(current == -1) {
				this.primesForPn = i + 1;
				return Pn;
			}
			Pn *= current;
		}
		
		return Pn;
	}
	
	private int totientOfPn() {
		int acc = 1;
		long currentPrime = 1;
		for(int i = 0 ; i < primesForPn ; i++) {
			currentPrime = firstNPrimes.getNextPrime(currentPrime);
			acc *= (currentPrime - 1);
		}
		return acc;
	}
	
	private TwoWayHashmap<Integer, Integer> calculateReducedResidueSystem() {
		TwoWayHashmap<Integer, Integer> reducedResidueSystem = new TwoWayHashmap<>();
		int counter = 0;
		for(int i = 1 ; i < Pn/2 ; i++)
			if(MathUtils.areCoPrime(i, Pn)) {
				reducedResidueSystem.put(Pn - i, Tn - counter - 1);
				reducedResidueSystem.put(i, counter++);
			}
		assert reducedResidueSystem.size() == Tn : "Size of Reduced Residue System != totient(Pn)";
		return reducedResidueSystem;
	}
	
	private Index2D[][] calculateMagicMatrix(){
		Index2D[][] m = new Index2D[Tn][Tn];
		
		for(int i = 0 ; i < m.length ; i++) {
			for(int j = i ; j < m.length ; j++) {
				Index2D product = getIndex2DFor(reducedResidueSystem.getKey(i) * reducedResidueSystem.getKey(j));
				m[i][j] = product;
				m[j][i] = product;
			}
		}
		
		return m;
	}
	
	public long getNthPrime(long index) {
		if(index < primesForPn)
			return firstNPrimes.getNthPrime(index);
		long count = primesForPn;
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
		
		if(index - count >= primesForPn)
			return -1;
		return firstNPrimes.getNthPrime(index - count);
	}
	
	public class Index2D{
		
		private int x, y;
		
		public Index2D(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public boolean isSmaller(Index2D i) {
			if(this.y < i.y)
				return true;
			if(this.y == i.y)
				return this.x < i.x;
			return false;
		}
		
		public void next() {
			this.x++;
			if(this.x == Tn) {
				this.x = 0;
				this.y++;
			}
		}
		
		public int index1D() {
			return this.y * Tn + this.x;
		}
		
		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
		
	}
	
	
}
