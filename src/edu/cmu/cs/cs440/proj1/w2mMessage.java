package edu.cmu.cs.cs440.proj1;

import java.io.Serializable;

public class w2mMessage implements Serializable {
	/**
	 *w2mMessage- Worker to Master server message 
	 */
	private static final long serialVersionUID = -8892524368576321548L;
	private int pId;
	private State curr_state;
	
	public w2mMessage(int pId,State curr_state){
		this.pId = pId;
		this.curr_state = curr_state;
	}
	
	public int getPId(){
		return this.pId;
	}
	
	public State getState(){
		return this.curr_state;
	}
}
