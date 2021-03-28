package com.oim.swing.view;

import javax.swing.JFrame;

import com.oim.core.business.view.NetSettingView;
import com.oim.core.common.config.ConfigManage;
import com.oim.core.common.config.data.ServerConfig;
import com.oim.swing.ui.NetSettingDialog;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;

/**
 * @author XiaHui
 * @date 2015年3月16日 上午11:48:23
 */
public class NetSettingViewImpl  extends AbstractView implements NetSettingView{
	
	NetSettingDialog netSettingDialog = new NetSettingDialog(new JFrame(), "网络录设置", false);;
	
	public NetSettingViewImpl(AppContext appContext) {
		super(appContext);
		initEvent();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			if (!netSettingDialog.isShowing()) {
				initConfig();
			}
		} 
		netSettingDialog.setVisible(visible);
		netSettingDialog.setAlwaysOnTop(visible);
	}

	private void initEvent() {
		
		
		netSettingDialog.setDoneAction(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				netSettingDialog.setVisible(false);
				String address = netSettingDialog.getAddress();
				ServerConfig sc = (ServerConfig) ConfigManage.get(ServerConfig.path, ServerConfig.class);
				sc.setAddress(address);
				ConfigManage.addOrUpdate(ServerConfig.path, sc);
			}
		});
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

	@Override
	public void setAddress(String address) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAddress() {
		return null;
	}
	private void initConfig() {
		ServerConfig sc = (ServerConfig) ConfigManage.get(ServerConfig.path, ServerConfig.class);
		netSettingDialog.setAddress(sc.getAddress());
	}
}
