package com.oim.swing.ui.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.only.OnlyLabel;

/**
 * @author XiaHui
 * @date 2015年2月28日 下午2:57:04
 */
public class Tab extends JPanel {

	private static final long serialVersionUID = 1L;

	private Color backgroundColor = new Color(255, 255, 255, 230);

	private Icon normalIcon = null;
	private Icon rolloverIcon = null;
	private Icon selectedIcon = null;

	//	private Color normalColor = new Color(250, 250, 250,230);
	//	private Color rolloverColor = new Color(165, 218, 175,230);
	//	private Color selectedColor = new Color(52, 204, 51,230);

	//	private Color normalColor = new Color(240, 240, 240);
//	private Color normalColor = new Color(40, 63, 85);
//	private Color rolloverColor = new Color(165, 218, 175);
//	private Color selectedColor = new Color(26, 188, 156);

	private Color normalColor = new Color(194, 194, 173);
	private Color rolloverColor = new Color(165, 218, 175);
	private Color selectedColor = new Color(62, 136, 245);
	
	private boolean selected = false;
	private boolean entered = false;
	private OnlyLabel iconLabel = new OnlyLabel();
	private Component component;

	JPanel pane=new JPanel();
	JPanel rootPane=new JPanel();
	JSeparator separator = new javax.swing.JSeparator();
	public Tab() {
		initComponent();
		initEvent();
	}
	
	public Tab(Icon normalIcon, Icon rolloverIcon, Icon selectedIcon) {
		this( null, normalIcon,  rolloverIcon,  selectedIcon);
	}
	
	public Tab(String text,Icon normalIcon, Icon rolloverIcon, Icon selectedIcon) {
		this.normalIcon = normalIcon;
		this.selectedIcon = selectedIcon;
		this.rolloverIcon = rolloverIcon;
		if(null!=text){
			this.setText(text);
		}
		initComponent();
		initEvent();
	}

	private void initComponent() {
		this.setOpaque(false);
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setLayout(new CardLayout());
		//this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 2));
		//this.setLayout(null);
		this.setLayout(new java.awt.BorderLayout());
		rootPane.setOpaque(false);
		rootPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 2));
		rootPane.add(iconLabel);
		
		this.add(rootPane, java.awt.BorderLayout.CENTER);
		this.add(pane, java.awt.BorderLayout.PAGE_END);
		
		//pane.setMaximumSize(new Dimension(2,2));
		//pane.setSize(2, 2);
		pane.setPreferredSize(new Dimension(2,2));
		//add(rootPane);
		//add(pane);
		this.setBackgroundColor(null);
		update();
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
//		this.addComponentListener(new ComponentAdapter() {
//			public void componentResized(ComponentEvent e) {
//				resized();
//			}
//		});
	}

	void resized(){
		
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (null != backgroundColor && backgroundColor.getAlpha() > 0) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(backgroundColor);
			g2.fillRect(0, 0, this.getWidth() + 1, this.getHeight() + 1);
		}
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
		if (selected) {
			iconLabel.setIcon(selectedIcon);
			pane.setBackground(selectedColor);
			//this.setBackgroundColor(selectedColor);
		} else {
			if (entered) {
				iconLabel.setIcon(rolloverIcon);
				pane.setBackground(rolloverColor);
				//this.setBackgroundColor(rolloverColor);
			} else {
				iconLabel.setIcon(normalIcon);
				pane.setBackground(normalColor);
				//this.setBackgroundColor(normalColor);
			}
		}
	}

	public Icon getNormalIcon() {
		return normalIcon;
	}

	public void setNormalIcon(Icon normalIcon) {
		this.normalIcon = normalIcon;
		update();
	}

	public Icon getSelectedIcon() {
		return selectedIcon;
	}

	public void setSelectedIcon(Icon selectedIcon) {
		this.selectedIcon = selectedIcon;
		update();
	}

	public Icon getRolloverIcon() {
		return rolloverIcon;
	}

	public void setRolloverIcon(Icon rolloverIcon) {
		this.rolloverIcon = rolloverIcon;
		update();
	}

	public Color getNormalColor() {
		return normalColor;
	}

	public void setNormalColor(Color normalColor) {
		this.normalColor = normalColor;
		update();
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
		update();
	}

	public Color getRolloverColor() {
		return rolloverColor;
	}

	public void setRolloverColor(Color rolloverColor) {
		this.rolloverColor = rolloverColor;
		update();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		update();
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		this.repaint();
	}

	public void setText(String text){
		iconLabel.setText(text);
	}
	
	public void setTextForeground(Color color){
		iconLabel.setForeground(color);
	}
}
