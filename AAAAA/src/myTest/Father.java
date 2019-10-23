package myTest;

public class Father {

	 int a = 5;
	private int age;
	private String sex;
	
	 public Father() {
		super();
	}

	public Father(int age, String sex) {
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
		 System.out.println("father's method");
	 }
	
	int getA(){
		return this.a;
	}
}
