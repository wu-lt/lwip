package com.oim.swing.ui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author: XiaHui
 * @date: 2017年7月26日 下午3:59:47
 */
public class ImageStyleButton extends JButton {

	private static final long serialVersionUID = 578291335105681478L;
	private Color backgroundColor = null;
	// private Color normalColor = new Color(40, 63, 85);
	private Color rolloverColor = new Color(200, 200, 200, 200);
	private Color selectedColor = new Color(26, 188, 156);

	private boolean entered;

	public ImageStyleButton() {

		initComponents();
		initEvent();
	}
	
	public ImageStyleButton(Icon normalIcon, Icon rolloverIcon, Icon pressedIcon) {
		initComponents();
		initEvent();
		this.setIcon(normalIcon);
		this.setPressedIcon(pressedIcon);
		this.setRolloverIcon(rolloverIcon);
	}

	private void initComponents() {
		this.setBorder(null);
		this.setContentAreaFilled(false);
		this.setFocusPainted(false);
		this.setDoubleBuffered(true);

		this.setOpaque(false);
		this.setFocusable(false);
		this.setActionCommand(null);
		this.setSelected(false);
	}

	private void initEvent() {
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				tabMouseClicked(evt);
			}

			public void mouseEntered(java.awt.event.MouseEvent evt) {
				tabMouseEntered(evt);
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				tabMouseExited(evt);
			}

		});

	}

	private void tabMouseClicked(MouseEvent evt) {
		// TODO Auto-generated method stub

	}

	private void tabMouseEntered(MouseEvent evt) {
		entered = true;
		update();
	}

	private void tabMouseExited(MouseEvent evt) {
		entered = false;
		update();
	}

	private void update() {
		boolean selected = this.isSelected();
		if (selected) {
			this.setBackgroundColor(selectedColor);
		} else {
			if (entered) {
				this.setBackgroundColor(rolloverColor);
			} else {
				this.setBackgroundColor(null);
			}
		}
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		// Color backgroundColor=this.getBackground();
		if (null != backgroundColor && backgroundColor.getAlpha() > 0) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(backgroundColor);
			g2.fillRect(0, 0, this.getWidth() + 1, this.getHeight() + 1);
		}
		super.paintComponent(g);
	}
}
