package com.oim.test.ui.video;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class VideoWebFrameTest extends JFrame {

	private static final long serialVersionUID = 1L;

	JPanel basePanel = new JPanel();
	JPanel buttonPanel = new JPanel(new FlowLayout());
	JPanel tabPanel = new JPanel();
	JPanel ownPanel = new JPanel();
	JTabbedPane tabbedPane = new JTabbedPane();
	boolean start = false;
	JLabel videoLabel = new JLabel();
	JPanel videoPanel = new JPanel();

	public VideoWebFrameTest() {
		initUI();
		init();
	}

	private void initUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("视频聊天");
		this.setSize(480, 680);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.setLayout(new CardLayout());
		this.add(basePanel);

		
		basePanel.setLayout(new BorderLayout());
		basePanel.add(videoPanel, BorderLayout.CENTER);
		basePanel.add(buttonPanel, BorderLayout.NORTH);

		videoPanel.setLayout(new CardLayout());
		videoPanel.setBackground(Color.blue);
		videoPanel.add(videoLabel);
		
		JButton startButton = new JButton("开始");
		buttonPanel.add(startButton);
		startButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				start = true;
			}
		});
		JButton stopButton = new JButton("停止");
		buttonPanel.add(stopButton);
		stopButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				start = false;
			}
		});
	}

	private void init() {
		new ImageThread().start();
	}

	public static void main(String[] args) {

		VideoWebFrameTest camera = new VideoWebFrameTest();
		camera.setVisible(true);
	}

	class ImageThread extends Thread {

		public void run() {
			while (true) {
				try {
					createImage();
					sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void createImage() {
		}
	}
}