public class Student {
    String name;
    int age;
    String sno;
    public Student(String name, int age, String sno) {
        this.name = name;
        this.age = age;
        this.sno = sno;
    }
    public String getName(){
        return this.name;
    }
    public int getAge(){
        return this.age;
    }
    public String getSno(){
        return this.sno;
    }
    public static void main(String[] args) {
        Student s1 = new Student("Mike", 20, "103928");
        System.out.println("姓名："+s1.getName()+" 年龄:"+s1.getAge()+" 学号："+s1.getSno());
    }
}
