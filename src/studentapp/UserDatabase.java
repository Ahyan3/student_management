package studentapp;

import java.io.*;

public class UserDatabase {

    private File file = new File("users.txt");

    public UserDatabase() {
        try {
            if (!file.exists()) file.createNewFile();
        } catch (Exception e) {}
    }

    public boolean addUser(String username, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(username + "," + password);
            bw.newLine();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkUser(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
