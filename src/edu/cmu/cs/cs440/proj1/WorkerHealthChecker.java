package edu.cmu.cs.cs440.proj1;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkerHealthChecker implements Runnable{
	/*
	 * This class checks to see if the workers present are responsive
	 */
	MasterServer master;
	public WorkerHealthChecker(MasterServer masterServer) {
		this.master = masterServer;
	}

	@Override
	public void run() {
		System.out.println("Started Worker Health Checking...");
		System.out.print("Master -> ");
		while(true){
			/* Goes through all communicators for each worker, and tests with a simple message.
			 * On failing to send the message, the worker is simply deleted.
			 */
			Map<Integer,Communicator> commMap = master.getCommunicatorMap();
			List<Integer> workersToRemove = new ArrayList<Integer>();
			for (Integer wId : commMap.keySet()){
				m2wMessage test = new m2wMessage(-1,State.TEST);
				try {
					commMap.get(wId).pushMessageToWorker(test);
				} catch (SocketException e) {
					System.out.println("Worker #" + wId + " has failed to recieve a master ping check!");
					workersToRemove.add(wId);
					//System.out.println("Worker #" + wId + " has been removed due to failure!");
					List<Integer> removePIds = new ArrayList<Integer>();
					for (Integer pId : master.getPid2Worker().keySet()){
						if (master.getPid2Worker().get(pId) == wId){
							removePIds.add(pId);
						}
					}
					
					for (Integer pId : removePIds){
						master.getPid2Worker().remove(pId);
						master.getPid2State().put(pId, State.QUIT);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			for (Integer wId : workersToRemove){
				try {
					master.getId2SocketMap().get(wId).close();
				} catch (IOException e) {
					System.out.println("Could not close socket with Worker#" + wId);
				}
				master.getId2SocketMap().remove(wId);
				try {
					if (master.getCommunicatorMap().get(wId).getInput() != null){
						master.getCommunicatorMap().get(wId).getInput().close();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Failed to close output stream with Worker#" + wId);
				}
				master.getCommunicatorMap().remove(wId);
				master.removeWorker(wId);
				
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				
			}
		
		}
		
	}

}
