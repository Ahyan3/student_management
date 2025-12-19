package studentapp.student;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import studentapp.database.DatabaseConnection;

public class GradesOverviewPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> yearFilter;

    public GradesOverviewPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 244, 248));

        // TITLE
        JLabel title = new JLabel("All Student Grades Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(new EmptyBorder(40, 50, 20, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // MAIN CARD
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 40, 40, 40)));

        // TOP CONTROL BAR
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        controlBar.setBackground(Color.WHITE);
        controlBar.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Search
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(300, 40));
        JButton searchBtn = createStyledButton("Search", new Color(52, 152, 219));

        // Year Filter
        JLabel filterLabel = new JLabel("Filter Year:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        yearFilter = new JComboBox<>(new String[] { "All", "1st Year", "2nd Year", "3rd Year", "4th Year" });
        yearFilter.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        yearFilter.setPreferredSize(new Dimension(180, 40));

        // Refresh Button
        JButton refreshBtn = createStyledButton("Refresh", new Color(46, 204, 113));

        controlBar.add(searchLabel);
        controlBar.add(searchField);
        controlBar.add(searchBtn);
        controlBar.add(Box.createHorizontalStrut(30));
        controlBar.add(filterLabel);
        controlBar.add(yearFilter);
        controlBar.add(Box.createHorizontalStrut(20));
        controlBar.add(refreshBtn);

        card.add(controlBar, BorderLayout.NORTH);

        // TABLE
        String[] columns = { "#", "ID", "Full Name", "Course", "Year", "Prelim", "Midterm", "Prefinal", "Finals",
                "Average", "Remarks", "Action" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 11; // Only Action editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);

        // Center columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 11; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(80);

        // Action column
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new GradeButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        card.add(scrollPane, BorderLayout.CENTER);

        // Back Button
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton backBtn = createStyledButton("← Back to Students", new Color(108, 117, 125));
        backBtn.addActionListener(e -> goBackToStudentPanel());
        bottomBar.add(backBtn);

        card.add(bottomBar, BorderLayout.SOUTH);

        // Wrap in scroll for responsiveness
        JScrollPane mainScroll = new JScrollPane(card);
        mainScroll.setBorder(null);
        add(mainScroll, BorderLayout.CENTER);

        // ACTIONS
        searchBtn.addActionListener(e -> searchAndFilter());
        refreshBtn.addActionListener(e -> loadAllGrades());
        yearFilter.addActionListener(e -> searchAndFilter());

        loadAllGrades();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private void loadAllGrades() {
        model.setRowCount(0);
        String sql = """
                SELECT s.student_id, s.fullname, s.course, s.year,
                       g.prelim, g.midterm, g.prefinal, g.finals
                FROM students s
                LEFT JOIN grades g ON s.student_id = g.student_id
                ORDER BY s.fullname
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            int counter = 1;
            while (rs.next()) {
                String prelim = rs.getString("prelim");
                String midterm = rs.getString("midterm");
                String prefinal = rs.getString("prefinal");
                String finals = rs.getString("finals");

                double avg = calculateAverage(prelim, midterm, prefinal, finals);
                String average = avg >= 0 ? String.format("%.2f", avg) : "-";
                String remarks = avg >= 75 ? "Passed" : (avg >= 0 ? "Failed" : "-");

                model.addRow(new Object[] {
                        counter++,
                        rs.getString("student_id"),
                        rs.getString("fullname"),
                        rs.getString("course"),
                        rs.getString("year"),
                        prelim != null ? prelim : "-",
                        midterm != null ? midterm : "-",
                        prefinal != null ? prefinal : "-",
                        finals != null ? finals : "-",
                        average,
                        remarks,
                        "Edit Grades"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage());
        }
    }

    private void searchAndFilter() {
        String keyword = searchField.getText().trim().toLowerCase();
        String selectedYear = (String) yearFilter.getSelectedItem();

        model.setRowCount(0);
        String sql = """
                SELECT s.student_id, s.fullname, s.course, s.year,
                       g.prelim, g.midterm, g.prefinal, g.finals
                FROM students s
                LEFT JOIN grades g ON s.student_id = g.student_id
                WHERE 1=1
                """ +
                (selectedYear.equals("All") ? "" : " AND s.year = '" + selectedYear + "'") +
                (keyword.isEmpty() ? ""
                        : " AND (LOWER(s.fullname) LIKE '%" + keyword + "%' " +
                                "OR s.student_id LIKE '%" + keyword + "%' " +
                                "OR s.course LIKE '%" + keyword + "%' " +
                                "OR COALESCE(g.prelim,'') LIKE '%" + keyword + "%' " +
                                "OR COALESCE(g.midterm,'') LIKE '%" + keyword + "%' " +
                                "OR COALESCE(g.prefinal,'') LIKE '%" + keyword + "%' " +
                                "OR COALESCE(g.finals,'') LIKE '%" + keyword + "%')")
                +
                " ORDER BY s.fullname";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            int counter = 1;
            while (rs.next()) {
                String prelim = rs.getString("prelim");
                String midterm = rs.getString("midterm");
                String prefinal = rs.getString("prefinal");
                String finals = rs.getString("finals");

                double avg = calculateAverage(prelim, midterm, prefinal, finals);
                String average = avg >= 0 ? String.format("%.2f", avg) : "-";
                String remarks = avg >= 75 ? "Passed" : (avg >= 0 ? "Failed" : "-");

                model.addRow(new Object[] {
                        counter++,
                        rs.getString("student_id"),
                        rs.getString("fullname"),
                        rs.getString("course"),
                        rs.getString("year"),
                        prelim != null ? prelim : "-",
                        midterm != null ? midterm : "-",
                        prefinal != null ? prefinal : "-",
                        finals != null ? finals : "-",
                        average,
                        remarks,
                        "Edit Grades"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }

    private double calculateAverage(String p, String m, String pf, String f) {
        try {
            double prelim = p == null || p.isEmpty() ? 0 : Double.parseDouble(p);
            double midterm = m == null || m.isEmpty() ? 0 : Double.parseDouble(m);
            double prefinal = pf == null || pf.isEmpty() ? 0 : Double.parseDouble(pf);
            double finals = f == null || f.isEmpty() ? 0 : Double.parseDouble(f);

            int count = 0;
            double sum = 0;
            if (prelim > 0) {
                sum += prelim;
                count++;
            }
            if (midterm > 0) {
                sum += midterm;
                count++;
            }
            if (prefinal > 0) {
                sum += prefinal;
                count++;
            }
            if (finals > 0) {
                sum += finals;
                count++;
            }

            return count > 0 ? sum / count : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private void goBackToStudentPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        Container contentPane = frame.getContentPane();
        if (contentPane instanceof JRootPane) {
            contentPane = ((JRootPane) contentPane).getContentPane();
        }
        if (contentPane.getLayout() instanceof CardLayout) {
            CardLayout cl = (CardLayout) contentPane.getLayout();
            cl.show(contentPane, "student");
        } else {
            frame.setContentPane(new StudentPanel());
            frame.revalidate();
            frame.repaint();
        }
    }

    // ButtonRenderer & GradeButtonEditor (same as before)
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(52, 152, 219));
            setForeground(Color.WHITE);
            setText("Edit Grades");
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class GradeButtonEditor extends DefaultCellEditor {
        private JButton button;

        public GradeButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Edit Grades");
            button.setOpaque(true);
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String studentId = (String) model.getValueAt(row, 1);
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GradesOverviewPanel.this);
                    frame.setContentPane(new GradePanel(studentId));
                    frame.revalidate();
                    frame.repaint();
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Edit Grades";
        }
    }

    // AUTO REFRESH when returning from GradePanel
    // In GradePanel.java → goBackToStudentPanel(), change to:
    // cl.show(contentPane, "grades"); // instead of "student"
    // Then call loadAllGrades() here if needed — but since we use CardLayout, just
    // refresh on show
}