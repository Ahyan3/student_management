package studentapp;

import java.io.*;
import java.util.*;

public class UserDatabase {

    private final String FILE_NAME = "users.txt";

    public UserDatabase() {
        File f = new File(FILE_NAME);
        try {
            if (!f.exists()) f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------
    // LOGIN VALIDATION
    // -------------------------------
    public boolean validateLogin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------
    // ADD USER
    // -------------------------------
    public boolean addUser(String username, String password, String role) {
        try {
            // Check duplicate
            List<String[]> users = getAllUsers();
            for (String[] u : users) {
                if (u[0].equals(username)) return false;
            }

            FileWriter fw = new FileWriter(FILE_NAME, true);
            fw.write(username + "," + password + "," + role + "\n");
            fw.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------
    // LOAD ALL USERS
    // -------------------------------
    public List<String[]> getAllUsers() {
        List<String[]> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) list.add(parts);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // -------------------------------
    // DELETE USER
    // -------------------------------
    public boolean deleteUser(String username) {
        try {
            List<String[]> users = getAllUsers();
            FileWriter fw = new FileWriter(FILE_NAME);

            boolean found = false;

            for (String[] u : users) {
                if (!u[0].equals(username)) {
                    fw.write(u[0] + "," + u[1] + "," + u[2] + "\n");
                } else {
                    found = true;
                }
            }

            fw.close();
            return found;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // -------------------------------
    // UPDATE USER
    // -------------------------------
    public boolean updateUser(String oldUsername, String newUsername,
                              String oldPassword, String newPassword) {

        try {
            List<String[]> users = getAllUsers();
            FileWriter fw = new FileWriter(FILE_NAME);

            boolean updated = false;

            for (String[] u : users) {
                if (u[0].equals(oldUsername) && u[1].equals(oldPassword)) {

                    fw.write(newUsername + "," + newPassword + "," + u[2] + "\n");
                    updated = true;

                } else {
                    fw.write(u[0] + "," + u[1] + "," + u[2] + "\n");
                }
            }

            fw.close();
            return updated;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    public boolean checkUser(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }

        } catch (Exception e) {}
        return false;
    }
}
