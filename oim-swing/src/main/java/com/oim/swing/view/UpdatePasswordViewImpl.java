package com.oim.swing.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.oim.core.business.sender.PersonalSender;
import com.oim.core.business.view.MainView;
import com.oim.core.business.view.UpdatePasswordView;
import com.oim.swing.ui.UpdatePasswordDialog;
import com.only.common.result.Info;
import com.only.common.result.util.MessageUtil;
import com.only.net.action.Back;
import com.only.net.data.action.DataBackActionAdapter;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2016年2月10日 下午4:34:09
 * @version 0.0.1
 */
public class UpdatePasswordViewImpl extends AbstractView implements UpdatePasswordView{
	
	UpdatePasswordDialog upd = new UpdatePasswordDialog(new javax.swing.JFrame(), "密码修改", false);

	public UpdatePasswordViewImpl(AppContext appContext) {
		super(appContext);
		initEvent();
	}

	private void initEvent() {
		upd.addSaveAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updatePassword();
			}
		});
	}

	public void setVisible(boolean visible) {
		upd.setVisible(visible);
	}

	private void updatePassword() {
		String oldPassword = upd.getOldPassword();
		String newPassword = upd.getNewPassword();
		String verifyPassword = upd.getVerifyPassword();

		if (null == oldPassword || "".equals(oldPassword.trim())) {
			upd.showPrompt("请输入原来的密码！");
			return;
		}
		if (null == newPassword || "".equals(newPassword.trim())) {
			upd.showPrompt("请输新密码！");
			return;
		}
		if (null == verifyPassword || "".equals(verifyPassword.trim())) {
			upd.showPrompt("请再一次输入新密码！");
			return;
		}
		if (!newPassword.equals(verifyPassword.trim())) {
			upd.showPrompt("两次输入密码不一致！");
			return;
		}

		DataBackActionAdapter action = new DataBackActionAdapter() {// 这是消息发送后回掉
			@Override
			public void lost() {
				upd.showPrompt("修改失败。");
			}

			@Override
			public void timeOut() {
				upd.showPrompt("修改失败。");
			}

			@Back
			public void back(Info info) {
				if (info.isSuccess()) {
					upd.setVisible(false);
					MainView mv = appContext.getSingleView(MainView.class);
					mv.showPrompt("修改成功。");
					//PersonalBox.put(UserData.class, user);
				} else {
					String text=MessageUtil.getDefaultErrorText(info);
					if(null==text){
						text="修改失败。";
					}
					upd.showPrompt("修改失败。");
				}
			}
		};
		PersonalSender ph = this.appContext.getSender(PersonalSender.class);
		ph.upadtePassword(oldPassword,newPassword, action);
	}

	@Override
	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}
}
