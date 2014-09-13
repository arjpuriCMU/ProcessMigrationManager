package proj1;

import java.io.IOException;

public class NumberIn extends MigratableProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2142258801527583755L;
	TransactionalFileInputStream inputstream;
	private int i;
	public NumberIn(String[] args) {
		super(args);
		this.i =0;
		inputstream = new TransactionalFileInputStream("nums.txt");
	}

	@Override
	public void suspend() {
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
		return null;
	}

	@Override
	public void run() {
		this.running = true;
	    this.suspended = false;
	    while(!suspended){		
	    	try {
				int val = inputstream.read();
				if (val == -1){
		    		this.complete = true;
		    		break;
		    	}
				else{
					System.out.println("Value read from file: " + val );
//					System.out.print(", i:" + this.i + ", inputI:" + this.inputstream.getIndex() + "\n");
					this.i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	    this.suspended = false;
		}

}
