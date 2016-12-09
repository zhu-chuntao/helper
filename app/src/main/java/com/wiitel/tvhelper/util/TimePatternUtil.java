package com.wiitel.tvhelper.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuchuntao on 16-11-30.
 */

public class TimePatternUtil {


    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {

                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }

    public static String getDate(String dateStr, TimePattern pattern) throws ParseException {
        return getSdf(pattern.getPattern()).format(dateStr);
    }

    public static String getDate(long dateStr, TimePattern pattern) throws ParseException {
        return getSdf(pattern.getPattern()).format(dateStr);
    }




    /**
     * SimpleDateFormat函数语法：
     * <p>
     * G 年代标志符
     * y 年
     * M 月
     * d 日
     * h 时 在上午或下午 (1~12)
     * H 时 在一天中 (0~23)
     * m 分
     * s 秒
     * S 毫秒
     * E 星期
     * D 一年中的第几天
     * F 一月中第几个星期几
     * w 一年中第几个星期
     * W 一月中第几个星期
     * a 上午 / 下午 标记符
     * k 时 在一天中 (1~24)
     * K 时 在上午或下午 (0~11)
     * z 时区
     */

    /**
     * 可以随着项目需要随时添加
     */
    public enum TimePattern {

        BASIC("yyyy-MM-dd HH-mm"),
        YM("yyyyMM");
        private String pattern;

        String getPattern() {
            return pattern;
        }

        TimePattern(String pattern) {
            this.pattern = pattern;
        }
    }
}
