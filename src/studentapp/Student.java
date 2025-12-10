// Full Java Swing Student Record System with File Saving (CSV)
// Project Structure:
// - Student.java
// - StudentDatabase.java
// - StudentManagementSystem.java (Main UI)


// ---------- Student.java ----------
package studentapp;


public class Student {
private String id;
private String name;
private int age;
private String course;


public Student(String id, String name, int age, String course) {
this.id = id;
this.name = name;
this.age = age;
this.course = course;
}


public String getId() { return id; }
public String getName() { return name; }
public int getAge() { return age; }
public String getCourse() { return course; }


public void setName(String name) { this.name = name; }
public void setAge(int age) { this.age = age; }
public void setCourse(String course) { this.course = course; }
}