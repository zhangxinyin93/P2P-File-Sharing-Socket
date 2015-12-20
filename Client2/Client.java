//package Client;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;

public class Client{
	public static final int CLIENT_PORT = 9002;
	private static final int own_clientnum=2;
	public static final int[] NEIGHBOR_PORT = {9001,9003}; 
	private static final String LOCALHOST = "127.0.0.1";
	private static ServerSocket serverSocket = null;
	private static Socket requestSocket;
	private static ObjectInputStream pin;
	public static int nchunk;
	private static int size=98*1024;
	private static BufferedInputStream in;
	public static String mergefile_name;
	public static LinkedList<Integer> chunkList=new LinkedList<Integer>();
	public static LinkedList<Integer> sendlist = new LinkedList<Integer>();
	public static LinkedList<Integer> wishlist = new LinkedList<Integer>();
	
	public void Serverdownload(){
		int num = 0;
		try {
			requestSocket = new Socket("localhost",8000);
			pin=new ObjectInputStream(requestSocket.getInputStream());
			mergefile_name=pin.readUTF();
			System.out.println("The file to be merged is: "+mergefile_name);
			nchunk=pin.readInt();
			System.out.println("Total chunk number: "+nchunk);
			int chunknum=0;
			chunknum=pin.readInt();
			System.out.println("Total chunk number will receive from server:"+chunknum);
			for(int i=0;i<chunknum;i++){
				num=pin.readInt();
				Download.downloadfile(num,pin);
				chunkList.add(num);
				System.out.println("Client "+own_clientnum+" has received chunk "+num+" from server");
				Collections.sort(chunkList);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Please Initialize the Server first");
		}
	}
	
	public static void sendlist(LinkedList<Integer> wishlist){
		sendlist= new LinkedList<Integer>();
		for(int i:wishlist){
			if(chunkList.contains(i)){
				sendlist.add(i);
			}
		}
		Collections.sort(sendlist);
	}
	
	public static LinkedList<Integer> wishlist(){
		wishlist=new LinkedList<Integer>();
		for(int i=0;i<nchunk;i++){
			wishlist.add(i+1);
		}
		for(int x:chunkList){
			if(wishlist.contains(x)){
				wishlist.remove((Object)x);
			}
		}
		Collections.sort(wishlist);
		return wishlist;
	}
	
	
	public static void main(String[] args) throws IOException{
		
		Client c1 = new Client();
		c1.Serverdownload();
		
		//serverSocket = new ServerSocket(CLIENT_PORT);
		Upload upload= new Upload();
		Thread uploadthread=new Thread(upload);
		Download download = new Download();
		Thread downloadthread = new Thread(download);
		uploadthread.start();
		downloadthread.start();
		try{
			uploadthread.join();
			downloadthread.join();
		}catch(InterruptedException ex)
		{
			System.out.println(ex.getMessage());
		}
		System.out.println("All chunks have been received");
		Merge.merge(chunkList,mergefile_name);
		System.out.println("File has been merged");
	}
	
	
}