package com.oim.swing.view;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import com.oim.core.business.box.ServerAddressBox;
import com.oim.core.business.constant.ServerAddressConstant;
import com.oim.core.business.manager.ServerAddressManager;
import com.oim.core.business.view.RegisterView;
import com.oim.core.net.http.HttpHandler;
import com.oim.core.net.http.Request;
import com.oim.swing.ui.RegisterDialog;
import com.only.common.result.Info;
import com.only.common.result.util.MessageUtil;
import com.only.common.util.OnlyMD5Util;
import com.only.general.annotation.parameter.Define;
import com.only.net.action.Back;
import com.only.net.data.action.DataBackAction;
import com.only.net.data.action.DataBackActionAdapter;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;
import com.onlyxiahui.im.bean.UserData;
import com.onlyxiahui.im.message.data.address.ServerAddressConfig;

public class RegisterViewImpl extends AbstractView implements RegisterView {

	RegisterDialog registerDialog = new RegisterDialog(new JFrame(), "用户注册", false);;

	public RegisterViewImpl(AppContext appContext) {
		super(appContext);
		initEvent();
	}

	private void initEvent() {
		registerDialog.setDoneAction(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				reg();
			}
		});
	}

	@Override
	public void setVisible(boolean visible) {
		registerDialog.setVisible(visible);
	}

	@Override
	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	private void reg() {

		boolean v = registerDialog.verify();
		if (!v) {
			return;
		}

		String nickname = registerDialog.getNickname();
		String password = registerDialog.getPassword();
		String gender = registerDialog.getGender();
		String birthdate = registerDialog.getBirthday();
		String locationAddress = registerDialog.getLocationAddress();

		Request message = new Request();
		message.setController("user");
		message.setMethod("/register.do");

		// UserData user = new UserData();
		Map<String, Object> user = new HashMap<String, Object>();
		user.put("password", OnlyMD5Util.md5L32(password));
		user.put("nickname", nickname);
		user.put("gender", gender);
		// user.put("blood", blood);
		user.put("birthdate", birthdate);
		// user.put("homeAddress", homeAddress);
		user.put("locationAddress", locationAddress);
		// user.put("mobile", mobile);
		// user.put("email", email);
		// user.put("signature", signature);
		// user.put("introduce", introduce);

		message.put("userData", user);

		registerDialog.showWaiting(true);
		DataBackActionAdapter dataBackAction = new DataBackActionAdapter() {

			@Override
			public void lost() {
				registerDialog.showWaiting(false);
				registerDialog.showPrompt("注册失败，\n请检查网络连接是否正常。");
			}

			@Back
			public void back(Info info, @Define("userData") UserData user) {
				registerDialog.showWaiting(false);
				registerDialog.handle(info, user);
			}
		};
		Runnable runnable = new Runnable() {
			Request request;
			DataBackAction dataBackAction;

			public Runnable execute(Request request, DataBackAction dataBackAction) {
				this.request = request;
				this.dataBackAction = dataBackAction;
				return this;
			}

			public void run() {
				ServerAddressManager sam = appContext.getManager(ServerAddressManager.class);
				Info backInfo = sam.loadServerAddress("");
				if (backInfo.isSuccess()) {
					ServerAddressBox sab = appContext.getBox(ServerAddressBox.class);
					ServerAddressConfig sac = sab.getAddress(ServerAddressConstant.server_main_http);
					String url = sac.getAddress();
					new HttpHandler().execute(url, request, dataBackAction);
				} else {
					String error = MessageUtil.getDefaultErrorText(backInfo);
					showPrompt(error);
				}
			}
		}.execute(message, dataBackAction);
		new Thread(runnable).start();
	}
}
