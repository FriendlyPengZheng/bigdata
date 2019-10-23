package myTest;

public class FatherAndSon {

	static char flag1 = ' ';
	static Character flag2 = '\u0000';
	public static void main(String[] args) {

		Son son = new Son(20,"N");
		
		Father father = new Son(40,"nan");
		Father father2 = new Father();
		father.print();
		System.out.println(father.getAge()+father.getSex()+father2.getAge()+father2.getSex());
		father2.print();
		System.out.println(father2.a);
		System.out.println(flag2.hashCode());
		System.out.println(Integer.toHexString(flag2));;

	}

}
