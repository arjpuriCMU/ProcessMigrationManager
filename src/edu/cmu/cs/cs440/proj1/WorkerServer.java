package edu.cmu.cs.cs440.proj1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class WorkerServer {
	
	private ConcurrentHashMap<Integer, Thread> pId2Threads;
	private int workerID;
	private String hostname;
	private Socket wSocket;
	private int portNumber;
	private Map<Integer,MigratableProcess> pId2Process;
	private List<Integer> processes;
	
	
	public WorkerServer(String hname, int port){
		this.hostname = hname;
		this.portNumber = port;
		pId2Threads = new ConcurrentHashMap<Integer,Thread>();
		pId2Process = new ConcurrentHashMap<Integer,MigratableProcess>();
		processes = new ArrayList<Integer>();
	}
	
	public int getId(){
		return workerID;
	}
	public void setId(int id){
		this.workerID = id;
	}

	public void run() throws ClassNotFoundException {
		ProcessController controlCenter = new ProcessController();
		try {
			this.wSocket = new Socket(hostname,portNumber);
			System.out.println("Connected to master");
			while(true){
				ObjectInputStream input = new ObjectInputStream(wSocket.getInputStream());
				m2wMessage msg = (m2wMessage)input.readObject();
				System.out.println("Message: <pId- " + msg.getPId() + ", State- " + msg.getState() + ">");
				if (msg.getState().equals(State.SUSPENDED)){
					pId2Process.get(msg.getPId()).suspend();
					processes.remove(msg.getPId());
					
				}
				else if (msg.getState().equals(State.QUIT)){
					wSocket.close();
					System.out.println("Worker " + msg.getPId() + "has been exited");
					System.exit(1);
				}
				
				else if (msg.getState().equals(State.START)){
					processes.add(msg.getPId());
					MigratableProcess deProcess = controlCenter.desrialize(msg.getPId());
					Thread processThread = new Thread(deProcess);
					processThread.start();
					
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Socket getSocket() {
		return this.wSocket;
	}

}
