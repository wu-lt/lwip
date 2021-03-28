package com.oim.swing.view;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.oim.common.event.ExecuteAction;
import com.oim.core.business.box.GroupBox;
import com.oim.core.business.box.PersonalBox;
import com.oim.core.business.manage.HeadImageManage;
import com.oim.core.business.manage.HeadPulseThread;
import com.oim.core.business.manager.ChatManage;
import com.oim.core.business.manager.ListManage;
import com.oim.core.business.manager.PersonalManager;
import com.oim.core.business.module.SystemModule;
import com.oim.core.business.view.FindView;
import com.oim.core.business.view.MainView;
import com.oim.core.business.view.ThemeView;
import com.oim.core.business.view.UpdatePasswordView;
import com.oim.swing.common.box.ImageBox;
import com.oim.swing.common.util.HeadUtil;
import com.oim.swing.ui.MainDialog;
import com.oim.swing.ui.component.ImageStyleButton;
import com.oim.swing.ui.component.list.HeadLabel;
import com.oim.swing.ui.component.list.HeadLabelAction;
import com.oim.swing.ui.component.list.Node;
import com.oim.swing.ui.main.MainPopupMenu;
import com.only.OnlyMessageBox;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;
import com.onlyxiahui.im.bean.Group;
import com.onlyxiahui.im.bean.GroupCategory;
import com.onlyxiahui.im.bean.GroupCategoryMember;
import com.onlyxiahui.im.bean.UserCategory;
import com.onlyxiahui.im.bean.UserCategoryMember;
import com.onlyxiahui.im.bean.UserData;
import com.onlyxiahui.im.bean.UserHead;
import com.onlyxiahui.im.message.data.LoginData;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月10日 下午10:20:49
 * @version 0.0.1
 */

public class MainViewImpl extends AbstractView implements MainView {

	MainDialog mainDialog = new MainDialog();
	MainPopupMenu menu = new MainPopupMenu();

	private HeadLabelAction userLabelAction;
	private HeadLabelAction groupLabelAction;

	/***** 存放好友分组列表组件 *****/
	private static Map<String, Node> userListNodeMap = new ConcurrentHashMap<String, Node>();
	/*** 存放群分组组件 **/
	private static Map<String, Node> groupListNodeMap = new ConcurrentHashMap<String, Node>();
	/** 存放单个好友组件 ***/
	private static Map<String, HeadLabel> userHeadLabelMap = new ConcurrentHashMap<String, HeadLabel>();
	/** 存放单个群 **/
	private static Map<String, HeadLabel> groupHeadLabelMap = new ConcurrentHashMap<String, HeadLabel>();

	private Map<String, HeadLabel> lastMap = new ConcurrentHashMap<String, HeadLabel>();

	GroupPanelMenuView gpm = null;
	HeadPulseThread headPulseThread=new HeadPulseThread();
	public MainViewImpl(AppContext appContext) {
		super(appContext);
		headPulseThread.start();
		gpm = new GroupPanelMenuView(appContext);
		initBottomPanel();
		initEvent();
	}

