/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oim.swing.ui;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import com.oim.common.component.AlphaPanel;
import com.oim.swing.ui.component.BaseDialog;
import com.only.OnlyButton;
import com.only.OnlyLabel;
import com.only.OnlyTextField;

/**
 * 
 * @author XiaHui
 */
@SuppressWarnings("serial")
public class NetSettingDialog extends BaseDialog {

	OnlyButton saveButton = new OnlyButton();
	OnlyButton cancelButton = new OnlyButton();
	OnlyLabel addressLabel = new OnlyLabel();
	OnlyTextField addressTextField = new OnlyTextField();

	AlphaPanel panel = new AlphaPanel();

	public NetSettingDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		initEvent();
	}

	public NetSettingDialog(JFrame frame, String text, boolean b) {
		super(frame, text, b);
		initComponents();
		initEvent();
	}

	private void initComponents() {
		this.setLayout(null);
		this.setResizable(false);
		this.setSize(420, 140);
		this.setShowIconImage(false);
		this.setTitle("网络设置");
		this.getRootPane().setBackground(new Color(0, 128, 157));
		this.setLocationRelativeTo(null);
		
		//panel.setOpaque(false);
		panel.setLayout(null);
		//panel.setImageAlpha(0.4f);
		//panel.setBackground(new Color(69, 159, 134));
		//panel.setBackgroundColor(new Color(69, 159, 134));
		//panel.setBackgroundColor(new Color(255, 255, 255));
		panel.setOpaque(false);
		panel.setBackgroundColorAlpha(100);
		panel.setBounds(0, 40, 420, 100);

		addressLabel.setText("服务器地址:");

		addressLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

		saveButton.setText("确定");
		cancelButton.setText("取消");

		addressLabel.setBounds(20, 15, 80, 25);
		addressTextField.setBounds(105, 15, 250, 25);


		cancelButton.setBounds(30, 55, 60, 25);
		saveButton.setBounds(310, 55, 60, 25);

		panel.add(addressLabel);
		panel.add(addressTextField);
		panel.add(saveButton);
		panel.add(cancelButton);

		add(panel);
	}

	private void initEvent() {
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
	}

	public void setDoneAction(ActionListener l) {
		saveButton.addActionListener(l);
	}

	public void setAddress(String value) {
		addressTextField.setText(value);
	}

	public String getAddress() {
		return addressTextField.getText();
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
		this.setVisible(false);
	}


	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	public static void main(String args[]) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(NetSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(NetSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(NetSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(NetSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/*
		 * Create and display the dialog
		 */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				NetSettingDialog dialog = new NetSettingDialog(new javax.swing.JFrame(), "登录设置", true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}
}
