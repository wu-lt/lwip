package com.oim.test.ui.component.list;

import java.awt.CardLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UnsupportedLookAndFeelException;

import com.oim.core.common.util.ImageUtil;
import com.oim.swing.ui.component.list.ItemPanel;
import com.only.OnlyBorderFrame;
import com.only.OnlyScrollPane;
import com.only.layout.ListLayout;

/**
 * @author XiaHui
 * @date 2015年1月5日 上午11:52:00
 */
public class ItemPanelTest extends OnlyBorderFrame {

	private static final long serialVersionUID = 1L;
	private JPanel chatPanel = new JPanel();
	private JPanel rootPanel = new JPanel();
	private OnlyScrollPane scrollPane = new OnlyScrollPane();

	/**
	 * Create the frame.
	 */
	public ItemPanelTest() {
		initComponent();
		initData();
	}

	private void initComponent() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 450, 300);
		// this.setMinimumSize(new java.awt.Dimension(520, 500));
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		chatPanel.setLayout(new CardLayout());
		chatPanel.add(scrollPane);

		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHeaderVisible(false);
		// scrollPane.setAlpha(0.0f);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);

		scrollPane.setViewportView(rootPanel);
		rootPanel.setLayout(new ListLayout(ListLayout.Y_AXIS, 30, 2,ListLayout.PREFER_SIZE));
		// rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
		add(chatPanel);
	}

	private void initData() {
		for (int i = 0; i < 10; i++) {
			ItemPanel item = new ItemPanel();
			item.setText("jjjjjjjjkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkjas");
			item.setImageIcon(ImageUtil.getRoundedCornerImageIcon("Resources/Images/Head/User/" + (i + 1) + ".png", 25, 25, 6, 6));
			rootPanel.add(item);
		}
	}

	public void formComponentResized() {
		chatPanel.setBounds(3, 50, this.getWidth() - 6, this.getHeight() - 100);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
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
					ItemPanelTest frame = new ItemPanelTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
