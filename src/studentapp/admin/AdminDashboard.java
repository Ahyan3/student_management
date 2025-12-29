package studentapp.admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import studentapp.AdminDatabase;
import studentapp.UserDatabase;
import studentapp.auth.LoginForm;

public class AdminDashboard extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> roleBox;
    private JTable userTable;
    private DefaultTableModel tableModel;

    private UserDatabase userDB = new UserDatabase();
    private AdminDatabase adminDB = new AdminDatabase();

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(920, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("User Management Panel", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 26));
        title.setBorder(new EmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // LEFT PANEL
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Poppins", Font.PLAIN, 16));
        left.add(lblUser);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Poppins", Font.PLAIN, 16));
        txtUser.setPreferredSize(new Dimension(260, 40));
        txtUser.setMaximumSize(new Dimension(260, 40));
        txtUser.setBorder(new LineBorder(new Color(180, 180, 180), 1, true));
        left.add(txtUser);
        left.add(Box.createVerticalStrut(12));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Poppins", Font.PLAIN, 16));
        left.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Poppins", Font.PLAIN, 16));
        txtPass.setPreferredSize(new Dimension(260, 40));
        txtPass.setMaximumSize(new Dimension(260, 40));
        txtPass.setBorder(new LineBorder(new Color(180, 180, 180), 1, true));
        left.add(txtPass);
        left.add(Box.createVerticalStrut(12));

        JLabel lblRole = new JLabel("Role");
        lblRole.setFont(new Font("Poppins", Font.PLAIN, 16));
        left.add(lblRole);

        roleBox = new JComboBox<>(new String[]{"User", "Admin"});
        roleBox.setFont(new Font("Poppins", Font.PLAIN, 15));
        roleBox.setPreferredSize(new Dimension(260, 40));
        roleBox.setMaximumSize(new Dimension(260, 40));
        left.add(roleBox);
        left.add(Box.createVerticalStrut(20));

        JButton addBtn = new JButton("Add Account");
        styleButton(addBtn);
        left.add(addBtn);
        left.add(Box.createVerticalStrut(10));

        JButton updateBtn = new JButton("Edit Account");
        styleButton(updateBtn);
        left.add(updateBtn);
        left.add(Box.createVerticalStrut(10));

        JButton deleteBtn = new JButton("Delete Account");
        styleButton(deleteBtn);
        left.add(deleteBtn);
        left.add(Box.createVerticalStrut(10));

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn);
        left.add(logoutBtn);

        add(left, BorderLayout.WEST);

        // TABLE
        String[] columns = {"Username", "Password", "Role"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Poppins", Font.PLAIN, 14));
        userTable.setRowHeight(28);

        loadUsers();

        add(new JScrollPane(userTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> addAccount());
        updateBtn.addActionListener(e -> updateAccount());
        deleteBtn.addActionListener(e -> deleteAccount());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Poppins", Font.BOLD, 15));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.DARK_GRAY);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(52, 152, 219), 12, true));
        btn.setMaximumSize(new Dimension(260, 45));
    }

    // LOAD USER + ADMIN
    private void loadUsers() {
        tableModel.setRowCount(0);

        // Load normal users
        for (String[] u : userDB.getAllUsers()) {
            tableModel.addRow(new Object[]{u[0], u[1], "User"});
        }

        // Load admins
        for (String[] a : adminDB.getAllAdmins()) {
            tableModel.addRow(new Object[]{a[0], a[1], "Admin"});
        }
    }

    // ADD ACCOUNT
    private void addAccount() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        String role = roleBox.getSelectedItem().toString();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!");
            return;
        }

        boolean success;

        if (role.equals("Admin")) {
            success = adminDB.addAdmin(user, pass);
        } else {
            success = userDB.addUser(user, pass, "User");
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Account created!");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        }
    }

    // UPDATE ACCOUNT
    private void updateAccount() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user first!");
            return;
        }

        String oldUser = tableModel.getValueAt(row, 0).toString();
        String oldRole = tableModel.getValueAt(row, 2).toString();

        String newUser = txtUser.getText();
        String newPass = new String(txtPass.getPassword());
        String newRole = roleBox.getSelectedItem().toString();

        boolean success = false;

        // If role did NOT change
        if (oldRole.equals(newRole)) {
            if (newRole.equals("Admin")) {
                success = adminDB.updateAdmin(oldUser, tableModel.getValueAt(row, 1).toString(), newUser, newPass);
            } else {
                success = userDB.updateUser(oldUser, newUser, newPass, "User");
            }
        } else {
            // Role changed → move from one file to another
            if (oldRole.equals("Admin")) {
                adminDB.deleteAdmin(oldUser);
                success = userDB.addUser(newUser, newPass, "User");
            } else {
                userDB.deleteUser(oldUser);
                success = adminDB.addAdmin(newUser, newPass);
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Account updated!");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating!");
        }
    }

    // DELETE ACCOUNT
    private void deleteAccount() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user!");
            return;
        }

        String user = tableModel.getValueAt(row, 0).toString();
        String role = tableModel.getValueAt(row, 2).toString();

        boolean success;

        if (role.equals("Admin")) {
            success = adminDB.deleteAdmin(user);
        } else {
            success = userDB.deleteUser(user);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Account deleted!");
            loadUsers();
        }
    }
}
