import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Jsnow on 2015/12/27.
 */
public class FtpClient {
    private ClientView clientView;
    /**
     * 两个Socket分别用来传输文件和控制信息
     * */
    private Socket commandSocket,dataSocket;
    //控制命令和服务器信息
    BufferedReader reader = null;
    BufferedWriter writer = null;
    //数据流
    BufferedReader rec = null;
    String dir = null;
    //被动模式地址
    String ip = "";
   int port;
    //目录表
    ArrayList<String> list;
    public FtpClient() {
        super();
    }

    public FtpClient(ClientView clientView) {
        super();
        this.clientView = clientView;
    }

    public synchronized  void connect(){
        try{//建立连接
            commandSocket = new Socket(clientView.getIPtf().getText(),
                    Integer.parseInt(clientView.getptf().getText()));
            reader = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(commandSocket.getOutputStream()));
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //发送用户名
            sendCommand("USER " + clientView.getUser().getText());
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //发送密码
            sendCommand("PASS " + clientView.getPass().getText());
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //到根目录
            sendCommand("PWD");
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //变为二进制传输
            sendCommand("TYPE I");
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //变为流模式
            sendCommand("MODE S");
            clientView.getJta().append(this.getMessage(reader) + "\n");
            sendCommand("OPTS UTF8 ON");
            clientView.getJta().append(this.getMessage(reader) + "\n");

        }catch (Exception e){
        }
    }
    //显示当前目录
    public  void getList() {
        try {
            //清空LIST
            clientView.getList().setListData(new Vector());
            //改为被动模式
            sendCommand("PASV");
            //被动模式解析
            String respond = this.getMessage(reader);
            clientView.getJta().append(respond + "\n");
            // 获取响应消息中代表IP地址和端口号的六个数字
            String[] infos = respond.substring(
                    respond.indexOf('(') + 1,
                    respond.indexOf(')'))
                    .split(",");
            // 获取IP地址
            this.ip = infos[0] + "." + infos[1] + "." + infos[2] + "." + infos[3];
            // 获取端口号
            int port = Integer.parseInt(infos[4]) * 256 + Integer.parseInt(infos[5]);
            sendCommand("LIST");
            dataSocket = new Socket(ip, port);
            clientView.getJta().append(reader.readLine() + "\n");
            rec = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            list = new ArrayList<String>();
            String temp = null;
            while ((temp = rec.readLine()) != null) {
                 list.add(temp.substring(temp.indexOf("           ") + 10));
                 //System.out.println(temp.substring(temp.lastIndexOf(" ")));
            }
            clientView.getList().setListData(list.toArray());
            rec.close();
            dataSocket.close();
            clientView.getJta().append(this.getMessage(reader) + "\n");
        }catch (IOException e){
            System.out.println("IOException: " + e);
        }
    }
    public void downldFile(String folder){
        try {
            sendCommand("PASV");
            //被动模式解析
            String respond = reader.readLine();
            clientView.getJta().append(respond + "\n");
            // 获取响应消息中代表IP地址和端口号的六个数字
            String[] infos = respond.substring(
                    respond.indexOf('(') + 1,
                    respond.indexOf(')'))
                    .split(",");
            // 获取IP地址
            this.ip = infos[0] + "." + infos[1] + "." + infos[2] + "." + infos[3];
            // 获取端口号
            int port = Integer.parseInt(infos[4]) * 256 + Integer.parseInt(infos[5]);
            sendCommand("RETR " + folder);
            dataSocket = new Socket(ip, port);
            clientView.getJta().append(reader.readLine() + "\n");
            //创建本地储存文件
//            this.dir = clientView.getPath();
//            System.out.print(this.dir);
//            File filedir = new File(dir,folder.substring(folder.lastIndexOf(" ")));
//            if (!filedir.exists())
//                filedir.createNewFile();
            FileOutputStream file = new FileOutputStream(clientView.getFile());
            System.out.print(clientView.getFile());
            // 开始获取数据
            BufferedInputStream bin = new BufferedInputStream(dataSocket.getInputStream());
            BufferedOutputStream bout = new BufferedOutputStream(file);
            int bsize = 1024;
            byte[] buffer = new byte[bsize];
            int count = 0;
            while((count = bin.read(buffer)) != -1) {
                bout.write(buffer, 0, count);
                bout.flush();
            }
            bout.close();
            bin.close();
            dataSocket.close();
            clientView.getJta().append(this.getMessage(reader) + "\n");
        }catch (IOException e){

        }

    }
    public void upldFile(){

    }
    //服务器响应信息处理
    private String getMessage(BufferedReader reader) throws IOException{
        String message = "";
        // 创建响应消息内容
        StringBuilder msg = new StringBuilder();
        // 获取第一行内容
        String temp = reader.readLine();
        msg.append(temp + "\r\n");
        int i = Integer.parseUnsignedInt(temp.substring(0, 3));
        // 判断是否是多行消息，如果是多行消息则继续组装消息
        if (temp.charAt(3) == '-') {
            do {
                temp = reader.readLine();
                msg.append(temp + "\r\n");
            }while(
                    Integer.parseInt(temp.substring(0, 3)) != i
                            || temp.charAt(3) != ' ');
            message = i+"";
        }
        message = message + msg.toString();
        return message;
    }
    public void openfolder(String folder){
        try {
            sendCommand("CWD "+folder.substring(folder.lastIndexOf(" ")));
            clientView.getJta().append(this.getMessage(reader) + "\n");
            getList();
        }catch (IOException e){

        }

    }
    //控制套接字发送命令
    public void  sendCommand(String com) throws IOException {
        if (commandSocket == null) {
            throw new IOException("SimpleFTP is not connected.");
        }
        try {
            writer.write(com + "\r\n");
            writer.flush();
        } catch (IOException e) {
            commandSocket = null;
            throw e;
        }
    }

}
