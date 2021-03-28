package com.oim.swing.view;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JList;
import javax.swing.JOptionPane;

import com.oim.core.business.manager.ListManage;
import com.oim.core.business.sender.GroupCategoryMemberSender;
import com.oim.core.business.sender.GroupCategorySender;
import com.oim.core.business.service.GroupService;
import com.oim.core.business.view.JoinGroupView;
import com.oim.swing.common.box.HeadImageIconBox;
import com.oim.swing.ui.AddFrame;
import com.only.common.result.Info;
import com.only.general.annotation.parameter.Define;
import com.only.laf.OnlyListCellRenderer;
import com.only.net.action.Back;
import com.only.net.data.action.DataBackActionAdapter;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;
import com.onlyxiahui.im.bean.Group;
import com.onlyxiahui.im.bean.GroupCategory;
import com.onlyxiahui.im.bean.GroupCategoryMember;

/**
 * 描述： 添加好友或者加入群显示窗口
 * 
 * @author XiaHui
 * @date 2015年3月16日 下午10:42:19
 * @version 0.0.1
 */
public class JoinGroupViewImpl extends AbstractView implements JoinGroupView {

	AddFrame addFrame = new AddFrame();
	private Group group;

	public JoinGroupViewImpl(AppContext appContext) {
		super(appContext);
		initUI();
		initEvent();
	}

	@SuppressWarnings({ "unchecked", "serial" })
	private void initUI() {
		addFrame.setComboBoxRenderer(new OnlyListCellRenderer() {
			@SuppressWarnings("rawtypes")
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value instanceof GroupCategory) {
					setText((value == null) ? "" : ((GroupCategory) value).getName());
				} else {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				}
				setOpaque(isSelected);
				setForeground(isSelected ? Color.white : Color.black);
				setBackground(isSelected ? Color.blue : Color.white);
				return this;
			}
		});

	}

	private void initEvent() {
		addFrame.addCategoryMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				addMouseClicked(evt);
			}
		});
		addFrame.addDoneAction(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				agreeActionPerformed(evt);
			}
		});
	}

	public void setGroupCategoryList(List<GroupCategory> groupCategoryList) {
		addFrame.removeAllItems();
		for (GroupCategory groupCategory : groupCategoryList) {
			addFrame.addItem(groupCategory);
		}
		addFrame.setTitleText("加入群");
	}

	public void setVisible(boolean visible) {
		addFrame.setVisible(visible);
	}

	public boolean isShowing() {
		return addFrame.isShowing();
	}

	public void setGroup(Group group) {
		this.group = group;
		addFrame.setShowIcon(HeadImageIconBox.getGroupHeadImageIcon(group.getHead(), 60, 60));
		addFrame.setShowText(group.getClassification());
		addFrame.setShowName(group.getName());
	}

	public void set(Group group, List<GroupCategory> groupCategoryList) {
		setGroup(group);
		setGroupCategoryList(groupCategoryList);
	}

	private void addMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseClicked
		String name = JOptionPane.showInputDialog(addFrame, "请输入组名");
		if (null != name && !"".equals(name)) {
			addGroupCategory(name);
		}
	}

	private void agreeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
		Object object = this.addFrame.getSelectedItem();
		String remarks = addFrame.getRemark();
		addGroupCategoryMember(group, (GroupCategory) object, remarks);
	}

	public void addGroupCategory(String name) {
		if (null != name && !"".equals(name)) {

			DataBackActionAdapter action = new DataBackActionAdapter() {
				
				@Override
				public void lost() {
					addFrame.showPrompt("添加失败！");
				}
				
				@Override
				public void timeOut() {
					addFrame.showPrompt("添加失败！");
				}
				@Back
				public void back(Info info,@Define("groupCategory") GroupCategory groupCategory) {
					if (info.isSuccess()) {
						GroupService groupService = appContext.getService(GroupService.class);
						groupService.addGroupCategory(groupCategory);
						addFrame.addItem(groupCategory);
					} else {
						addFrame.showPrompt("添加失败！");
					}
				}
			};
			GroupCategorySender gch = this.appContext.getSender(GroupCategorySender.class);
			gch.addGroupCategory(name, action);
		}
	}

	public void addGroupCategoryMember(Group group, GroupCategory groupCategory, String remark) {

		addFrame.showWaiting(true);
		DataBackActionAdapter action = new DataBackActionAdapter() {

			@Override
			public void lost() {
				addFrame.showWaiting(false);
			}

			@Override
			public void timeOut() {
				addFrame.showWaiting(false);
			}

			@Back
			public void back(Info info, @Define("groupCategoryMember") GroupCategoryMember groupCategoryMember) {
				addFrame.showWaiting(false);
				if (info.isSuccess()) {
					addFrame.setVisible(false);
					ListManage listManage = appContext.getManager(ListManage.class);
					Group group = this.getAttribute(Group.class);
					listManage.add(group, groupCategoryMember);
				} else {
					addFrame.showPrompt("加入失败！");
				}
			}
		};
		action.addAttribute(Group.class, group);
		GroupCategoryMemberSender gch = this.appContext.getSender(GroupCategoryMemberSender.class);
		gch.addGroupCategoryMember(groupCategory.getId(), group.getId(), remark, action);
	}

	public void showWaiting(boolean show) {
		addFrame.showWaiting(show);
	}

	@Override
	public void showPrompt(String text) {
		// TODO Auto-generated method stub

	}
}
