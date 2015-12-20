//package Client;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;


public class Download implements Runnable{
	private static final int size=98*1024;
	public static ObjectOutputStream oos;
	public static ObjectInputStream ois;
	
	@SuppressWarnings("resource")
	public static void downloadfile(int num,ObjectInputStream ois){
		byte[] filearray = null;
		int length = 0;
		String filename = Integer.toString(num);
		try{
			try{
				FileOutputStream fos = null;
				fos = new FileOutputStream(new File(filename));
				filearray = new byte[size];
				int c = 0;
				while((length = ois.read(filearray, 0, filearray.length)) > 0 ){//can not put size here for the reason that not every file will be 100KB(this is maximum value)
					c+=1024;
					fos.write(filearray, 0, length);
					fos.flush();
					if(c >= size) break;
					if(num == Client.nchunk) {          //the last file: boundary value
						int remainingBytes;
						ObjectInputStream _is = ois;
						remainingBytes = _is.available();
						if(remainingBytes == 0) break;
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket nei_socket=null;
		FileOutputStream fos;
		try{
			String flag;
			while(nei_socket==null){
				try{
					nei_socket=new Socket("localhost",Client.NEIGHBOR_PORT[0]);
				}catch(IOException e){
					//System.out.println("Connecting Download Neighbor");
				}
			}
			if(nei_socket!=null){
				System.out.println("----Connected With Download Neighbor----");
				ois=new ObjectInputStream(nei_socket.getInputStream());
				oos=new ObjectOutputStream(nei_socket.getOutputStream());
				flag="continue";
				while(!flag.equals("finish")){
					Thread.currentThread().sleep(2000);
					LinkedList<Integer> list = new LinkedList<Integer>();
					list=Client.wishlist();
					System.out.println("list size:"+list.size());

					oos.writeInt(list.size());//wishlist.size()
					oos.flush();
					for(int i:list){
						oos.writeInt(i);
						oos.flush();
						System.out.println("wishlist:"+i);

					}
					LinkedList<Integer> llist= new LinkedList<Integer>();
					llist=(LinkedList<Integer>)ois.readObject();
					System.out.println("sendlist size:"+llist.size());

					for(int count=0;count<llist.size();count++){
						System.out.println("Downloading chunk "+llist.get(count));
						downloadfile(llist.get(count),ois);
						Client.chunkList.add(llist.get(count));
						Collections.sort(Client.chunkList);
					}
					if(isContinue()){
						flag="continue";
					}
					else{
						flag="finish";
					}
					oos.writeObject(flag);
					oos.flush();
				}
				oos.close();
				ois.close();
			}
		}catch(Exception e){}
	}
	private boolean isContinue() {
		// TODO Auto-generated method stub
		boolean check=true;
		//Client.wishlist
		if(Client.wishlist().size()==0){
			check=false;
		}
//		System.out.println(check);

		return check;
	}
}