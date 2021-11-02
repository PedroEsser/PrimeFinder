package main;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import PrimeLists.BigPrimeList;
import PrimeLists.EssersSieve;
import PrimeLists.PrimeList;

public class Main {
	
	public static void main(String[] args) {
		//long u = (125 * ((long)Integer.MAX_VALUE >> 4));
		//System.out.println(u); //16777215875
		/*PrimeFinder f = new PrimeFinder(u);
		System.out.println(f.getNumberOfPrimes());
		System.out.println(f.getLastNthPrime(0));*/
		//System.out.println("Last prime: " + f.getLastNthPrime(0));
		/*for(int i = 0 ; i < primes.size() ; i+=10) {
			for(int j = i ; j < primes.size() && j < i+10 ; j++)
				System.out.print(primes.get(j) + "  ");
			System.out.println();
		}*/
		//System.out.println(PrimeFinder.isPrime(16777215857L));
		
//		BigPrimeList p = new BigPrimeList(1000000000, 4);//20.885 seconds
//		System.out.println(p.getNumberOfPrimes());//50847534
		
		long x = (long)Integer.MAX_VALUE << 5;
		BigPrimeList e = new BigPrimeList(1000000000, 4);//4.415 seconds
		System.out.println(e.getNumberOfPrimes());//50847534
		
		//BigPrimeList p = new BigPrimeList(x);
		//writePrimesUpTo("D:\\primes", x);
		
		/*System.out.println("###########################");
		
		System.out.println("Number of primes:" + p.getNumberOfPrimes());
		System.out.println("###########################");*/
		
		
		
	}
	
	public static void writePrimesUpTo(String path, long x) {
		String fileName = path + "/PrimesUpTo_" + x + ".txt";
		BigPrimeList p = new BigPrimeList(x, 4);
		long timestamp = System.currentTimeMillis();
		try {
			PrintWriter writer = new PrintWriter(new File(fileName));
			
			for(long prime : p)
				writer.write(prime + ",");
			
			double seconds = (double)(System.currentTimeMillis() - timestamp) / 1000;
			System.out.println("Primes up to " + x + " successfully written, total time ellapsed: " + seconds + " seconds");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
