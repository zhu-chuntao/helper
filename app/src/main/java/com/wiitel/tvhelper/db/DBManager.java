package com.wiitel.tvhelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wiitel.tvhelper.data.AppFlow;
import com.wiitel.tvhelper.data.AppFlowDao;
import com.wiitel.tvhelper.data.DaoMaster;
import com.wiitel.tvhelper.data.DaoSession;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


public class DBManager {
    private final static String dbName = "helper_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * 插入一条记录
     *
     * @param flow
     */
    public void insertFlow(AppFlow flow) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AppFlowDao drugDao = daoSession.getAppFlowDao();
        drugDao.insertOrReplace(flow);
    }

    /**
     * 查询用户列表
     */
    public AppFlow queryFlowList(String currentMonth, int flowId) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AppFlowDao drugDao = daoSession.getAppFlowDao();
        QueryBuilder<AppFlow> qb = drugDao.queryBuilder().where(AppFlowDao.Properties.Appid.eq(flowId)).where(AppFlowDao.Properties.CurrentMonth.eq(currentMonth));
        //List<AppFlow> list = qb.unique().list();
        AppFlow flow = qb.unique();
        return flow;
    }


    /**
     * 更新一条记录
     *
     * @param flow
     */
    public void updateFlow(AppFlow flow) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AppFlowDao flowDao = daoSession.getAppFlowDao();
        flowDao.update(flow);
    }

    public void deleteFlow(String currentMonth){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AppFlowDao drugDao = daoSession.getAppFlowDao();
        DeleteQuery<AppFlow> qb=drugDao.queryBuilder().where(AppFlowDao.Properties.CurrentMonth.le(currentMonth)).buildDelete();
        qb.executeDeleteWithoutDetachingEntities();
    }



    public void executeSql(){
        //String sql = “select * from "+ xxxDao;
        //Cursor c = session.getDatabase().rawQuery(sql,null);
    }


}