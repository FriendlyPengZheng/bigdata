package taomee_io_object_flow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class ObjecStream {
	
	@Test
	public void test1()
	{
		try {
			Person p1=new Person(10,"looper");
			Person p2=new Person(20, "Tom");
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(new File("ok.txt")));
			oos.writeObject(p1);
			oos.writeObject(p2);
			oos.close();
			System.out.println("写完");
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(new File("ok.txt")));
			Person p3=(Person) ois.readObject();
			Person p4=(Person) ois.readObject();
			System.out.println(p3.toString());
			System.out.println(p4.toString());
			ois.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
