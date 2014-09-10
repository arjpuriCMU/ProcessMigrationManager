package edu.cmu.cs.cs440.proj1;

import java.io.IOException;

public class Test2 {
	public static void test() throws IOException, ClassNotFoundException{
		Main.main(new String[] {"-w","localhost","8080"});
	}
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		test();
	}
}

class MainWrapper1 implements Runnable{
	private String[] str;
	public MainWrapper1(String[] s) throws ClassNotFoundException, IOException{
		this.str = s;
	}
	
	@Override
	public void run() {
		try {
			Main.main(str);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}