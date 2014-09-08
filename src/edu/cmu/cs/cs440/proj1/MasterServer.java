package edu.cmu.cs.cs440.proj1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class MasterServer implements Runnable {
	private int currPid;
	private Socket serverSocket;
	private int portNumber;
	private List<Integer> workerIds;
	private Map<Integer, Integer> pid2WorkerID;
	private Map<Integer, State> pid2State;
	private Map<Integer, Socket> wId2Socket;
	private Map<Integer, Communicator> wId2Communication;

	
	
	public MasterServer(int portNum){
		currPid = 0;
		portNumber = portNum;
		pid2WorkerID = new ConcurrentHashMap<Integer,Integer>();
		pid2State = new ConcurrentHashMap<Integer,State>();
		wId2Socket = new ConcurrentHashMap<Integer,Socket>();
		wId2Communication = new ConcurrentHashMap<Integer,Communicator>();
		workerIds = new ArrayList<Integer>();
	}

	@Override
	public void run() {	
		Scanner scanner = new Scanner(System.in);
		String usrInput;
		String[] args;
		Connector connector = new Connector(this);
		Thread connectionThread  = new Thread(connector);
		connectionThread.start();
		System.out.println("Master Server connection is now open!");
		while(true){
			System.out.println("Master Server" + "<Port: " + portNumber + "> : ") ;
			try{
				usrInput = scanner.nextLine();
				args = usrInput.split(" ");
				if (args[0].toLowerCase().equals("quit")){
					for (int workerId: wId2Communication.keySet()){
						Communicator comm = wId2Communication.get(workerId);
						m2wMessage msg = new m2wMessage(-1, State.QUIT); //a -1 pId instructs it to send to all pIds
						comm.pushMessageToWorker(msg);
					}
					System.out.println("Shutdown Successful!");
					System.exit(0);
				}
				if (args[0].toLowerCase().equals("help")){
					displayHelp();
				}
			}
			finally{
				scanner.close();
			}
		}
		
	}
	
	public int getPort(){
		return this.portNumber;
	}
	
	private void displayHelp() {
		// TODO Auto-generated method stub
		
	}
	
	public Map<Integer,Communicator> getCommunicatorMap(){
		return this.wId2Communication;
	}


	public void addWorker(WorkerServer worker){
		workerIds.add(worker.getId());
	}
	
	public Map<Integer, Socket> getId2SocketMap(){
		return this.wId2Socket;
	}

	public Map<Integer, State> getPid2State() {
		return pid2State;
	}

	public Map<Integer, Integer> getPid2Worker() {
		return pid2WorkerID;
	}

	
} 

class Connector implements Runnable {
	private MasterServer master;
	
	Connector(MasterServer master){
		this.master = master;
		
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		int currWorkerID = master.getId2SocketMap().size();
		try {
			ServerSocket server_socket = new ServerSocket(master.getPort());
			System.out.println("Server socket opened on port: " + server_socket.getLocalPort());
			while(true){
				Socket socket = server_socket.accept();
				System.out.println("Worker #" + currWorkerID);
				master.getId2SocketMap().put(currWorkerID,socket);
				Communicator comm = new Communicator(master,currWorkerID);
				Thread th = new Thread(comm);
				master.getCommunicatorMap().put(currWorkerID, comm);
				th.start();
				currWorkerID = master.getId2SocketMap().size();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		finally{
		}
		
	}
	
}
class Communicator implements Runnable{
	private Socket socket;
	private MasterServer master;
	private int wId;
	private ObjectInputStream input;
	Communicator(MasterServer master, int wId){
		this.master =master ;
		this.wId = wId;
		this.socket = master.getId2SocketMap().get(wId);
	}
	
	@Override
	public void run() {
		try {
			while(true){
				input = new ObjectInputStream(socket.getInputStream());
				w2mMessage inputMsg = (w2mMessage) input.readObject();
				if (inputMsg.getState().equals(State.DONE)){
					master.getPid2State().remove(inputMsg.getPId());
					master.getPid2Worker().remove(inputMsg.getPId());
				}
				else{
					master.getPid2State().put(inputMsg.getPId(), inputMsg.getState());
				}
				
				
			} 
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void pushMessageToWorker(m2wMessage msg) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeObject(msg);
			outputStream.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.print("Message failed to send to: pId- " + msg.getPId() + " State- " + msg.getState());
		}
		
	}
	
}
