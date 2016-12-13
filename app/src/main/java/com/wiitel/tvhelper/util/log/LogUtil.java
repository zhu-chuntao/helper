package com.wiitel.tvhelper.util.log;

import android.text.TextUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
	public static final int EVENT_TYPE_CLICK = 1;
	public static final int EVENT_TYPE_TOUCH = 2;
	public static final int EVENT_TYPE_DOWN = 3;
	public static final int EVENT_TYPE_UP = 4;
	public static final int EVENT_TYPE_MOVE = 5;
	public static final int EVENT_TYPE_SCORLL = 6;
	public static final int EVENT_TYPE_NORMAL = 10;

	public static void writeLog(Object obj, String msg) {
		if (obj == null) {
			return;
		}
		writeLog(obj.getClass().getName(), msg);
	}

	public static void writeLog(String tag, String msg) {
		if ((TextUtils.isEmpty(msg)) || (TextUtils.isEmpty(tag))) {
			return;
		}
		writeLog(tag, msg, 10);
	}

	public static void writeLog(String tag, String msg, int type) {
		LogData log = new LogData();
		log.setTag(tag);
		log.setMsg(msg);
		log.setTime(getTimeFormatter());
		log.setType(type);
		LogAction.getInstance().putLog(log);
	}

	private static String getTimeFormatter() {
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format1.format(new Date());
	}

	public static void setSaveLog(boolean save, String folderName) {
		LogAction.getInstance().setSaveSdcard(save);
		if (LogAction.getInstance().canChangeFolderName()) {
			LogAction.getInstance().setFolderName(folderName);
		}
	}

	public static void setSaveLog(boolean save) {
		setSaveLog(save, "");
	}

	public static void setDisplayLog(boolean save) {
		LogAction.getInstance().setDisplayCommand(save);
	}

	public static String getLogFileName() {
		return LogAction.getInstance().getLogFileName();
	}

	public static void destory() {
		LogAction.getInstance().destroy();
	}

	public static void clearOldFile() {
		LogAction.getInstance().clearOldFile();
	}

	public static void cpFile() {
		try {
			LogAction.getInstance().cpFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
