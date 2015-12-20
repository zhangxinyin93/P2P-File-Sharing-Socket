import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Merge{
	public static void merge(LinkedList<Integer> chunkList,String filename){
		FileInputStream fis;
        BufferedInputStream bis;
        BufferedOutputStream bos;
        FileOutputStream fos;
        try{ 
                fos = new FileOutputStream(filename);
                bos = new BufferedOutputStream(fos);                  
                for(int i=0;i<chunkList.size();i++)
                {
                		String filepath = Integer.toString(i+1);
                        fis = new FileInputStream(filepath);
                        bis = new BufferedInputStream(fis);
                        int b;
                        while((b=bis.read())!= -1)
                        {
                                bos.write(b);
                        }
                		System.out.println("write file "+filepath);

                }
                bos.close();
        }
catch(FileNotFoundException ex)
        {
                System.out.println("File Not Found");
        }
        catch(IOException ex)
        {
                System.out.println("Some IOException occurred");
        }
	}
}