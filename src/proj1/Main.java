package proj1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	private static final String MASTER_LOG = "master_log.txt";
	private static final String IP_LOG = "ip_log.txt"; //using afs to store ip of master

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		MasterServer master = null;
		File master_file = new File(MASTER_LOG);
		if (!master_file.exists()){
			master_file.createNewFile();
		}
		BufferedReader br = new BufferedReader(new FileReader(MASTER_LOG));
		String line = br.readLine();
		br.close();
		FileWriter writer_master = new FileWriter(master_file.getAbsoluteFile());
		BufferedWriter mWriter = new BufferedWriter(writer_master);
		if (args.length == 3){
			if (args[0].toLowerCase().equals("-w")){
				if (line.length() == 0){
					System.out.println("Must run the master server first!");
				} 
				else{
					System.out.println("Creating Worker Server...");
					String master_host = args[1];
					int worker_port = Integer.parseInt(args[2]);
					WorkerServer worker = new WorkerServer(master_host,worker_port);
					worker.run();
				}
			}
		}
		else if (args.length == 2){
			if (args[0].toLowerCase().equals("-m")){
				System.out.println("Creating Master Server...");
				int master_port = Integer.parseInt(args[1]);
				mWriter.write("Master Server: <Port: " + master_port +">");
				mWriter.close();
				master = new MasterServer(master_port);
				master.run();
			}
			else{
				System.out.println("Arguments were not recognized");
			}
		}
		else{
			System.out.println("Arguments were not recognized");
		}

			
	}
}
