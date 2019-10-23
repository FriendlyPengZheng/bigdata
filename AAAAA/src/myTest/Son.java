package myTest;

public class Son extends Father{

	 int a = 10;
	
	private int age;
	private String sex;
	public Son() {
		super();
	}
	public Son(int age, String sex) {
		super();
		this.age = age;
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	void print(){
		System.out.println("son's method");
	}
	@Override
	int getA() {
		return this.a;
	}
	
}
