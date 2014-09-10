package edu.cmu.cs.cs440.proj1;

import java.io.IOException;

public class Test {
	public static void test() throws IOException, ClassNotFoundException{
		Thread t1 = new Thread(new MainWrapper(new String[] {"-m", "8080"}));
		Thread t2 = new Thread(new MainWrapper(new String[] {"-w", "8080"}));
		t1.start();
	}
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		test();
	}
}

class MainWrapper implements Runnable{
	private String[] str;
	public MainWrapper(String[] s) throws ClassNotFoundException, IOException{
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