package com.oim.swing.view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.oim.common.chat.util.CoreContentUtil;
import com.oim.common.event.ExecuteAction;
import com.oim.core.business.box.PersonalBox;
import com.oim.core.business.box.UserDataBox;
import com.oim.core.business.manage.HeadImageManage;
import com.oim.core.business.manager.ImageManager;
import com.oim.core.business.manager.LastManager;
import com.oim.core.business.manager.PromptManager;
import com.oim.core.business.manager.VideoManager;
import com.oim.core.business.sender.ChatSender;
import com.oim.core.business.sender.GroupSender;
import com.oim.core.business.sender.VideoSender;
import com.oim.core.business.view.ChatListView;
import com.oim.core.common.AppConstant;
import com.oim.core.common.action.BackAction;
import com.oim.core.common.action.BackInfo;
import com.oim.core.common.action.CallBackAction;
import com.oim.core.common.component.file.FileHandleInfo;
import com.oim.core.common.component.file.FileInfo;
import com.oim.core.common.util.ColorUtil;
import com.oim.swing.common.box.HeadImageIconBox;
import com.oim.swing.common.box.ImageBox;
import com.oim.swing.common.util.ContentUtil;
import com.oim.swing.ui.ListChatFrame;
import com.oim.swing.ui.chat.ChatItem;
import com.oim.swing.ui.chat.ChatPanel;
import com.oim.swing.ui.chat.GroupUserList;
import com.oim.swing.ui.component.list.ItemPanel;
import com.only.common.result.Info;
import com.only.common.util.OnlyDateUtil;
import com.only.general.annotation.parameter.Define;
import com.only.net.action.Back;
import com.only.net.data.action.DataBackAction;
import com.only.net.data.action.DataBackActionAdapter;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;
import com.onlyxiahui.im.bean.Group;
import com.onlyxiahui.im.bean.GroupMember;
import com.onlyxiahui.im.bean.UserData;
import com.onlyxiahui.im.message.data.FileData;
import com.onlyxiahui.im.message.data.chat.Content;
import com.onlyxiahui.im.message.data.chat.Item;
import com.onlyxiahui.im.message.data.chat.history.UserChatHistoryData;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月14日 上午11:14:38
 * @version 0.0.1
 */
public class ChatListViewImpl extends AbstractView implements ChatListView {

	private Map<String, ChatItem> userChatPanelItemMap = new ConcurrentHashMap<String, ChatItem>();
	private Map<String, ChatItem> groupChatPanelItemMap = new ConcurrentHashMap<String, ChatItem>();
	private Map<String, ChatPanel> userChatPanelMap = new ConcurrentHashMap<String, ChatPanel>();
	private Map<String, ChatPanel> groupChatPanelMap = new ConcurrentHashMap<String, ChatPanel>();

	private Map<String, GroupUserList> groupUserListMap = new ConcurrentHashMap<String, GroupUserList>();
	private Map<String, List<GroupMember>> groupMemberListMap = new ConcurrentHashMap<String, List<GroupMember>>();

	private SelectChatItem selectChatItem = new SelectChatItem();
	private ListChatFrame listChatFrame = new ListChatFrame();
	private Set<ChatItem> chatItemSet = new HashSet<ChatItem>();// 储存聊天窗口左边列表内容
	private ExecuteAction executeAction;
	private ExecuteAction sendUserAction;// 执行用户信息发送动作
	private ExecuteAction sendGroupAction;// 执行群信息发送动作
	private ExecuteAction colseAction;
	private MouseAdapter chatItemMouseAdapter;
	private MouseAdapter userItemMouseAdapter;
	private ExecuteAction twitterAction;
	private ExecuteAction videoAction;
	private ImageIcon twitterIcon = new ImageIcon("Resources/Images/Default/ChatFrame/MidToolbar/aio_quickbar_twitter.png");
	private ImageIcon videoIcon = new ImageIcon("Resources/Images/Default/ChatFrame/MidToolbar/video.png");

