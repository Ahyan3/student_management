// // ---------- StudentDatabase.java ----------
// package studentapp;


// import java.io.*;
// import java.util.*;


// public class StudentDatabase {
// private List<Student> students = new ArrayList<>();
// private final String FILE_PATH = "students.csv";


// public StudentDatabase() {
// loadFromFile();
// }


// public void addStudent(Student s) {
// students.add(s);
// saveToFile();
// }


// public void updateStudent(String id, String name, int age, String course) {
// for (Student s : students) {
// if (s.getId().equals(id)) {
// s.setName(name);
// s.setAge(age);
// s.setCourse(course);
// saveToFile();
// return;
// }
// }
// }


// public void deleteStudent(String id) {
// students.removeIf(s -> s.getId().equals(id));
// saveToFile();
// }


// public List<Student> getStudents() {
// return students;
// }


// public Student searchStudent(String id) {
// for (Student s : students) {
// if (s.getId().equals(id)) return s;
// }
// return null;
// }


// public void saveToFile() {
// try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
// for (Student s : students) {
// pw.println(s.getId() + "," + s.getName() + "," + s.getAge() + "," + s.getCourse());
// }
// } catch (Exception e) {
// System.out.println("Error saving file: " + e.getMessage());
// }
// }


// public void loadFromFile() {
// File file = new File(FILE_PATH);
// if (!file.exists()) return;


// try (BufferedReader br = new BufferedReader(new FileReader(file))) {
// String line;
// while ((line = br.readLine()) != null) {
// String[] d = line.split(",");
// if (d.length == 4) {
// students.add(new Student(d[0], d[1], Integer.parseInt(d[2]), d[3]));
// }
// }
// } catch (Exception e) {
// System.out.println("Error loading file: " + e.getMessage());
// }
// }
// }