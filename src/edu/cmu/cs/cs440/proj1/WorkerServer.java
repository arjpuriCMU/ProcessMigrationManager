package edu.cmu.cs.cs440.proj1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class WorkerServer {
	
	private ConcurrentHashMap<Integer, Thread> pId2Threads;
	private int workerID;
	private String master_host;
	private Socket wSocket;
	private int portNumber;
	private Map<Integer,MigratableProcess> pId2Process;
	private List<Integer> processes;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public WorkerServer(String hname, int port){
		this.master_host = hname;
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
	
	public Map<Integer,Thread> getPId2Threads(){
		return pId2Threads;
	}
	
	public Map<Integer,MigratableProcess> getPId2Process(){
		return this.pId2Process;
	}

	public void run() throws ClassNotFoundException, IOException {
		ProcessController controlCenter = new ProcessController();
		WorkerHelper workerHelper = new WorkerHelper(this);
		Thread helperThread = new Thread(workerHelper);
		try {
			this.wSocket = new Socket(this.master_host,portNumber);
			System.out.println("Connected to master");
			while(true){
				if (input == null){
					input = new ObjectInputStream(wSocket.getInputStream());
				}
				m2wMessage msg = (m2wMessage)input.readObject();
				if (msg.getState() != State.TEST){
					System.out.println("Message: <pId- " + msg.getPId() + ", State- " + msg.getState() + ">");
					if (msg.getPId() == -1) {
						System.out.println("Message sent to all processes!");
					}
				}
				
				if (msg.getState().equals(State.TOSUSPEND)){
					pId2Process.get(msg.getPId()).suspend();
					processes.remove(msg.getPId());
					controlCenter.serialize(pId2Process.get(msg.getPId()));
					
				}
				else if (msg.getState().equals(State.QUIT)){
					wSocket.close();
					System.out.println("All workers have exited!");
					System.exit(1);
				}
				else if (msg.getState().equals(State.START)){
					processes.add(msg.getPId());
					MigratableProcess deProcess = controlCenter.desrialize(msg.getPId());
					Thread processThread = new Thread(deProcess);
					processThread.start();
					this.pId2Process.put(msg.getPId(), deProcess);
					this.pId2Threads.put(msg.getPId(),processThread);
				}
				else if (msg.getState().equals(State.TEST)){
					/* This is for checking the health of the worker server.
					 * Receives message from master, and then pings back!
					 */
					w2mMessage msgNew = new w2mMessage(-1,State.TESTDONE);
					pushMessageToMaster(msgNew);
					
				}
				else{
					System.out.println("System could not process message");
				}
				
			}
		} catch (IOException e) {
			System.out.println("Worker has been exited!");
		}
		
		helperThread.run();
		
	}
	
	public void pushMessageToMaster(w2mMessage msg) throws IOException{
		if (output == null){
			output = new ObjectOutputStream(wSocket.getOutputStream());
		}
		output.writeObject(msg);
		output.flush();
	}

	public Socket getSocket() {
		return this.wSocket;
	}

}

class WorkerHelper implements Runnable{
	private WorkerServer worker;
	private boolean paused;
	public WorkerHelper(WorkerServer worker){
		this.worker = worker;
		paused = false;
	}
	
	public void run(){
		while (true){
			Map<Integer,MigratableProcess> processes = worker.getPId2Process();
			for (Integer pId : processes.keySet()){
				MigratableProcess p = processes.get(pId);
				if (p.getCompleteValue() == true){
					worker.getPId2Process().remove(pId);
					worker.getPId2Threads().remove(pId);
					w2mMessage msg = new w2mMessage(pId,State.DONE);
					try {
						worker.pushMessageToMaster(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
