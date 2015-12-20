//package Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Upload implements Runnable{
	private static int size=98*1024;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerSocket s;
		Socket up_nei_socket;
		ObjectOutputStream oos;
		ObjectInputStream ois;
		boolean iscontinue=true;
		try{
			s=new ServerSocket(Client.CLIENT_PORT);
			up_nei_socket=s.accept();
			System.out.println("Neighbor is connected");
			oos = new ObjectOutputStream(up_nei_socket.getOutputStream());
			ois = new ObjectInputStream(up_nei_socket.getInputStream());
			String flag="continue";
			while(!flag.equals("finish")){
				System.out.println("flag:"+flag);
				Thread.currentThread().sleep(2000);
				LinkedList<Integer> nlist = new LinkedList<Integer>();
				int count=ois.readInt();
				System.out.println("wishlist size:"+count);
				for(int n=0;n<count;n++){
					nlist.add(ois.readInt());
					System.out.println("wishlist:"+nlist.get(n));
				}
				Client.sendlist(nlist);
				//nlist=null;
				System.out.println("sendlist size:"+Client.sendlist.size());

				oos.writeObject(Client.sendlist);
				oos.flush();
				for(int j=0;j<Client.sendlist.size();j++){//??
					String filename=Integer.toString(Client.sendlist.get(j));//??
					File f = new File(filename);
					byte[] filesend= new byte[size];
					int len=0;
					FileInputStream fis = new FileInputStream(f);
					while((len=fis.read(filesend,0,filesend.length))>0){
						oos.write(filesend,0,len);
						oos.flush();
					}
					System.out.println("Uploading chunk "+Client.sendlist.get(j));
				}
				flag=(String)ois.readObject();
				System.out.println(flag);
			}
			ois.close();
			oos.close();
		}catch(Exception e){}
	}
	
}