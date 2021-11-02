package PrimeLists;

import utils.BitGrid;
import utils.TwoWayHashmap;

public class Sieve extends Thread{

	private static int currentID = 0;
	private int id = currentID++;
	
	PrimeBlockingQueue primes;
	long upperbound;
	int magicNumber;
	TwoWayHashmap<Integer, Integer> coPrimes;
	BitGrid flags;
	
	public Sieve(PrimeBlockingQueue primes, long upperbound, int magicNumber, TwoWayHashmap<Integer, Integer> coPrimes, BitGrid flags) {
		this.primes = primes;
		this.upperbound = upperbound;
		this.magicNumber = magicNumber;
		this.coPrimes = coPrimes;
		this.flags = flags;
	}
	
	@Override
	public void run() {
		long prime = primes.take();
		while(prime != -1) {
			//System.out.println(id + "th sieve took " + prime);
			
			long primeMultiple = prime * prime;
			long inc = prime << 1;
			
			int currentMultiple = (int)(primeMultiple / magicNumber);
			int currentMod = (int)(primeMultiple % magicNumber);
			int modInc = (int)(inc % magicNumber);
			int multipleInc = (int)(inc / magicNumber);
			
			for( ; primeMultiple <= upperbound ; primeMultiple += inc) {
				if(coPrimes.containsKey(currentMod))
					flags.setBit(coPrimes.getValue(currentMod),  currentMultiple, true);
				currentMod += modInc;
				currentMultiple += multipleInc;
				if(currentMod >= magicNumber) {
					currentMod -= magicNumber;
					currentMultiple++;
				}
			}
			prime = primes.take();
		}
	}
	
}
