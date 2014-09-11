package proj1;

import java.io.PrintStream;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

public class GrepProcess extends MigratableProcess
{
	private TransactionalFileInputStream  inFile;
	private TransactionalFileOutputStream outFile;
	private String query;
	
	public GrepProcess(String args[]) throws Exception
	{
		super(args);
		if (args.length != 3) {
			System.out.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}
		
		query = args[0];
		inFile = new TransactionalFileInputStream(args[1]);
		outFile = new TransactionalFileOutputStream(args[2]);
	}

	public void run()
	{
		this.running = true;
		this.suspended = false;
		this.complete = false;
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		try {
			while (!suspended) {
				String line = br.readLine();

				if (line == null) break;
				
				if (line.contains(query)) {
					out.println(line);
				}
				
				// Make grep take longer so that we don't require extremely large files for interesting results
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore it
				}
			}
		} catch (EOFException e) {
			this.complete = true;
		} catch (IOException e) {
			System.out.println ("GrepProcess: Error: " + e);
		}
		suspended = false;
	}

	public void suspend()
	{
		this.suspended = true;
		while (suspended);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}