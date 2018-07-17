package com.hirohiro716.javafx.control.table;

import com.hirohiro716.RudeArray;
import com.hirohiro716.javafx.control.HashMapComboBox;
import com.hirohiro716.javafx.control.RudeDatePicker;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * ColumnとRowの概念があり 様々なコントロールを行として並べて表示し 複数のRudeArrayオブジェクトを編集するクラス.
 * @author hiro
 */
public class RudeArrayTable extends EditableTable<RudeArray> {

    /**
     * Labelを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成するCallback
     */
    public <T extends Label> void appendColumnLabel(String id, String text, ControlFactory<T> controlFactory) {
        this.appendColumnLabel(id, text, new ReadOnlyControlFactory<RudeArray, T>() {
            @Override
            public T newInstance(RudeArray item) {
                return controlFactory.newInstance(item);
            }
            @Override
            public void setValueForControl(RudeArray item, T control) {
                control.setText(item.getString(id));
            }
        });
    }

    /**
     * TextFieldを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成するCallback
     */
    public <T extends TextField> void appendColumnTextField(String id, String text, ControlFactory<T> controlFactory) {
        super.appendColumnTextField(id, text, new EditableTable.ControlFactory<RudeArray, T>() {
            @Override
            public T newInstance(RudeArray item) {
                return controlFactory.newInstance(item);
            }
            @Override
            public void setValueForControl(RudeArray item, T control) {
                control.setText(item.getString(id));
            }
            @Override
            public void setValueForItem(RudeArray item, T control) {
                item.put(id, control.getText());
            }
        });
    }

    /**
     * PasswordFieldを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成するCallback
     */
    public <T extends PasswordField> void appendColumnPasswordField(String id, String text, ControlFactory<T> controlFactory) {
        super.appendColumnPasswordField(id, text, new EditableTable.ControlFactory<RudeArray, T>() {
            @Override
            public T newInstance(RudeArray item) {
                return controlFactory.newInstance(item);
            }
            @Override
            public void setValueForControl(RudeArray item, T control) {
                control.setText(item.getString(id));
            }
            @Override
            public void setValueForItem(RudeArray item, T control) {
                item.put(id, control.getText());
            }
        });
    }

    /**
     * ComboBoxを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param <V> コンボボックスの値型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成するCallback
     */
    public <T extends ComboBox<V>, V> void appendColumnComboBox(String id, String text, ControlFactory<T> controlFactory) {
        super.appendColumnComboBox(id, text, new EditableTable.ControlFactory<RudeArray, T>() {
            @Override
            public T newInstance(RudeArray item) {
                return controlFactory.newInstance(item);
            }
            @SuppressWarnings("unchecked")
            @Override
            public void setValueForControl(RudeArray item, T control) {
                control.setValue((V) item.get(id));
            }
            @Override
            public void setValueForItem(RudeArray item, T control) {
                item.put(id, control.getValue());
            }
        });
    }

    /**
     * HashMapComboBoxを内包するセルを追加する.
     * @param <K> HashMapComboBoxのキー型
     * @param <V> HashMapComboBoxの値型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成するCallback
     */
    public <K, V> void appendColumnHashMapComboBox(String id, String text, ControlFactory<HashMapComboBox<K, V>> controlFactory) {
        super.appendColumnComboBox(id, text, new EditableTable.ControlFactory<RudeArray, HashMapComboBox<K, V>>() {
            @Override
            public HashMapComboBox<K, V> newInstance(RudeArray item) {
                return controlFactory.newInstance(item);
            }
            @SuppressWarnings("unchecked")
            @Override
            public void setValueForControl(RudeArray item, HashMapComboBox<K, V> control) {
                control.setKey((K) item.get(id));
            }
            @Override
            public void setValueForItem(RudeArray item, HashMapComboBox<K, V> control) {
                item.put(id, control.getKey());
            }
        });
    }
    
    /**
     * RudeDatePickerを内包するセルを追加する.
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成するCallback
     */
    public void appendColumnDatePicker(String id, String text, ControlFactory<RudeDatePicker> controlFactory) {
        super.appendColumnDatePicker(id, text, new EditableTable.ControlFactory<RudeArray, RudeDatePicker>() {
            @Override
            public RudeDatePicker newInstance(RudeArray item) {
                return controlFactory.newInstance(item);
            }
            @Override
            public void setValueForControl(RudeArray item, RudeDatePicker control) {
                control.setValue(item.getDate(id));
            }
            @Override
            public void setValueForItem(RudeArray item, RudeDatePicker control) {
                item.put(id, control.getDate());
            }
        });
    }

    /**
     * CheckBoxを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成するCallback
     */
    public <T extends CheckBox> void appendColumnCheckBox(String id, String text, ControlFactory<T> controlFactory) {
        super.appendColumnCheckBox(id, text, new EditableTable.ControlFactory<RudeArray, T>() {
            @Override
            public T newInstance(RudeArray item) {
                return controlFactory.newInstance(item);
            }
            @Override
            public void setValueForControl(RudeArray item, T control) {
                if (item.getBoolean(id) != null) {
                    control.setSelected(item.getBoolean(id));
                }
            }
            @Override
            public void setValueForItem(RudeArray item, T control) {
                item.put(id, control.isSelected());
            }
        });
    }

    /**
     * セルに内包するコントロールを生成し値の受け渡しを行うCallbackクラス.
     * @author hiro
     * @param <T> Control型
     */
    public static abstract class ControlFactory<T> extends EditableTable.ControlFactory<RudeArray, T> {
        
        @Override
        public final void setValueForControl(RudeArray item, T control) {
            // nop 上でそれぞれOverrideする
        }
        
        @Override
        public final void setValueForItem(RudeArray item, T control) {
            // nop 上でそれぞれOverrideする
        }
        
    }
    
}
