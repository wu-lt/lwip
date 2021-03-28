package com.oim.swing.common.util;

import com.onlyxiahui.im.bean.UserData;

/**
 * @author: XiaHui
 * @date: 2016年10月11日 下午12:50:33
 */
public class HeadUtil {
	
	public static boolean isGray(String status){
		boolean gray=true;
		switch (status) {
		case UserData.status_online:
			gray=false;
			break;
		case UserData.status_call_me:
			gray=false;
			break;
		case UserData.status_away:
			gray=false;
			break;
		case UserData.status_busy:
			gray=false;
			break;
		case UserData.status_mute:
			gray=false;
			break;
		case UserData.status_invisible:
			gray=true;
			break;
		case UserData.status_offline:
			gray=true;
			break;
		default:
			gray=true;
			break;
		}
		return gray;
	}
}
