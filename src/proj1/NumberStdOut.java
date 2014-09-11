package proj1;
public class NumberStdOut extends MigratableProcess{
 
	private static final long serialVersionUID = -5085546396792853058L;
	private int num;
  
  public NumberStdOut(String[] args){
	super(args);
	this.num = 0;
  }
  
  public void run(){
    this.running = true;
    this.suspended = false;
    while(!suspended){
      if (num < 10){
    	  System.out.println(num);
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
