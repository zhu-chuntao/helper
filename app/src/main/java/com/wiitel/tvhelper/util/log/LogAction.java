package com.wiitel.tvhelper.util.log;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogAction {
	private ConcurrentLinkedQueue<LogData> messageList = new ConcurrentLinkedQueue();
	public static final int ADDLOG = 1;
	private static final String suffixName = ".log";
	private static final String defaultFolderName = "default";
	private boolean canChangeFolderName;
	private boolean saveSdcard;
	private boolean displayCommand;
	private String folderPath;
	private String logPath;
	private LogThread thread;
	private byte[] lock = new byte[0];
	private boolean isRunning = false;
	public static LogAction instance;

	private static int DAYS = 7;

	public static LogAction getInstance() {
		if (instance == null) {
			instance = new LogAction();
		}
		return instance;
	}

	public LogAction() {
		this.saveSdcard = false;
		this.displayCommand = false;
		this.canChangeFolderName = true;
		this.folderPath =

		(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "wiitel" + File.separator
				+ "logs" + File.separator + "default");
		this.logPath = (this.folderPath + File.separator + getCurrentDate() + ".log");
	}

	public void putLog(LogData log) {
		if (this.displayCommand) {
			Log.i("wiitel", log.getTime() + ":" + log.getTag() + ":" + log.getMsg());
		}
		if (this.saveSdcard) {
			this.messageList.add(log);
			if (this.thread == null) {
				this.thread = new LogThread();
				this.thread.start();
			} else if (!this.isRunning) {
				this.thread.interrupt();
				System.gc();
				this.thread = null;
				this.thread = new LogThread();
				this.thread.start();
			}
		}
	}

	public void clearOldFile() {
//		File parent = new File(folderPath);
//		if (parent.isDirectory()) {
//			String last7 = TimeUtil.getLastSevenDate();
//			for (File file : parent.listFiles()) {
//				String fileName = file.getName();
//				String abFileName = fileName.substring(0, fileName.indexOf("."));
//				if (fileName.contains(".") && TimeUtil.compare_date(last7, abFileName)) {
//					file.delete();
//				}
//			}
//		} else {
//
//		}
	}

	private class LogThread extends Thread {
		private LogThread() {
		}

		public void run() {
			synchronized (LogAction.this.lock) {
				LogAction.this.isRunning = true;
				for (LogData log : LogAction.this.messageList) {
					LogAction.this.saveLog2File(log);
					LogAction.this.removeMsg(log);
				}
			}
			LogAction.this.isRunning = false;
		}
	}

	private void removeMsg(LogData log) {
		this.messageList.remove(log);
	}

	private void saveLog2File(LogData log) {
		File file = new File(this.logPath);
		File folder = new File(this.folderPath);
		if (folder.exists()) {
			startSaveLog(file, log);
		} else {
			folder.mkdirs();
			startSaveLog(file, log);
		}
	}

	private void startSaveLog(File file, LogData log) {
		if (file.exists()) {
			writeToFile(file, log, true);
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writeToFile(file, log, true);
		}
	}

	private void writeToFile(File file, LogData log, boolean isAppend) {

		OutputStreamWriter osw = null;

		try {
			osw = new OutputStreamWriter(new FileOutputStream(file, isAppend), "UTF-8");
			String message = ( "1.0:" + log.getTime() + "--->" + log.getTag()
					+ "--->" + log.getMsg() + "\n\n");
			osw.write(message);
		} catch (Exception e1) {
		} finally {
			try {
				if (osw != null) {
					osw.close();
				}
			} catch (IOException e) {
			}
		}

	}

	private String getCurrentDate() {
		DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		return format1.format(new Date());
	}

	public String getLogFileName() {
		return this.logPath;
	}

	public boolean isSaveSdcard() {
		return this.saveSdcard;
	}

	public void setSaveSdcard(boolean save) {
		this.saveSdcard = save;
	}

	public void setDisplayCommand(boolean displayCommand) {
		this.displayCommand = displayCommand;
	}

	public void setFolderName(String folderName) {
		if (TextUtils.isEmpty(folderName)) {
			folderName = "default";
		}
		this.canChangeFolderName = false;
		this.folderPath =

		(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "wiitel" + File.separator
				+ "logs" + File.separator + folderName);
		this.logPath = (this.folderPath + File.separator + getCurrentDate() + ".log");
	}

	public boolean canChangeFolderName() {
		return this.canChangeFolderName;
	}

	public void destroy() {
		instance = null;
		System.gc();
	}

	public void cpFiles() throws Exception {
		String copyPath = "/mnt/usbhost/Storage01/" + getCurrentDate();
		File cpFile = new File(copyPath);
		if (!cpFile.exists()) {
			cpFile.mkdirs();
		}else{
			cpFile.delete();
			cpFile.mkdirs();
		}
		cpUdisk(folderPath, copyPath);
	}

	public void cpUdisk(String path, String copyPath) throws Exception {
		File filePath = new File(path);
		DataInputStream read;
		DataOutputStream write;
		if (filePath.isDirectory()) {
			File[] list = filePath.listFiles();
			for (int i = 0; i < list.length; i++) {
				String newPath = path + File.separator + list[i].getName();
				String newCopyPath = copyPath + File.separator + list[i].getName();
				cpUdisk(newPath, newCopyPath);
			}
		} else if (filePath.isFile()) {
			read = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
			write = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(copyPath)));
			byte[] buf = new byte[1024 * 512];
			while (read.read(buf) != -1) {
				write.write(buf);
			}
			read.close();
			write.close();
		} else {
		}
	}
}
