// package studentapp.student;

// import java.awt.*;
// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import studentapp.database.DatabaseConnection;

// public class StudentManagementSystem extends JFrame {

//     // Declare fields
//     private JTextField txtId, txtName, txtAge, txtCourse;
//     private JTable table;
//     private DefaultTableModel model;
//     private DatabaseConnection db = new DatabaseConnection();

//     public StudentManagementSystem() {
//         setTitle("Student Record System");
//         setSize(700, 500);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setLocationRelativeTo(null); // Center window
//         setLayout(new BorderLayout(10, 10));

//         db = new DatabaseConnection();

//         // Form Panel
//         JPanel form = new JPanel();
//         form.setLayout(new GridLayout(4, 2, 10, 10));
//         form.setBorder(BorderFactory.createTitledBorder("Student Information"));

//         form.add(new JLabel("Student ID:"));
//         txtId = new JTextField();
//         form.add(txtId);

//         form.add(new JLabel("Name:"));
//         txtName = new JTextField();
//         form.add(txtName);

//         form.add(new JLabel("Age:"));
//         txtAge = new JTextField();
//         form.add(txtAge);

//         form.add(new JLabel("Course:"));
//         txtCourse = new JTextField();
//         form.add(txtCourse);

//         add(form, BorderLayout.NORTH);

//         // Button Panel
//         JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 10));

//         JButton addBtn = new JButton("Add");
//         JButton updateBtn = new JButton("Update");
//         JButton deleteBtn = new JButton("Delete");
//         JButton searchBtn = new JButton("Search");

//         btnPanel.add(addBtn);
//         btnPanel.add(updateBtn);
//         btnPanel.add(deleteBtn);
//         btnPanel.add(searchBtn);

//         add(btnPanel, BorderLayout.SOUTH);

//         // Table
//         model = new DefaultTableModel(new String[] { "ID", "Name", "Age", "Course" }, 0);
//         table = new JTable(model);
//         add(new JScrollPane(table), BorderLayout.CENTER);

//         refreshTable();

//         // Listeners
//         addBtn.addActionListener(e -> addStudent());
//         updateBtn.addActionListener(e -> updateStudent());
//         deleteBtn.addActionListener(e -> deleteStudent());
//         searchBtn.addActionListener(e -> searchStudent());

//         setVisible(true);
//     }

//     private void addStudent() {
//         String id = txtId.getText();
//         String name = txtName.getText();
//         int age = Integer.parseInt(txtAge.getText());
//         String course = txtCourse.getText();

//         db.addStudent(new Student(id, name, age, course));
//         refreshTable();
//     }

//     private void updateStudent() {
//         String id = txtId.getText();
//         String name = txtName.getText();
//         int age = Integer.parseInt(txtAge.getText());
//         String course = txtCourse.getText();

//         db.updateStudent(id, name, age, course);
//         refreshTable();
//     }

//     private void deleteStudent() {
//         db.deleteStudent(txtId.getText());
//         refreshTable();
//     }

//     private void searchStudent() {
//         Student s = db.searchStudent(txtId.getText());

//         if (s != null) {
//             txtName.setText(s.getName());
//             txtAge.setText(String.valueOf(s.getAge()));
//             txtCourse.setText(s.getCourse());
//         } else {
//             JOptionPane.showMessageDialog(this, "Student not found!");
//         }
//     }

//     private void refreshTable() {
//         model.setRowCount(0);
//         for (Student s : db.getStudents()) {
//             model.addRow(new Object[] {
//                     s.getId(), s.getName(), s.getAge(), s.getCourse()
//             });
//         }
//     }

//     // public static void main(String[] args) {
//     //     new StudentManagementSystem();
//     // }
// }