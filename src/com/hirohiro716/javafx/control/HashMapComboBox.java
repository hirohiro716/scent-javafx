package com.hirohiro716.javafx.control;

import java.util.HashMap;

import com.hirohiro716.awt.RobotJapanese.ImeMode;
import com.hirohiro716.javafx.IMEHelper;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;

/**
 * 連想配列を使用できるComboBox. 値は通常のComboBoxと同じように取得できキーはgetKey()メソッドで取得する.
 * @author hiro
 * @param <K> キー
 * @param <V> 値
 */
public class HashMapComboBox<K, V> extends ComboBox<V> {

    /**
     * コンストラクタ
     */
    public HashMapComboBox() {
        this(null);
    }

    /**
     * コンストラクタで連想配列を指定する.
     * @param hashMap
     */
    public HashMapComboBox(HashMap<K, V> hashMap) {
        super();
        this.addEventFilter(KeyEvent.KEY_RELEASED, this.keyReleasedEventHanler);
        this.valueProperty().addListener(this.valueChangeListener);
        this.setHashMap(hashMap);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                IMEHelper.apply(HashMapComboBox.this, ImeMode.OFF);
            }
        });
    }

    /**
     * 値をBSやDELキーで消す事ができるかどうかを示すプロパティ.
     */
    private final BooleanProperty clearableProperty = new SimpleBooleanProperty(false);

    /**
     * 値をBSやDELキーで消す事ができるかどうかを示すプロパティを取得する.
     * @return BooleanProperty
     */
    public BooleanProperty clearbleProperty() {
        return this.clearableProperty;
    }

    /**
     * 値をBSやDELキーで消す事ができるようになる.
     * @param isClearable
     */
    public void setClearable(boolean isClearable) {
        this.clearableProperty.set(isClearable);
    }
    /**
     * 値をBSやDELキーで消すことができるかどうか.
     * @return isClearable
     */
    public boolean getClearable() {
        return this.clearableProperty.get();
    }

    /**
     * 値をBSやDELキーで消すことができるかどうか.
     * @return isClearable
     */
    public boolean isClearable() {
        return this.clearableProperty.get();
    }

    private HashMap<V, K> keys = new HashMap<>();
    private HashMap<K, V> values;

    /**
     * 連想配列をリセットする.
     */
    public void clearHashMap() {
        this.keys = new HashMap<>();
        this.values = null;
        this.getItems().clear();
    }

    /**
     * 連想配列をセットする.
     * @param hashMap
     */
    public void setHashMap(HashMap<K, V> hashMap) {
        if (hashMap == null) {
            this.clearHashMap();
            return;
        }
        ObservableList<V> list = FXCollections.observableArrayList();
        for (K key: hashMap.keySet()) {
            list.add(hashMap.get(key));
            this.keys.put(hashMap.get(key), key);
        }
        this.setItems(list);
        this.values = hashMap;
    }
    
    private final ObjectProperty<K> keyProperty = new SimpleObjectProperty<>();
    
    /**
     * 選択されるキープロパティを取得する.
     * @return ObjectProperty
     */
    public ObjectProperty<K> keyProperty() {
        return this.keyProperty;
    }
    
    /**
     * 選択された値のキーを取得する.
     * @return キー
     */
    public K getKey() {
        return this.keyProperty.get();
    }

    /**
     * キーを指定して値を入力する.
     * @param key キー
     */
    public void setKey(K key) {
        if (this.values == null) {
            this.getSelectionModel().clearSelection();
        } else {
            try {
                this.getSelectionModel().select(this.values.get(key));
            } catch (IndexOutOfBoundsException exception) {
            }
        }
    }
    
    /**
     * 値が変更された場合にキーも変更するListener.
     */
    private ChangeListener<V> valueChangeListener = new ChangeListener<V>() {
        @Override
        public void changed(ObservableValue<? extends V> observable, V oldValue, V newValue) {
            HashMapComboBox<K, V> comboBox = HashMapComboBox.this;
            comboBox.keyProperty.set(comboBox.keys.get(newValue));
        }
    };

    /**
     * BSやDELキーで値にnullを入力する.
     */
    private EventHandler<KeyEvent> keyReleasedEventHanler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            HashMapComboBox<K, V> comboBox = HashMapComboBox.this;
            if (comboBox.clearableProperty.get() && comboBox.isEditable() == false) {
                switch (event.getCode()) {
                case BACK_SPACE:
                case DELETE:
                    comboBox.setValue(null);
                    break;
                default:
                }
            }
        }
    };

}
