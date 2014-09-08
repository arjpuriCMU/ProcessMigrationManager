package edu.cmu.cs.cs440.proj1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	private static final String MASTER_LOG = "master_log.txt";

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		MasterServer master = null;
		File master_file = new File(MASTER_LOG);
		if (!master_file.isFile()){
			master_file.createNewFile();}
		FileWriter writer_master = new FileWriter(MASTER_LOG);
		BufferedWriter mWriter = new BufferedWriter(writer_master);
		if (args.length == 3){
			if (args[0].toLowerCase().equals("-w")){
				if (master_file.length() ==0){
					System.out.println("Must run the master server first!");
				}
				else{
					System.out.println("Creating Worker Server...");
					String hostname = args[1];
					int worker_port = Integer.parseInt(args[2]);
					WorkerServer worker = new WorkerServer(hostname,worker_port);
					worker.run();
				}
			}
		}
		else if (args.length == 2){
			if (args[0].toLowerCase().equals("-m")){
				System.out.println("Creating Master Server...");
				int master_port = Integer.parseInt(args[1]);
				mWriter.write("Master Server: <Port: " + master_port);
				master = new MasterServer(master_port);
				master.run();
			}
		}
		else{
			System.out.println("Arguments were not recognized");
		}

			
	}
}
