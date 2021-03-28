package com.oim.swing.app;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.oim.core.business.module.NetModule;
import com.oim.core.business.module.SystemModule;
import com.oim.core.business.view.AddUserView;
import com.oim.core.business.view.ChatListView;
import com.oim.core.business.view.FindView;
import com.oim.core.business.view.GroupDataView;
import com.oim.core.business.view.JoinGroupView;
import com.oim.core.business.view.LoginView;
import com.oim.core.business.view.MainView;
import com.oim.core.business.view.NetSettingView;
import com.oim.core.business.view.RegisterView;
import com.oim.core.business.view.ThemeView;
import com.oim.core.business.view.TrayView;
import com.oim.core.business.view.UpdatePasswordView;
import com.oim.core.business.view.VideoView;
import com.oim.core.common.action.CallAction;
import com.oim.core.common.config.ConfigManage;
import com.oim.core.common.config.data.Theme;
import com.oim.swing.UIBox;
import com.oim.swing.view.AddUserViewImpl;
import com.oim.swing.view.ChatListViewImpl;
import com.oim.swing.view.FindViewImpl;
import com.oim.swing.view.GroupDataViewImpl;
import com.oim.swing.view.JoinGroupViewImpl;
import com.oim.swing.view.LoginViewImpl;
import com.oim.swing.view.MainViewImpl;
import com.oim.swing.view.NetSettingViewImpl;
import com.oim.swing.view.RegisterViewImpl;
import com.oim.swing.view.ThemeViewImpl;
import com.oim.swing.view.TrayViewImpl;
import com.oim.swing.view.UpdatePasswordViewImpl;
import com.oim.swing.view.VideoViewImpl;
import com.onlyxiahui.app.base.AppContext;



/**
 * @author: XiaHui
 * @date: 2017年9月7日 下午8:09:04
 */
public class Startup {

	protected AppContext appContext = new AppContext();

	public Startup() {
		initBase();
		initTheme();
		registerView();
		initPartView();
		showStartView();
		startThread();
		initView();
	}
	
	
	
	protected void initBase() {
		NetModule nm = appContext.getModule(NetModule.class);
		SystemModule sm = appContext.getModule(SystemModule.class);
		nm.start();
		sm.start();
	}

	protected void initTheme() {
		Theme theme = (Theme) ConfigManage.get(Theme.config_file_path, Theme.class);
		String backgroundImageUrl = theme.getBackgroundImage();
		//Color color = Color.color(theme.getRed(), theme.getGreen(), theme.getBlue(), theme.getOpacity());
		Image image = new ImageIcon(backgroundImageUrl).getImage();
		if (image.getWidth(null) > 0 && image.getHeight(null) > 0) {
//			BufferedImage bi = new BufferedImage(imaeg.getWidth(null), imaeg.getHeight(null), BufferedImage.TYPE_INT_RGB);
//			Graphics2D biContext = bi.createGraphics();
//			biContext.drawImage(imaeg, 0, 0, null);
			UIBox.put("key_window_background_image", image);
			UIBox.put("key_window_background_image_path", backgroundImageUrl);
		}
	}

	protected void registerView() {
		appContext.register(LoginView.class, LoginViewImpl.class);
		appContext.register(ChatListView.class, ChatListViewImpl.class);
		appContext.register(NetSettingView.class, NetSettingViewImpl.class);
		appContext.register(RegisterView.class, RegisterViewImpl.class);
		appContext.register(TrayView.class, TrayViewImpl.class);
		appContext.register(JoinGroupView.class, JoinGroupViewImpl.class);
		appContext.register(AddUserView.class, AddUserViewImpl.class);
		appContext.register(FindView.class, FindViewImpl.class);
		appContext.register(MainView.class, MainViewImpl.class);
		appContext.register(ThemeView.class, ThemeViewImpl.class);
		appContext.register(UpdatePasswordView.class, UpdatePasswordViewImpl.class);
		appContext.register(GroupDataView.class, GroupDataViewImpl.class);
		appContext.register(VideoView.class, VideoViewImpl.class);
	}

	protected void initPartView() {
		ChatListViewImpl chatListViewImpl = new ChatListViewImpl(appContext);
		appContext.put(ChatListViewImpl.class, chatListViewImpl);
	}

	protected void showStartView() {
		LoginView loginView = appContext.getSingleView(LoginView.class);
		loginView.setVisible(true);
	}

	protected void startThread() {
		CallAction exitAction = new CallAction() {
			@Override
			public void execute() {
				System.exit(0);
			}
		};
		NetModule nm = appContext.getModule(NetModule.class);
		SystemModule sm = appContext.getModule(SystemModule.class);
		nm.getConnectThread().setAutoConnectCount(10);// 设置自动重试次数
		sm.setExitAction(exitAction);
	}

	protected void initView() {
		new InitApp().start();
	}
	

	
	class InitApp extends Thread {
		@Override
		public void run() {
			/** 因为有比较多的图片，所以io操作比较多，为了在后面体验更好，所以异步把界面对象都实例化 **/
			long time = System.currentTimeMillis();
			
			appContext.getSingleView(MainView.class);
			appContext.getSingleView(ChatListView.class);
			appContext.getSingleView(TrayView.class);
			appContext.getSingleView(NetSettingView.class);
			appContext.getSingleView(RegisterView.class);
			appContext.getSingleView(ThemeView.class);
			appContext.getSingleView(JoinGroupView.class);
			appContext.getSingleView(AddUserView.class);
			appContext.getSingleView(FindView.class);
			appContext.getSingleView(MainView.class);
			
			SystemModule sm = appContext.getModule(SystemModule.class);
			sm.setViewReady(true);
			System.out.println(System.currentTimeMillis() - time);
		}

//		void initTheme() {
//			Theme theme = (Theme) ConfigManage.get(Theme.config_file_path, Theme.class);
//
//			Image imaeg = new ImageIcon(theme.getWindowBackgroundImage()).getImage();
//			BufferedImage bi = new BufferedImage(imaeg.getWidth(null), imaeg.getHeight(null), BufferedImage.TYPE_INT_RGB);
//
//			Graphics2D biContext = bi.createGraphics();
//			biContext.drawImage(imaeg, 0, 0, null);
//			bi = OnlyImageUtil.applyGaussianFilter(bi, null, theme.getGaussian());
//			UIBox.put("key_window_background_image", bi);
//			//			
//			for (OnlyBorderFrame ourFrame : UIBox.frameSet) {
//				ourFrame.setBackgroundImage(bi);
//			}
//			for (OnlyBorderDialog ourFrame : UIBox.dialogSet) {
//				ourFrame.setBackgroundImage(bi);
//			}
//		}
	}
}
