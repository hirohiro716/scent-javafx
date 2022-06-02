package com.hirohiro716.javafx;

import com.hirohiro716.javafx.control.AutoCompleteTextField;
import com.hirohiro716.javafx.control.HashMapComboBox;
import com.hirohiro716.javafx.control.LimitComboBox;
import com.hirohiro716.javafx.control.LimitPasswordField;
import com.hirohiro716.javafx.control.LimitTextArea;
import com.hirohiro716.javafx.control.LimitTextField;
import com.hirohiro716.javafx.control.RudeDatePicker;
import com.hirohiro716.javafx.control.table.DynamicTableView;
import com.hirohiro716.javafx.control.table.EditableTable;
import com.hirohiro716.javafx.control.table.RudeArrayTable;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Pnae内のコントロールを検索して取得するクラス。
 *
 * @author hiro
 */
public class PaneNodeFinder {

    private Pane pane;

    /**
     * コンストラクタ。
     *
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param pane
     */
    public <T extends Pane> PaneNodeFinder(T pane) {
        this.pane = pane;
    }
    
    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return Pane(該当するものがなければnull)
     */
    public Pane findPane(String selector) {
        return PaneHelper.findNode(this.pane, Pane.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return HBox(該当するものがなければnull)
     */
    public HBox findHBox(String selector) {
        return PaneHelper.findNode(this.pane, HBox.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return HBox(該当するものがなければnull)
     */
    public VBox findVBox(String selector) {
        return PaneHelper.findNode(this.pane, VBox.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return Label(該当するものがなければnull)
     */
    public Label findLabel(String selector) {
        return PaneHelper.findNode(this.pane, Label.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return Button(該当するものがなければnull)
     */
    public Button findButton(String selector) {
        return PaneHelper.findNode(this.pane, Button.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return TextField(該当するものがなければnull)
     */
    public TextField findTextField(String selector) {
        return PaneHelper.findNode(this.pane, TextField.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return LimitTextField(該当するものがなければnull)
     */
    public LimitTextField findLimitTextField(String selector) {
        return PaneHelper.findNode(this.pane, LimitTextField.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return AutoCompleteTextField(該当するものがなければnull)
     */
    public AutoCompleteTextField findAutoCompleteTextField(String selector) {
        return PaneHelper.findNode(this.pane, AutoCompleteTextField.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return TextArea(該当するものがなければnull)
     */
    public TextArea findTextArea(String selector) {
        return PaneHelper.findNode(this.pane, TextArea.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return LimitTextArea(該当するものがなければnull)
     */
    public LimitTextArea findLimitTextArea(String selector) {
        return PaneHelper.findNode(this.pane, LimitTextArea.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return PasswordField(該当するものがなければnull)
     */
    public PasswordField findPasswordField(String selector) {
        return PaneHelper.findNode(this.pane, PasswordField.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return LimitPasswordField(該当するものがなければnull)
     */
    public LimitPasswordField findLimitPasswordField(String selector) {
        return PaneHelper.findNode(this.pane, LimitPasswordField.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param <T> ComboBoxの値型
     * @param selector
     * @return ComboBox(該当するものがなければnull)
     */
    @SuppressWarnings("unchecked")
    public <T> ComboBox<T> findComboBox(String selector) {
        return PaneHelper.findNode(this.pane, ComboBox.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param <T> LimitComboBoxの値型
     * @param selector
     * @return LimitComboBox(該当するものがなければnull)
     */
    @SuppressWarnings("unchecked")
    public <T> LimitComboBox<T> findLimitComboBox(String selector) {
        return PaneHelper.findNode(this.pane, LimitComboBox.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param <K> HashMapComboBoxのキー型
     * @param <V> HashMapComboBoxの値型
     * @param selector
     * @return HashMapComboBox(該当するものがなければnull)
     */
    @SuppressWarnings("unchecked")
    public <K, V> HashMapComboBox<K, V> findHashMapComboBox(String selector) {
        return PaneHelper.findNode(this.pane, HashMapComboBox.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return CheckBox(該当するものがなければnull)
     */
    public CheckBox findCheckBox(String selector) {
        return PaneHelper.findNode(this.pane, CheckBox.class, selector);
    }
    
    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return ImageView(該当するものがなければnull)
     */
    public ImageView findImageView(String selector) {
        return PaneHelper.findNode(this.pane, ImageView.class, selector);
    }
    
    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param <S> TableViewのitem型
     * @param selector
     * @return TableView(該当するものがなければnull)
     */
    @SuppressWarnings("unchecked")
    public <S> TableView<S> findTableView(String selector) {
        return PaneHelper.findNode(this.pane, TableView.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return DatePicker(該当するものがなければnull)
     */
    public DatePicker findDatePicker(String selector) {
        return PaneHelper.findNode(this.pane, DatePicker.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return RudeDatePicker(該当するものがなければnull)
     */
    public RudeDatePicker findRudeDatePicker(String selector) {
        return PaneHelper.findNode(this.pane, RudeDatePicker.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return DynamicTableView(該当するものがなければnull)
     */
    public DynamicTableView findDynamicTableView(String selector) {
        return PaneHelper.findNode(this.pane, DynamicTableView.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param <S> EditableTableのitem型
     * @param selector
     * @return EditableTable(該当するものがなければnull)
     */
    @SuppressWarnings("unchecked")
    public <S> EditableTable<S> findEditableTable(String selector) {
        return PaneHelper.findNode(this.pane, EditableTable.class, selector);
    }
    
    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return RudeArrayTable(該当するものがなければnull)
     */
    public RudeArrayTable findRudeArrayTable(String selector) {
        return PaneHelper.findNode(this.pane, RudeArrayTable.class, selector);
    }
    
    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return ColorPicker(該当するものがなければnull)
     */
    public ColorPicker findColorPicker(String selector) {
        return PaneHelper.findNode(this.pane, ColorPicker.class, selector);
    }

    /**
     * セレクターを指定してコントロールを取得する。
     *
     * @param selector
     * @return Hyperlink(該当するものがなければnull)
     */
    public Hyperlink findHyperlink(String selector) {
        return PaneHelper.findNode(this.pane, Hyperlink.class, selector);
    }

}
