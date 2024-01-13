package projecte1_xat;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.file.*;
import java.sql.*;
import java.util.logging.*;
import java.util.*;
import static projecte1_xat.ServerMain.*;

/**
 *
 * @author Administrador
 */
public class ClientMain {
    

    //Properties variables
    public static Properties properties = new Properties();
    static String FileConfigurationClient = ("C:\\Users\\Jordan\\OneDrive\\Documentos\\NetBeansProjects\\Projecte1_XAT\\src\\main\\java\\projecte1_xat\\configClient.properties");

    static {
        try (FileInputStream configFile = new FileInputStream(FileConfigurationClient)) {
            properties.load(configFile);
        } catch (IOException ex) {
            System.err.println("Failed to load server configuration: " + ex.getMessage());
        }
    }

    static Scanner in = new Scanner(System.in);
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        System.out.println("Inicia cliente");

        Socket sk = new Socket(properties.getProperty("ServerIP"), Integer.parseInt(properties.getProperty("Port")));
        Client cl = new Client(sk);
        cl.start();
    }

    public static class Client extends Thread {

        DataInputStream dis;
        DataOutputStream dos;
        InputStream is;
        OutputStream os;
        Socket sk;

        Client(Socket sk) throws IOException {
            this.sk = sk;
            this.is = sk.getInputStream();
            this.os = sk.getOutputStream();
            dos = new DataOutputStream(os);
            dis = new DataInputStream(is);

        }

        @Override
        public void run() {
            try {
                Menu();
                sk.close();
            } catch (IOException ex) {
                ex.getMessage();
            }
        }

        public void Menu() {
            try {
                System.out.println(dis.readUTF());
                int option = in.nextInt();
                dos.writeInt(option);
                switch (option) {
                    case 1:
                        SignIn();
                        break;
                    case 2:
                        SignUp();
                        break;
                    case 3:
                        dos.writeUTF("See you later");
                        break;
                    default:
                        System.out.println(dis.readUTF());
                        Menu();
                        break;
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void SignUp() {
            try {
                String username, password, confirm;

                System.out.println(dis.readUTF());
                username = sc.nextLine();
                dos.writeUTF(username);
                dos.flush();

                System.out.println(dis.readUTF());
                password = sc.nextLine();
                dos.writeUTF(password);
                dos.flush();

                System.out.println(dis.readUTF());
                confirm = sc.nextLine();
                dos.writeUTF(confirm);
                dos.flush();

                while (!confirm.equals(password)) {
                    System.out.println(dis.readUTF());
                    confirm = sc.nextLine();
                    dos.writeUTF(confirm);
                    dos.flush();
                }

                insertUser();
                Menu();
                Thread.sleep(1000);
            } catch (Exception ex) {
                Menu();
                System.out.println(ex.getMessage());
            }
        }

        private void insertUser() {

            try {
                System.out.println(dis.readUTF());
            } catch (IOException ex) {
                Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void SignIn() {
            int id = -1;
            try {
                String user, pass, query;

                do {
                    System.out.println(dis.readUTF());
                    user = sc.nextLine();
                    dos.writeUTF(user);
                    dos.flush();

                    System.out.println(dis.readUTF());
                    pass = sc.nextLine();
                    dos.writeUTF(pass);
                    dos.flush();

                    Thread.sleep(1000);
                    System.out.println(dis.readUTF());
                } while (dis.readInt() != 1);

                Thread.sleep(1000);
                inAppMenu(id);
            } catch (Exception ex) {
                Menu();
                System.out.println(ex.getMessage());
            }
        }

        public void inAppMenu(int u) {
            try {
                System.out.println(dis.readUTF());
                int option = in.nextInt();
                dos.writeInt(option);
                switch (option) {
                    case 1:
                        listUsers();
                        inAppMenu(u);
                        break;
                    case 2:
                        creatGroup();
                        inAppMenu(u);
                        break;
                    case 3:
                        deleteGroup();
                        inAppMenu(u);
                        break;
                    case 4:
                        manageGroup();
                        inAppMenu(u);
                        break;
                    case 5:
                        uploadFile(userDB.get(u));
                        inAppMenu(u);
                        break;
                    case 6:
                        ServerMessage();
                        inAppMenu(u);
                        break;
                    case 7:
                        ReadMessage();
                        inAppMenu(u);
                        break;
                    case 8:
                        listFiles(userDB.get(u));
                        inAppMenu(u);
                        break;
                    case 9:
                        downloadFile(userDB.get(u));
                        inAppMenu(u);
                        break;
                    case 10:
                        SignOut(userDB.get(u));
                        Menu();
                        break;
                    default:
                        System.out.println(dis.readUTF());
                        inAppMenu(u);
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void SignOut(User u) {
            try {
                System.out.println(dis.readUTF());

                Thread.sleep(1000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void listUsers() {
            try {
                System.out.println(dis.readUTF());
                int option = in.nextInt();
                dos.writeInt(option);
                switch (option) {
                    case 1:
                        users();
                        break;
                    case 2:
                        onlineUsers();
                        break;
                    case 3:
                        System.out.println(dis.readUTF());
                        break;
                    default:
                        System.out.println(dis.readUTF());
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void onlineUsers() {
            try {
                System.out.println(dis.readUTF());
                Thread.sleep(3000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void users() {
            try {
                System.out.println(dis.readUTF());
                Thread.sleep(3000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void creatGroup() {
            try {
                do {
                    System.out.println(dis.readUTF());
                    String Usergroup = sc.nextLine();
                    dos.writeUTF(Usergroup);
                    dos.flush();
                } while (dis.readInt() != 1);
                String confirm = dis.readUTF();
                System.out.println(confirm);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void deleteGroup() {
            try {
                System.out.println(dis.readUTF());
                String Deletegroup = sc.nextLine();
                dos.writeUTF(Deletegroup);
                dos.flush();

                String confirm = dis.readUTF();
                System.out.println(confirm);

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void manageGroup() {

            try {
                System.out.println(dis.readUTF());
                String groupname = sc.nextLine();
                dos.writeUTF(groupname);
                dos.flush();

                int res = dis.readInt();
                if (res == 1) {
                    Confgroup();
                } else {
                    System.out.println(dis.readUTF());
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }

        public void Confgroup() {
            try {
                System.out.println(dis.readUTF());
                int option = in.nextInt();
                dos.writeInt(option);
                switch (option) {
                    case 1:
                        System.out.println(dis.readUTF());
                        String UserAdd = sc.nextLine();
                        dos.writeUTF(UserAdd);
                        addMemberGroup();
                        break;
                    case 2:
                        System.out.println(dis.readUTF());
                        String UserRemove = sc.nextLine();
                        dos.writeUTF(UserRemove);
                        removeMemberGroup();
                        break;
                    case 3:
                        listGroupMembers();
                        break;
                    case 4:
                        System.out.println(dis.readUTF());
                        break;
                    default:
                        System.out.println(dis.readUTF());
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void addMemberGroup() {
            try {
                System.out.println(dis.readUTF());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }

        public void removeMemberGroup() {

            String Adduser, confirm;

            try {
                System.out.println(dis.readUTF());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }

        public void listGroupMembers() {
            try {
                System.out.println(dis.readUTF());
            } catch (IOException ex) {
                Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        public void uploadFile(User u) {
            try {
                String message = dis.readUTF();
                boolean exit = false;
                while (!exit) {
                    System.out.println(message);
                    String filePath = sc.nextLine();
                    Path fileToUpload = Paths.get(filePath);

                    if (Files.exists(fileToUpload)) {
                        dos.writeUTF("Sending file path");
                        dos.flush();

                        dos.writeUTF(fileToUpload.getFileName().toString()); // Send the file name
                        dos.flush();

                        dos.writeLong(Files.size(fileToUpload)); // Send the file length
                        dos.flush();

                        InputStream fileInputStream = Files.newInputStream(fileToUpload);
                        int filesize = (int) Files.size(fileToUpload);
                        byte[] buffer = new byte[filesize];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            dos.write(buffer, 0, bytesRead);
                            dos.flush();
                        }

                        if (dis.readInt() == 1) {
                            System.out.println(dis.readUTF());
                            boolean salir = false;
                            while (!salir) {
                                System.out.println(dis.readUTF());
                                int option = in.nextInt();
                                dos.writeInt(option);
                                salir = dis.readBoolean();
                                System.out.println(dis.readUTF());
                            }
                        } else {
                            System.out.println(dis.readUTF());
                        }
                        System.out.println(dis.readUTF());
                        break;
                    } else {
                        System.out.println("Wrong file path!!");
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("Something went wrong !!");
            }
        }

        private void ServerMessage() {
            try {

                System.out.println(dis.readUTF());
                String message = sc.nextLine();
                dos.writeUTF(message);
                dos.flush();

                System.out.println("Message sent: " + message);

                String name;
                System.out.println(dis.readUTF());
                int option = in.nextInt();
                dos.writeInt(option);
                switch (option) {
                    case 1:
                        System.out.println(dis.readUTF());
                        name = sc.nextLine();
                        dos.writeUTF(name);
                        break;
                    case 2:
                        System.out.println(dis.readUTF());
                        name = sc.nextLine();
                        dos.writeUTF(name);
                        break;
                    default:
                        System.out.println("invalid option");
                        break;
                }

                System.out.println(dis.readUTF());

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }

        private void ReadMessage() {
            try {
                System.out.println(dis.readUTF());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public void listFiles(User u) {
            try {
                int exit = 0;
                do {
                    System.out.println(dis.readUTF());
                    String user = sc.nextLine();
                    dos.writeUTF(user);
                    dos.flush();
                    System.out.println(dis.readUTF());
                    exit = dis.readInt();
                } while (exit != 1);
                System.out.println(dis.readUTF());

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void downloadFile(User u) {
            boolean exit = false;
            try {
                listFiles(u);
                System.out.println(dis.readUTF());
                String filename = sc.nextLine();
                dos.writeUTF(filename);
                if (dis.readInt() == 1) {
                    int arraysize = dis.readInt();
                    byte[] filedata = new byte[arraysize];
                    int bytesRead;
                    int totalBytesRead = 0;
                    while (totalBytesRead < arraysize) {
                        bytesRead = dis.read(filedata, totalBytesRead, (int) (arraysize - totalBytesRead));
                        if (bytesRead == -1) {
                            break;
                        }
                        totalBytesRead += bytesRead;
                        break;
                    }
                    String filePath = properties.getProperty("PathFiles");
                    File outputFile = new File(filePath + File.separator + filename);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    fos.write(filedata);
                    fos.close();
                } else {
                    System.out.println(dis.readUTF());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }
}
