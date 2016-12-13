package com.wiitel.tvhelper.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;

import com.wiitel.tvhelper.util.log.LogUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 下载更新文件并设置进度条显示
 */
public class AppUpdateAsync extends AsyncTask<String, Integer, Integer> {

    private static final int SHOW_TOAST_ERROR_SDCARD_STATE = 1;
    private static final int SHOW_TOAST_ERROR_SDCARD_SIZE = 2;
    private static final int SHOW_TOAST_ERROR_SDCARD_UNKNOW = 3;

    private Context mcontext = null;
    private boolean mEnableCanceled = true;
    private String path = "/download/";
    private long fileSize;
    private int downLoadFileSize;
    private String filename;
    private String dir;
    private boolean install;

    private DownloadListener listener;

    public AppUpdateAsync(Context context, boolean install, boolean enableCanceled, DownloadListener listener) {
        this.mcontext = context;
        this.mEnableCanceled = enableCanceled;
        this.listener = listener;
        this.install = install;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (install) {
            installPackage();
        }

    }

    /**
     * ִ执行具体文件下载操作
     */
    @Override
    protected Integer doInBackground(String... params) {
        String strUrl = null;
        try {
            if (params != null) {
                strUrl = params[0];
                return downloadData(strUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 响应publish操作
     */
    int count = 0;

    @Override
    protected void onProgressUpdate(Integer... values) {
        int percent = values[0];
        if (percent % 2 == 0) {
            count++;
        } else {
            count = 0;
        }

        if (count == 1) {
            // mProgressDialog.setProgress(percent);
            if (null != listener) {
                listener.download(percent);
            }
        }

        super.onProgressUpdate(values);
    }

    /**
     * 返回下载的字节数
     *
     * @param url
     * @return
     * @throws IOException
     */
    private int downloadData(String url) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            // 下载函数
            filename = url.substring(url.lastIndexOf("/") + 1);

            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url);
            HttpResponse res = client.execute(request);

            if (res == null || res.getStatusLine() == null || res.getStatusLine().getStatusCode() != 200) {
                mHandler.sendEmptyMessage(SHOW_TOAST_ERROR_SDCARD_UNKNOW);
                return 0;
            }

            this.fileSize = res.getEntity().getContentLength();
            System.out.println("downloadData percent======"+fileSize);
            is = res.getEntity().getContent();
            if (this.fileSize <= 0) {
                mHandler.sendEmptyMessage(SHOW_TOAST_ERROR_SDCARD_UNKNOW);
                return 0;
            }
            if (is == null) {
                mHandler.sendEmptyMessage(SHOW_TOAST_ERROR_SDCARD_UNKNOW);
                return 0;
            }
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File path = Environment.getExternalStorageDirectory();
                // 取得sdcard文件路径
                StatFs statfs = new StatFs(path.getPath());
                // 获取block的SIZE
                long blocSize = statfs.getBlockSize();
                // 获取BLOCK数量
                // long totalBlocks = statfs.getBlockCount();
                // 己使用的Block的数量
                long availaBlock = statfs.getAvailableBlocks();
                if (fileSize > (availaBlock * blocSize)) {
                    mHandler.sendEmptyMessage(SHOW_TOAST_ERROR_SDCARD_SIZE);
                    return 0;
                }
            } else {
                mHandler.sendEmptyMessage(SHOW_TOAST_ERROR_SDCARD_STATE);
                return 0;
            }

            dir = getSDCardDir() + path;
            LogUtil.writeLog(this, "download apk dir=" + dir);
            File fileDir = new File(dir);
            File file = null;
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if (fileDir != null) {
                file = new File(dir + filename);
                if (file.exists()) {
                    file.delete();
                }
                if (file != null && !file.exists())
                    file.createNewFile();
            }

            fos = new FileOutputStream(file);

            byte buf[] = new byte[1024];
            downLoadFileSize = 0;

            do {
                if (isStop()) {
                    break;
                }
                // 循环读取
                int numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;

                int percent = downLoadFileSize * 100 / (int) fileSize;
                LogUtil.writeLog(this, "percent=====" + percent);
                publishProgress(percent);

            } while (true);
        } catch (Exception e) {
            LogUtil.writeLog(this, "error: " + e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ex) {
                LogUtil.writeLog(this, "error: " + ex.getMessage());
            }
        }

        return downLoadFileSize;
    }

    /**
     * 从下载的SD卡上直接安装应用程序
     */
    private void installPackage() {
        if (null != listener) {
            listener.installStart();
        }
        String pkgname = dir + filename;
        LogUtil.writeLog(this, "installPackage =" + pkgname);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 应用安装apk文件属于默认支持的文件类型，它的的mime
        // type被定义为"application/vnd.android.package-archive"
        intent.setDataAndType(Uri.fromFile(new File(pkgname)), "application/vnd.android.package-archive");
        mcontext.startActivity(intent);

//		PackageManager manger=mcontext.getPackageManager();
//		manger.installPackage(arg0, arg1, arg2, arg3);
//		PackageInstaller piInstaller = new PackageInstaller(mcontext);
//		   piInstaller.instatllBatch(pkgname);
    }

    /**
     * 设置是否终止当前下载操作
     */
    private boolean isStop = false;

    public void stopDownload(boolean flag) {
        isStop = flag;
    }

    public boolean isStop() {
        return isStop;
    }

    /**
     * @return sdcard文件路径
     */
    private String getSDCardDir() {
        String sdDir = null;
        if (!isSDCardExist()) {
            return null;
        }
        File file = Environment.getExternalStorageDirectory();
        sdDir = file.getAbsolutePath();

        StatFs st = new StatFs(sdDir);
        int count = st.getAvailableBlocks();
        if (count < 100) {
            return null;
        }
        return sdDir;
    }

    /**
     * 判断SD卡是否存在
     *
     * @return 存在 true,否则false
     */
    private boolean isSDCardExist() {
        String stat = Environment.getExternalStorageState();
        if (stat.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST_ERROR_SDCARD_STATE:
                    // ToastUtil.getInstance().showToast(true,
                    // R.string.external_storge_not_found);
                    break;
                case SHOW_TOAST_ERROR_SDCARD_SIZE:
                    // ToastUtil.getInstance().showToast(true,
                    // R.string.external_storge_no_space);
                    break;
                case SHOW_TOAST_ERROR_SDCARD_UNKNOW:
                    // ToastUtil.getInstance().showToast(true,
                    // R.string.version_update_fail);
                    break;
                default:
                    break;
            }
        }
    };

    public interface DownloadListener {
        public void download(int percent);

        public void installStart();
    }

}
