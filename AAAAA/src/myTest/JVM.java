package myTest;

public class JVM {

	public static void main(String[] args) {

		System.out.println("虚拟机最大内存："+Runtime.getRuntime().maxMemory()/1024d/1024d+"m");
		System.out.println("虚拟机内存总量："+Runtime.getRuntime().totalMemory()/1024d/1024d+"m");
	}

}
