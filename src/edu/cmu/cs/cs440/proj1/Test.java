package edu.cmu.cs.cs440.proj1;

import java.io.IOException;

public class Test {
	public static void test() throws IOException{
		Main main = new Main();
		main.main(new String[] {"-m", "8080"});
	}
	public static void main(String args[]) throws IOException{
		test();
	}
}
