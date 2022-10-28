package com.xiaolu.qqserver.service;

import com.xiaolu.qqcommon.Message;
import com.xiaolu.qqcommon.MessageType;
import com.xiaolu.qqcommon.User;

import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 林小鹿
 * @version 1.0
 * 这是服务端，在监听9999，等待客户端的连接，并保持通信
 */
public class QQServer {

    private ServerSocket ss = null;
    //创建一个集合，存放多个用户，如果是这些用户登录，就认为是合法
    // 这里我们也可以使用 ConcurrentHashMap，可以处理并发的集合，没有线程安全问题
    // HashMap 没有处理线程安全，在多线程下是不安全的
    private static HashMap<String, User> validUsers = new HashMap<>();

    static {// 在静态代码块中初始化 validUsers
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("admin", new User("admin", "123456"));
        validUsers.put("林小鹿", new User("林小鹿", "root"));
        validUsers.put("超级用户", new User("超级用户", "SuperRoot"));
        validUsers.put("泥马", new User("泥马", "123456"));

    }
    public boolean checkUser(String userId, String passwd) {
        User user = validUsers.get(userId);
        if (user == null) {
            return false;
        }
        if (!user.getPasswd().equals(passwd)) {
            return false;
        }
        return true;
    }


    public QQServer() {
        //注意：端口可以写在配置文件
        try {
            System.out.println("服务端在9999端口监听");
            ss = new ServerSocket(9999);
            // 启动推送新闻的线程
            new Thread(new SendNewsToAllService()).start();
            while (true) {//当和某个客户端连接后，会继续监听
                Socket socket = ss.accept();
                //得到socket关联的对象输入流
                ObjectInputStream ois =
                        new ObjectInputStream(socket.getInputStream());
                //得到一个socket关联的对象输出流
                ObjectOutputStream oos =
                        new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();//读取客户端发送到User对象
                //创建一个Message对象，准备回复客户端
                Message message = new Message();
                //验证
                if (checkUser(u.getUserId(), u.getPasswd())) {//登录成功
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复客户端
                    oos.writeObject(message);
                    //创建一个线程，和客户端保持通信，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, u.getUserId());
                    // 启动该线程
                    serverConnectClientThread.start();
                    //把该线程对象，放入到一个集合中，进行管理
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);


                } else {//登录失败
                    System.out.println("用户 id=" + u.getUserId() + "登录失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    // 关闭socket
                    socket.close();
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //如果服务器退出了while，说明服务器端不在监听，因此关闭ServerSocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
