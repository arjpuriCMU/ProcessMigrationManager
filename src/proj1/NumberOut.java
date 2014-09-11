import MigratableProcess;

public class number extends MigratableProcess{
  
  private int num = 0;
  private TransactionalFileOutputStream outstream;
  
  public number(String[] args){
    super(args);
    outstream = new TransactionalFileOutputStream(args[0]);
  }
  
  public void run(){
    this.running = true;
    this.suspended = false;
    while(!suspended){
      if (num < 20){
        outstream.write(num.toString() + "\n");
      }
      Thread.sleep(200);
      
    }
  }
  
  public void suspend(){
    
  }
}
