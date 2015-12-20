//package Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;

public class SplitFile{
	private static int size=98*1024;//100KB
	public  int nchunk=0;
	public LinkedList<Integer> chunkid= new LinkedList<Integer>();
	public void split(String filename){
		String newFileName;
		File fstream=new File(filename);
		FileInputStream in;
		int filesize=(int)fstream.length();
		int readlength=size;
		int read=0;
		byte[] schunk;
		try{
			in=new FileInputStream(fstream);
			while(filesize>0){
				if(filesize<=size){
					readlength=filesize;
				}
				schunk=new byte[readlength];
				read=in.read(schunk, 0, readlength);
				filesize-=read;
				nchunk++;
				chunkid.add(nchunk);
				newFileName=Integer.toString(nchunk);
				FileOutputStream out=new FileOutputStream(new File(newFileName));
				out.write(schunk);
				out.flush();
				out.close();
				out=null;
				schunk=null;
			}
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
