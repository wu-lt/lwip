package com.oim.core.business.manage;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;

import com.oim.common.util.MapUtil;
import com.oim.core.business.box.HeadBox;
import com.oim.core.business.box.PathBox;
import com.oim.core.business.box.PersonalBox;
import com.oim.core.business.box.UserDataBox;
import com.oim.swing.common.box.ImageBox;
import com.onlyxiahui.app.base.AppContext;
import com.onlyxiahui.app.base.component.AbstractManager;
import com.onlyxiahui.im.bean.GroupHead;
import com.onlyxiahui.im.bean.UserData;
import com.onlyxiahui.im.bean.UserHead;

/**
 * @author: XiaHui
 * @date: 2017年6月19日 上午9:31:04
 */
public class HeadImageManage extends AbstractManager {

	String defaultUserHeadPath = "Resources/Images/Head/User/";
	String defaultGroupHeadPath = "Resources/Images/Head/Group/";

	Map<String, String> userHeadMap = new HashMap<String, String>();
	Map<String, String> groupHeadMap = new HashMap<String, String>();

	public HeadImageManage(AppContext appContext) {
		super(appContext);
		initData();
	}

	private void initData() {
		for (int i = 1; i < 101; i++) {
			userHeadMap.put(i + "", i + "_350.png");
			userHeadMap.put(i + "_350", i + "_350.png");
			userHeadMap.put(i + "_100", i + "_100.gif");
			userHeadMap.put(i + "_40", i + ".png");
			userHeadMap.put(i + "_16", i + "_16.bmp");
		}

		for (int i = 174; i < 265; i++) {
			userHeadMap.put(i + "", i + "_100.gif");
			userHeadMap.put(i + "_100", i + "_100.gif");
			userHeadMap.put(i + "_40", i + ".png");
			userHeadMap.put(i + "_16", i + "_16.bmp");
		}

		for (int i = 1; i < 10; i++) {
			groupHeadMap.put(i + "", i + ".png");
		}
	}

	/**
	 * 默认头像路径
	 * 
	 * @author: XiaHui
	 * @param size
	 * @return
	 * @createDate: 2017年6月29日 上午9:18:09
	 * @update: XiaHui
	 * @updateDate: 2017年6月29日 上午9:18:09
	 */
	public String getDefaultUserHeadImagePath(int size) {
		String name = "1_350.png";
		String key = "1";
		if (size > 100) {
			key = "1_350";
			name = "1_350.png";
		} else if (100 >= size && size > 40) {
			key = "1_100";
			name = "1_100.gif";
		} else if (40 >= size && size > 16) {
			key = "1_40";
			name = "1.png";
		} else if (16 >= size) {
			key = "1_16";
			name = "1_16.bmp";
		}
		String head = MapUtil.getOrDefault(userHeadMap, key, name);
		String defaultHeadPath = defaultUserHeadPath + head;
		return defaultHeadPath;
	}

	public String getUserHeadImagePath(String userId, String defaultHead, int size) {

		HeadBox hb = appContext.getBox(HeadBox.class);
		UserHead ud = hb.getUserHead(userId);

		String userHeadPath = null;
		if (null != ud) {
			if (UserHead.type_system.equals(ud.getType())) {
				defaultHead = ud.getHeadId();
			} else {
				userHeadPath = ud.getAbsolutePath();
			}
		}

		String name = "1_350.png";
		String key = defaultHead;
		if (size > 100) {
			key = defaultHead + "_350";
			name = "1_350.png";
		} else if (100 >= size && size > 40) {
			key = defaultHead + "_100";
			name = "1_100.gif";
		} else if (40 >= size && size > 16) {
			key = defaultHead + "_40";
			name = "1.png";
		} else if (16 >= size) {
			key = defaultHead + "_16";
			name = "1_16.bmp";
		}

		String head = MapUtil.getOrDefault(userHeadMap, key, name);
		String defaultHeadPath = defaultUserHeadPath + head;

		String headPath = (StringUtils.isNotBlank(userHeadPath)) ? userHeadPath : defaultHeadPath;
		return headPath;
	}

	/**
	 * 获取默认头像
	 * 
	 * @author: XiaHui
	 * @param size
	 * @return
	 * @createDate: 2017年6月29日 上午9:17:57
	 * @update: XiaHui
	 * @updateDate: 2017年6月29日 上午9:17:57
	 */
	public Image getDefaultUserHeadImage(int size, int round) {
		String defaultHeadPath = getDefaultUserHeadImagePath(size);
		Image image = ImageBox.getImage(defaultHeadPath, size, size, round, round);
		return image;
	}

