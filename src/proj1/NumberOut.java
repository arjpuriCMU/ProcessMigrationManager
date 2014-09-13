package proj1;

import java.io.File;
import java.io.IOException;

public class NumberOut extends MigratableProcess{
  
  
	private static final long serialVersionUID = 6663514053767574658L;
	private int num;
	private TransactionalFileOutputStream outstream;
  
  public NumberOut(String[] args){
	super(args);
	this.num = 0;
	new File("nums.txt").delete();
	try {
		File nums = new File("nums.txt");
		nums.createNewFile();
	} catch (IOException e) {
		System.out.println("file exists");
		e.printStackTrace();
	}
	outstream = new TransactionalFileOutputStream("nums.txt");
   
  }
  
  public void run(){
    this.running = true;
    this.suspended = false;
    while(!suspended){
      if (num < 10){
        try {
        	System.out.println("Wrote " + num + " to file");
			outstream.write(num);
			
		} catch (IOException e) {
			System.out.println("Could not write!");
			e.printStackTrace();
		}
      }
      else {
    	  this.complete = true;
    	  return;
      }
      num++;
      try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
      
    }
    suspended = false;
  }
  
  public void suspend(){
	  this.suspended = true;
	  while (suspended){
		  try{
			  Thread.sleep(4000);
		  }
		  catch (InterruptedException e){
			  e.printStackTrace();
		  }
	  }
	
    
  }

@Override
public String toString() {
	// TODO Auto-generated method stub
	return null;
}
}
