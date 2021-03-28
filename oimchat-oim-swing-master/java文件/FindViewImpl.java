package com.oim.swing.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.oim.common.event.ExecuteAction;
import com.oim.core.business.box.GroupBox;
import com.oim.core.business.box.UserDataBox;
import com.oim.core.business.manager.ListManage;
import com.oim.core.business.sender.GroupSender;
import com.oim.core.business.sender.UserSender;
import com.oim.core.business.view.AddUserView;
import com.oim.core.business.view.FindView;
import com.oim.core.business.view.JoinGroupView;
import com.oim.swing.common.box.HeadImageIconBox;
import com.oim.swing.ui.FindFrame;
import com.oim.swing.ui.component.WaitingPanel.WaitingType;
import com.oim.swing.ui.find.ItemHead;
import com.only.common.result.Info;
import com.only.general.annotation.parameter.Define;
import com.only.net.action.Back;
import com.only.net.data.action.DataBackActionAdapter;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.view.AbstractView;
import com.onlyxiahui.im.bean.Group;
import com.onlyxiahui.im.bean.GroupCategory;
import com.onlyxiahui.im.bean.UserCategory;
import com.onlyxiahui.im.bean.UserData;
import com.onlyxiahui.im.message.data.PageData;
import com.onlyxiahui.im.message.data.query.GroupQuery;
import com.onlyxiahui.im.message.data.query.UserQuery;

/**
 * 描述：
 * 
 * @author XiaHui
 * @date 2015年3月16日 下午10:42:19
 * @version 0.0.1
 */
public class FindViewImpl extends AbstractView implements FindView{

	FindFrame findFrame = new FindFrame();
	ExecuteAction userExecuteAction;
	ExecuteAction groupExecuteAction;
	PageData userPage;
	PageData groupPage;
	UserQuery userQuery;
	GroupQuery groupQuery;

	public FindViewImpl(AppContext appContext) {
		super(appContext);
		initEvent();
	}

