package edu.cmu.cs.cs440.proj1;

import java.io.Serializable;

public abstract class MigratableProcess implements Runnable, Serializable{
	
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
	
	
	
	
	

}
