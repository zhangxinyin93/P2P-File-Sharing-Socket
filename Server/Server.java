//package Server;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server{
	private static final int port=8000;
	private static int size=98*1024;//100KB
	public static  String filename;//send chunk-id to client
	static SplitFile s = new SplitFile();
	public static String sfile;
	public static void main(String[] args) throws Exception{
		int clientNum=1;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please input the file to split");
		sfile=bufferedReader.readLine();
		s.split(sfile);
		System.out.println("Server is running");
		ServerSocket listener = new ServerSocket(port);
		try {
			while(true){
				new Handler(listener.accept(),clientNum).start();
				System.out.println("Client "+clientNum+" is connected");
				clientNum++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			listener.close();
		}
	}
	private static class Handler extends Thread{
		private Socket connection;
		private int no;
		private OutputStream out=null;
		BufferedInputStream bis=null;
		
		public Handler(Socket connection, int no){
			this.connection=connection;
			this.no=no;
		}
		
		public void run(){
			try {
				int x=5;//number of Client
				ObjectOutputStream pout = new ObjectOutputStream(connection.getOutputStream());
				//out = new ObjectOutputStream(connection.getOutputStream());
				pout.writeUTF(sfile);
				pout.flush();
				pout.writeInt(s.nchunk);
				pout.flush();
				if(!s.chunkid.isEmpty()){
					if(s.nchunk%x==0){
						pout.writeInt((s.nchunk/x));
						pout.flush();
						for(int i=0;i<(s.nchunk/x);i++){
							int fileid=s.chunkid.getFirst();
							String filename=Integer.toString(fileid);//??
							pout.writeInt(fileid);
							pout.flush();
							File f = new File(filename);
							byte[] filesend= new byte[size];
							int len=0;
							FileInputStream fis = new FileInputStream(f);
							while((len=fis.read(filesend,0,filesend.length))>0){
								pout.write(filesend,0,len);
								pout.flush();
							}
							System.out.println("Chunk "+filename+" sends to "+no+" client");
							s.chunkid.removeFirst();
						}
					}
					else{
						if(no>=1&&no<=4){
							pout.writeInt((s.nchunk/x));
							pout.flush();
							for(int i=0;i<(s.nchunk/x);i++){
								int fileid=s.chunkid.getFirst();
								String filename=Integer.toString(fileid);//??
								pout.writeInt(fileid);
								pout.flush();
								File f = new File(filename);
								byte[] filesend= new byte[size];
								int len=0;
								FileInputStream fis = new FileInputStream(f);
								while((len=fis.read(filesend,0,filesend.length))>0){
									pout.write(filesend,0,len);
									pout.flush();
								}
								System.out.println("Chunk "+filename+" sends to "+no+" client");
								s.chunkid.removeFirst();
							}
						}
						else{
							int id=0;
							id=s.nchunk-(4*(s.nchunk/x));
							pout.writeInt(id);
							pout.flush();
							for(int i=0;i<id;i++){
								int fileid=s.chunkid.getFirst();
								String filename=Integer.toString(fileid);//??
								pout.writeInt(fileid);
								pout.flush();
								File f = new File(filename);
								byte[] filesend= new byte[size];
								int len=0;
								FileInputStream fis = new FileInputStream(f);
								while((len=fis.read(filesend,0,filesend.length))>0){
									pout.write(filesend,0,len);
									pout.flush();
								}
								System.out.println("Chunk "+filename+" sends to "+no+" client");
								s.chunkid.removeFirst();
								if(s.chunkid.isEmpty()) break;
							}
						} 
					}
				}
				else{
					System.out.println("All chunks have been sent out");
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try{
					//out.close();
					connection.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}