package projecte1_xat;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 *
 * @author Administrador
 */
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.sql.*;
import java.util.logging.*;
import java.util.*;

public class ServerMain {

    //Properties variables
    public static Properties properties = new Properties();
    static String FileConfigurationServer = ("C:\\Users\\Jordan\\OneDrive\\Documentos\\NetBeansProjects\\Projecte1_XAT\\src\\main\\java\\projecte1_xat\\configServer.properties");

    static {
        try ( FileInputStream configFile = new FileInputStream(FileConfigurationServer)) {
            properties.load(configFile);
        } catch (IOException ex) {
            System.err.println("Failed to load server configuration: " + ex.getMessage());
        }
    }

    //Connection variables
    static final String jdbcUrl = properties.getProperty("ServerName");
    static final String username = properties.getProperty("ClientAdmin");
    static final String password = properties.getProperty("PasswordDDBB");
    static final int MaxConnections = Integer.parseInt(properties.getProperty("Connexions"));
    static Connection connection;

    //Users and Groups Variables
    static User u = null;
    static Group g = null;
    public static HashMap<Integer, User> userDB = new HashMap<Integer, User>();
    static int n;

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        System.out.println("Inicia servidor");

        ServerSocket ssk = new ServerSocket(Integer.parseInt(properties.getProperty("Port")), MaxConnections);
        while (true) {
            Socket sk = ssk.accept();
            Server sv = new Server(sk);
            sv.start();
        }
    }

    public static class Server extends Thread {

        DataInputStream dis;
        DataOutputStream dos;
        Socket sk;
        InputStream is;
        OutputStream os;

        Server(Socket sk) throws IOException {
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
                dos.writeUTF("1-Sign In.\n2-Sign-up.\n3-Turn off the app.");
                int option = dis.readInt();
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
                        dos.writeUTF("Invalid option !!");
                        Menu();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void SignUp() {
            User u = null;
            int i = -1;
            try {
                dos.writeUTF("Enter a username");
                dos.flush();
                String username = dis.readUTF();

                dos.writeUTF("Enter a password");
                dos.flush();
                String password = dis.readUTF();

                dos.writeUTF("confirm your password");
                dos.flush();
                String confirm = dis.readUTF();

                while (!confirm.equals(password)) {
                    dos.writeUTF("Please enter the same password.");
                    dos.flush();
                    confirm = dis.readUTF();
                }

                u = new User(username, password);
                i = insertUser(u);

                System.out.println("You have registered - Welcome:" + username);

                Thread.sleep(1000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            if (i != -1) {
                u.setId_user(i);
                userDB.put(u.getId_user(), u);
            }
            Menu();
        }

        private int insertUser(User u) {

            try {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                String query = "INSERT INTO user (user, password) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, u.getUser());
                preparedStatement.setString(2, u.getPassword());
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    dos.writeUTF("Account created succesfully.");
                    System.out.println("User created : " + u);
                    query = "SELECT Id_user FROM user WHERE user=? ";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, u.getUser());
                    ResultSet result = preparedStatement.executeQuery();
                    if (result.next()) {
                        return result.getInt("Id_user");
                    }
                } else {
                    dos.writeUTF("Failed to create account.");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            return -1;
        }

        public User SignIn() {
            int id = -1;
            try {
                String user, pass, query;
                ResultSet result;
                PreparedStatement preparedStatement;

                do {
                    dos.writeUTF("Enter your username");
                    dos.flush();
                    user = dis.readUTF();
                    dos.writeUTF("Enter your password");
                    dos.flush();
                    pass = dis.readUTF();

                    connection = DriverManager.getConnection(jdbcUrl, username, password);
                    query = "SELECT Id_user, password FROM user WHERE user=? AND password=?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, user);
                    preparedStatement.setString(2, pass);
                    result = preparedStatement.executeQuery();

                    if (!result.next()) {
                        dos.writeUTF("Incorrect username or password !!");
                        dos.writeInt(-1);
                        dos.flush();
                    } else {
                        id = result.getInt("Id_user");
                        userDB.put(id, new User(user, pass));
                        userDB.get(id).setIsOnline(true);

                        query = "UPDATE user SET isOnline = 1 WHERE Id_user =?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setString(1, String.valueOf(id));
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            dos.writeUTF("Welcome Back.");
                            dos.writeInt(1);
                            dos.flush();
                            System.out.println("User connected : " + user);
                            break;
                        }
                    }

                    Thread.sleep(1000);
                } while (!result.next());

                Thread.sleep(1000);
                inAppMenu(id);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            return userDB.get(id);
        }

        public void inAppMenu(int u) {
            try {
                dos.writeUTF("1-List users."
                        + "\n2-Create group."
                        + "\n3-Delete group."
                        + "\n4-Manage group."
                        + "\n5-Send file."
                        + "\n6-Send message."
                        + "\n7-Read message."
                        + "\n8-List files."
                        + "\n9-Download file."
                        + "\n10-sign Out");
                int option = dis.readInt();
                switch (option) {
                    case 1:
                        listUsers(userDB.get(u));
                        inAppMenu(u);
                        break;
                    case 2:
                        creatGroup(u);
                        inAppMenu(u);
                        break;
                    case 3:
                        deleteGroup(u);
                        inAppMenu(u);
                        break;
                    case 4:
                        manageGroup(u);
                        inAppMenu(u);
                        break;
                    case 5:
                        uploadFile(userDB.get(u));
                        inAppMenu(u);
                        break;
                    case 6:
                        ServerMessage(u);
                        inAppMenu(u);
                        break;
                    case 7:
                        ReadMessage(u);
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
                        dos.writeUTF("Invalid option !!");
                        inAppMenu(u);
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void SignOut(User u) {
            try {
                u.setIsOnline(false);
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                String query = "UPDATE user SET isOnline = 0 WHERE user =?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, u.getUser());
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    dos.writeUTF("Login out...");
                    System.out.println("User disconnected : " + u.user);
                }
                Thread.sleep(1000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void listUsers(User u) {
            try {
                dos.writeUTF("1-List all users."
                        + "\n2-List online users."
                        + "\n3-Cancel");
                int option = dis.readInt();
                switch (option) {
                    case 1:
                        users(u);
                        break;
                    case 2:
                        onlineUsers(u);
                        break;
                    case 3:
                        dos.writeUTF("Operation canceled ...");
                        break;
                    default:
                        dos.writeUTF("Invalid option !!");
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void onlineUsers(User u) {
            try {
                String query = "SELECT Id_user, user FROM user WHERE isOnline = 1";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet result = preparedStatement.executeQuery();
                String utf = "";
                while (result.next()) {
                    String id = result.getString("Id_user");
                    String user = result.getString("user");
                    utf += id + " - " + user + "\n";
                }
                dos.writeUTF(utf);
                System.out.println(u.user + " listed the online users");
                Thread.sleep(3000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void users(User u) {
            try {
                String query = "SELECT Id_user, user FROM user";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet result = preparedStatement.executeQuery();
                String utf = "";
                while (result.next()) {
                    String id = result.getString("Id_user");
                    String user = result.getString("user");
                    utf += id + " - " + user + "\n";
                }
                dos.writeUTF(utf);
                System.out.println(u.user + " listed all the users");
                Thread.sleep(3000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void creatGroup(int u) {

            try {

                dos.writeUTF("Enter a group");
                dos.flush();
                String GrupName = dis.readUTF();

                String CheackGroupName = "SELECT name FROM `group` WHERE name = ?";
                PreparedStatement CheckGroup = connection.prepareStatement(CheackGroupName);
                CheckGroup.setString(1, GrupName);
                ResultSet resultSet = CheckGroup.executeQuery();

                while (resultSet.next()) {
                    dos.writeInt(-1);
                    dos.writeUTF("Group name already exit, try again");
                    dos.flush();
                    GrupName = dis.readUTF();
                    CheckGroup.setString(1, GrupName);
                    resultSet = CheckGroup.executeQuery();
                }
                dos.writeInt(1);

                g = new Group(GrupName);

                String query = "INSERT INTO `group`(id_admin, name) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, u);
                preparedStatement.setString(2, GrupName);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    dos.writeUTF("group created succesfully -> " + g.getName() + " by " + userDB.get(u).user);
                    dos.flush();

                } else {
                    dos.writeUTF("Failed to create group.");
                    dos.flush();
                }
                query = "SELECT id_group FROM `group` WHERE name=? ";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, GrupName);
                ResultSet result = preparedStatement.executeQuery();
                while (result.next()) {
                    String Idgroup = result.getString("id_group");
                    query = "INSERT INTO `groupmember`(id_grup_member, id_grup,isAdmin) VALUES (?, ?,1)";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, u);
                    preparedStatement.setString(2, Idgroup);
                    rowsAffected = preparedStatement.executeUpdate();
                }

                System.out.println(userDB.get(u).user + " created a group");
            } catch (SQLException | IOException SQLE) {
                System.out.println(SQLE.getMessage());
            }

        }

        public void deleteGroup(int u) {

            try {

                dos.writeUTF("Enter a group for delete");
                dos.flush();
                String GrupName = dis.readUTF();
                int idadmin = u;

                String CheackGroupName = "SELECT id_group, id_admin FROM `group` WHERE name = ? AND id_admin = ?";
                PreparedStatement CheckGroup = connection.prepareStatement(CheackGroupName);
                CheckGroup.setString(1, GrupName);
                CheckGroup.setInt(2, idadmin);
                ResultSet resultSet = CheckGroup.executeQuery();

                if (resultSet.next()) {

                    int IdGroup = resultSet.getInt("id_group");

                    String querygroupmember = "DELETE FROM `groupmember` WHERE id_grup = ?";
                    PreparedStatement preparedStatementGM = connection.prepareStatement(querygroupmember);
                    preparedStatementGM.setInt(1, IdGroup);
                    int rowsAffectedGM = preparedStatementGM.executeUpdate();

                    String querygroup = "DELETE FROM `group` WHERE id_group = ?";
                    PreparedStatement preparedStatementG = connection.prepareStatement(querygroup);
                    preparedStatementG.setInt(1, IdGroup);
                    int rowsAffectedG = preparedStatementG.executeUpdate();

                    if (rowsAffectedGM > 0 && rowsAffectedG > 0) {
                        dos.writeUTF("Group deleted succesfully.");
                        dos.flush();
                    } else {
                        dos.writeUTF("Failed to delate group.");
                        dos.flush();
                    }
                } else {
                    dos.writeUTF("You are not administrator of the group");
                    dos.flush();

                }

                System.out.println(userDB.get(u).user + " deleted a group");
            } catch (SQLException | IOException SQLE) {
                System.out.println(SQLE.getMessage());
            }

        }

        public void manageGroup(int u) {

            try {
                dos.writeUTF("Enter the name of group");
                String name = dis.readUTF();
                String CheackGroupName = "SELECT id_group FROM `group` WHERE name = ? AND id_admin = ?";
                PreparedStatement CheckGroup = connection.prepareStatement(CheackGroupName);
                CheckGroup.setString(1, name);
                CheckGroup.setInt(2, u);
                ResultSet resultSet = CheckGroup.executeQuery();

                if (resultSet.next()) {
                    dos.writeInt(1);
                    int groupId = resultSet.getInt("id_group");

                    dos.writeUTF("Manage Group: "
                            + "\n1. Add Member"
                            + "\n2. Remove Member"
                            + "\n3. List Members"
                            + "\n4. Go back");

                    int option = dis.readInt();

                    switch (option) {
                        case 1:
                            dos.writeUTF("Enter the user of the member to add:");
                            String UserAdd = dis.readUTF();
                            addMemberGroup(groupId, UserAdd);
                            System.out.println(userDB.get(u).user + " added " + UserAdd + " to his group");
                            inAppMenu(u);
                            break;
                        case 2:
                            dos.writeUTF("Enter the user of the member to remove:");
                            String UserRemove = dis.readUTF();
                            removeMemberGroup(groupId, UserRemove);
                            System.out.println(userDB.get(u).user + " removed " + UserRemove + " from his group");
                            inAppMenu(u);
                            break;
                        case 3:
                            listGroupMembers(groupId, u);
                            inAppMenu(u);
                            break;
                        case 4:
                            dos.writeUTF("Going back to the main menu.");
                            inAppMenu(u);
                        default:
                            dos.writeUTF("Invalid option!");
                    }
                } else {
                    dos.writeInt(1);
                    dos.writeUTF("You are not the administrator of the group.");
                }

            } catch (Exception SQLE) {
                System.out.println(SQLE.getMessage());
            }
        }

        public void addMemberGroup(int groupId, String UserAdd) {

            try {
                String CheckuserMemeber = "SELECT Id_user FROM user WHERE user = ?";
                PreparedStatement CheckUserStatement = connection.prepareStatement(CheckuserMemeber);
                CheckUserStatement.setString(1, UserAdd);
                ResultSet resultSetUser = CheckUserStatement.executeQuery();

                if (resultSetUser.next()) {

                    int Id_userAdd = resultSetUser.getInt("Id_user");

                    String CheckuserMemeberGroup = "SELECT * FROM groupmember WHERE id_grup_member = ? AND id_grup = ?";
                    PreparedStatement checkMembershipStatement = connection.prepareStatement(CheckuserMemeberGroup);
                    checkMembershipStatement.setInt(1, Id_userAdd);
                    checkMembershipStatement.setInt(2, groupId);
                    ResultSet resultSetGroup = checkMembershipStatement.executeQuery();

                    if (!resultSetGroup.next()) {

                        String addMemberQuery = "INSERT INTO groupmember (id_grup_member, id_grup) VALUES (?, ?)";
                        PreparedStatement addMemberStatement = connection.prepareStatement(addMemberQuery);
                        addMemberStatement.setInt(1, Id_userAdd);
                        addMemberStatement.setInt(2, groupId);
                        int rowsAffected = addMemberStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            dos.writeUTF("Member added to the group.");
                        } else {
                            dos.writeUTF("Failed to add member to the group.");
                        }
                    } else {
                        dos.writeUTF("The user is already a member of the group.");
                    }
                } else {
                    dos.writeUTF("User not found.");
                }

            } catch (SQLException | IOException ex) {
                System.out.println(ex.getMessage());
            }

        }

        public void removeMemberGroup(int groupId, String UserRemove) {

            try {
                String CheckuserMemeber = "SELECT Id_user FROM user WHERE user = ?";
                PreparedStatement CheckUserStatement = connection.prepareStatement(CheckuserMemeber);
                CheckUserStatement.setString(1, UserRemove);
                ResultSet resultSetUser = CheckUserStatement.executeQuery();

                if (resultSetUser.next()) {

                    int Id_userAdd = resultSetUser.getInt("Id_user");

                    String CheckuserMemeberGroup = "SELECT * FROM groupmember WHERE id_grup_member = ? AND id_grup = ?";
                    PreparedStatement checkMembershipStatement = connection.prepareStatement(CheckuserMemeberGroup);
                    checkMembershipStatement.setInt(1, Id_userAdd);
                    checkMembershipStatement.setInt(2, groupId);
                    ResultSet resultSetGroup = checkMembershipStatement.executeQuery();

                    if (resultSetGroup.next()) {

                        String addMemberQuery = "DELETE FROM groupmember WHERE id_grup_member = ? AND id_grup = ?";
                        PreparedStatement addMemberStatement = connection.prepareStatement(addMemberQuery);
                        addMemberStatement.setInt(1, Id_userAdd);
                        addMemberStatement.setInt(2, groupId);
                        int rowsAffected = addMemberStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            dos.writeUTF("Member removed to the group.");
                        } else {
                            dos.writeUTF("Failed to remove member to the group.");
                        }
                    } else {
                        dos.writeUTF("The user is not a member of the group.");
                    }
                } else {
                    dos.writeUTF("User not found.");
                }

            } catch (IOException | SQLException ex) {
                System.out.println(ex.getMessage());
            }

        }

        public void listGroupMembers(int groupId, int u) {
            try {

                String List = "SELECT u.user "
                        + "FROM user u "
                        + "INNER JOIN groupmember gm ON u.Id_user = gm.id_grup_member "
                        + "WHERE gm.id_grup = ?";

                PreparedStatement listMembersStatement = connection.prepareStatement(List);
                listMembersStatement.setInt(1, groupId);
                ResultSet membersResult = listMembersStatement.executeQuery();
                String utf = "Members of the group:\n";
                while (membersResult.next()) {
                    String memberUsername = membersResult.getString("user");
                    utf += memberUsername + "\n";
                }
                dos.writeUTF(utf);

                System.out.println(userDB.get(u).user + " listed his group members");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void uploadFile(User u) {
            try {
                dos.writeUTF("Please enter the file path.");
                dos.flush();
                String message = dis.readUTF();
                System.out.println(u.user + " " + message);
                String fileName = dis.readUTF();
                long fileLength = dis.readLong();

                ByteBuffer buffer = ByteBuffer.allocate(Integer.parseInt(properties.getProperty("Tamany")));
                int bytesRead;
                byte[] fileData = new byte[(int) fileLength];
                int totalBytesRead = 0;

                while (totalBytesRead < fileLength) {
                    bytesRead = dis.read(fileData, totalBytesRead, (int) (fileLength - totalBytesRead));
                    if (bytesRead == -1) {
                        break;
                    }
                    totalBytesRead += bytesRead;
                }

                connection = DriverManager.getConnection(jdbcUrl, username, password);
                String query = "INSERT INTO fitxer (name, file_user, file_data) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, fileName);
                preparedStatement.setString(2, u.user);
                preparedStatement.setBytes(3, fileData); // Set the file data as bytes
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    dos.writeInt(1);
                    dos.writeUTF("File uploaded succesfully");
                    System.out.println(u.user + " uploaded a file");

                    boolean salir = false;
                    while (!salir) {
                        dos.writeUTF("You want the file to be :"
                                + "\n1-Private."
                                + "\n2-Protected."
                                + "\n3-Public.");
                        dos.flush();
                        query = "UPDATE fitxer SET acces=? WHERE name=? AND file_user=?";
                        preparedStatement = connection.prepareStatement(query);
                        int option = dis.readInt();
                        switch (option) {
                            case 1:
                                preparedStatement.setString(1, "private");
                                salir = true;
                                dos.writeBoolean(salir);
                                dos.writeUTF("Done.");
                                break;
                            case 2:
                                preparedStatement.setString(1, "protected");
                                salir = true;
                                dos.writeBoolean(salir);
                                dos.writeUTF("Done.");
                                break;
                            case 3:
                                preparedStatement.setString(1, "public");
                                salir = true;
                                dos.writeBoolean(salir);
                                dos.writeUTF("Done.");
                                break;
                            default:
                                dos.writeBoolean(salir = false);
                                dos.writeUTF("Option in valid.");
                                break;
                        }
                    }
                    preparedStatement.setString(2, fileName);
                    preparedStatement.setString(3, u.user);
                    rowsAffected = preparedStatement.executeUpdate();
                    dos.writeUTF("Acces modified");
                } else {
                    dos.writeInt(-1);
                    dos.writeUTF("Failed uploading the file");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("error: " + ex.getMessage());
            }
        }

        public void ServerMessage(int u) {

            try {

                connection = DriverManager.getConnection(jdbcUrl, username, password);
                String querySelect = "SELECT Id_user, user FROM user WHERE Id_user = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
                preparedStatement.setInt(1, u);
                ResultSet result = preparedStatement.executeQuery();

                while (result.next()) {
                    String id = result.getString("Id_user");
                    String NameUser = result.getString("user");

                    dos.writeUTF("Enter Message");
                    String message = dis.readUTF();

                    String Name = "", cadena = "";

                    String name = "";
                    dos.writeUTF("You want to send it to a:"
                            + "\n1-user."
                            + "\n2-group.");
                    int option = dis.readInt();
                    switch (option) {
                        case 1:
                            dos.writeUTF("Enter the username :");
                            name = dis.readUTF();
                            Name = "SELECT Id_user FROM `user` WHERE user = ? ";
                            cadena = "Id_user";
                            break;
                        case 2:
                            dos.writeUTF("Enter the groupname :");
                            name = dis.readUTF();
                            Name = "SELECT id_group FROM `group` WHERE name = ? ";
                            cadena = "id_group";
                            break;
                        default:
                            break;
                    }

                    PreparedStatement CheckName = connection.prepareStatement(Name);
                    CheckName.setString(1, name);
                    ResultSet resultSet = CheckName.executeQuery();

                    if (resultSet.next()) {
                        int desId = resultSet.getInt(cadena);
                        String queryInsert = "";
                        if (cadena.equals("Id_user")) {
                            queryInsert = "INSERT INTO messege (id_forwarder,Id_user,id_group, content) VALUES (?, ?,null, ?)";
                        } else {
                            queryInsert = "INSERT INTO messege (id_forwarder,Id_user,id_group, content) VALUES (?, null,?, ?)";
                        }
                        PreparedStatement preparedStatementInsrt = connection.prepareStatement(queryInsert);
                        preparedStatementInsrt.setInt(1, u);
                        preparedStatementInsrt.setInt(2, desId);
                        preparedStatementInsrt.setString(3, message);

                        int rowsAffected = preparedStatementInsrt.executeUpdate();

                        if (rowsAffected > 0) {
                            dos.writeUTF("Message added to the content.");
                            System.out.println("Message received: " + message + " BY: " + NameUser);
                            System.out.println(userDB.get(u).user + " sent a message");
                        } else {
                            dos.writeUTF("Failed to add message.");
                        }

                    } else {
                        dos.writeUTF("name dont`t exit");
                    }

                }

            } catch (IOException | SQLException IOE) {
                System.out.println(IOE.getMessage());
            }

        }

        public void ReadMessage(int u) {

            try {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                String querySelectuser = "SELECT content,id_forwarder FROM messege WHERE Id_user = ?";
                PreparedStatement preparedStatementu = connection.prepareStatement(querySelectuser);
                preparedStatementu.setInt(1, u);
                ResultSet resultuser = preparedStatementu.executeQuery();

                String messageuser = "";
                messageuser += "User Messages:\n";
                while (resultuser.next()) {

                    String querysend = "SELECT user FROM user WHERE Id_user = ?";
                    PreparedStatement preparedStatement2 = connection.prepareStatement(querysend);
                    preparedStatement2.setInt(1, resultuser.getInt("id_forwarder"));
                    ResultSet result2 = preparedStatement2.executeQuery();

                    String reply = resultuser.getString("content");
                    while (result2.next()) {

                        String reply2 = result2.getString("user");
                        messageuser += reply2 + " : " + reply + "\n";

                    }
                }

                String query = " SELECT id_grup FROM groupmember WHERE id_grup_member=? ";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, u);
                ResultSet result = preparedStatement.executeQuery();

                while (result.next()) {
                    String querySelectgroup = "SELECT content,id_forwarder FROM messege WHERE id_group = ?";
                    PreparedStatement preparedStatementgroup = connection.prepareStatement(querySelectgroup);
                    preparedStatementgroup.setInt(1, result.getInt("id_grup"));
                    ResultSet resultgroup = preparedStatementgroup.executeQuery();
                    while (resultgroup.next()) {

                        String querysend = "SELECT user FROM user WHERE Id_user = ?";
                        PreparedStatement preparedStatement2 = connection.prepareStatement(querysend);
                        preparedStatement2.setInt(1, resultgroup.getInt("id_forwarder"));
                        ResultSet result2 = preparedStatement2.executeQuery();

                        messageuser += "Group Messages:\n";

                        String reply = resultgroup.getString("content");
                        while (result2.next()) {

                            String reply2 = result2.getString("user");
                            messageuser += reply2 + " : " + reply + "\n";

                        }
                    }
                }
                dos.writeUTF(messageuser);

                System.out.println(userDB.get(u).user + " displayed his messages");

            } catch (Exception SQLE) {
                System.out.println(SQLE.getMessage());
            }

        }

        public void listFiles(User u) {
            try {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                String query = "SELECT Id_user FROM user WHERE user=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet result;
                String user;
                int exit = 0;
                do {
                    dos.writeUTF("Enter the name of the user .");
                    user = dis.readUTF();
                    preparedStatement.setString(1, user);
                    result = preparedStatement.executeQuery();
                    if (!result.next()) {
                        dos.writeUTF("Incorrect username !!");
                    } else {
                        dos.writeUTF("Username found");
                        exit = 1;
                        dos.writeInt(exit);
                        break;
                    }
                    dos.writeInt(exit);
                } while (!result.next());

                query = "SELECT name FROM fitxer "
                        + "WHERE file_user =? "
                        + "AND acces ='public'";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, user);
                ResultSet resultSet = preparedStatement.executeQuery();
                String utf = "List of files uploaded by " + user + ": \n";

                while (resultSet.next()) {
                    utf += resultSet.getString("name") + " \n";
                }

                query = "SELECT id_grup_member FROM groupmember "
                        + "WHERE id_grup = ( SELECT id_grup FROM groupmember WHERE id_grup_member = ?) ";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, user);
                resultSet = preparedStatement.executeQuery();
                boolean state = false;
                while (resultSet.next()) {

                    if (resultSet.getInt("id_grup_member") == u.id_user) {
                        state = true;
                        break;
                    }

                }
                if (state) {
                    query = "SELECT name FROM fitxer "
                            + "WHERE file_user =? "
                            + "AND acces ='protected'";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, user);
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        utf += resultSet.getString("name") + "\n";
                    }
                }

                dos.writeUTF(utf);

                System.out.println(u.user + " listed his files");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void downloadFile(User u) {
            try {
                listFiles(u);
                dos.writeUTF("Choose the file you want to download.");
                dos.flush();
                String filename = dis.readUTF();
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                String query = "SELECT file_data FROM fitxer WHERE name=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, filename);
                ResultSet result = preparedStatement.executeQuery();
                byte[] fileData;
                if (result.next()) {
                    dos.writeInt(1);
                    while (result.next()) {
                        fileData = result.getBytes("file_data").clone();
                        dos.writeInt(fileData.length);
                        int bytesRead;
                        while ((bytesRead = dis.read(fileData)) != -1) {
                            dos.write(fileData, 0, bytesRead);
                            dos.flush();
                            if ((bytesRead = dis.read(fileData)) == -1) {
                                dos.write(fileData, 0, bytesRead);
                                dos.flush();
                            }
                        }
                    }
                    System.out.println(u.user + " downloaded a file");
                } else {
                    dos.writeInt(0);
                    dos.writeUTF("No data found !!");
                    dos.flush();
                }dos.writeInt(-1);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
}
