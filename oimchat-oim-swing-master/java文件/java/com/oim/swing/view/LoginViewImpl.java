package com.oim.swing.view;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.oim.core.business.controller.PersonalController;
import com.oim.core.business.manage.HeadImageManage;
import com.oim.core.business.module.SystemModule;
import com.oim.core.business.view.LoginView;
import com.oim.core.business.view.NetSettingView;
import com.oim.core.common.config.ConfigManage;
import com.oim.core.common.data.UserSaveData;
import com.oim.core.common.data.UserSaveDataBox;
import com.oim.swing.ui.LoginFrame;
import com.oim.swing.ui.RegisterDialog;
import com.oim.swing.ui.login.ComboBoxAction;
import com.only.common.util.OnlyMD5Util;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;
import com.onlyxiahui.im.message.data.LoginData;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月8日 下午4:10:51
 * @version 0.0.1
 */
public class LoginViewImpl extends AbstractView implements LoginView {

	LoginFrame loginFrame = new LoginFrame();
	RegisterDialog registerDialog;

	public LoginViewImpl(AppContext appContext) {
		super(appContext);
		initUI();
		initEvent();
	}

	private void initUI() {
		registerDialog = new RegisterDialog(loginFrame, "用户注册", true);
		final UserSaveDataBox usdb = (UserSaveDataBox) ConfigManage.get(UserSaveDataBox.path, UserSaveDataBox.class);
		if (null != usdb && null != usdb.getMap() && !usdb.getMap().isEmpty()) {
			Map<String, UserSaveData> map = usdb.getMap();
			List<UserSaveData> list = new ArrayList<UserSaveData>(map.values());
			UserSaveData ud = list.get(0);
			for (UserSaveData u : list) {
				loginFrame.addAccount(u.getAccount());
			}
			HeadImageManage him = appContext.getManager(HeadImageManage.class);
			Image image = him.getUserHeadImage(ud.getUserHead(), 80, 0);
			loginFrame.setHeadIcon(new ImageIcon(image));
		}
		loginFrame.add(new ComboBoxAction() {
			ImageIcon imageIcon = new ImageIcon("Resources/Images/login/1.png");

			@Override
			public void itemChange(String text) {
				if (null != text) {
					UserSaveData ud = usdb.get(text);
					if (null != ud) {
						HeadImageManage him = appContext.getManager(HeadImageManage.class);
						Image image = him.getUserHeadImage(ud.getUserHead(), 80, 0);
						loginFrame.setHeadIcon(new ImageIcon(image));
					} else {
						loginFrame.setHeadIcon(imageIcon);
					}
				}
			}

			@Override
			public void delete(Object o) {
				if (null != o) {
					usdb.remove(o.toString());
					ConfigManage.addOrUpdate(UserSaveDataBox.path, usdb);
				}
			}

			@Override
			public void select(Object o) {
				if (null != o) {
					UserSaveData ud = usdb.get(o.toString());
					if (null != ud) {
						HeadImageManage him = appContext.getManager(HeadImageManage.class);
						Image image = him.getUserHeadImage(ud.getUserHead(), 80,0);
						loginFrame.setHeadIcon(new ImageIcon(image));
					} else {
						loginFrame.setHeadIcon(imageIcon);
					}
				}
			}
		});
	}

	private void initEvent() {
		loginFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				closing();
			}
		});
		loginFrame.addLoginKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyPressed(java.awt.event.KeyEvent evt) {
				formKeyPressed(evt);
			}
		});
		loginFrame.addLoginAction(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loginActionPerformed(evt);
			}
		});
		loginFrame.addRegisterListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openRegisterDialog();
			}
		});
		loginFrame.addSettingAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openSettingDialog();

			}
		});
	}

	private void formKeyPressed(java.awt.event.KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
			login();
		}
	}

	private void loginActionPerformed(java.awt.event.ActionEvent evt) {
		login();
	}

	public void setVisible(boolean visible) {
		loginFrame.setVisible(visible);
	}

	public void login() {

		if (!loginFrame.isCheck()) {
			return;
		}

		String account = loginFrame.getAccount();
		String password = loginFrame.getPassword();
		String status = loginFrame.getStatus();
		LoginData loginData = new LoginData();

		loginData.setStatus(status);
		loginData.setAccount(account);
		loginData.setPassword(OnlyMD5Util.md5L32(password));

		PersonalController pc = appContext.getController(PersonalController.class);
		pc.login(loginData);
		// if (loginFrame.isCheck()) {
		// LoginData loginData = new LoginData();
		// loginData.setStatus(status);
		// loginData.setAccount(account);
		// loginData.setPassword(password);
		// loginFrame.showWaiting(true);
		// final DataBackActionAdapter action = new DataBackActionAdapter() {//
		// 这是消息发送后回掉
		// @Override
		// public void lost() {
		// showWaiting(false);
		// showPromptMessage("登录失败，请检查网络是否正常。");
		// }
		//
		// @Override
		// public void timeOut() {
		// showWaiting(false);
		// showPromptMessage("登录超时，请检查网络是否正常。");
		// }
		// };
		// PersonalHandler ph =
		// this.appContext.getHandler(PersonalHandler.class);
		// ph.login(loginData, action);
		// }
	}

	@Override
	public void showPrompt(String text) {
		loginFrame.showPrompt(text);
	}

	public void showWaiting(boolean show) {
		loginFrame.showWaiting(show);
	}

	public void showPromptMessage(String text) {
		loginFrame.showPrompt(text);
	}

	public void closing() {
		SystemModule sm = appContext.getModule(SystemModule.class);
		sm.exit();
	}

	public void openRegisterDialog() {
		registerDialog.clear();
		registerDialog.setLocationRelativeTo(loginFrame);
		registerDialog.setVisible(true);
	}

	public void openSettingDialog() {
		NetSettingView netSettingView = appContext.getSingleView(NetSettingView.class);
		netSettingView.setVisible(true);
	}

	@Override
	public boolean isShowing() {
		return loginFrame.isShowing();
	}

	@Override
	public boolean isSavePassword() {
		// TODO Auto-generated method stub
		return false;
	}
}
