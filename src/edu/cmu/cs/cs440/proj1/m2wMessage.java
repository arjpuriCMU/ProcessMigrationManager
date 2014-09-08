package edu.cmu.cs.cs440.proj1;

import java.io.Serializable;

public class m2wMessage implements Serializable {
	/**
	 * m2wMessage - Master to worker server message.
	 */
	private static final long serialVersionUID = -5243747850990276845L;
	private int pId;
	private State state;
	
	public m2wMessage(int pId, State state){
		this.pId =pId;
		this.state = state;
	}
	
	public int getPId(){
		return pId;
	}
	
	public State getState(){
		return state;
	}
}
