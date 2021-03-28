package com.oim.swing.view;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.oim.common.event.ExecuteAction;
import com.oim.core.business.manage.HeadImageManage;
import com.oim.core.business.manager.PersonalManager;
import com.oim.core.business.manager.PromptManager;
import com.oim.core.business.module.SystemModule;
import com.oim.core.business.view.LoginView;
import com.oim.core.business.view.MainView;
import com.oim.core.business.view.TrayView;
import com.oim.core.common.app.dto.PromptData;
import com.oim.core.common.app.dto.PromptData.IconType;
import com.oim.swing.common.box.HeadImageIconBox;
import com.oim.swing.common.box.ImageBox;
import com.oim.swing.ui.tray.Tray;
import com.oim.swing.ui.tray.TrayPopupMenu;
import com.only.OnlyTrayIcon;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月16日 下午9:16:16
 * @version 0.0.1
 */
public class TrayViewImpl extends AbstractView implements TrayView {

	private Image image = new ImageIcon("Resources/Images/Logo/logo_16.png").getImage();
	private OnlyTrayIcon trayIcon;
	private TrayPopupMenu trayPopupMenu = new TrayPopupMenu();
	private Image tempImage = new ImageIcon("Resources/Images/Tray/01.png").getImage();
	private ConcurrentLinkedQueue<String> keyQueue = new ConcurrentLinkedQueue<String>();// 消息队列
	private boolean isShowTempImage = false;
	private boolean isShowTrayImage = true;
	private String key = null;

	public TrayViewImpl(AppContext appContext) {
		super(appContext);
		initTrayIcon();
		initTray();
		initEvent();
	}

	private void initTrayIcon() {
		trayIcon = new OnlyTrayIcon(image, "OIM");
		trayIcon.setMenu(trayPopupMenu);
		this.showAllMenu(false);
	}

	private void initEvent() {
		Timer timer = new Timer();
		timer.schedule(new PromptTask(), 1000, 600);
		trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				trayMouseClicked(evt);
			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				trayMouseEntered(evt);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				trayMouseExited(evt);
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				trayMousePressed(evt);
			}
		});

		trayPopupMenu.setStatusAction(new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof String) {
					PersonalManager pm = appContext.getManager(PersonalManager.class);
					pm.updateStatus((String) value);
				}
				return null;
			}

		});

		trayPopupMenu.addExitActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exit();
			}
		});
		trayPopupMenu.addOpenMainActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showMainFrame();
			}
		});
	}

	private void initTray() {
		try {
			if (SystemTray.isSupported()) {
				SystemTray tray = SystemTray.getSystemTray();
				tray.add(trayIcon);
			}
		} catch (AWTException ex) {
			Logger.getLogger(Tray.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void trayMouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
			doAction();
		}
	}

	private void trayMouseEntered(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
	}

	private void trayMouseExited(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
	}

	private void trayMousePressed(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
	}

	public void setImage(Image image) {
		trayIcon.setImage(image);
	}

	public void showAllMenu(boolean show) {
		trayPopupMenu.showAll(show);
	}

	public void exit() {
		SystemModule sm = appContext.getModule(SystemModule.class);
		sm.exit();
	}

	public void changeStatus(String status) {
		PersonalManager pm = this.appContext.getManager(PersonalManager.class);
		pm.updateStatus(status);
	}

	public void doAction() {
		if (null != key) {
			PromptManager pm = appContext.getManager(PromptManager.class);
			pm.execute(key);
			key = null;
		} else {
			showMainFrame();
		}
	}

	public void showMainFrame() {
		SystemModule sm = appContext.getModule(SystemModule.class);
		if (sm.isLogin()) {
			MainView mainView = appContext.getSingleView(MainView.class);
			mainView.setVisible(true);
		} else {
			LoginView loginView = appContext.getSingleView(LoginView.class);
			loginView.setVisible(true);
		}
	}

	public void put(String key) {
		keyQueue.add(key);
	}

	class PromptTask extends TimerTask {

		@Override
		public void run() {
			Image showImage = null;
			if (key == null && keyQueue.peek() != null) {
				key = keyQueue.poll();
			}
			if (key != null) {
				PromptManager pm = appContext.getManager(PromptManager.class);
				PromptData pd = pm.getPromptData(key);
				if (null!=pd&&null != pd.getIcon() && !"".equals(pd.getIcon())) {
					if (pd.getIconType() == IconType.userHead) {
						//ImageIcon imageIcon = HeadImageIconBox.getUserHeadImageIcon(pd.getIcon(), 16);
						//TODO 消息头像提醒做的有点生硬，有机会再重构
						HeadImageManage him=appContext.getManager(HeadImageManage.class);
						String path=him.getUserHeadImagePath(pd.getKey(), 16);
						showImage =ImageBox.getImage(path,16,16);
						//showImage = (null == imageIcon) ? null : imageIcon.getImage();
					}
					if (pd.getIconType() == IconType.groupHead) {
						ImageIcon imageIcon = HeadImageIconBox.getGroupHeadImageIcon(pd.getIcon(), 16);
						showImage = (null == imageIcon) ? null : imageIcon.getImage();
					}
					if (pd.getIconType() == IconType.pathImage) {
						String path=pd.getIcon();
						showImage =ImageBox.getImage(path,16,16);
						//showImage = (null == imageIcon) ? null : imageIcon.getImage();
					}
				}
				if (null == showImage) {
					key = null;
				}
			}
			if (showImage != null) {
				if (isShowTempImage) {
					isShowTempImage = false;
					trayIcon.setImage(showImage);
				} else {
					isShowTempImage = true;
					trayIcon.setImage(tempImage);
				}
				isShowTrayImage = false;
			} else {
				if (!isShowTrayImage) {
					isShowTrayImage = true;
					trayIcon.setImage(image);
				}
			}
		}
	}

	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println(1111);
			}

		}, 1000, 800);
	}

	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showPrompt(String text) {
		// TODO Auto-generated method stub

	}
}
