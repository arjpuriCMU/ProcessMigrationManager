package edu.cmu.cs.cs440.proj1;

import java.net.Socket;
import java.util.Map;

public class MasterServer implements Runnable {
	private int currPid;
	private Socket serverSocket;
	private int portNumber;
	private int[] workerIds;
	private Map<Integer, Integer> pid2WorkerID;
	private Map<Integer, State> pid2State;
	private
	
	
	public MasterServer(){
		currPid = 0;
		portNumber = 5000; 
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
}
