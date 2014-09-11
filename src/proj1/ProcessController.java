package proj1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ProcessController {
	String serDirectory;
	
	public ProcessController() throws IOException{
		init();
	}
	
	public void init() throws IOException{	
		FileReader fr = new FileReader("dir_loc.txt");
		BufferedReader reader = new BufferedReader(fr);	
		String ser_dir = reader.readLine();
		this.serDirectory = ser_dir;
		
	}

	public MigratableProcess deserialize(int pId){
		System.out.println("Deserializing process " + pId);
		FileInputStream inputFile;
		try {
			inputFile = new FileInputStream("serialized_processes" + "/" + pId + ".ser");
			ObjectInputStream input = new ObjectInputStream(inputFile);
			MigratableProcess process = (MigratableProcess) input.readObject();
			input.close();
			new File(serDirectory + "/" + pId + ".ser").delete();
			System.out.println("Process deserialized and ready to run...");
			return process;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
		
	}
	
	public void serialize(MigratableProcess process) {
		System.out.println("Serializing process " + process.getPid());
		File test = new File("serialized_processes" + "/" + process.toString()+ process.getPid() + ".ser");
		try {
			test.delete();
			FileOutputStream serFile = new FileOutputStream("serialized_processes" + "/" + process.getPid() + ".ser");
			ObjectOutputStream output = new ObjectOutputStream(serFile);
			output.writeObject(process);
			output.flush();
			output.close();
			System.out.println("Process serialized and stored...");
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
