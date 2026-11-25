package main.com.models;

import java.time.LocalDateTime;
import java.util.Scanner;
import main.com.enums.UserRole;

public abstract class User {
    private int userId;
    private String userName;
    private String email;
    private String password;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;

    protected User(int userId, String userName, String email, String password, String phone, LocalDateTime CreatedAt,
            UserRole role) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.createdAt = CreatedAt;
        this.role = role;

    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getPhone() {
        return phone;

    }

    public void updateProfile(String name, String email, String phone) {

        // Scanner sc = new Scanner(System.in);
        // System.out.println("Update User Name : ");
        // this.userName = sc.nextLine();
        // System.out.println("New Phone Number : ");
        // this.phone = sc.nextLine();
        // System.out.println("Update email : ");
        // this.email = sc.nextLine();

        this.userName = name;
        this.email = email;
        this.phone = phone;

    }

    boolean passwordStrength(String s) {
        if (s.length() <= 8) {
            return false;
        }
        boolean isLower = false;
        boolean isdigit = false;
        boolean isSpecial = false;
        boolean isUpper = false;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i)) && Character.isUpperCase(s.charAt(i))) {
                isUpper = true;
            }
            if (Character.isLowerCase(s.charAt(i))) {
                isLower = true;
            }
            if (s.charAt(i) <= '9' && s.charAt(i) >= '0') {
                isdigit = true;
            }
            if (s.charAt(i) == ' ') {
                return false;
            }
            if (!Character.isLetterOrDigit(s.charAt(i))) {
                isSpecial = true;
            }
        }

        if (isdigit && isSpecial && isUpper && isLower) {
            return true;
        } else {
            return false;
        }

    }

    public void changePassword() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your previous password : ");
        String p = sc.nextLine();
        if (p.equals(this.password)) {
            System.out.println("Enter new Password : ");

            String newPassword = sc.nextLine();
            while (!passwordStrength(newPassword)) {
                System.out.println(
                        "Password should be more than 8 Characters, Must contains at least a digit ,at least a special character and should contains at least one lowercase letter and upper case letter.");
                System.out.println("Enter password again : ");
                newPassword = sc.nextLine();
            }
            if (passwordStrength(newPassword)) {
                this.password = newPassword;
                System.out.println("password updated successfully.");
            }

        } else {
            System.out.println("Password is incorrect");
        }
    }

    public void displayProfile() {
        System.out.println(this.userName);
        System.out.println(this.email);
        System.out.println(this.phone);

    }

    // public abstract void displayDashboard();
    // System.out.println("User Name : " + userName);
    // System.out.println("Phone no : " + phone);
    // System.out.println("Email : "+ email);
    // System.out.println("Created At : "+ createdAt);
    // }

}