package com.xiaolu.service;

import com.xiaolu.qqcommon.Message;
import com.xiaolu.qqcommon.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.logging.SocketHandler;

/**
 * @author 林小鹿
 * @version 1.0
 * 保持和服务端通信
 */
public class ClientConnectServerThread extends Thread {
    //该线程需要持有Socket
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // 由于Thread需要在后台和服务器通信，因此我们需要while循环
        while (true) {
            try {
                System.out.println("客户端线程，等待从读取到服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果 服务器没有发送Message，线程会阻塞在这里
                Message message = (Message) ois.readObject();
                //判断这个message类型，做出相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    //对接收的内容做出规定
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n==========当前在线用户列表=============");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户：" + onlineUsers[i]);
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println("\n" + message.getSender() + "对" + message.getGetter() + "说：" + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    System.out.println("\n" + message.getSender() + "对大家说：" + message.getContent());
                }else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    System.out.println("\n"+message.getSender()+"给"+message.getGetter()
                            +"发文件："+message.getSrc()+"到我的电脑的目录"+message.getDest());
                    //取出message的文件字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n保存文件成功~");
                }
                else {
                    System.out.println("其他类型");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
