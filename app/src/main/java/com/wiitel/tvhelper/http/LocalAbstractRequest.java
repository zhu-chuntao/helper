package com.wiitel.tvhelper.http;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.manager.RequestManager;
import com.android.volley.manager.RequestManager.RequestListener;
import com.wiitel.tvhelper.util.log.LogUtil;

import java.util.Map;

public abstract class LocalAbstractRequest {

    public Uri dbPath;
    public String url;

    public Context context;

    public String tag;

    private boolean searchLocal;
    private boolean saveLocal;

    public LocalAbstractRequest(Context context, boolean searchLocal, boolean saveLocal) {
        this.context = context;
        this.searchLocal = searchLocal;
        this.saveLocal = saveLocal;
        tag = getClass().getSimpleName();
    }

    public LocalAbstractRequest(String tagName, Context context) {
        this.context = context;
    }


    public abstract RespData localRequestData(Context context);

    public abstract void notifyData(RespData data);

    public abstract RespData getData(String msg);

    public void searchLocalData(Context context) {
        if (searchLocal) {
            SearchTask task = new SearchTask();
            task.execute(context);
        }
    }

    private class SearchTask extends AsyncTask<Context, String, RespData> {

        @Override
        protected RespData doInBackground(Context... params) {
            return localRequestData(params[0]);
        }

        @Override
        protected void onPostExecute(RespData result) {
            super.onPostExecute(result);
            LocalAbstractRequest.this.notifyData(result);
        }
    }

    public void serverRequestData(RequestData requestParams) {
        if (TextUtils.isEmpty(url)) {
            notifyError("url empty");
        } else {
            String realUrl = "";
            if (null != requestParams) {
                realUrl = url + requestParams.getParams();
            } else {
                realUrl = url;
            }
            LogUtil.writeLog(this, "reqest url==" + realUrl);
            RequestManager.getInstance().post(realUrl, null, new RequestListener() {

                @Override
                public void onRequest() {
                    notifyStart(url);
                }

                @Override
                public void onError(String arg0, String arg1, int arg2) {
                    LogUtil.writeLog(this, "reqest error==" + arg0);
                    notifyError(arg0);
                }

                @Override
                public void onSuccess(String arg0, Map<String, String> arg1, String arg2, int arg3) {
                    LogUtil.writeLog(this, "reqest success==" + arg0);
                    System.out.println("onSuccess ===="+arg0);
                    notifySuccess(arg0);
                }

            }, 0);
        }
    }

    public void notifySuccess(String msg) {
        RespData data = getData(msg);

        notifyData(data);

    }


    public final void notifyStart(String msg) {
        if (null != listener) {
            listener.start(tag, msg);
        }
    }

    public final void notifyError(String msg) {
        if (null != listener) {
            listener.error(tag, msg);
        }
    }

    public Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        public void start(String tag, String msg);

        public void error(String tag, String msg);
    }
}
