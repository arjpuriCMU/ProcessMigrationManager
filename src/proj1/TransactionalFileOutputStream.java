package proj1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements Serializable {

	private static final long serialVersionUID = 7471902923846292198L;
	private String filename;
	private int index;
	public TransactionalFileOutputStream(String file){
		super();
		this.filename = file;
		this.index = 0;
	}
	
	public int getIndex(){
		return this.index;
	}
	@Override
	public void write(int b) throws IOException {
		RandomAccessFile output = new RandomAccessFile(new File(filename), "rws");
		output.seek(this.index);
		output.write(b);
		this.index++;
		output.close();
		
	}

}
