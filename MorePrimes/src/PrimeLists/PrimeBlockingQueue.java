package PrimeLists;

public class PrimeBlockingQueue{

	private PrimeList list;
	private long currentPrime;
	
	public PrimeBlockingQueue(PrimeList list, int start) {
		this.list = list;
		this.currentPrime = list.getNthPrime(start);
	}
	
	
	public long take() {
		long p = currentPrime;
		if(p == -1)
			return -1;
		currentPrime = list.getNextPrime(p);
		return p;
	}
	
}
