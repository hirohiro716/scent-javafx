package com.hirohiro716.javafx.dialog.datetime;

import com.hirohiro716.StringConverter;

/**
 * 月の選択ダイアログの結果クラス.
 * @author hiro
 */
public class MonthResult {
    
    /**
     * コンストラクタ.
     * @param year
     * @param month
     */
    public MonthResult(int year, int month) {
        this.year = year;
        this.month = month;
    }
    
    private int year;
    
    /**
     * 年を取得する.
     * @return 年
     */
    public int getYear() {
        return this.year;
    }
    
    private int month;
    
    /**
     * 月を取得する.
     * @return 月
     */
    public int getMonth() {
        return this.month;
    }

    @Override
    public String toString() {
        return StringConverter.join(this.year, "-", StringConverter.paddingLeft(String.valueOf(this.month), '0', 2));
    }
    
}
