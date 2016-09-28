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
     * ����Socket�ֱ����������ļ��Ϳ�����Ϣ
     * */
    private Socket commandSocket,dataSocket;
    //��������ͷ�������Ϣ
    BufferedReader reader = null;
    BufferedWriter writer = null;
    //������
    BufferedReader rec = null;
    String dir = null;
    //����ģʽ��ַ
    String ip = "";
   int port;
    //Ŀ¼��
    ArrayList<String> list;
    public FtpClient() {
        super();
    }

    public FtpClient(ClientView clientView) {
        super();
        this.clientView = clientView;
    }

    public synchronized  void connect(){
        try{//��������
            commandSocket = new Socket(clientView.getIPtf().getText(),
                    Integer.parseInt(clientView.getptf().getText()));
            reader = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(commandSocket.getOutputStream()));
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //�����û���
            sendCommand("USER " + clientView.getUser().getText());
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //��������
            sendCommand("PASS " + clientView.getPass().getText());
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //����Ŀ¼
            sendCommand("PWD");
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //��Ϊ�����ƴ���
            sendCommand("TYPE I");
            clientView.getJta().append(this.getMessage(reader) + "\n");
            //��Ϊ��ģʽ
            sendCommand("MODE S");
            clientView.getJta().append(this.getMessage(reader) + "\n");
            sendCommand("OPTS UTF8 ON");
            clientView.getJta().append(this.getMessage(reader) + "\n");

        }catch (Exception e){
        }
    }
    //��ʾ��ǰĿ¼
    public  void getList() {
        try {
            //���LIST
            clientView.getList().setListData(new Vector());
            //��Ϊ����ģʽ
            sendCommand("PASV");
            //����ģʽ����
            String respond = this.getMessage(reader);
            clientView.getJta().append(respond + "\n");
            // ��ȡ��Ӧ��Ϣ�д���IP��ַ�Ͷ˿ںŵ���������
            String[] infos = respond.substring(
                    respond.indexOf('(') + 1,
                    respond.indexOf(')'))
                    .split(",");
            // ��ȡIP��ַ
            this.ip = infos[0] + "." + infos[1] + "." + infos[2] + "." + infos[3];
            // ��ȡ�˿ں�
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
            //����ģʽ����
            String respond = reader.readLine();
            clientView.getJta().append(respond + "\n");
            // ��ȡ��Ӧ��Ϣ�д���IP��ַ�Ͷ˿ںŵ���������
            String[] infos = respond.substring(
                    respond.indexOf('(') + 1,
                    respond.indexOf(')'))
                    .split(",");
            // ��ȡIP��ַ
            this.ip = infos[0] + "." + infos[1] + "." + infos[2] + "." + infos[3];
            // ��ȡ�˿ں�
            int port = Integer.parseInt(infos[4]) * 256 + Integer.parseInt(infos[5]);
            sendCommand("RETR " + folder);
            dataSocket = new Socket(ip, port);
            clientView.getJta().append(reader.readLine() + "\n");
            //�������ش����ļ�
//            this.dir = clientView.getPath();
//            System.out.print(this.dir);
//            File filedir = new File(dir,folder.substring(folder.lastIndexOf(" ")));
//            if (!filedir.exists())
//                filedir.createNewFile();
            FileOutputStream file = new FileOutputStream(clientView.getFile());
            System.out.print(clientView.getFile());
            // ��ʼ��ȡ����
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
    //��������Ӧ��Ϣ����
    private String getMessage(BufferedReader reader) throws IOException{
        String message = "";
        // ������Ӧ��Ϣ����
        StringBuilder msg = new StringBuilder();
        // ��ȡ��һ������
        String temp = reader.readLine();
        msg.append(temp + "\r\n");
        int i = Integer.parseUnsignedInt(temp.substring(0, 3));
        // �ж��Ƿ��Ƕ�����Ϣ������Ƕ�����Ϣ�������װ��Ϣ
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
    //�����׽��ַ�������
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
