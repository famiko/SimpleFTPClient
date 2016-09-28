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
    // FtpClient�ͻ���
    private FtpClient client;
    // �ı���
    private JTextArea jta;
    // IP��ǩ
    private JLabel iplb = new JLabel("ip");
    // IP��ַ�ı���
    private JTextField iptf = new JTextField("10.78.22.223");
    // �˿ںű�ǩ
    private JLabel plb = new JLabel("port");
    // �˿ں��ı���
    private JTextField ptf = new JTextField("21");
    // �û�����ǩ
    private JLabel uslb = new JLabel("user");
    //�û����ı���
    private JTextField ustf = new JTextField("fpga");
    // �����ǩ
    private JLabel pslb = new JLabel("password");
    //�����ı���
    private JTextField pstf = new JTextField("fpga");
    // ���Ӱ�ť
    private JButton conBut = new JButton("go");
    //�趨����Ŀ¼
    private JFileChooser chooser = new JFileChooser();
    //Ŀ¼�ַ���
    private File file;
    //�����˵�
    private JPopupMenu jpopmenu = new JPopupMenu();
    private JMenuItem downld = new JMenuItem("download");
    private JMenuItem upld = new JMenuItem("upload");
    //�б�
    private JList listview = new JList();
    //�б�ѡ����
    private String listSelect;


    public ClientView(){
        client = new FtpClient(this);
        //���ô��ڱ���ʹ�Сλ��
        this.setTitle("Client");
        this.setBounds(600, 100, 900, 500);
        this.setLayout(new BorderLayout());
        //�趨�˵���
//        JMenuBar jmb = new JMenuBar();
//        JMenu jmset = new JMenu("set");
//        JMenuItem local = new JMenuItem("setlocation");
//        jmset.add(local);
//        jmb.add(jmset);
//        local.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//����ֻ��ѡ��Ŀ¼
//                int returnVal = chooser.showOpenDialog(chooser);
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    path = chooser.getSelectedFile().getPath();
//
//                    jta.append("��ѡ���Ŀ¼�ǣ�" + path+"\n");
//                    chooser.hide();
//                }
//
//            }
//        });
//        this.setJMenuBar(jmb);
        //�趨�����˵�
        jpopmenu.add(downld);
        //����������Ӧ
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
        //�趨����
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
        //���Ӱ�ť��Ӧ
        conBut.addActionListener(new HandlerClientCon(this));

        listview.addMouseListener(new MouseAdapter() {
            @Override
            //JList�Ҽ���Ӧ����popmenu
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);

            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()&&listview.getSelectedIndex()!=-1) {
                    //��ȡѡ�����ֵ
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
