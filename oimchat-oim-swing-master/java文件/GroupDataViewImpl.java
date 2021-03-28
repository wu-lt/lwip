package com.oim.swing.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.oim.core.business.manager.ListManage;
import com.oim.core.business.sender.GroupSender;
import com.oim.core.business.view.GroupDataView;
import com.oim.swing.ui.component.BaseFrame;
import com.oim.swing.ui.user.GroupDataPanel;
import com.only.OnlyBorderButton;
import com.only.OnlyScrollPane;
import com.only.common.result.Info;
import com.only.general.annotation.parameter.Define;
import com.only.net.action.Back;
import com.only.net.data.action.DataBackActionAdapter;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;
import com.onlyxiahui.im.bean.Group;
import com.onlyxiahui.im.bean.GroupCategoryMember;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月16日 下午10:42:19
 * @version 0.0.1
 */
public class GroupDataViewImpl extends AbstractView  implements GroupDataView{

	BaseFrame frame = new BaseFrame();
	GroupDataPanel gdp = new GroupDataPanel();
	JPanel basePanel = new JPanel();
	OnlyScrollPane scrollPane = new OnlyScrollPane();
	JPanel buttonPanel = new JPanel();

	OnlyBorderButton addButton = new OnlyBorderButton();
	OnlyBorderButton closeButton = new OnlyBorderButton();
	Group group;

	public GroupDataViewImpl(AppContext appContext) {
		super(appContext);
		initUI();
		initEvent();
	}

	private void initUI() {
		frame.setMinimumSize(new java.awt.Dimension(400, 520));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new CardLayout());
		frame.setShowTitle(false);
		frame.setShowIconImage(false);

		frame.add(basePanel);

		scrollPane.setViewportView(gdp);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHeaderVisible(false);
		scrollPane.setAlpha(0.0f);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);

		addButton.setPreferredSize(new Dimension(60, 25));
		closeButton.setPreferredSize(new Dimension(60, 25));

		addButton.setText("保存");
		closeButton.setText("取消");
		// JPanel panel=new JPanel();
		// panel.setOpaque(false);
		// panel.setPreferredSize(new Dimension(60, 45));

		basePanel.setLayout(new BorderLayout());
		basePanel.setOpaque(false);
		// basePanel.add(panel, BorderLayout.NORTH);
		basePanel.add(gdp, BorderLayout.CENTER);
		basePanel.add(buttonPanel, BorderLayout.SOUTH);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setOpaque(false);
		buttonPanel.add(addButton);
		buttonPanel.add(closeButton);
	}

	private void initEvent() {
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addOrUpdate();
			}
		});
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	public void addOrUpdate() {
		boolean isAdd = (group == null || null == group.getId() || "".equals(group.getId()));
		if (group == null) {
			group = new Group();
		}
		String name = gdp.getGroupName();
		String type = gdp.getType();
		String introduce = gdp.getIntroduce();

		if (null == name || "".equals(name)) {
			showPromptMessage("名称不能空！");
			return;
		}

		group.setName(name);
		group.setClassification(type);
		group.setIntroduce(introduce);
		frame.setVisible(false);
		if (isAdd) {
			DataBackActionAdapter action = new DataBackActionAdapter() {

				@Back
				public void back(Info info,
						@Define("group") Group group,
						@Define("groupCategoryMember") GroupCategoryMember groupCategoryMember) {

					if (info.isSuccess()) {
						ListManage listManage = appContext.getManager(ListManage.class);
						listManage.add(group, groupCategoryMember);
					} else {
						frame.setVisible(true);
						frame.showPrompt("添加失败！");
					}
				}
			};
			GroupSender gh = this.appContext.getSender(GroupSender.class);
			gh.addGroup(group, action);
		} else {
			DataBackActionAdapter action = new DataBackActionAdapter() {

				@Back
				public void back(Info info,
						@Define("group") Group group) {
					if (info.isSuccess()) {
						 ListManage listManage =
						 appContext.getManager(ListManage.class);
						 listManage.updateGroup(group);
					} else {
						frame.setVisible(true);
						frame.showPrompt("修改失败！");
					}
				}
			};
			GroupSender gh = this.appContext.getSender(GroupSender.class);
			gh.updateGroup(group, action);
		}
	}

	public void setGroup(Group group) {
		if (null == group) {
			gdp.setGroupName("");
			gdp.setType("");
			gdp.setIntroduce("");
		} else {
			gdp.setGroupName(group.getName());
			gdp.setType(group.getClassification());
			gdp.setIntroduce(gdp.getIntroduce());
		}
	}

	public void showPromptMessage(String text) {
		frame.showPrompt(text);
	}

	@Override
	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}
}
