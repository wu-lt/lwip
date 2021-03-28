package com.oim.swing.view;

import com.only.OnlyBorderFrame;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月16日 下午10:42:19
 * @version 0.0.1
 */
public class UserDataView extends AbstractView {

	OnlyBorderFrame frame = new OnlyBorderFrame();

	public UserDataView(AppContext appContext) {
		super(appContext);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

}
