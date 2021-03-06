package proj1;

import java.io.Serializable;

public abstract class MigratableProcess implements Runnable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3297922993842510729L;
	protected boolean running;
	protected boolean suspended;
	protected boolean complete;
	protected int pid; 
	
	public MigratableProcess(String args[]){
		running = false;
		suspended = false;
		complete = false;
	}
	
	public int getPid(){
		return pid;
	}
	
	public void setPid(int processId){
		this.pid = processId;
	}
	
	public boolean isRunning(){
		return running;
	}
	public boolean isSuspended(){
		return suspended;
	}
	public boolean isComplete(){
		return complete;
	}
	
	public abstract void suspend();
	
	public abstract String toString();
	
	@Override
	public abstract void run();

	public boolean getCompleteValue() {
		return complete;
	}
	
	

}