	private void initBottomPanel() {

		ImageIcon menuNormalIcon = new ImageIcon("Resources/Images/Default/MainFrame/menu_btn_normal.png");
		ImageIcon menuRolloverIcon = new ImageIcon("Resources/Images/Default/MainFrame/menu_btn_highlight.png");
		ImageIcon menuSelectedIcon = new ImageIcon("Resources/Images/Default/MainFrame/menu_btn_down.png");

		ImageIcon settingNormalIcon = new ImageIcon("Resources/Images/Default/MainFrame/Tools.png");
		ImageIcon settingRolloverIcon = new ImageIcon("Resources/Images/Default/MainFrame/tools_hover.png");
		ImageIcon settingSelectedIcon = new ImageIcon("Resources/Images/Default/MainFrame/tools_down.png");

		ImageIcon skinNormalIcon = new ImageIcon("Resources/Images/Default/MainFrame/skin_manage_normal.png");
		ImageIcon skinRolloverIcon = new ImageIcon("Resources/Images/Default/MainFrame/skin_manage_hover.png");
		ImageIcon skinSelectedIcon = new ImageIcon("Resources/Images/Default/MainFrame/skin_manage_down.png");

		ImageIcon messageNormalIcon = new ImageIcon("Resources/Images/Default/MainFrame/message.png");
		ImageIcon messageRolloverIcon = new ImageIcon("Resources/Images/Default/MainFrame/message_highlight.png");
		ImageIcon messageSelectedIcon = new ImageIcon("Resources/Images/Default/MainFrame/message_down.png");

		ImageIcon findNormalIcon = new ImageIcon("Resources/Images/Default/MainFrame/find.png");
		ImageIcon findRolloverIcon = new ImageIcon("Resources/Images/Default/MainFrame/find_hover.png");
		ImageIcon findSelectedIcon = new ImageIcon("Resources/Images/Default/MainFrame/find_down.png");

		ImageStyleButton menuTab = new ImageStyleButton(menuNormalIcon, menuRolloverIcon, menuSelectedIcon);
		ImageStyleButton settingTab = new ImageStyleButton(settingNormalIcon, settingRolloverIcon, settingSelectedIcon);
		ImageStyleButton skinTab = new ImageStyleButton(skinNormalIcon, skinRolloverIcon, skinSelectedIcon);

		ImageStyleButton messageTab = new ImageStyleButton(messageNormalIcon, messageRolloverIcon, messageSelectedIcon);
		ImageStyleButton findTab = new ImageStyleButton(findNormalIcon, findRolloverIcon, findSelectedIcon);
		findTab.setText("查找");
		findTab.setForeground(new Color(255, 255, 255));

		mainDialog.addBottom(menuTab);
		mainDialog.addBottom(settingTab);
		mainDialog.addBottom(skinTab);
		mainDialog.addBottom(messageTab);
		mainDialog.addBottom(findTab);

		menuTab.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				showMenu();
			}
		});

		findTab.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				openFindView();
			}
		});

		skinTab.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				openThemeView();
			}
		});

		ExecuteAction statusAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof String) {
					PersonalManager pm = MainViewImpl.this.appContext.getManager(PersonalManager.class);
					pm.updateStatus((String) value);
				}
				return null;
			}

		};
		mainDialog.setStatusAction(statusAction);
		menu.setStatusAction(statusAction);

		menu.addQuitAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SystemModule sm = appContext.getModule(SystemModule.class);
				sm.exit();
			}
		});
		menu.addUpdatePasswordAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UpdatePasswordView upw = MainViewImpl.this.appContext.getSingleView(UpdatePasswordView.class);
				upw.setVisible(true);
			}
		});

	}

	private void initEvent() {
		mainDialog.getGroupRoot().addListMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					gpm.show(mainDialog.getGroupRoot(), e.getX(), e.getY());
				}
			}
		});

		userLabelAction = new HeadLabelAction() {// 好友头像点击事件

			@Override
			public void action(MouseEvent e, HeadLabel headLabel) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.getClickCount() == 2) {
						UserData userData = headLabel.getAttribute(UserData.class.getName());
						ChatManage chatManage = appContext.getManager(ChatManage.class);
						chatManage.showCahtFrame(userData);
					}
				}
			}
		};

		groupLabelAction = new HeadLabelAction() {// 群头像点击事件

			@Override
			public void action(MouseEvent e, HeadLabel headLabel) {
				if (e.getClickCount() == 2) {
					Group group = headLabel.getAttribute(Group.class);
					ChatManage chatManage = appContext.getManager(ChatManage.class);
					chatManage.showCahtFrame(group);
				}
			}
		};
	}

	public void setVisible(boolean visible) {
		mainDialog.setVisible(visible);
	}

	public void addUserCategoryNode(Node node) {
		mainDialog.addUserCategoryNode(node);
	}

	public void addGroupCategoryNode(Node node) {
		mainDialog.addGroupCategoryNode(node);
	}

	public void addLastItem(HeadLabel node) {
		mainDialog.addLastNode(node);
	}

	public void removeUserCategoryNode(Node node) {
		mainDialog.removeUserCategoryNode(node);
	}

	public void removeGroupCategoryNode(Node node) {
		mainDialog.removeGroupCategoryNode(node);
	}

	public void removeLastNode(HeadLabel node) {
		mainDialog.removeLastNode(node);
	}

	public void shwoDifferentLogin() {
		int button = OnlyMessageBox.createQuestionMessageBox(mainDialog, "连接", "你的帐号在其他的地方登录！\n是否重新连接？").open();
		if (OnlyMessageBox.YES_OPTION == button) {
			// PersonalHandler userHandle =
			// appContext.getHandler(PersonalHandler.class);
			// userHandle.reconnect();
		}
	}

	private void showMenu() {
		menu.show(mainDialog, 0, mainDialog.getHeight() - 260);
	}

	public void openFindView() {
		FindView findView = this.appContext.getSingleView(FindView.class);
		if (!findView.isShowing()) {
			findView.initData();
		}
		findView.setVisible(true);
	}

	public void openThemeView() {
		ThemeView themeView = this.appContext.getSingleView(ThemeView.class);
		themeView.setVisible(true);
	}

	public void updateStatus(String status) {
		mainDialog.updateStatus(status);
	}

	public void showPromptMessage(String text) {
		mainDialog.showPrompt(text);
	}

	@Override
	public boolean isShowing() {
		return mainDialog.isShowing();
	}

	@Override
	public void setStatus(String status) {
		mainDialog.updateStatus(status);
	}

	@Override
	public void addOrUpdateGroupCategory(GroupCategory groupCategory) {

		Node node = groupListNodeMap.get(groupCategory.getId());
		if (null == node) {
			node = new Node();
			groupListNodeMap.put(groupCategory.getId(), node);
		}

		node.setTitleText(groupCategory.getName());
		node.setCountText("[0]");

		addGroupCategoryNode(node);

		setGroupList(node, groupCategory.getId());

	}

	@Override
	public void addOrUpdateGroup(String groupCategoryId, Group group) {

		HeadLabel head = groupHeadLabelMap.get(group.getId());
		if (null == head) {
			head = new HeadLabel();
			groupHeadLabelMap.put(group.getId(), head);
		}
		setGroupHead(head, group);
		setGroupHeadEvent(head, group);
		Node node = groupListNodeMap.get(groupCategoryId);
		if (null != head && null != node) {
			node.addNode(head);
		}
	}

	@Override
	public void addOrUpdateUserCategory(UserCategory userCategory) {

		Node node = userListNodeMap.get(userCategory.getId());
		if (null == node) {
			node = new Node();
			userListNodeMap.put(userCategory.getId(), node);
		}

		node.setTitleText(userCategory.getName());
		node.setCountText("[0/0]");

		setUserList(node, userCategory.getId());
		addUserCategoryNode(node);
	}

	@Override
	public void addOrUpdateUser(String userCategoryId, UserData userData) {

		HeadLabel head = userHeadLabelMap.get(userData.getId());
		if (null == head) {
			head = new HeadLabel();
			userHeadLabelMap.put(userData.getId(), head);
		}

		setUserDataHeadEvent(head, userData);
		setUserDataHead(head, userData);

		Node node = userListNodeMap.get(userCategoryId);
		if (null != node) {
			node.addNode(head);
		}
	}

	@Override
	public void updateUserCategoryMemberCount(String userCategoryId, int totalCount, int onlineCount) {
		Node node = userListNodeMap.get(userCategoryId);
		if (null != node) {
			node.setCountText("[" + onlineCount + "/" + totalCount + "]");
		}
	}

	@Override
	public void updateGroupCategoryMemberCount(String groupCategoryId, int totalCount) {
		Node node = groupListNodeMap.get(groupCategoryId);
		if (null != node) {
			node.setCountText("[" + totalCount + "]");
		}
	}

	@Override
	public void showUserHeadPulse(String userId, boolean pulse) {
		HeadPulseThread hpt = headPulseThread;
		HeadLabel head = lastMap.get(userId);
		if (pulse) {
			if (null != head && !hpt.has(userId)) {
				hpt.putHead(userId, head.getIocnPanel());
			}
		} else {
			if (hpt.has(userId)) {
				hpt.removeHead(userId);
			}
		}
	}

	@Override
	public void showGroupHeadPulse(String groupId, boolean pulse) {
		HeadPulseThread hpt = headPulseThread;
		HeadLabel head = groupHeadLabelMap.get(groupId);
		if (pulse) {
			if (null != head && !hpt.has(groupId)) {
				hpt.putHead(groupId, head.getIocnPanel());
			}
		} else {
			if (hpt.has(groupId)) {
				hpt.removeHead(groupId);
			}
		}
	}

	@Override
	public void setPersonalData(UserData user) {
		
		PersonalBox pb=appContext.getBox(PersonalBox.class);
		
		LoginData loginData = pb.getLoginData();
		// ImageIcon statusImage =
		// ImageBox.getStatusImageIcon(loginData.getStatus());
		HeadImageManage him = appContext.getManager(HeadImageManage.class);
		Image headImage = him.getPersonalHeadImage(60);

		mainDialog.setHeadIcon(new ImageIcon(headImage));
		mainDialog.setNickname(user.getNickname());
		mainDialog.setSignature(user.getSignature());
		mainDialog.setStatus(loginData.getStatus());
	}

	@Override
	public void setUserHead(UserHead userHead) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshPersonalHead() {
		HeadImageManage him = appContext.getManager(HeadImageManage.class);
		Image headImage = him.getPersonalHeadImage(60);
		mainDialog.setHeadIcon(new ImageIcon(headImage));
	}

	@Override
	public void refreshUserHead() {
		Set<String> keySet = userHeadLabelMap.keySet();
		for (String userId : keySet) {
			PersonalBox pb=appContext.getBox(PersonalBox.class);
			UserData userData = pb.getUserData();
			if (null != userData) {
				HeadImageManage him = appContext.getManager(HeadImageManage.class);
				Image image = him.getUserHeadImage(userData.getId(), userData.getHead(), 40, 40);
				HeadLabel head = userHeadLabelMap.get(userId);
				head.setHeadIcon(new ImageIcon(image));

				String statusTemp = userData.getStatus();
				String status = (null != statusTemp && !(UserData.status_invisible.equals(statusTemp))) ? statusTemp : UserData.status_offline;
				boolean isGray = HeadUtil.isGray(status);

				if (isGray) {
					head.setGray(true);
					head.setStatus("[离线]");
				} else {
					head.setGray(false);
					head.setStatus("[在线]");
				}

				HeadLabel lastHead = lastMap.get(userId);
				if (null != lastHead) {
					lastHead.setHeadIcon(new ImageIcon(image));
					if (isGray) {
						lastHead.setGray(true);
						head.setStatus("[离线]");
					} else {
						lastHead.setGray(false);
						head.setStatus("[在线]");
					}
				}
			}
		}

	}

	@Override
	public void refreshGroupHead() {
		Set<String> keySet = groupHeadLabelMap.keySet();
		for (String groupId : keySet) {
			GroupBox gb = appContext.getBox(GroupBox.class);
			Group group = gb.getGroup(groupId);
			if (null != group) {
				HeadImageManage him = appContext.getManager(HeadImageManage.class);
				Image image = him.getGroupHeadImage(group.getId(), group.getHead(), 40, 40);
				HeadLabel head = groupHeadLabelMap.get(groupId);
				head.setHeadIcon(new ImageIcon(image));
				HeadLabel lastHead = lastMap.get(groupId);
				if (null != lastHead) {
					lastHead.setHeadIcon(new ImageIcon(image));
				}
			}
		}
	}

	@Override
	public void addOrUpdateLastGroup(Group group, String text) {
		HeadLabel head = getGroupLastHeadItem(group);
		setGroupLastHead(head, group, text);
		addLastItem(head);

	}

	@Override
	public void addOrUpdateLastUser(UserData userData, String text) {
		HeadLabel head = getUserDataLastHeadItem(userData);
		setUserDataLastHead(head, userData, text);
		addLastItem(head);
	}

	private HeadLabel getUserDataLastHeadItem(UserData userData) {
		HeadLabel head = lastMap.get(userData.getId());
		if (null == head) {
			head = new HeadLabel();
			lastMap.put(userData.getId(), head);
			setUserDataHeadEvent(head, userData);
		}
		return head;
	}

	private HeadLabel getGroupLastHeadItem(Group group) {
		HeadLabel head = lastMap.get(group.getId());
		if (null == head) {
			head = new HeadLabel();
			lastMap.put(group.getId(), head);
			setGroupHeadEvent(head, group);
		}
		return head;
	}

	private void setUserDataHead(HeadLabel head, UserData userData) {
		String status = UserData.status_offline;
		if (null != userData.getStatus() && !(UserData.status_invisible.equals(userData.getStatus()))) {
			status = userData.getStatus();
		}

		HeadImageManage him = appContext.getManager(HeadImageManage.class);
		Image image = him.getUserHeadImage(userData.getId(), userData.getHead(), 40, 40);
		String remark = (null == userData.getRemarkName() || "".equals(userData.getRemarkName())) ? userData.getNickname() : userData.getRemarkName();
		String nickname = (null == userData.getRemarkName() || "".equals(userData.getRemarkName())) ? userData.getAccount() : userData.getNickname();

		head.addAttribute(UserData.class.getName(), userData);
		head.setRoundedCorner(40, 40);
		head.setHeadIcon(new ImageIcon(image));

		head.setRemark(remark);// 备注名
		head.setNickname("(" + nickname + ")");// 昵称
		head.setShowText(userData.getSignature());// 个性签名

		JLabel iconButton = head.getAttribute("statusLabel");
		ImageIcon iconImage = ImageBox.getStatusImageIcon(status);

		if (null == iconButton) {// 状态图标显示组件
			iconButton = new JLabel();
			head.addAttribute("statusLabel", iconButton);
			head.addBusinessAttribute("statusLabel", iconButton);
		} else {
			iconButton.setIcon(iconImage);
		}

		head.revalidate();

		// 如果用户不是在线状态，则使其头像变灰
		if (HeadUtil.isGray(status)) {
			head.setGray(true);
			head.setStatus("[离线]");
		} else {
			head.setGray(false);
			head.setStatus("[在线]");
		}
	}

	private void setGroupHead(HeadLabel head, Group group) {
		HeadImageManage him = appContext.getManager(HeadImageManage.class);
		Image image = him.getGroupHeadImage(group.getId(), group.getHead(), 40, 40);

		head.addAttribute(Group.class, group);
		head.setRoundedCorner(40, 40);

		head.setHeadIcon(new ImageIcon(image));

		head.setRemark(group.getName());
		head.setNickname("(" + group.getNumber() + ")");
		head.setShowText(group.getIntroduce());
	}

	private void setGroupLastHead(HeadLabel head, Group group, String text) {
		HeadImageManage him = appContext.getManager(HeadImageManage.class);
		Image image = him.getGroupHeadImage(group.getId(), group.getHead(), 40, 40);

		head.setHeadIcon(new ImageIcon(image));
		head.addAttribute(Group.class, group);
		head.setRoundedCorner(40, 40);
		head.setRemark(group.getName());
		head.setNickname("(" + group.getNumber() + ")");
		if (null != text && !"".equals(text)) {
			head.setShowText(text);
		}
		head.setShowSeparator(true);
	}

	private void setUserDataLastHead(HeadLabel head, UserData userData, String text) {
		String statusTemp = userData.getStatus();
		String status = (null != statusTemp && !(UserData.status_invisible.equals(statusTemp))) ? statusTemp : UserData.status_offline;

		HeadImageManage him = appContext.getManager(HeadImageManage.class);
		Image image = him.getUserHeadImage(userData.getId(), userData.getHead(), 40, 40);
		String account = userData.getAccount();
		String remark = userData.getRemarkName();
		String nickname = userData.getNickname();
		boolean hasRemark = (null != remark && !"".equals(remark));

		String remarkText = (hasRemark) ? remark : nickname;
		String nicknameText = (hasRemark) ? nickname : account;

		String nt = (null != nicknameText && !"".equals(nicknameText)) ? "(" + nicknameText + ")" : "";

		head.addAttribute(UserData.class.getName(), userData);
		head.setRoundedCorner(40, 40);
		head.setHeadIcon(new ImageIcon(image));

		head.setRemark(remarkText);// 备注名
		head.setNickname(nt);// 昵称

		if (null != text && !"".equals(text)) {
			head.setShowText(text);
		}

		head.setShowSeparator(true);

		JLabel iconButton = head.getAttribute("statusLabel");
		ImageIcon iconImage = ImageBox.getStatusImageIcon(status);

		if (null == iconButton) {// 状态图标显示组件
			iconButton = new JLabel();
			head.addAttribute("statusLabel", iconButton);
			head.addBusinessAttribute("statusLabel", iconButton);
		} else {
			iconButton.setIcon(iconImage);
		}

		head.revalidate();

		// 如果用户不是在线状态，则使其头像变灰
		if (HeadUtil.isGray(status)) {
			head.setGray(true);
			head.setStatus("[离线]");
		} else {
			head.setGray(false);
			head.setStatus("[在线]");
		}
	}

	/**
	 * 设置群头像的点击事件
	 * 
	 * @author: XiaHui
	 * @param head
	 * @param group
	 * @createDate: 2017年6月9日 上午11:12:22
	 * @update: XiaHui
	 * @updateDate: 2017年6月9日 上午11:12:22
	 */
	private void setGroupHeadEvent(HeadLabel head, Group group) {
		head.addAction(groupLabelAction);
	}

	/**
	 * 设置用户头像点击事件
	 * 
	 * @author: XiaHui
	 * @param head
	 * @param userData
	 * @createDate: 2017年6月9日 上午11:17:31
	 * @update: XiaHui
	 * @updateDate: 2017年6月9日 上午11:17:31
	 */
	private void setUserDataHeadEvent(HeadLabel head, UserData userData) {
		head.addAction(userLabelAction);// 添加头像点击事件
	}

	private void setUserList(Node node, String userCategoryId) {
		ListManage lm = appContext.getManager(ListManage.class);
		List<UserCategoryMember> userCategoryMemberList = lm.getUserCategoryMemberList(userCategoryId);
		if (null != userCategoryMemberList) {
			for (UserCategoryMember ucm : userCategoryMemberList) {
				HeadLabel head = userHeadLabelMap.get(ucm.getMemberUserId());
				if (null != head) {
					node.addNode(head);
				}
			}
		}
	}

	private void setGroupList(Node node, String groupCategoryId) {
		ListManage lm = appContext.getManager(ListManage.class);
		List<GroupCategoryMember> list = lm.getGroupCategoryMemberList(groupCategoryId);
		if (null != list) {
			for (GroupCategoryMember gcm : list) {
				HeadLabel head = groupHeadLabelMap.get(gcm.getGroupId());
				if (null != head) {
					node.addNode(head);
				}
			}
		}
	}

	@Override
	public void clearUserCategory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUserCategoryMember(String userCategoryId, String userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearUserCategoryMember(String userCategoryId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearGroupCategory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearGroupCategoryMember(String groupCategoryId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeGroupCategoryMember(String groupCategoryId, String groupId) {
		// TODO Auto-generated method stub
		
	}
}
