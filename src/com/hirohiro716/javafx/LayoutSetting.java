package com.hirohiro716.javafx;

import java.io.IOException;
import com.hirohiro716.ByteConverter;
import com.hirohiro716.file.xml.InterfaceProperty;
import com.hirohiro716.RudeArray;
import com.hirohiro716.database.AbstractBindTable;

/**
 * レイアウト設定を保存するクラス.
 * @author hiro
 */
public class LayoutSetting {

    /**
     * 配列に格納する項目列挙型.
     * @author hiro
     */
    public enum Property implements InterfaceProperty {
        /**
         * 左位置
         */
        LAYOUT_X("左位置", 0),
        /**
         * 上位置
         */
        LAYOUT_Y("上位置", 0),
        /**
         * 幅
         */
        WIDTH("幅", 0),
        /**
         * 高さ
         */
        HEIGHT("高さ", 0),
        ;

        private Property(String logicalName, Object defaultValue) {
            this.logicalName = logicalName;
            this.defaultValue = defaultValue;
            this.maxLength = -1;
        }

        private String logicalName;

        @Override
        public String getLogicalName() {
            return this.logicalName;
        }

        private Object defaultValue;

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        private int maxLength;

        @Override
        public int getMaxLength() {
            return this.maxLength;
        }

    }

    /**
     * コンストラクタ.
     */
    public LayoutSetting() {
        this.values = AbstractBindTable.createDefaultRow(Property.values());
    }

    /**
     * コンストラクタで以前の設定を復元する.
     * @param bytesString
     */
    public LayoutSetting(String bytesString) {
        try {
            byte[] bytes = ByteConverter.stringToBytes(bytesString);
            this.values = RudeArray.desirialize(bytes);
        } catch (Exception exception) {
            this.values = AbstractBindTable.createDefaultRow(Property.values());
        }
    }
    
    private RudeArray values;

    /**
     * データベースに保存するための文字列を取得する.
     * @return bytesString
     */
    public String getConvertString() {
        try {
            byte[] bytes = this.values.toSerialize();
            String bytesString = ByteConverter.bytesToString(bytes);
            return bytesString;
        } catch (IOException exception) {
            return null;
        }
    }
    
    /**
     * 横方向の位置を取得する.
     * @return 横方向の位置
     */
    public double getLayoutX() {
    	Property column = Property.LAYOUT_X;
    	Double value = this.values.getDouble(column.getPhysicalName());
    	if (value == null) {
    		return (double) column.getDefaultValue();
    	}
    	return this.values.getDouble(column.getPhysicalName());
    }
    
    /**
     * 横方向の位置をセットする.
     * @param layoutX
     */
    public void setLayoutX(double layoutX) {
    	this.values.put(Property.LAYOUT_X.getPhysicalName(), layoutX);
    }

    /**
     * 縦方向の位置を取得する.
     * @return 縦方向の位置
     */
    public double getLayoutY() {
    	Property column = Property.LAYOUT_Y;
    	Double value = this.values.getDouble(column.getPhysicalName());
    	if (value == null) {
    		return (double) column.getDefaultValue();
    	}
    	return this.values.getDouble(column.getPhysicalName());
    }
    
    /**
     * 縦方向の位置をセットする.
     * @param layoutY
     */
    public void setLayoutY(double layoutY) {
    	this.values.put(Property.LAYOUT_Y.getPhysicalName(), layoutY);
    }

    /**
     * 幅を取得する.
     * @return 幅
     */
    public double getWidth() {
    	Property column = Property.WIDTH;
    	Double value = this.values.getDouble(column.getPhysicalName());
    	if (value == null) {
    		return (double) column.getDefaultValue();
    	}
    	return this.values.getDouble(column.getPhysicalName());
    }
    
    /**
     * 幅をセットする.
     * @param width
     */
    public void setWidth(double width) {
    	this.values.put(Property.WIDTH.getPhysicalName(), width);
    }

    /**
     * 高さを取得する.
     * @return 高さ
     */
    public double getHeight() {
    	Property column = Property.HEIGHT;
    	Double value = this.values.getDouble(column.getPhysicalName());
    	if (value == null) {
    		return (double) column.getDefaultValue();
    	}
    	return this.values.getDouble(column.getPhysicalName());
    }
    
    /**
     * 高さをセットする.
     * @param height
     */
    public void setHeight(double height) {
    	this.values.put(Property.HEIGHT.getPhysicalName(), height);
    }
    
    /**
     * 横方向の位置・縦方向の位置・幅・高さをセットする.
     * @param layoutX 横方向の位置
     * @param layoutY 縦方向の位置
     * @param width 幅
     * @param height 高さ
     */
    public void setAll(double layoutX, double layoutY, double width, double height) {
    	this.setLayoutX(layoutX);
    	this.setLayoutY(layoutY);
    	this.setWidth(width);
    	this.setHeight(height);
    }
    
}
