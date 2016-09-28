import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jsnow on 2015/12/27.
 */
public class ClientView extends JFrame{
    // FtpClient客户端
    private FtpClient client;
    // 文本域
    private JTextArea jta;
    // IP标签
    private JLabel iplb = new JLabel("ip");
    // IP地址文本域
    private JTextField iptf = new JTextField("10.78.22.223");
    // 端口号标签
    private JLabel plb = new JLabel("port");
    // 端口号文本域
    private JTextField ptf = new JTextField("21");
    // 用户名标签
    private JLabel uslb = new JLabel("user");
    //用户名文本域
    private JTextField ustf = new JTextField("fpga");
    // 密码标签
    private JLabel pslb = new JLabel("password");
    //密码文本域
    private JTextField pstf = new JTextField("fpga");
    // 连接按钮
    private JButton conBut = new JButton("go");
    //设定下载目录
    private JFileChooser chooser = new JFileChooser();
    //目录字符串
    private File file;
    //弹出菜单
    private JPopupMenu jpopmenu = new JPopupMenu();
    private JMenuItem downld = new JMenuItem("download");
    private JMenuItem upld = new JMenuItem("upload");
    //列表
    private JList listview = new JList();
    //列表选中项
    private String listSelect;


    public ClientView(){
        client = new FtpClient(this);
        //设置窗口标题和大小位置
        this.setTitle("Client");
        this.setBounds(600, 100, 900, 500);
        this.setLayout(new BorderLayout());
        //设定菜单栏
//        JMenuBar jmb = new JMenuBar();
//        JMenu jmset = new JMenu("set");
//        JMenuItem local = new JMenuItem("setlocation");
//        jmset.add(local);
//        jmb.add(jmset);
//        local.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//设置只能选择目录
//                int returnVal = chooser.showOpenDialog(chooser);
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    path = chooser.getSelectedFile().getPath();
//
//                    jta.append("你选择的目录是：" + path+"\n");
//                    chooser.hide();
//                }
//
//            }
//        });
//        this.setJMenuBar(jmb);
        //设定弹出菜单
        jpopmenu.add(downld);
        //单击下载响应
        downld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG | JFileChooser.DIRECTORIES_ONLY);
                chooser.showDialog(null,null);
                File fi = chooser.getSelectedFile();
                String path = fi.getAbsolutePath()+"\\"+listSelect.substring(listSelect.lastIndexOf(" ")+1);
                file = new File(path);
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException w) {
                         // TODO Auto-generated catch block
                        w.printStackTrace();
                    }
                }
                client.downldFile(listSelect.substring(listSelect.lastIndexOf(" ")+1));
            }
        });
        jpopmenu.addSeparator();
        jpopmenu.add(upld);
        //设定界面
        BorderLayout border = new BorderLayout();       
        Container content = this.getContentPane();   
        content. setLayout(border);
        jta = new JTextArea();
        JSplitPane hsplitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                true,			
                new JScrollPane(listview),			
                new JScrollPane(jta));	
        hsplitpane.setDividerLocation(1000000);
        hsplitpane.setOneTouchExpandable(true);
        hsplitpane.setLastDividerLocation(600);
        content.add(hsplitpane, BorderLayout.CENTER);

        JPanel jpd = new JPanel();
        new BoxLayout(jpd,BoxLayout.X_AXIS);
        jpd.add(iplb);
        jpd.add(iptf);
        jpd.add(plb);
        jpd.add(ptf);
        jpd.add(uslb);
        jpd.add(ustf);
        jpd.add(pslb);
        jpd.add(pstf);
        jpd.add(conBut);
        content.add(jpd, BorderLayout.NORTH);
        //连接按钮响应
        conBut.addActionListener(new HandlerClientCon(this));

        listview.addMouseListener(new MouseAdapter() {
            @Override
            //JList右键响应弹出popmenu
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);

            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()&&listview.getSelectedIndex()!=-1) {
                    //获取选择项的值
                    Object selected = listview.getModel().getElementAt(listview.getSelectedIndex());
                    listSelect = (String)selected;
                    jpopmenu.show(e.getComponent(),e.getX(), e.getY());
                }
            }

        });
        listview.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                twoClick(e);
            }public void twoClick(MouseEvent e) {
                if(listview.getSelectedIndex() != -1) {
                    if(e.getClickCount() == 2){
                        listSelect = (String) listview.getSelectedValue();
                        client.openfolder(listSelect);
                    }
                }

            }
        });

        this.setVisible(true);

    }
    public FtpClient getClient() {
        return client;
    }

    public void setClient(FtpClient client) {
        this.client = client;
    }

    public JTextArea getJta() {
        return jta;
    }

    public void setJta(JTextArea jta) {
        this.jta = jta;
    }

    public JTextField getIPtf() {
        return iptf;
    }


    public JTextField getptf() {
        return ptf;
    }


    public JTextField getUser(){
        return ustf;
    }
    public JTextField getPass(){
        return pstf;
    }
    public JList getList(){
        return listview;
    }
    public void setList(JList listview){
        this.listview = listview;
    }
    public File getFile(){
        return this.file;
    }


}
