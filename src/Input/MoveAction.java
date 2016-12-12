package Input;

import tcp.tcpClient.RealTcpClient;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by TsunglinYang on 2016/12/7.
 */
public class MoveAction extends AbstractAction {
    private int direction;

    public MoveAction(int dir) {
        direction = dir;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// TODO follow API
			RealTcpClient.getInstance().inputMoves(0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

    }
}

