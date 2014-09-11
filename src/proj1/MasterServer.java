package proj1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
	private static final String MASTER_LOG = "master_log.txt";
	private WorkerHealthChecker checker;

	
	
	public MasterServer(int portNum){
		currPid = 0;
		portNumber = portNum;
		pid2WorkerID = new ConcurrentHashMap<Integer,Integer>();
		pid2State = new ConcurrentHashMap<Integer,State>();
		wId2Socket = new ConcurrentHashMap<Integer,Socket>();
		wId2Communication = new ConcurrentHashMap<Integer,Communicator>();
		workerIds = new ArrayList<Integer>();
		try {
			initSerDir();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initSerDir() throws FileNotFoundException, UnsupportedEncodingException {
		File ser = new File("serialized_processes");
		if (!ser.exists()){
			ser.mkdir();
		}
//		File dir_loc = new File("dir_loc.txt");
//		PrintWriter writer = new PrintWriter(dir_loc,"UTF-8");
//		writer.write("/serialized_processes");
//		writer.close();
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
		System.out.println("Master Server" + "<Port: " + portNumber + "> : ") ;
		while(true){
			try{
				usrInput = scanner.nextLine();
				args = usrInput.split(" ");
				if (args[0].toLowerCase().equals("quit")){
					for (int workerId: wId2Communication.keySet()){
						Communicator comm = wId2Communication.get(workerId);
						m2wMessage msg = new m2wMessage(-1, State.QUIT); //a -1 pId instructs it to send to all pIds
						comm.pushMessageToWorker(msg);
					}
					connector.getSocket().close();
					connector.setClosed(true);
					connector.getServerSocket().close();
					System.out.println("Shutdown Successful!");
					new File(MASTER_LOG).delete();
					System.exit(0);
				}
				processCLInput(args);

				if (args[0].toLowerCase().equals("help" )){
					displayHelp();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("Master -> ");
		
		}
		
	}
	
	public WorkerHealthChecker getHealthChecker(){
		return this.checker;
	}
	
	public void startWorkerHealthChecker(){
		checker = new WorkerHealthChecker(this);
		Thread healthThread = new Thread(checker);
		healthThread.start();
	}
	
	public void processCLInput(String[] args) {
		if (args[0].toLowerCase().equals("ser_dir")){ //attempting to change the serialization directory
			String dir = args[1];
			File dir_loc = new File("dir_loc.txt");
			PrintWriter writer;
			try {
				writer = new PrintWriter(dir_loc,"UTF-8");
				writer.write(dir);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (args[0].toLowerCase().equals("workers?")){
			System.out.println("Workers Present:");
			for (Integer wId : wId2Communication.keySet()){
				String host = wId2Communication.get(wId).getSocket().getLocalAddress().getHostAddress();
				int port = wId2Communication.get(wId).getSocket().getLocalPort();
				System.out.println("Worker #" + wId + " <host: " +host + ">" + " <port: " + port + ">" );
			}
		}
		else if (args[0].toLowerCase().equals("processes?")){
			System.out.println("Processes:");
			for (Integer pId : pid2State.keySet()){
				System.out.println("pId: " + pId + ", running on Worker #" + pid2WorkerID.get(pId) + 
						", State: " + pid2State.get(pId));
			}
		}
		
		else if (args[0].toLowerCase().equals("create")){
			MigratableProcess process;
			try {
				@SuppressWarnings("unchecked")
				Class<MigratableProcess> pClass = (Class<MigratableProcess>) (Class.forName(args[1]));
				Constructor<MigratableProcess> constructor = (Constructor<MigratableProcess>) (pClass.getConstructor(String[].class));
				process = constructor.newInstance((Object) args);
				process.setPid(currPid);
				ProcessController controller = new ProcessController();
				controller.serialize(process);
				pid2State.put(currPid, State.SUSPENDED);
				System.out.println("Process created: <pId = " + currPid + ">" + "< Class = " + process.getClass() + ">");
				currPid++;
			} catch (ClassNotFoundException e) {
				System.out.println("The process you tried to istantiate is not defined!");
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				System.out.println("No constructor given for the mentioned process!");
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				System.out.println("Failed to instantiate process!");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (args[0].toLowerCase().equals("start")){
			int pid = Integer.parseInt(args[1]);
			int wid = Integer.parseInt(args[2]);
			if (pid2State.get(pid) != null){
				if (wId2Socket.get(wid) != null && wId2Communication.get(wid) != null){
					if (pid2State.get(pid).equals(State.SUSPENDED)){
						m2wMessage msg = new m2wMessage(pid,State.START);
						Communicator workerCommunicator = wId2Communication.get(wid);
						try {
							workerCommunicator.pushMessageToWorker(msg);
						} catch (IOException e) {
							e.printStackTrace();
						}
						pid2State.put(pid, State.RUNNING);
						pid2WorkerID.put(pid, wid);
						System.out.println("Process has been started!");
						
					}
					else{
						System.out.println("Process is already running!");
					}
				}
				else{
					System.out.println("Worker does not exist!");
				}
			}
			else{
				System.out.println("Process does not exist!");
			}
		}
		
		else if (args[0].toLowerCase().equals("suspend")){
			int pid = Integer.parseInt(args[1]);
			if (pid2State.get(pid) != null){
				if (pid2State.get(pid).equals(State.RUNNING)){
					m2wMessage msg = new m2wMessage(pid,State.TOSUSPEND);
					Communicator communicator = wId2Communication.get(pid2WorkerID.get(pid));
					try {
						communicator.pushMessageToWorker(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
					pid2State.put(pid, State.SUSPENDED);
					pid2WorkerID.remove(pid);
					System.out.println("Process has been suspended!");
				}
				else{
					System.out.println("Process is already suspended!");
				}
			}
			else{
				System.out.println("Process does not exist!");
			}
		}
		
		else{
			System.out.println("Command not recognized. Type 'help' for available commands");
		}
		
		
		
		
		
	}

	public int getPort(){
		return this.portNumber;
	}
	
	private void displayHelp() {
		System.out.println("General:");
		System.out.println("'quit' to exit the entire system.");
		System.out.println("'ser_der <directory>' to set the serialization drectory to <directory>");
		System.out.println("Status:");
		System.out.println("'workers?' to display all running workers");
		System.out.println("'processes?' to display all running workers");
		System.out.println("Process Related:");
		System.out.println("'create <process_classname>' to create a process of type <process_classname>");
		System.out.println("'start <pid> <wid>' starts a process <pid> on the worker <wid>");
		System.out.println("'suspend <pid>' suspends a process <pid>");
	}
	
	public Map<Integer,Communicator> getCommunicatorMap(){
		return this.wId2Communication;
	}


	public void addWorker(int wId){
		workerIds.add(wId);
	}
	
	public void removeWorker(int wId){
		workerIds.remove(wId);
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
	private ServerSocket server_socket;
	private Socket socket;
	private boolean isClosed;
	
	Connector(MasterServer master){
		this.isClosed = false;
		this.master = master;
		
	}
	
	public void setClosed(boolean b) {
		this.isClosed = b;
		
	}

	public Socket getSocket(){
		return socket;
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		int currWorkerID = master.getId2SocketMap().size();
		try {
			server_socket = new ServerSocket(master.getPort());
			while(true && !isClosed){
				socket = server_socket.accept();
				System.out.println("Server socket opened on port: " + server_socket.getLocalPort());
				System.out.print("Master -> ");
				System.out.println("Worker #" + currWorkerID + " running");
				master.addWorker(currWorkerID);
				master.getId2SocketMap().put(currWorkerID,socket);
				Communicator comm = new Communicator(master,currWorkerID);
				Thread th = new Thread(comm);
				master.getCommunicatorMap().put(currWorkerID, comm);
				th.start();
				currWorkerID = master.getId2SocketMap().size();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
		}
		
	}
	
	public ServerSocket getServerSocket(){
		return this.server_socket;
	}
	
}
class Communicator implements Runnable{
	private Socket socket;
	private MasterServer master;
	private int wId;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	Communicator(MasterServer master, int wId){
		this.master =master ;
		this.wId = wId;
		this.socket = master.getId2SocketMap().get(wId);
	}
	
	public Socket getSocket(){
		return this.socket;
	}
	
	public ObjectInputStream getInput(){
		return input;
	}
	
	public ObjectOutputStream getOutput(){
		return output;
	}
	
	
	@Override
	public void run() {
		System.out.println("Communication Stream opened for Worker #" + wId);
		if (wId == 0){
			master.startWorkerHealthChecker(); //Ensures that the worker health checker only starts after a worker is loaded!
		}
		System.out.print("Master -> ");
		try {
			while(true){
				if (input == null){
					input = new ObjectInputStream(socket.getInputStream());}
				w2mMessage inputMsg = (w2mMessage) input.readObject();
				if (inputMsg.getState().equals(State.DONE)){
					master.getPid2State().remove(inputMsg.getPId());
					master.getPid2Worker().remove(inputMsg.getPId());
				}
				else if (inputMsg.getState().equals(State.TESTDONE)){
					
				}
				else{
					master.getPid2State().put(inputMsg.getPId(), inputMsg.getState());
				}
			} 
		}
		catch (IOException | ClassNotFoundException e) {
			System.out.println("Stream with Worker#" + wId + " has been broken!");
		}
	}
	
	public void pushMessageToWorker(m2wMessage msg) throws IOException, SocketException {
		if (output == null){
			output = new ObjectOutputStream(socket.getOutputStream());
		}
//		try {
		output.writeObject(msg);
		output.flush();
//		} 
//		catch (IOException e) {
//			e.printStackTrace();
//			System.out.print("Message failed to send to: pId- " + msg.getPId() + " State- " + msg.getState());
//		}	
	}
}
