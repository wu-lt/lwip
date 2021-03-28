package com.oim.test.ui.main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;

import com.oim.swing.ui.component.list.HeadLabel;
import com.oim.swing.ui.component.list.Node;
import com.oim.swing.ui.component.list.Root;
import com.oim.swing.ui.main.TabPanel;
import com.only.OnlyBorderFrame;
import com.only.common.WindowAutoHide;

/**
 * @author XiaHui
 * @date 2015年1月5日 上午11:52:00
 */
public class MainTest extends OnlyBorderFrame {

	private static final long serialVersionUID = 1L;

	private TabPanel tabPanel = new TabPanel();
	private TabPanel panel = new TabPanel();
	private Root userRoot = new Root();

	/**
	 * Create the frame.
	 */
	public MainTest() {
		initComponent();
		initEvent();
		initUserList();
	}

	private void initComponent() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(280, 630);
		this.setMinimumSize(new java.awt.Dimension(280, 530));
		this.setMaximumSize(new java.awt.Dimension(520, 760));
		this.setLocationRelativeTo(null);
		this.setLayout(null);

		//this.setBackgroundImage(new ImageIcon("Resources/Images/Wallpaper/000.jpg").getImage());

		new WindowAutoHide(this);

		panel.setLayout(new CardLayout());

		JPanel b = new JPanel();
		JPanel g = new JPanel();
		JPanel r = new JPanel();

		b.setBackground(Color.blue);
		g.setBackground(Color.green);
		r.setBackground(Color.red);

		tabPanel.add(new ImageIcon("Resources/Images/Default/MainPanel/icon_contacts_normal.png"), new ImageIcon("Resources/Images/Default/MainPanel/icon_contacts_hover.png"), new ImageIcon("Resources/Images/Default/MainPanel/icon_contacts_selected.png"), userRoot);
		tabPanel.add(new ImageIcon("Resources/Images/Default/MainPanel/icon_group_normal.png"), new ImageIcon("Resources/Images/Default/MainPanel/icon_group_hover.png"), new ImageIcon("Resources/Images/Default/MainPanel/icon_group_selected.png"), g);
		tabPanel.add(new ImageIcon("Resources/Images/Default/MainPanel/icon_last_normal.png"), new ImageIcon("Resources/Images/Default/MainPanel/icon_last_hover.png"), new ImageIcon("Resources/Images/Default/MainPanel/icon_last_selected.png"), r);

		panel.setOpaque(false);
		// panel.add(tabPanel);

		add(tabPanel);

	}

	private void initEvent() {

	}

	private void initUserList() {
		Node[] node = new Node[5];
		for (int j = 0; j < 5; j++) {
			node[j] = new Node();
			node[j].setTitleText("我的好友" + j);

			for (int i = 0; i < 5; i++) {

				HeadLabel head = new HeadLabel();

				head.setRoundedCorner(40, 40);
				
				head.setStatusIcon(new ImageIcon("Resources/Images/Default/Status/FLAG/Big/MobilePhoneQQOn.png"));

				head.setHeadIcon(new ImageIcon("Resources/Images/Head/User/"+(i+j+1)+"_100.gif"));

				head.setRemark("女神经");
				head.setNickname("(也的JJ刚刚)");
				head.setStatus("[2G]");
				head.setShowText("哈哈哈，又有新闻了");
				head.addBusinessAttribute("s",new JLabel(new ImageIcon("Resources/Images/Default/Status/FLAG/Big/imonline.png")));

				node[j].addNode(head);
			}
			userRoot.addNode(node[j]);
		}
	}

	public void formComponentResized() {
		tabPanel.setBounds(1, 100, this.getWidth() - 2, this.getHeight() - 160);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			// for (javax.swing.UIManager.LookAndFeelInfo info :
			// javax.swing.UIManager.getInstalledLookAndFeels()) {
			// if ("Nimbus".equals(info.getName())) {
			// javax.swing.UIManager.setLookAndFeel(info.getClassName());
			// break;
			// }
			// }
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainTest frame = new MainTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
