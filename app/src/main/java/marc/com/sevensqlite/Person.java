package marc.com.sevensqlite;

/**
 * Created by 王成达
 * Date: 2017/9/14
 * Time: 16:45
 * Version: 1.0
 * Description:
 * Email:wangchengda1990@gmail.com
 **/
public class Person {
	private String name;
	private int age;

	public Person() {
	}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return name+" - "+age;
	}
}
