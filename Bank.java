/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author An Nguyen
 */
public class Bank extends Menu {
// Tạo list Tài khoản và khách hàng 
    private static Vector<Account> aList = new Vector<Account>(20, 10);
    private static Vector<Customer> cList = new Vector<Customer>(20, 10);
    static String[] mc = {"Menu 1", "Vietcombank banking counters welcome !", "Dang Ky Thanh Vien", "Dang Nhap Thanh Vien", "Nap Tien Vao Tai Khoan", "Ket Thuc"};
    Customer curenrCustomer;
    Account currentAccount;

    public Bank() {
        super(mc);
        loadData("account.txt");
    }
// Chưa hiểu 
    public Bank(String[] mang) {
        super(mc);
    }

    @Override
    public void execute(int n) {
        switch (n) {
            case 1:
                customerRegistration();
                saveData("account.txt");
                break;
            case 2:
                try {
                    curenrCustomer = customerLogIn();
                    System.out.println(">>> Dang Nhap Thanh Cong, Xin Chao " + curenrCustomer.getTen() + " !");
                } catch (Exception e) {
                    System.out.println("Loi Dang Nhap: " + e.getMessage());
                }
                try {
                    curenrCustomer.setMenu();
                    curenrCustomer.run();
                } catch (Exception e) {
                    System.out.println("Loi Menu: " + e.toString());
                }
                break;
            case 3:
                doDepositCash();
                saveData("account.txt");
                break;
            case 4:
                viewCustomerList();
                break;
            case 5:
//                saveData("account.txt");
                System.out.println("\t\tVietcombank Hen Gap Lai Quy Khach !");
                System.exit(0);
        }
    }

    public void customerRegistration() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Dang Ky Thanh Vien -------");

        String ten;
        int cmnd = 0;
        String ngaySinh = null;
        try {
            System.out.println("Nhap Ten");
            ten = sc.nextLine();
            System.out.println("Nhap CMND");
            cmnd = Integer.parseInt(sc.nextLine());
            System.out.println("Nhap Ngay Sinh");
            ngaySinh = sc.nextLine();
            Customer c = new Customer(ten, cmnd, ngaySinh);
            cList.add(c);

            //Random rand = new Random();
            //int randomNum = minimum + rand.nextInt((maximum - minimum) + 1);
            Random rd = new Random();
            int pin = 100000 + rd.nextInt((900000 - 100000) + 1);//pin = [100000, 900000]
            double soDu = 100;
            Account a = new Account(c, soDu, pin);
            aList.add(a);
            System.out.println("\n>>> Dang Ky Tai Khoan Thanh Cong, Xin Chao " + ten + " !");
            System.out.println("\t------- *** -------");
            System.out.println("Ma Khach Hang Cua Ban: " + c.getMaKH() + "\nMat Khau Mac Dinh: " + c.getMatKhau());
            System.out.println("Ban Duoc Cap So Tai Khoan: " + a.getSoTK() + "\nPin Mac Dinh: " + a.getPin() + "\nSo Du Mac Dinh: " + a.getSoDu());
        } catch (Exception e) {
            System.out.println("Loi Nhap Sai Du Lieu: " + e.getMessage());
        }
    }
// Đang nhap thanh vien
    public Customer customerLogIn() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Dang Nhap Thanh Vien -------");
        System.out.println("Ma Khach Hang: ");
        String maKH = sc.nextLine();
        System.out.println("Mat Khau: ");
        String matKhau = sc.nextLine();
        for (Customer c : cList) {
            if (maKH.equals(c.maKH) && matKhau.equals(c.matKhau)) {
                return c;
            }
        }
        throw new RuntimeException("Tai Khoan Va Mat Khau Khong Dung !");
    }
// Nạp tiền vào tài khoản thành công 
    public void doDepositCash() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Nap Tien Vao Tai Khoan -------");
        System.out.println("Nhap So Tai Khoan: ");
        int stk = sc.nextInt();
        Account a = getAccount(stk);
        double tienNop = 0;
        try {
            tienNop = depositCash(a);
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
            return;
        }
        double soDuCu = a.getSoDu();
        a.setSoDu(tienNop + soDuCu);
        System.out.println(">>> Nap Tien Vao Tai Khoan " + a.getSoTK() + " Thanh Cong !");
    }
