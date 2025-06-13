package com.portfoliomanagement;

import java.sql.*;
import java.util.Scanner;

public class App {
    static final String URL = "jdbc:mysql://localhost:3306/class_data";
    static final String USER = "root";
    static final String PASS = "Sz20050420";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            while (true) {
                System.out.println("\n====== Student Management ======");
                System.out.println("1. Add Student");
                System.out.println("2. View All Students");
                System.out.println("3. Search Student by ID");
                System.out.println("4. Delete Student by ID");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");

                int choice;
                try {
                    choice = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid input. Please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1: {
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        System.out.print("Grade: ");
                        double grade;
                        try {
                            grade = Double.parseDouble(sc.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Invalid grade.");
                            break;
                        }
                        addStudent(conn, name, email, grade);
                        break;
                    }
                    case 2:
                        viewAllStudents(conn);
                        break;
                    case 3: {
                        System.out.print("Enter ID: ");
                        int id;
                        try {
                            id = Integer.parseInt(sc.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Invalid ID.");
                            break;
                        }
                        searchStudent(conn, id);
                        break;
                    }
                    case 4: {
                        System.out.print("Enter ID to delete: ");
                        int id;
                        try {
                            id = Integer.parseInt(sc.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Invalid ID.");
                            break;
                        }
                        deleteStudent(conn, id);
                        break;
                    }
                    case 5:
                        System.out.println("Exiting program...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("❌ Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed:");
            e.printStackTrace();
        }
    }

    static void addStudent(Connection conn, String name, String email, double grade) {
        String sql = "INSERT INTO students (name, email, grade) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setDouble(3, grade);
            stmt.executeUpdate();
            System.out.println("✅ Student added successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Failed to add student:");
            e.printStackTrace();
        }
    }

    static void viewAllStudents(Connection conn) {
        String sql = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.printf("\n%-5s %-20s %-25s %-7s\n", "ID", "Name", "Email", "Grade");
            System.out.println("----------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-25s %-7.2f\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getDouble("grade"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve students:");
            e.printStackTrace();
        }
    }

    static void searchStudent(Connection conn, int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Student found:");
                System.out.printf("Name: %s\nEmail: %s\nGrade: %.2f\n",
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getDouble("grade"));
            } else {
                System.out.println("⚠️ Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Search failed:");
            e.printStackTrace();
        }
    }

    static void deleteStudent(Connection conn, int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "🗑️ Student deleted." : "⚠️ Student not found.");
        } catch (SQLException e) {
            System.out.println("❌ Deletion failed:");
            e.printStackTrace();
        }
    }
}