	public void handleUserHead(UserHead userHead) {
		PathBox pb = appContext.getBox(PathBox.class);

		// String id = userHead.getHeadId();
		String name = userHead.getFileName();
		String savePath = pb.getUserHeadPath();
		String fileName = savePath + name;
		File file = new File(fileName);
		if ((file.exists() && !file.isDirectory())) {
			userHead.setAbsolutePath(file.getAbsolutePath());
		}
	}

	public ImageIcon getUserHeadImageIcon(String userId, String defaultHead, int size, int round) {
		String headPath = getUserHeadImagePath(userId, defaultHead, size);
		ImageIcon image = ImageBox.getImageIcon(headPath, size, size, round, round);
		return image;
	}

	public Image getUserHeadImage(String userId, String defaultHead, int size, int round) {
		String headPath = getUserHeadImagePath(userId, defaultHead, size);
		Image image = ImageBox.getImage(headPath, size, size, round, round);
		return image;
	}

	public String getUserHeadImagePath(String userId, int size) {
		UserDataBox ub = appContext.getBox(UserDataBox.class);
		UserData ud = ub.getUserData(userId);
		String headPath = getDefaultUserHeadImagePath(size);
		if (null != ud) {
			headPath = getUserHeadImagePath(userId, ud.getHead(), size);
		}
		return headPath;
	}

	public Image getUserHeadImage(String userId, String defaultHead) {
		return getUserHeadImage(userId, defaultHead, 40, 40);
	}

	public Image getGroupHeadImage(String groupId, String defaultHead, int size, int round) {
		HeadBox hb = appContext.getBox(HeadBox.class);
		GroupHead gh = hb.getGroupHead(groupId);
		String groupHeadPath = null;
		if (null != gh) {
			if (GroupHead.type_system.equals(gh.getType())) {
				defaultHead = gh.getHeadId();
			} else {
				groupHeadPath = gh.getAbsolutePath();
			}
		}
		String headPath = (StringUtils.isNotBlank(groupHeadPath)) ? groupHeadPath : defaultGroupHeadPath + MapUtil.getOrDefault(groupHeadMap, defaultHead, "1.png");
		Image image = ImageBox.getImage(headPath, size, size, round, round);
		return image;
	}

	public Image getPersonalHeadImage(int size) {
		PersonalBox pb = appContext.getBox(PersonalBox.class);
		String defaultHead = pb.getDefaultHead();
		String userHeadPath = null;
		UserHead ud = pb.getUserHead();
		if (null != ud) {
			if (UserHead.type_system.equals(ud.getType())) {
				defaultHead = ud.getHeadId();
			} else {
				userHeadPath = ud.getAbsolutePath();
			}
		}

		String headPath = (StringUtils.isNotBlank(userHeadPath)) ? userHeadPath : defaultUserHeadPath + MapUtil.getOrDefault(userHeadMap, defaultHead, "1_350.png");
		Image image = ImageBox.getImage(headPath, size, size);
		return image;
	}

	public Image getUserHeadImage(UserHead ud, int size, int round) {

		String defaultHead = "1";
		String userHeadPath = null;
		if (null != ud) {
			// handleUserHead(ud);
			if (UserHead.type_system.equals(ud.getType())) {
				defaultHead = ud.getHeadId();
			} else {
				userHeadPath = ud.getAbsolutePath();
			}
		}

		String name = "1_350.png";
		String key = defaultHead;
		if (size > 100) {
			key = defaultHead + "_350";
			name = "1_350.png";
		} else if (100 >= size && size > 40) {
			key = defaultHead + "_100";
			name = "1_100.gif";
		} else if (40 >= size && size > 16) {
			key = defaultHead + "_40";
			name = "1.png";
		} else if (16 >= size) {
			key = defaultHead + "_16";
			name = "1_16.bmp";
		}

		String head = (((userHeadMap.get(key)) != null) || userHeadMap.containsKey(key)) ? userHeadMap.get(key) : name;
		String defaultHeadPath = defaultUserHeadPath + head;

		String headPath = (StringUtils.isNotBlank(userHeadPath)) ? userHeadPath : defaultHeadPath;
		Image image = ImageBox.getImage(headPath, size, size, round, round);
		if (null == image) {
			image = this.getDefaultUserHeadImage(size, round);
		}
		return image;
	}
}
