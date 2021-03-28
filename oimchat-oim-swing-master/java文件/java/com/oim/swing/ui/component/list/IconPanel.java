package com.oim.swing.ui.component.list;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月1日 上午8:34:37
 * @version 0.0.1
 */
public class IconPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	//private OverDecoratedImage label = new OverDecoratedImage();
	private JLabel label = new JLabel();
	ImageIcon imageIcon;
	private int rx = 6;
	private int ry = 6;
	// private Color borderColor = new Color(169, 238, 236);
	private Color borderColor = new Color(102, 213, 132);

	// private Color borderColorTemp = new Color(169, 238, 236,120);
	private boolean drawBorder = false;
	private JPanel statusIconPanel = new JPanel();
	private JLabel statusIconLabel = new JLabel();

	public IconPanel() {
		this.setOpaque(false);
		// this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3,
		// 3));
		this.setLayout(new java.awt.GridBagLayout());

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));

		//label.setBounds(r);
		//label.setShadeWidth(40);
		
		//label.setSize(40, 40);
		
		//label.setDrawGlassLayer(false);
		//label.setDrawBorder(false);
		//label.setBorderColor(borderColor);
		//label.setBackground(borderColor);
		
		label.setLayout(new CardLayout());
		label.add(statusIconPanel);

		statusIconPanel.setLayout(new java.awt.BorderLayout());
		statusIconPanel.setOpaque(false);

		JPanel statusTempPanel = new JPanel();
		statusTempPanel.setOpaque(false);
		statusTempPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 0, 0));
		statusTempPanel.add(statusIconLabel);

		statusIconPanel.add(statusTempPanel, java.awt.BorderLayout.PAGE_END);

		panel.add(label);

		add(panel);
		// TODO
		// statusIconLabel.setIcon(new
		// ImageIcon("Resources/Images/Default/Status/FLAG/Big/MobilePhoneQQOn.png"));

	}

	public void setImageIcon(ImageIcon imageIcon) {
		//this.imageIcon = imageIcon;
		label.setIcon(imageIcon);
		//label.repaint();
	}

	public void setStatusIcon(Icon icon) {
		statusIconLabel.setIcon(icon);
	}

	public void setRoundedCorner(int rx, int ry) {
		if (this.rx != rx || this.ry != ry) {
			this.rx = rx;
			this.ry = ry;
			
			//label.setSize(ry, ry);
			//label.setRound(ry);
			repaint();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(1));
		if (drawBorder) {// 边框
			// g2.setColor(borderColorTemp);
			// g2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1,
			// rx, ry);

			g2.setColor(borderColor);
			g2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, rx, ry);
		}
	}

	public boolean isGray() {
		return label.isEnabled();
	}

	public void setGray(boolean gray) {
		label.setEnabled(!gray);
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		repaint();
	}

	public boolean isDrawBorder() {
		return drawBorder;
	}

	public void setDrawBorder(boolean drawBorder) {
		this.drawBorder = drawBorder;
		repaint();
	}

	class ImageLabel extends JLabel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			//super.paint(g);
			if (null != imageIcon) {
				Image image = imageIcon.getImage();
				if (null != image) {
					int w = this.getWidth();
					int h = this.getHeight();

					Graphics2D g2 = (Graphics2D) g;

					if (w > 0 && h > 0) {

					} else {
						w = image.getWidth(null);
						h = image.getHeight(null);
					}
					g2.setComposite(AlphaComposite.Src);
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setStroke(new BasicStroke(1));
					g2.fillRoundRect(0, 0, w, h, rx, ry);
					g2.setComposite(AlphaComposite.SrcAtop);
					g2.drawImage(image, 0, 0, w, h, null);
					g2.dispose();
				}
			}else {
				super.paint(g);
			}
		}
	}
}
