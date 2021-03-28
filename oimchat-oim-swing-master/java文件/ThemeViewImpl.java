package com.oim.swing.view;

import com.oim.core.business.view.ThemeView;
import com.oim.swing.ui.ThemeFrame;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月16日 下午10:42:19
 * @version 0.0.1
 */
public class ThemeViewImpl extends AbstractView implements ThemeView {

	ThemeFrame findFrame = new ThemeFrame();

	public ThemeViewImpl(AppContext appContext) {
		super(appContext);
	}

	public void setVisible(boolean visible) {
		findFrame.setVisible(visible);
	}

	public boolean isShowing() {
		return findFrame.isShowing();
	}

	@Override
	public void showPrompt(String text) {
		
	}
}
