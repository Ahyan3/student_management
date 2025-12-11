package studentapp;

import java.io.*;
import java.util.*;

public class AdminDatabase {

    private final String FILE_NAME = "admins.txt";

    public AdminDatabase() {
        File f = new File(FILE_NAME);
        try {
            if (!f.exists()) f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------
    // VALIDATE ADMIN LOGIN
    // -------------------------------
    public boolean validateAdmin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true; // VALID ADMIN
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------
    // LOAD ADMINS
    // -------------------------------
    public List<String[]> getAllAdmins() {
        List<String[]> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) list.add(parts);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // -------------------------------
    // ADD ADMIN
    // -------------------------------
    public boolean addAdmin(String username, String password) {
        try {
            // Prevent duplicate
            for (String[] a : getAllAdmins()) {
                if (a[0].equals(username))
                    return false;
            }

            FileWriter fw = new FileWriter(FILE_NAME, true);
            fw.write(username + "," + password + "\n");
            fw.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------
    // DELETE ADMIN
    // -------------------------------
    public boolean deleteAdmin(String username) {
        try {
            List<String[]> admins = getAllAdmins();
            FileWriter fw = new FileWriter(FILE_NAME);

            boolean found = false;

            for (String[] a : admins) {
                if (!a[0].equals(username)) {
                    fw.write(a[0] + "," + a[1] + "\n");
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
    // UPDATE ADMIN
    // -------------------------------
    public boolean updateAdmin(String oldUser, String oldPass,
                               String newUser, String newPass) {

        try {
            List<String[]> admins = getAllAdmins();
            FileWriter fw = new FileWriter(FILE_NAME);

            boolean updated = false;

            for (String[] a : admins) {
                if (a[0].equals(oldUser) && a[1].equals(oldPass)) {

                    fw.write(newUser + "," + newPass + "\n");
                    updated = true;

                } else {
                    fw.write(a[0] + "," + a[1] + "\n");
                }
            }

            fw.close();
            return updated;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
