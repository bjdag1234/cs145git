import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	Socket clientConnection;
	DataOutputStream outgoing;
	BufferedReader incoming;

	public static void main(String args[]) throws Exception{
		Client c = new Client();
		String ipaddress = "127.0.0.1";
		int port = 50000;

		c.connect(ipaddress, port);
		c.setupStreams();
/*		for(int i = 0; i<5; i++){
		c.send();
		System.out.println("Server: "+c.readLine());
		}
*/
		c.threads();
		c.disconnect();
		System.exit(0);
	}

	public String readLine() throws Exception{
		return incoming.readLine();
	}
/*	
	public void send() throws Exception{
		Scanner c = new Scanner(System.in);
		System.out.print("Client: ");
		outgoing.writeBytes(c.nextLine()+"\n");
		//System.out.println("SENT");
	}
*/
	public void disconnect() throws Exception{
		System.out.print("Client: closing...");
		clientConnection.close();
		System.out.print("closed!\n");
	}

	public void connect(String ipaddress, int port) throws Exception{
		System.out.print("Client: Connecting to "+ipaddress+":"+port+"...");
		clientConnection = new Socket(ipaddress, port);
		System.out.print("Connected!!\n");
	}

	public void setupStreams() throws Exception{
		System.out.print("Setting up streams...");
		outgoing = new DataOutputStream(clientConnection.getOutputStream());
		incoming = new BufferedReader(new InputStreamReader (clientConnection.getInputStream()));
		System.out.print("OK!\n");		
	}
	
	public void threads(){
		ClientReadThread clntreadthread = new ClientReadThread (this.incoming, "Client");
		ClientWriteThread clntwritethread = new ClientWriteThread(this.outgoing, "Client");
				
		clntwritethread.start();
		clntreadthread.start();
		try{
			
			while(true){
			if(clntreadthread.isInterrupted()){
				clntreadthread.interrupt();
				clntwritethread.interrupt();
				break;
			}
			if(clntwritethread.isInterrupted()){
				clntreadthread.interrupt();
				clntwritethread.interrupt();
				break;
			}
			}
			clntreadthread.interrupt();
			clntwritethread.interrupt();
		} catch ( Exception e){
			System.err.println("Thread stopped");
		}
	}
	
	
}
