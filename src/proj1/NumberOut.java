package proj1;

public class NumberOut extends MigratableProcess{
  
  
	private static final long serialVersionUID = 6663514053767574658L;
	private int num;
  private TransactionalFileOutputStream outstream;
  
  public NumberOut(String[] args){
	super(args);
	this.num = 0;
   
  }
  
  public void run(){
    this.running = true;
    this.suspended = false;
    while(!suspended){
      if (num < 20){
        System.out.println(num);
      }
      else {
    	  this.complete = true;
      }
      try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
      num++;
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