	private void initEvent() {
		userExecuteAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				openAddView((ItemHead) value);
				return null;
			}
		};
		groupExecuteAction = new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				openJoinGroupView((ItemHead) value);
				return null;
			}
		};
		findFrame.addUserPageChangeAction(new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof Integer) {
					userPageChange((Integer) value);
				}
				return null;
			}
		});

		findFrame.addQueryUserAction(new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof UserQuery) {
					queryUserDataList((UserQuery) value);
				}
				return null;
			}
		});

		findFrame.addGroupPageChangeAction(new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof Integer) {
					groupPageChange((Integer) value);
				}
				return null;
			}
		});

		findFrame.addQueryGroupAction(new ExecuteAction() {

			@Override
			public <T, E> E execute(T value) {
				if (value instanceof GroupQuery) {
					queryGroupList((GroupQuery) value);
				}
				return null;
			}
		});
	}

	public void setVisible(boolean visible) {
		findFrame.setVisible(visible);
	}

	public boolean isShowing() {
		return findFrame.isShowing();

	}

	public void initData() {
		findFrame.initData();
	}

	private void userPageChange(int number) {
		if (null == userPage) {
			userPage = new PageData();
			userPage.setPageSize(16);
		}
		userPage.setPageNumber(number);
		queryUserDataList(userQuery, userPage);
	}

	private void groupPageChange(int number) {
		if (null == groupPage) {
			groupPage = new PageData();
			groupPage.setPageSize(16);
		}
		groupPage.setPageNumber(number);
		queryGroupList(groupQuery, userPage);
	}

	public void queryUserDataList(UserQuery userQuery) {
		this.userQuery = userQuery;
		if (null == userPage) {
			userPage = new PageData();
			userPage.setPageSize(16);
		}
		userPage.setPageNumber(1);
		queryUserDataList(userQuery, userPage);
	}

	public void queryGroupList(GroupQuery groupQuery) {
		this.groupQuery = groupQuery;
		if (null == groupPage) {
			groupPage = new PageData();
			groupPage.setPageSize(16);
		}
		groupPage.setPageNumber(1);
		queryGroupList(groupQuery, groupPage);
	}

	public void queryUserDataList(UserQuery userQuery, PageData page) {

		findFrame.showUserWaiting(true, WaitingType.waiting);
		DataBackActionAdapter dataBackAction = new DataBackActionAdapter() {

			@Override
			public void lost() {
				findFrame.showUserWaiting(true, WaitingType.result);
			}

			@Override
			public void timeOut() {
				findFrame.showUserWaiting(true, WaitingType.result);
			}

			@Back
			public void back(Info info, @Define("userDataList")final  List<UserData> userDataList, @Define("page")final  PageData page) {
				
				java.awt.EventQueue.invokeLater(new Runnable() {
		            public void run() {
		            	findFrame.showUserWaiting(false, WaitingType.waiting);
						setUserDataList(userDataList, page);
		            }
		        });
			}
		};

		UserSender uh = this.appContext.getSender(UserSender.class);
		uh.queryUserDataList(userQuery, page, dataBackAction);
	}

	public void queryGroupList(GroupQuery groupQuery, PageData page) {

		findFrame.showGroupWaiting(true, WaitingType.waiting);
		DataBackActionAdapter dataBackAction = new DataBackActionAdapter() {

			@Override
			public void lost() {
				findFrame.showGroupWaiting(true, WaitingType.result);
			}

			@Override
			public void timeOut() {
				findFrame.showGroupWaiting(true, WaitingType.result);
			}

			@Back
			public void back(Info info, @Define("groupList")final  List<Group> groupList, @Define("page")final  PageData page) {
				java.awt.EventQueue.invokeLater(new Runnable() {
		            public void run() {
		            	findFrame.showGroupWaiting(false, WaitingType.waiting);
						setGroupList(groupList, page);
		            }
		        });
			}
		};

		GroupSender gh = this.appContext.getSender(GroupSender.class);
		gh.queryGroupList(groupQuery, page, dataBackAction);
	}

	public void setUserDataList(List<UserData> userDataList, PageData page) {
		List<ItemHead> itemHeadList = new ArrayList<ItemHead>();
		if (null != userDataList) {
			for (UserData userData : userDataList) {
				ItemHead itemHead = new ItemHead();
				itemHead.add(userExecuteAction);
				itemHead.addAttribute(UserData.class.getName(), userData);
				itemHead.setHeadIcon(HeadImageIconBox.getUserHeadImageIcon(userData.getHead(), 40, 40));
				itemHead.setName(userData.getNickname());
				itemHead.setShowText(userData.getSignature());
				itemHead.setPreferredSize(new Dimension(185, 55));
				itemHeadList.add(itemHead);
			}
		}
		findFrame.setUserPage(page.getPageNumber(), page.getTotalPage());
		findFrame.setUserItemList(itemHeadList);
	}

	public void setGroupList(List<Group> groupList, PageData page) {
		List<ItemHead> itemHeadList = new ArrayList<ItemHead>();
		if (null != groupList) {
			for (Group group : groupList) {
				ItemHead itemHead = new ItemHead();
				itemHead.add(groupExecuteAction);
				itemHead.addAttribute(Group.class, group);
				itemHead.setHeadIcon(HeadImageIconBox.getGroupHeadImageIcon(group.getHead(), 40, 40));
				// itemHead.setHeadIcon(ImageUtil.getRoundedCornerIcon("Resources/Images/Default/GroupHead/"
				// + group.getDefaultHead() + ".png", 40, 40, 40, 40));
				itemHead.setName(group.getName());
				itemHead.setShowText(group.getIntroduce());
				itemHead.setPreferredSize(new Dimension(185, 55));
				itemHeadList.add(itemHead);
			}
		}
		findFrame.setGroupPage(page.getPageNumber(), page.getTotalPage());
		findFrame.setGroupItemList(itemHeadList);
	}

	public void openAddView(ItemHead itemHead) {
		UserData user = itemHead.getAttribute(UserData.class.getName());
		UserDataBox ub = appContext.getBox(UserDataBox.class);
		if (ub.inMemberList(user.getId())) {
			findFrame.showPrompt(user.getNickname() + "已经是你的好友！");
			return;
		}
		ListManage listManage = this.appContext.getManager(ListManage.class);
		AddUserView addView = appContext.getSingleView(AddUserView.class);
		List<UserCategory> userCategoryList = listManage.getUserCategoryList();
		addView.set(user, userCategoryList);
		addView.setVisible(true);
	}

	public void openJoinGroupView(ItemHead itemHead) {
		Group g = itemHead.getAttribute(Group.class);
		GroupBox gb = appContext.getBox(GroupBox.class);
		if (gb.hasGroup(g.getId())) {
			findFrame.showPrompt("你已经加入" + g.getName());
			return;
		}
		ListManage listManage = this.appContext.getManager(ListManage.class);
		JoinGroupView addView = appContext.getSingleView(JoinGroupView.class);
		List<GroupCategory> groupCategoryList = listManage.getGroupCategoryList();
		addView.set(g, groupCategoryList);
		addView.setVisible(true);
	}

	@Override
	public void showPrompt(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectedTab(int index) {
		// TODO Auto-generated method stub
		
	}
}
