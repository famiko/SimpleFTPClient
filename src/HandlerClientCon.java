/**
 * Created by Jsnow on 2015/12/27.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class HandlerClientCon implements ActionListener {
    private ClientView clientView;

    public HandlerClientCon() {
        super();
    }

    public HandlerClientCon(ClientView clientView) {
        super();
        this.clientView = clientView;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            clientView.getClient().connect();
            clientView.getClient().getList();
        }catch (java.lang.NullPointerException r){
            r.printStackTrace();
        }

    }

}
