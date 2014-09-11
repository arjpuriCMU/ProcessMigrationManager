package proj1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class TransactionalFileInputStream extends InputStream implements Serializable {
	public String filename;
	public int index;
	private static final long serialVersionUID = -7284705238532925032L;
	public TransactionalFileInputStream(String file){
		super();
		this.filename = file;
		this.index = 0;
		
	}

	@Override
	public int read() throws IOException {
		@SuppressWarnings("resource")
		FileInputStream input = new FileInputStream(new File(filename));
		input.skip(index);
		int val = input.read();
		if (val> -1){
			this.index++;
		}
		return val;
	}
	 
	

}