// Nạp không thành công
    public double depositCash(Account a) {
        if (a == null) {
            throw new RuntimeException("So Tai Khoan Khong Ton Tai !");
        }
        Scanner sc = new Scanner(System.in);
        double tienNop = 0;
        System.out.println("So Tien Gui Vao Tai Khoan: ");
        try {
            tienNop = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Tien Phai La Kieu So !");
        }
        String mota = "Nop Tien Vao Tai Khoan";
        if (tienNop <= 0) {
            throw new RuntimeException("Tien Nap Vao Tai Khoan Phai > 0");
        }
        a.transactionDiary.add(new Transaction(a, tienNop, "Nap Tien", mota));
        return tienNop;
    }
// Danh sách thành viên
    public void viewCustomerList() {
        System.out.println("\n------- Danh Sach Thanh Vien -------");
        if (cList.size() == 0) {
            System.out.println("Danh Sach Rong !");
        } else {
            Collections.sort(cList);
            System.out.printf("%-7s| %-16s| %-15s", "Ma KH", "Ho Ten", "Mat");
            for (int i = 0; i < cList.size(); i++) {
                if (i < cList.size() - 1) {
                    if ((cList.get(i).getMaKH()).equals(cList.get(i + 1).getMaKH())) {
                        continue;
                    }
                }
                System.out.print(cList.get(i));
            }
        }
        System.out.println("\n\t ------- *** -------");
    }

    public static void saveData(String path) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        File fileName = new File(path);
        try {
            fileName.createNewFile();
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            Collections.sort(aList);
            for (Account a : aList) {
                String dateOfBirth = dateFormat.format(a.getNgaySinh());
                bw.write(a.getMaKH() + "::" + a.getTen() + "::" + a.getMatKhau() + "::" + a.getCmnd() + "::" + dateOfBirth
                        + "::" + a.getSoTK() + "::" + a.getPin() + "::" + a.getSoDu() + "\n");
            }
            bw.close();
            fw.close();
        } catch (IOException ex) {
            System.out.println("Lỗi lưu file !" + ex.toString());
        }
    }

    public void loadData(String path) {
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] array = line.trim().split("::");
                Customer c = createCustomer(array);
                cList.add(c);
                Account a = createAccount(c, array);
                aList.add(a);
            }
            br.close();
            fr.close();
        } catch (Exception ex) {
            System.out.println("Lỗi load file: " + ex.toString());
        }
    }

    public Customer createCustomer(String[] array) {
        String maKH = array[0];
        String ht = array[1];
        String matKhau = array[2];
        int cmnd = Integer.parseInt(array[3]);
        String ngaySinh = array[4];
        int count = Integer.parseInt(array[0].trim().substring(1, 5));
        Customer.setCount(++count);
        return new Customer(maKH, ht, matKhau, cmnd, ngaySinh);
    }

    public Account createAccount(Customer c, String[] array) {
        int soTK = Integer.parseInt(array[5]);
        int pin = Integer.parseInt(array[6]);
        double soDu = Double.parseDouble(array[7]);
        int count = soTK;
        //Đảm bảo CountSoTK cuối cùng khi load xong file là lớn nhất để không bị trùng, vì file sắp xếp theo tên Khách hàng
        if (count >= Account.getCountSoTK()) {
            Account.setCountSoTK(++count);
        }
        return new Account(c, soTK, pin, soDu);
    }

    public static Account getAccount(int soTK) {
        for (Account a : aList) {
            if (soTK == a.getSoTK()) {
                return a;
            }
        }
        return null;
    }

    public static void addAcountInaList(Account a) {
        aList.add(a);
    }

    public static Vector<Account> getaList() {
        return aList;
    }

    public static void setaList(Vector<Account> aList) {
        Bank.aList = aList;
    }
// Lấy custeme list
    public static Vector<Customer> getcList() {
        return cList;
    }

    public static void setcList(Vector<Customer> cList) {
        Bank.cList = cList;
    }

}