	private long shakeTime = 0;// 记录收到或者发送抖动信息的时间，为了不过于频繁抖动。

	public ChatListViewImpl(AppContext appContext) {
		super(appContext);
		initEvent();
	}

	private void initEvent() {
		executeAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof ChatItem) {
					Runnable runnable = new Runnable() {
						ChatItem chatItem;

						@Override
						public void run() {
							removeChatItem(chatItem);
						}

						public Runnable setChatItem(ChatItem chatItem) {
							this.chatItem = chatItem;
							return this;
						}

					}.setChatItem(((ChatItem) value));
					java.awt.EventQueue.invokeLater(runnable);
				}
				return null;
			}
		};

		sendUserAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof ChatPanel) {
					sendUserMessage((ChatPanel) value);
				}
				return null;
			}
		};
		sendGroupAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof ChatPanel) {
					sendGroupMessage((ChatPanel) value);
				}
				return null;
			}
		};
		colseAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof ChatPanel) {
					UserData user = ((ChatPanel) value).getAttribute(UserData.class);
					if (null != user) {
						ChatItem item = userChatPanelItemMap.get(user.getId());
						if (null != item) {
							removeChatItem(item);
						}
					}
					Group group = ((ChatPanel) value).getAttribute(Group.class);
					if (null != group) {
						ChatItem item = groupChatPanelItemMap.get(group.getId());
						if (null != item) {
							removeChatItem(item);
						}
					}
				}
				return null;
			}
		};
		chatItemMouseAdapter = new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getSource() instanceof ChatItem) {
					ChatItem item = (ChatItem) e.getSource();
					if (e.getButton() == MouseEvent.BUTTON1) {
						changeChatItem(item);
					}
				}
			}
		};
		twitterAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof ChatPanel) {
					UserData user = ((ChatPanel) value).getAttribute(UserData.class);
					if (null != user) {
						sendShake(user.getId());
					}
				}
				return null;
			}
		};

		videoAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof ChatPanel) {
					UserData user = ((ChatPanel) value).getAttribute(UserData.class);
					if (null != user) {
						sendVideo(user);
					}
				}
				return null;
			}
		};

		userItemMouseAdapter = new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getSource() instanceof ItemPanel) {
					ItemPanel item = (ItemPanel) e.getSource();
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (e.getClickCount() == 2) {
							UserData userData = item.getAttribute(UserData.class);
							showCahtFrame(userData);
						}
					}
				}
			}
		};
	}

	public boolean isShowing() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		listChatFrame.setVisible(visible);
		if (visible) {
			listChatFrame.toFront();
		}
	}

	@Override
	public boolean isGroupChatShowing(String groupId) {
		ChatPanel chatPanel = groupChatPanelMap.get(groupId);
		if (null == chatPanel) {
			return false;
		}
		boolean mark = listChatFrame.isShowing() && chatPanel.isShowing();
		return mark;
	}

	@Override
	public boolean isUserChatShowing(String userId) {
		ChatPanel chatPanel = userChatPanelMap.get(userId);
		if (null == chatPanel) {
			return false;
		}
		boolean mark = listChatFrame.isShowing() && chatPanel.isShowing();
		return mark;
	}

	@Override
	public boolean hasGroupChat(String groupId) {
		return groupChatPanelMap.containsKey(groupId);
	}

	@Override
	public boolean hasUserChat(String userId) {
		return userChatPanelMap.containsKey(userId);
	}

	@Override
	public void show(UserData userData) {
		showCahtFrame(userData);

	}

	@Override
	public void show(Group group) {
		showCahtFrame(group);
	}

	@Override
	public void userChat(boolean isOwn, UserData userData, Content content) {
		if (null != userData) {

			String name = userData.getNickname();
			String time = OnlyDateUtil.dateToString(new Date(content.getTimestamp()), "yyyy-MM-dd HH:mm:ss");
			String color = ColorUtil.getColorInHexFromRGB(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue());// ColorUtil.getColorInHexFromRGB(32,

			ChatPanel chatPanel = getChatPanel(userData);
			if (null != chatPanel) {
				insertShowChat(chatPanel, name, color, time, content); // 143,
			}
		}
	}

	@Override
	public void groupChat(Group group, UserData userData, Content content) {
		if (null != userData) {
			PersonalBox pb = appContext.getBox(PersonalBox.class);
			UserData user = pb.getUserData();
			boolean isOwn = user.getId().equals(userData.getId());
			String name = userData.getNickname();
			String time = OnlyDateUtil.getCurrentDateTime();
			String color = (isOwn) ? ColorUtil.getColorInHexFromRGB(32, 143, 62) : ColorUtil.getColorInHexFromRGB(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue());
			ChatPanel chatPanel = getChatPanel(group);
			insertShowChat(chatPanel, name, color, time, content);
		}
	}

	@Override
	public void updateGroupUserList(final String groupId) {
		DataBackAction dataBackAction = new DataBackActionAdapter() {

			@Back
			public void back(@Define("userDataList") List<UserData> userDataList,
					@Define("groupMemberList") List<GroupMember> groupMemberList) {
				setGroupUserList(groupId, userDataList, groupMemberList);
			}
		};
		GroupSender gh = this.appContext.getSender(GroupSender.class);
		gh.getGroupMemberListWithUserDataList(groupId, dataBackAction);
	}

	@Override
	public void doShake(UserData userData) {
		// TODO Auto-generated method stub

	}

	/**
	 * 发送抖动操作
	 * 
	 * @Author: XiaHui
	 * @Date: 2016年2月16日
	 * @ModifyUser: XiaHui
	 * @ModifyDate: 2016年2月16日
	 * @param receiveId
	 */
	private void sendShake(String receiveId) {
		PersonalBox pb = appContext.getBox(PersonalBox.class);
		UserData sendUser = pb.getUserData();
		ChatSender ch = this.appContext.getSender(ChatSender.class);
		ch.sendShake(receiveId, sendUser.getId());// 发送给接受方
		shake();// 自己执行抖动
	}

	private void sendVideo(UserData userData) {
		PersonalBox pb = appContext.getBox(PersonalBox.class);
		UserData sendUser = pb.getUserData();
		VideoManager vm = this.appContext.getManager(VideoManager.class);
		vm.showSendVideoFrame(userData);
		VideoSender vh = this.appContext.getSender(VideoSender.class);
		vh.requestVideo(sendUser.getId(), userData.getId());
	}

	/**
	 * 接受到抖动信息，执行抖动
	 * 
	 * @Author: XiaHui
	 * @Date: 2016年2月16日
	 * @ModifyUser: XiaHui
	 * @ModifyDate: 2016年2月16日
	 * @param sendId
	 */
	public void doShake(String sendId) {
		UserDataBox ub = appContext.getBox(UserDataBox.class);
		UserData user = ub.getUserData(sendId);
		if (null == user) {// 如果发送抖动的不是好友，暂时不支持抖动
			return;
		}
		showCahtFrame(user);// 如果聊天窗口没有显示，则显示聊天窗口
		shake();
	}

	/**
	 * 判断发送消息用户与自己聊天的窗口是否已经显示了。
	 * 
	 * @Author: XiaHui
	 * @Date: 2016年2月16日
	 * @ModifyUser: XiaHui
	 * @ModifyDate: 2016年2月16日
	 * @param sendId
	 * @return
	 */
	public boolean isShowUserChat(String sendId) {
		ChatPanel chatPanel = userChatPanelMap.get(sendId);
		if (null == chatPanel) {
			return false;
		}
		boolean mark = listChatFrame.isShowing() && chatPanel.isShowing();
		return mark;
	}

	/**
	 * 判断群消息聊天窗口是否已经显示了
	 * 
	 * @Author: XiaHui
	 * @Date: 2016年2月16日
	 * @ModifyUser: XiaHui
	 * @ModifyDate: 2016年2月16日
	 * @param sendId
	 * @return
	 */
	public boolean isShowGroupChat(String groupId) {
		ChatPanel chatPanel = groupChatPanelMap.get(groupId);
		if (null == chatPanel) {
			return false;
		}
		boolean mark = listChatFrame.isShowing() && chatPanel.isShowing();
		return mark;
	}

	public void showShake(String sendUserId) {
		UserDataBox ub = appContext.getBox(UserDataBox.class);
		UserData user = ub.getUserData(sendUserId);
		ChatItem item = getChatItem(user);
		if (!chatItemSet.contains(item)) {
			chatItemSet.add(item);
			listChatFrame.addChatItem(item);
			item.addAttribute(ChatPanel.class, getChatPanel(user));
		}
		ChatPanel chatPanel = getChatPanel(user);
		selectChatItem.selectItem(item);

		listChatFrame.setChatPanel(chatPanel);
		listChatFrame.setIconImage(HeadImageIconBox.getUserHeadImageIcon40(user.getHead()).getImage());
		listChatFrame.setVisible(true);
	}

	public ChatPanel getChatPanel(UserData userData) {
		ChatPanel chatPanel = userChatPanelMap.get(userData.getId());
		if (null == chatPanel) {
			chatPanel = new ChatPanel();
			userChatPanelMap.put(userData.getId(), chatPanel);
			chatPanel.addAttribute(UserData.class, userData);

			HeadImageManage him = appContext.getManager(HeadImageManage.class);
			ImageIcon image = him.getUserHeadImageIcon(userData.getId(), userData.getHead(), 40, 40);

			chatPanel.setIcon(image);

			if (null != userData.getRemarkName() && !"".equals(userData.getRemarkName())) {
				chatPanel.setName(userData.getRemarkName());
			} else {
				chatPanel.setName(userData.getNickname());
			}
			chatPanel.setText(userData.getSignature());
			chatPanel.addCloseExecuteAction(colseAction);
			chatPanel.addSendExecuteAction(sendUserAction);
			chatPanel.addFunctionButton(twitterIcon, twitterAction);
			chatPanel.addFunctionButton(videoIcon, videoAction);

			JPanel rightPane = new JPanel();
			rightPane.setLayout(new CardLayout());
			// rightPane.setOpaque(false);
			rightPane.setBackground(new Color(245, 245, 245));
			// rightPane.setBackground(Color.blue);
			rightPane.setPreferredSize(new Dimension(150, 380));
			rightPane.setMaximumSize(new Dimension(150, 380));
			JLabel imageView = new JLabel();
			imageView.setPreferredSize(new Dimension(150, 380));
			rightPane.add(imageView);

			if (!"2".equals(userData.getGender())) {
				ImageIcon icon = ImageBox.getImageIcon("Resources/Images/Default/Show/default_av_girl_v3.png", 140, 380);
				imageView.setIcon(icon);
			} else {
				ImageIcon icon = ImageBox.getImageIcon("Resources/Images/Default/Show/default_av_boy_v3.png", 140, 380);
				imageView.setIcon(icon);
			}
			chatPanel.setUserListPanel(rightPane);
		}
		return chatPanel;
	}

	public ChatPanel getChatPanel(Group group) {
		ChatPanel chatPanel = groupChatPanelMap.get(group.getId());
		if (null == chatPanel) {
			chatPanel = new ChatPanel();
			groupChatPanelMap.put(group.getId(), chatPanel);
			chatPanel.addAttribute(Group.class, group);

			HeadImageManage him = appContext.getManager(HeadImageManage.class);
			Image image = him.getGroupHeadImage(group.getId(), group.getHead(), 40, 40);

			chatPanel.setIcon(new ImageIcon(image));

			chatPanel.setName(group.getName());
			chatPanel.setText(group.getIntroduce());
			chatPanel.addCloseExecuteAction(colseAction);
			chatPanel.addSendExecuteAction(sendGroupAction);
			setGroupUserList(group.getId());
			// GroupUserList groupUserList = new GroupUserList();
			// for (int i = 0; i < 13; i++) {
			// ItemPanel item = new ItemPanel();
			// HeadImageIconBox
			// item.setText("jjjjjjjjkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkjas");
			// item.setIcon(ImageUtil.getRoundedCornerIcon("Resources/Images/Default/UserHead/"
			// + (i + 1) + ".png", 20, 20, 20, 20));
			// groupUserList.add(item);
			// }
			//
			// chatPanel.setUserListPanel(groupUserList);
		}
		return chatPanel;
	}

	public ChatItem getChatItem(UserData user) {
		ChatItem item = userChatPanelItemMap.get(user.getId());
		if (null == item) {
			item = new ChatItem();
			userChatPanelItemMap.put(user.getId(), item);
			if (null != user.getRemarkName() && !"".equals(user.getRemarkName())) {
				item.setText(user.getRemarkName());
			} else {
				item.setText(user.getNickname());
			}
			item.addCloseExecuteAction(executeAction);
			item.addMouseListener(chatItemMouseAdapter);
			item.addAttribute(UserData.class, user);

			HeadImageManage him = appContext.getManager(HeadImageManage.class);
			ImageIcon image = him.getUserHeadImageIcon(user.getId(), user.getHead(), 40, 40);

			item.setIcon(image);
		}
		return item;
	}

	public ChatItem getChatItem(Group group) {
		ChatItem item = groupChatPanelItemMap.get(group.getId());
		if (null == item) {
			item = new ChatItem();
			groupChatPanelItemMap.put(group.getId(), item);
			item.setText(group.getName());
			item.addCloseExecuteAction(executeAction);
			item.addMouseListener(chatItemMouseAdapter);
			item.addAttribute(Group.class, group);

			HeadImageManage him = appContext.getManager(HeadImageManage.class);
			Image image = him.getGroupHeadImage(group.getId(), group.getHead(), 40, 40);

			item.setIcon(new ImageIcon(image));
		}
		return item;
	}

	public void changeChatItem(ChatItem item) {
		UserData user = item.getAttribute(UserData.class);
		if (null != user) {
			ChatPanel chatPanel = getChatPanel(user);
			listChatFrame.setChatPanel(chatPanel);
			PromptManager pm = this.appContext.getManager(PromptManager.class);
			pm.showUserHeadPulse(user.getId(), false);// 停止头像跳动
			pm.remove(user.getId());
		}
		Group group = item.getAttribute(Group.class);
		if (null != group) {
			ChatPanel chatPanel = getChatPanel(group);
			listChatFrame.setChatPanel(chatPanel);
			PromptManager pm = this.appContext.getManager(PromptManager.class);
			pm.showGroupHeadPulse(group.getId(), false);// 停止头像跳动
			pm.remove(group.getId());
		}
		selectChatItem.selectItem(item);
		listChatFrame.repaint();
	}

	public void removeChatItem(ChatItem item) {

		listChatFrame.removeChatItem(item);
		chatItemSet.remove(item);
		if (chatItemSet.size() == 0) {
			listChatFrame.setVisible(false);
		} else {
			if (item.isSelected()) {
				Iterator<ChatItem> iterator = chatItemSet.iterator();
				if (iterator.hasNext()) {
					ChatItem i = iterator.next();
					UserData user = i.getAttribute(UserData.class);
					if (null != user) {
						selectChatItem.selectItem(i);
						ChatPanel chatPanel = getChatPanel(user);
						listChatFrame.setChatPanel(chatPanel);
					}
					Group group = i.getAttribute(Group.class);
					if (null != group) {
						selectChatItem.selectItem(i);
						ChatPanel chatPanel = getChatPanel(group);
						listChatFrame.setChatPanel(chatPanel);
					}
				}
			}
		}
		listChatFrame.repaint();
	}

	public void showCahtFrame(UserData user) {
		ChatItem item = getChatItem(user);
		if (!chatItemSet.contains(item)) {
			chatItemSet.add(item);
			listChatFrame.addChatItem(item);
			item.addAttribute(ChatPanel.class, getChatPanel(user));

		}
		PromptManager pm = this.appContext.getManager(PromptManager.class);
		pm.showUserHeadPulse(user.getId(), false);// 停止头像跳动
		pm.remove(user.getId());// 系统托盘停止跳动

		ChatPanel chatPanel = getChatPanel(user);
		selectChatItem.selectItem(item);

		listChatFrame.setChatPanel(chatPanel);
		listChatFrame.setIconImage(HeadImageIconBox.getUserHeadImageIcon(user.getHead(), 40, 40).getImage());

		listChatFrame.setVisible(true);

	}

	public void showCahtFrame(Group group) {
		ChatItem item = getChatItem(group);
		if (!chatItemSet.contains(item)) {
			chatItemSet.add(item);
			listChatFrame.addChatItem(item);
			item.addAttribute(ChatPanel.class, getChatPanel(group));

		}
		PromptManager pm = this.appContext.getManager(PromptManager.class);
		pm.showGroupHeadPulse(group.getId(), false);// 停止头像跳动
		pm.remove(group.getId());// 系统托盘停止跳动

		ChatPanel chatPanel = getChatPanel(group);

		selectChatItem.selectItem(item);

		listChatFrame.setChatPanel(chatPanel);
		listChatFrame.setIconImage(HeadImageIconBox.getGroupHeadImageIcon(group.getHead(), 40, 40).getImage());

		listChatFrame.setVisible(true);

	}

	/**
	 * 处理聊天窗口左边列表被选中的效果
	 * 
	 * @Author: XiaHui
	 * @Date: 2016年2月16日
	 * @ModifyUser: XiaHui
	 * @ModifyDate: 2016年2月16日
	 */
	class SelectChatItem {
		ChatItem chatItem;// 当前被选中的对象

		public void selectItem(ChatItem item) {
			if (item != this.chatItem) {
				if (null != this.chatItem) {
					this.chatItem.setSelected(false);
				}
				item.setSelected(true);
			}
			this.chatItem = item;
		}
	}

	public void sendUserMessage(final ChatPanel chatPanel) {

		PersonalBox pb = appContext.getBox(PersonalBox.class);
		final UserData sendUser = pb.getUserData();
		final UserData receiveUser = chatPanel.getAttribute(UserData.class);
		final String receiveUserId = receiveUser.getId();

		boolean underline = chatPanel.isUnderline();
		boolean bold = chatPanel.isBold();
		Color colorObject = chatPanel.getColor();
		boolean italic = chatPanel.isItalic();
		String fontName = chatPanel.getFontName();
		int fontSize = chatPanel.getFontSize();
		String color = ColorUtil.getColorInHexFromRGB(colorObject.getRed(), colorObject.getGreen(), colorObject.getBlue());
		String html = chatPanel.getWriteText();

		final Content content = ContentUtil.getContent(html, fontName, fontSize, color, underline, bold, italic);
		if (null != content) {
			chatPanel.initWriteText();

			final 	String name = sendUser.getNickname();
			final String time = OnlyDateUtil.getCurrentDateTime();
			final String nameColor = ColorUtil.getColorInHexFromRGB(32, 143, 62);

			final DataBackActionAdapter action = new DataBackActionAdapter() {
				@Back
				public void back(Info info, @Define("userData") UserData user) {
					if (info.isSuccess()) {
						UserDataBox ub = appContext.getBox(UserDataBox.class);
						UserData userData = ub.getUserData(receiveUserId);
						if (null != userData) {
							LastManager lastManage = appContext.getManager(LastManager.class);
							String text = CoreContentUtil.getText(content);
							lastManage.addOrUpdateLastUserData(userData, text);
						}
					}
				}
			};
			final 	List<Item> items = ContentUtil.getImageItemList(content);
			if (!items.isEmpty()) {
				final 	CallBackAction<List<FileData>> fileBackAction = new CallBackAction<List<FileData>>() {
					@Override
					public void execute(List<FileData> list) {

						for (FileData fd : list) {
							String name = fd.getName();
							String path = "file:/" + AppConstant.userHome + "/" + AppConstant.app_home_path + "/" + sendUser.getNumber() + "/image/" + name;
							chatPanel.replaceImageTag(fd.getId(), path);
						}
					}
				};

				final 	ImageManager im = this.appContext.getManager(ImageManager.class);
				BackAction<List<FileHandleInfo>> backAction = new BackAction<List<FileHandleInfo>>() {

					@Override
					public void back(BackInfo backInfo, List<FileHandleInfo> t) {
						ChatSender ch = appContext.getSender(ChatSender.class);
						ch.sendUserChatMessage(receiveUserId, sendUser.getId(), content, action);
						insertShowChat(chatPanel, name, nameColor, time, content);
						im.handleLocalImage(items, fileBackAction);
					}
				};
				im.uploadImage(items, backAction);
			} else {
				ChatSender ch = appContext.getSender(ChatSender.class);
				ch.sendUserChatMessage(receiveUserId, sendUser.getId(), content, action);
				insertShowChat(chatPanel, name, nameColor, time, content);
			}
		}
	}

	public void sendGroupMessage(final ChatPanel chatPanel) {
		PersonalBox pb = appContext.getBox(PersonalBox.class);
		final UserData user = pb.getUserData();

		String html = chatPanel.getWriteText();

		boolean underline = chatPanel.isUnderline();
		boolean bold = chatPanel.isBold();
		Color colorObject = chatPanel.getColor();
		boolean italic = chatPanel.isItalic();
		final String fontName = chatPanel.getFontName();
		int fontSize = chatPanel.getFontSize();
		String color = ColorUtil.getColorInHexFromRGB(colorObject.getRed(), colorObject.getGreen(), colorObject.getBlue());

		final Content content = ContentUtil.getContent(html, fontName, fontSize, color, underline, bold, italic);
		final String groupId = chatPanel.getAttribute("groupId");
		if (null != content) {
			chatPanel.initWriteText();
			List<Item> items = ContentUtil.getImageItemList(content);
			if (!items.isEmpty()) {// 判断信息中是否有图片，没有图片直接发送，节省响应事件
				ImageManager im = this.appContext.getManager(ImageManager.class);
				BackAction<List<FileHandleInfo>> backAction = new BackAction<List<FileHandleInfo>>() {
					@Override
					public void back(BackInfo backInfo, List<FileHandleInfo> t) {
						ChatSender ch = appContext.getSender(ChatSender.class);
						ch.sendGroupChatMessage(groupId, user.getId(), content);
					}
				};
				im.uploadImage(items, backAction);
			} else {
				ChatSender ch = appContext.getSender(ChatSender.class);
				ch.sendGroupChatMessage(groupId, user.getId(), content);
			}
		}
	}

	public void shake() {
		if (System.currentTimeMillis() - shakeTime < 3000) {
			return;
		}
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 3; i++) {
					listChatFrame.setBounds(listChatFrame.getX() + 4, listChatFrame.getY() - 4, listChatFrame.getWidth(), listChatFrame.getHeight());
					listChatFrame.formResized();
					try {
						Thread.sleep(40);
						listChatFrame.setBounds(listChatFrame.getX() - 8, listChatFrame.getY(), listChatFrame.getWidth(), listChatFrame.getHeight());
						listChatFrame.formResized();
						Thread.sleep(40);
						listChatFrame.setBounds(listChatFrame.getX(), listChatFrame.getY() + 4, listChatFrame.getWidth(), listChatFrame.getHeight());
						listChatFrame.formResized();
						Thread.sleep(40);
						listChatFrame.setBounds(listChatFrame.getX() + 4, listChatFrame.getY(), listChatFrame.getWidth(), listChatFrame.getHeight());
						listChatFrame.formResized();
						Thread.sleep(40);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
				shakeTime = System.currentTimeMillis();
			}
		});
	}

	public void setGroupUserList(final String groupId) {

		List<GroupMember> gml = groupMemberListMap.get(groupId);
		if (null == gml) {
			DataBackAction dataBackAction = new DataBackActionAdapter() {
				@Back
				public void back(@Define("userDataList") List<UserData> userDataList,
						@Define("groupMemberList") List<GroupMember> groupMemberList) {
					setGroupUserList(groupId, userDataList, groupMemberList);
				}
			};
			GroupSender gh = this.appContext.getSender(GroupSender.class);
			gh.getGroupMemberListWithUserDataList(groupId, dataBackAction);
		}
	}

	private void setGroupUserList(String groupId, List<UserData> userDataList, List<GroupMember> groupMemberList) {
		groupMemberListMap.put(groupId, groupMemberList);
		GroupUserList groupUserList = groupUserListMap.get(groupId);
		if (groupUserList == null) {
			groupUserList = new GroupUserList();
			groupUserList.setOpaque(true);
			groupUserList.setBackground(new Color(245, 245, 245));
			groupUserList.setPreferredSize(new Dimension(150, 380));
			groupUserList.setMaximumSize(new Dimension(150, 380));
		}
		for (UserData userData : userDataList) {
			ItemPanel item = new ItemPanel();

			item.addMouseListener(userItemMouseAdapter);
			item.setText(userData.getNickname());
			item.setImageIcon(HeadImageIconBox.getUserHeadImageIcon(userData.getHead(), 20));
			item.addAttribute(UserData.class, userData);
			groupUserList.add(item);
		}
		ChatPanel chatPanel = groupChatPanelMap.get(groupId);
		if (null != chatPanel) {
			chatPanel.setUserListPanel(groupUserList);
			chatPanel.validate();
		}
	}

	@Override
	public void showChatHistoryView(String userId) {
		// TODO Auto-generated method stub

	}

	private void insertShowChat(final ChatPanel cp,final  String name, String color, String time, Content content) {
		if (null != cp) {

			String nameTag = ContentUtil.getInsertNameTag(name, color, time);
			String contentTag = ContentUtil.getInsertContentTag(content);

			cp.insertHtmlText(nameTag);
			cp.insertHtmlText(contentTag);
			List<Item> items = ContentUtil.getImageItemList(content);
			if (!items.isEmpty()) {

				PersonalBox pb = appContext.getBox(PersonalBox.class);
				final UserData sendUser = pb.getUserData();

				BackAction<List<FileHandleInfo>> backAction = new BackAction<List<FileHandleInfo>>() {

					@Override
					public void back(BackInfo backInfo, List<FileHandleInfo> list) {
						for (FileHandleInfo info : list) {
							FileInfo fi = info.getFileInfo();
							if (info.isSuccess()) {
								File file = (null != fi) ? fi.getFile() : null;
								String name = (null != file) ? file.getName() : "";
								String id = (null != fi) ? fi.getId() : "";
								String path = "file:/" + AppConstant.userHome + "/" + AppConstant.app_home_path + "/" + sendUser.getNumber() + "/image/" + name;
								cp.replaceImageTag(id, path);
							} else {
								String id = (null != fi) ? fi.getId() : "";
								String tempImage = "Resources/Images/Default/ChatFrame/ImageLoading/image_download_fail.png";
								File file = new File(tempImage);
								if (file.exists()) {
									tempImage = file.getAbsolutePath();
								}
								cp.replaceImageTag(id, tempImage);
							}
						}
					}
				};
				ImageManager im = this.appContext.getManager(ImageManager.class);
				// im.downloadImage(content, fileBackAction);
				im.downloadImage(items, backAction);
			}
		}
	}

	@Override
	public boolean hasRequestRemoteControl(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showRequestRemoteControl(UserData userData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userChatHistory(UserData userData, List<UserChatHistoryData> contents) {
		// TODO 自动生成的方法存根

	}
}
