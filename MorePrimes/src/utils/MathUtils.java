package utils;

public class MathUtils {

	public static long myFloor(long n) {						//"rounding down" n to the closest number in the form 6n +- 1
		long nMod6 = n % 6;
		if(nMod6 == 5) return n;
		if(nMod6 == 0) return n - 1;
		return n + 1 - nMod6;
	}
	
	public static long myCeiling(long n) {						//"rounding up" n to the closest number in the form 6n +- 1
		long nMod6 = n % 6;		
		return (nMod6 < 2 ? n + 1 : n + 5) - nMod6;
	}
	
	public static int gcd(int p, int q) {
	    if (q == 0) 
	        return p;
	    return gcd(q, p % q);
	}
	
	public static boolean areCoPrime(int p, int q) {
	    return (gcd(p, q) == 1);
	}
	
}
