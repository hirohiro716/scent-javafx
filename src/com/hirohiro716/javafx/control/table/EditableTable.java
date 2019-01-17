package com.hirohiro716.javafx.control.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.PaneNodeFinder;
import com.hirohiro716.javafx.control.ScrollToNodePane;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * ColumnとRowの概念があり 様々なコントロールを行として並べて表示し 複数のオブジェクトを編集するクラス.<br>
 * Ctrl+↑で行を上に移動し Ctrl+↓で行を下に移動する<br>
 * （スタイルクラス:"editable-table" ヘッダーHBoxのスタイルクラス:"header" 全行VBoxのスタイルクラス:"rows" 行HBoxのスタイルクラス:"row"）
 * @author hiro
 * @param <S>
 */
public class EditableTable<S> extends AnchorPane {

    /**
     * カラムの種類.
     * @author hiro
     */
    @SuppressWarnings("javadoc")
    public enum ColumnType {
        LABEL,
        TEXTFIELD,
        PASSWORD,
        COMBOBOX,
        DATEPICKER,
        CHECKBOX,
        BUTTON,
    }
    
    /**
     * Header用のScrollPane.
     */
    private ScrollPane headerScrollPane = new ScrollPane();
    
    /**
     * Header用のHBox.
     */
    private HBox headerHBox = new HBox();

    /**
     * Rowを追加していくためのVBox.
     */
    private VBox rowsVBox = new VBox();
    
    /**
     * itemとRowのHBoxを関連付ける連想配列.
     */
    private HashMap<S, HBox> rowHBoxes = new HashMap<>();
    
    /**
     * 選択状態の行HBoxを表す擬似CSSクラス.
     */
    private PseudoClass selectedPseudoClass = new PseudoClass() {
        @Override
        public String getPseudoClassName() {
            return "selected";
        }
    };
    
    /**
     * コンストラクタ.
     */
    public EditableTable() {
        EditableTable<S> table = this;
        this.getStyleClass().add("editable-table");
        this.getStylesheets().add(this.getClass().getResource("EditableTable.css").toExternalForm());
        // ヘッダー用HBox
        this.headerHBox.getStyleClass().add("header");
        this.headerHBox.setPadding(new Insets(this.rowTopBottomPaddingProperty.get() + 2, 0, this.rowTopBottomPaddingProperty.get(), 0));
        this.headerScrollPane.setContent(this.headerHBox);
        // ヘッダー用HBoxをスクロールPaneにセットして横スクロールバーは除去
        this.headerScrollPane.maxWidthProperty().bind(this.widthProperty());
        this.headerScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        this.headerScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.headerScrollPane.setMinHeight(0);
        this.headerScrollPane.setVmax(0);
        this.headerScrollPane.hvalueProperty().bind(this.rowsScrollPane.hvalueProperty());
        this.getChildren().add(this.headerScrollPane);
        LayoutHelper.setAnchor(this.headerScrollPane, 0d, 0d, null, 5d);
        // ヘッダーとテーブルデータのボーダー
        Separator separator = new Separator(Orientation.HORIZONTAL);
        this.getChildren().add(separator);
        // テーブルデータ用GridPaneをスクロールPaneにセット
        this.rowsVBox.getStyleClass().add("rows");
        this.rowsScrollPane.maxWidthProperty().bind(this.widthProperty());
        this.rowsScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        this.rowsScrollPane.setContent(this.rowsVBox);
        this.getChildren().add(this.rowsScrollPane);
        // Rowsの上位置調整
        this.headerScrollPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                LayoutHelper.setAnchor(table.rowsScrollPane, newValue.doubleValue() + 4, 0d, 0d, 0d);
                LayoutHelper.setAnchor(separator, newValue.doubleValue() + 3, 0d, null, 0d);
            }
        });
        // 選択されている行の変更
        this.selectedItemProperty.addListener(new ChangeListener<S>() {
            @Override
            public void changed(ObservableValue<? extends S> observable, S oldValue, S newValue) {
                HBox oldHBox = table.rowHBoxes.get(oldValue);
                if (oldHBox != null) {
                    oldHBox.pseudoClassStateChanged(EditableTable.this.selectedPseudoClass, false);
                    if (table.isDisabled() == false) {
                        table.disableRowControlFocusTraversable(oldValue);
                    }
                }
                HBox newHBox = table.rowHBoxes.get(newValue);
                if (newHBox != null) {
                    newHBox.pseudoClassStateChanged(EditableTable.this.selectedPseudoClass, true);
                    table.rollbackRowControlFocusTraversable(newValue);
                }
            }
        });
        // コントロール自体が無効にされる場合は次にフォーカスした場合にFocusTraversableが戻らない
        this.disabledProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue == false) {
                    table.rollbackRowControlFocusTraversable(table.getSelectedItem());
                }
            }
        });
        // 後から入れないとStageのサイズに影響を及ぼす
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                table.loadMoreRows();
            }
        });
        // スクロールが最下部なら更に行を読み込む
        this.rowsScrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.doubleValue() == 1) {
                    table.loadMoreRows();
                }
            }
        });
        // キーイベントを設定
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isControlDown()) {
                    int index;
                    switch (event.getCode()) {
                    case UP:
                        index = table.getItems().indexOf(table.getSelectedItem());
                        if (index > 0) {
                            S selectItem = table.getItems().get(index - 1);
                            if (table.getFocusedControl() == null) {
                                table.setSelectedItem(selectItem);
                            } else {
                                table.getControl(selectItem, table.getFocusedControl().getId()).requestFocus();
                            }
                        }
                        if (index == 0 && table.cantMoveUpAtFirstRowCallback != null) {
                            table.cantMoveUpAtFirstRowCallback.call(table.getSelectedItem());
                        }
                        event.consume();
                        break;
                    case DOWN:
                        index = table.getItems().indexOf(table.getSelectedItem());
                        if (index != -1 && index < table.getItems().size() - 1) {
                            S selectItem = table.getItems().get(index + 1);
                            if (table.getFocusedControl() == null) {
                                table.setSelectedItem(selectItem);
                            } else {
                                table.getControl(selectItem, table.getFocusedControl().getId()).requestFocus();
                            }
                        }
                        if (index == table.getItems().size() - 1 && table.cantMoveDownAtLastRowCallback != null) {
                            table.cantMoveDownAtLastRowCallback.call(table.getSelectedItem());
                        }
                        event.consume();
                        break;
                    default:
                    }
                }
            }
        });
        // とりあえず非表示
        for (Node node: this.getChildren()) {
            node.setVisible(false);
        }
    }
    
    private CantMoveCallback<S> cantMoveUpAtFirstRowCallback = null;
    
    /**
     * 最初の行でCtrl+↑が押された場合に発生するイベントをセットする.
     * @param cantMoveCallback
     */
    public void setCantMoveUpAtFirstRowCallback(CantMoveCallback<S> cantMoveCallback) {
        this.cantMoveUpAtFirstRowCallback = cantMoveCallback;
    }
    
    private CantMoveCallback<S> cantMoveDownAtLastRowCallback = null;
    
    /**
     * 最後の行でCtrl+↓が押された場合に発生するイベントをセットする.
     * @param cantMoveCallback
     */
    public void setCantMoveDownAtLastRowCallback(CantMoveCallback<S> cantMoveCallback) {
        this.cantMoveDownAtLastRowCallback = cantMoveCallback;
    }
    
    /**
     * RowをスクロールさせるためのScrollPane.
     */
    private ScrollToNodePane rowsScrollPane = new ScrollToNodePane();
    
    /**
     * RowをスクロールしているScrollPaneを取得する.
     * @return ScrollToNodePane
     */
    public ScrollToNodePane getRowsScrollPane() {
        return this.rowsScrollPane;
    }
    
    private List<S> items = new ArrayList<>();
    
    /**
     * 読み取り専用ですべてのitemを取得する.
     * @return 読み取り専用のList<S>
     */
    public List<S> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    /**
     * Rowを追加する.
     * @param item
     */
    public void appendRow(S item) {
        this.items.add(item);
    }
    
    /**
     * Rowを追加する.
     * @param item
     * @param index
     */
    public void appendRow(S item, int index) {
        if (this.items.size() <= index) {
            this.appendRow(item);
        }
        if (this.visibleRowsCount > index) {
            this.initializeRow(item);
            HBox hBox = this.rowHBoxes.get(item);
            this.rowsVBox.getChildren().remove(hBox);
            this.rowsVBox.getChildren().add(index, hBox);
            this.items.add(index, item);
        }
    }
    
    /**
     * Rowを追加する.
     * @param items
     */
    public void appendRows(S[] items) {
        for (S item: items) {
            this.items.add(item);
        }
    }
    
    /**
     * Rowを追加する.
     * @param items
     */
    public void appendRows(List<S> items) {
        for (S item: items) {
            this.items.add(item);
        }
    }
    
    /**
     * 最初の行かどうかを判定する.
     * @param item
     * @return 結果
     */
    public boolean isFirstRow(S item) {
        return this.items.indexOf(item) == 0;
    }
    
    /**
     * 最後の行かどうかを判定する.
     * @param item
     * @return 結果
     */
    public boolean isLastRow(S item) {
        return this.items.indexOf(item) == this.items.size() - 1;
    }

    /**
     * Rowを削除する.
     * @param item
     */
    public void removeRow(S item) {
        this.rowsVBox.getChildren().remove(this.rowHBoxes.get(item));
        this.rowHBoxes.remove(item);
        this.items.remove(item);
        this.visibleRowsCount--;
        this.requestFocus();
    }
    
    /**
     * Rowをすべて削除する.
     */
    public void clearRows() {
        this.rowsVBox.getChildren().clear();
        this.rowHBoxes.clear();
        this.items.clear();
        this.visibleRowsCount = 0;
        this.requestFocus();
    }
    
    /**
     * Rowの値をitemから再読み込みする.
     * @param item
     */
    @SuppressWarnings("unchecked")
    public void updateRow(S item) {
        if (this.rowHBoxes.get(item) == null) {
            return;
        }
        // HBoxを取得
        HBox itemHBox = this.rowHBoxes.get(item);
        PaneNodeFinder paneNodeFinder = new PaneNodeFinder(itemHBox);
        // 各カラムの設定に基づい値を再表示
        try {
            for (String id: this.columnIds) {
                ColumnType columnType = this.columnTypes.get(id);
                switch (columnType) {
                case LABEL:
                    ReadOnlyControlFactory<S, Label> labelFactory = (ReadOnlyControlFactory<S, Label>) this.controlFactories.get(id);
                    Label label = paneNodeFinder.findLabel("#" + id);
                    labelFactory.setValueForControl(item, label);
                    break;
                case TEXTFIELD:
                    ControlFactory<S, TextField> textFieldFactory = (ControlFactory<S, TextField>) this.controlFactories.get(id);
                    TextField textField = paneNodeFinder.findTextField("#" + id);
                    textFieldFactory.setValueForControl(item, textField);
                    break;
                case PASSWORD:
                    ControlFactory<S, PasswordField> passwordFieldFactory = (ControlFactory<S, PasswordField>) this.controlFactories.get(id);
                    PasswordField passwordField = paneNodeFinder.findPasswordField("#" + id);
                    passwordFieldFactory.setValueForControl(item, passwordField);
                    break;
                case COMBOBOX:
                    ControlFactory<S, ComboBox<?>> comboBoxFactory = (ControlFactory<S, ComboBox<?>>) this.controlFactories.get(id);
                    ComboBox<?> comboBox = paneNodeFinder.findComboBox("#" + id);
                    comboBoxFactory.setValueForControl(item, comboBox);
                    break;
                case DATEPICKER:
                    ControlFactory<S, DatePicker> datePickerFactory = (ControlFactory<S, DatePicker>) this.controlFactories.get(id);
                    DatePicker datePicker = datePickerFactory.newInstance(item);
                    datePickerFactory.setValueForControl(item, datePicker);
                    break;
                case CHECKBOX:
                    ControlFactory<S, CheckBox> checkBoxFactory = (ControlFactory<S, CheckBox>) this.controlFactories.get(id);
                    CheckBox checkBox = paneNodeFinder.findCheckBox("#" + id);
                    checkBoxFactory.setValueForControl(item, checkBox);
                    break;
                case BUTTON:
                    break;
                }
            }
        } catch (Exception exception) {
        }
    }
    
    private SimpleDoubleProperty rowTopBottomPaddingProperty = new SimpleDoubleProperty(8);
    
    /**
     * 行の上下余白を取得する.
     * @return TOP & Bottom padding
     */
    public double getRowTopBottomPadding() {
        return this.rowTopBottomPaddingProperty.get();
    }
    
    /**
     * 行の上下余白をセットする.
     * @param rowTopBottomPadding
     */
    public void setRowTopBottomPadding(double rowTopBottomPadding) {
        this.rowTopBottomPaddingProperty.set(rowTopBottomPadding);
        // 既存のHBoxにも
        this.headerHBox.setPadding(new Insets(rowTopBottomPadding + 2, 0, rowTopBottomPadding, 0));
        for (S item: this.items) {
            HBox hBox = this.rowHBoxes.get(item);
            if (hBox != null) {
                hBox.setPadding(new Insets(rowTopBottomPadding, 0, rowTopBottomPadding, 0));
            }
        }
    }
    
    /**
     * Rowを初期化する.
     * @param item
     */
    @SuppressWarnings("unchecked")
    public void initializeRow(S item) {
        if (item == null) {
            return;
        }
        HBox itemHBox = this.rowHBoxes.get(item);
        if (itemHBox == null) {
            itemHBox = new HBox();
            itemHBox.setUserData(item);
            itemHBox.setAlignment(Pos.CENTER);
            itemHBox.setPadding(new Insets(this.rowTopBottomPaddingProperty.get(), 0, this.rowTopBottomPaddingProperty.get(), 0));
            itemHBox.getStyleClass().add("row");
            if (this.selectedItemProperty.get() == item) {
                itemHBox.pseudoClassStateChanged(EditableTable.this.selectedPseudoClass, true);
            }
            itemHBox.setOnMouseClicked(new FocusingItemMouseEventHandler(item));
            this.rowsVBox.getChildren().add(itemHBox);
            this.rowHBoxes.put(item, itemHBox);
        } else {
            itemHBox.getChildren().clear();
        }
        // 各カラムの設定に基づいたセルを生成
        for (String id: this.columnIds) {
            Separator columnSeparator = new Separator(Orientation.VERTICAL);
            columnSeparator.setOpacity(0);
            itemHBox.getChildren().add(columnSeparator);
            ColumnType columnType = this.columnTypes.get(id);
            Label headerLabel = this.columnHeaderLabels.get(id);
            Control addedControl = null;
            switch (columnType) {
            case LABEL:
                ReadOnlyControlFactory<S, Label> labelFactory = (ReadOnlyControlFactory<S, Label>) this.controlFactories.get(id);
                Label label = labelFactory.newInstance(item);

                labelFactory.setValueForControl(item, label);
                label.prefWidthProperty().bind(headerLabel.widthProperty());
                itemHBox.getChildren().add(label);
                addedControl = label;
                break;
            case TEXTFIELD:
                ControlFactory<S, TextField> textFieldFactory = (ControlFactory<S, TextField>) this.controlFactories.get(id);
                TextField textField = textFieldFactory.newInstance(item);
                textFieldFactory.setValueForControl(item, textField);
                textField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        textFieldFactory.setValueForItem(item, textField);
                    }
                });
                textField.prefWidthProperty().bind(headerLabel.widthProperty());
                itemHBox.getChildren().add(textField);
                addedControl = textField;
                break;
            case PASSWORD:
                ControlFactory<S, PasswordField> passwordFieldFactory = (ControlFactory<S, PasswordField>) this.controlFactories.get(id);
                PasswordField passwordField = passwordFieldFactory.newInstance(item);
                passwordFieldFactory.setValueForControl(item, passwordField);
                passwordField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        passwordFieldFactory.setValueForItem(item, passwordField);
                    }
                });
                passwordField.prefWidthProperty().bind(headerLabel.widthProperty());
                itemHBox.getChildren().add(passwordField);
                addedControl = passwordField;
                break;
            case COMBOBOX:
                ControlFactory<S, ComboBox<?>> comboBoxFactory = (ControlFactory<S, ComboBox<?>>) this.controlFactories.get(id);
                ComboBox<?> comboBox = comboBoxFactory.newInstance(item);
                comboBoxFactory.setValueForControl(item, comboBox);
                comboBox.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        comboBoxFactory.setValueForItem(item, comboBox);
                    }
                });
                comboBox.prefWidthProperty().bind(headerLabel.widthProperty());
                itemHBox.getChildren().add(comboBox);
                addedControl = comboBox;
                break;
            case DATEPICKER:
                ControlFactory<S, DatePicker> datePickerFactory = (ControlFactory<S, DatePicker>) this.controlFactories.get(id);
                DatePicker datePicker = datePickerFactory.newInstance(item);
                datePickerFactory.setValueForControl(item, datePicker);
                datePicker.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        datePickerFactory.setValueForItem(item, datePicker);
                    }
                });
                datePicker.prefWidthProperty().bind(headerLabel.widthProperty());
                itemHBox.getChildren().add(datePicker);
                addedControl = datePicker;
                break;
            case CHECKBOX:
                ControlFactory<S, CheckBox> checkBoxFactory = (ControlFactory<S, CheckBox>) this.controlFactories.get(id);
                CheckBox checkBox = checkBoxFactory.newInstance(item);
                checkBoxFactory.setValueForControl(item, checkBox);
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        checkBoxFactory.setValueForItem(item, checkBox);
                    }
                });
                StackPane stackPaneCheckBox = new StackPane(checkBox);
                stackPaneCheckBox.prefWidthProperty().bind(headerLabel.widthProperty());
                itemHBox.getChildren().add(stackPaneCheckBox);
                addedControl = checkBox;
                break;
            case BUTTON:
                FixControlFactory<S, Button> buttonFactory = (FixControlFactory<S, Button>) this.controlFactories.get(id);
                Button button = buttonFactory.newInstance(item);
                StackPane stackPaneButton = new StackPane(button);
                stackPaneButton.prefWidthProperty().bind(headerLabel.widthProperty());
                itemHBox.getChildren().add(stackPaneButton);
                addedControl = button;
                break;
            }
            // コントロール共通の設定
            addedControl.setId(id);
            // 現在の行を特定するためのListenerを設定
            ChangeListener<Boolean> innerControlFocusChangeListener = new InnerControlFocusChangeListener(item, addedControl);
            addedControl.focusedProperty().addListener(innerControlFocusChangeListener);
        }
        // すべてのコントロールのFocusTraversableを無効にする
        if (this.rowControlFocusTraversables.get(item) == null) {
            this.rowControlFocusTraversables.put(item, new HashMap<>());
        }
        this.disableRowControlFocusTraversable(item);
        // 最後にカラムと幅を同じにするためにseparatorを追加する
        Separator columnSeparator = new Separator(Orientation.VERTICAL);
        columnSeparator.setOpacity(0);
        itemHBox.getChildren().add(columnSeparator);
    }
    
    private SimpleIntegerProperty loadRowsCountProperty = new SimpleIntegerProperty(20);
    
    /**
     * Rowを更に読み込む際の行数（初期値は20）.
     * @return 読み込み行数
     */
    public int getLoadRowsCount() {
        return this.loadRowsCountProperty.get();
    }
    
    /**
     * Rowを更に読み込む際の行数をセットする（初期値は20）.
     * @param loadRowsCount 読み込み行数
     */
    public void setLoadRowsCount(int loadRowsCount) {
        this.loadRowsCountProperty.set(loadRowsCount);
    }
    
    private int visibleRowsCount = 0;
    
    /**
     * すでに表示されている行数を取得する.
     * @return 表示されている行数
     */
    public int getVisibleRowsCount() {
        return this.visibleRowsCount;
    }
    
    /**
     * Rowを更に読み込む.
     */
    public void loadMoreRows() {
        for (int index = this.visibleRowsCount; index < this.visibleRowsCount + this.loadRowsCountProperty.get(); index++) {
            if (this.items.size() - 1 < index) {
                break;
            }
            S item = this.items.get(index);
            this.initializeRow(item);
        }
        this.visibleRowsCount += this.loadRowsCountProperty.get();
        if (this.visibleRowsCount > this.items.size()) {
            this.visibleRowsCount = this.items.size();
        }
        if (this.getSelectedItem() == null && this.visibleRowsCount > 0) {
            this.setSelectedItem(this.items.get(0));
        }
    }
    
    /**
     * コントロールに関連づいているitemを取得する.
     * @param control 対象Control
     * @return item（取得できない場合はnull）
     */
    @SuppressWarnings("unchecked")
    public S findRelationalItem(Control control) {
        try {
            Parent parent = control.getParent();
            return (S) parent.getUserData();
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * itemとカラムIDから内部のコントロールを取得する.
     * @param <T>
     * @param item 対象のitem
     * @param id 対象のカラムID
     * @return コントロール（表示されていない場合はnull）
     * @throws ClassCastException 取得しようとしているclassと内部コントロールのclassが不一致の場合
     */
    @SuppressWarnings("unchecked")
    public <T extends Control> T getControl(S item, String id) throws ClassCastException {
        HBox hBox = this.rowHBoxes.get(item);
        if (hBox == null) {
            return null;
        }
        T control = (T) hBox.lookup("#" + id);
        return control;
    }
    
    private HashMap<S, HashMap<String, Boolean>> rowControlFocusTraversables = new HashMap<>();
    
    private HashMap<S, Boolean> isDisabledRowControlFocusTraversables = new HashMap<>();
    
    /**
     * 指定された行のFocusTraversableを保存しておく.
     * @param item
     */
    private void disableRowControlFocusTraversable(S item) {
        HashMap<String, Boolean> hashMap = this.rowControlFocusTraversables.get(item);
        HBox hBox = this.rowHBoxes.get(item);
        if (hashMap != null && hBox != null) {
            for (String id: this.columnIds) {
                Set<Node> nodes = hBox.lookupAll("#" + id);
                for (Node node: nodes) {
                    if (this.isDisabledRowControlFocusTraversables.get(item) == null || this.isDisabledRowControlFocusTraversables.get(item) == false) {
                        hashMap.put(id, node.isFocusTraversable());
                        node.setFocusTraversable(false);
                    }
                    break;
                }
            }
            this.isDisabledRowControlFocusTraversables.put(item, true);
        }
    }
    
    /**
     * 指定された行のFocusTraversableを保存している値に戻す.
     * @param item
     */
    private void rollbackRowControlFocusTraversable(S item) {
        HashMap<String, Boolean> hashMap = this.rowControlFocusTraversables.get(item);
        HBox hBox = this.rowHBoxes.get(item);
        if (hashMap != null && hBox != null) {
            for (String id: this.columnIds) {
                Set<Node> nodes = hBox.lookupAll("#" + id);
                for (Node node: nodes) {
                    if (hashMap.containsKey(id)) {
                        node.setFocusTraversable(hashMap.get(id));
                    }
                    break;
                }
            }
            this.isDisabledRowControlFocusTraversables.put(item, false);
        }
    }
    
    private HashMap<String, Label> columnHeaderLabels = new HashMap<>();
    
    /**
     * ColumnのHeaderに配置されているLabelコントロールを取得する.
     * @param id 対象のカラムID
     * @return Label（存在しない場合はnull）
     */
    public Label getHeaderLabel(String id) {
        return this.columnHeaderLabels.get(id);
    }
    
    private SimpleObjectProperty<S> selectedItemProperty = new SimpleObjectProperty<>();
    
    /**
     * 現在選択されているitemプロパティ.
     * @return selectedItemProperty
     */
    public ReadOnlyProperty<S> selectedItemProperty() {
        return this.selectedItemProperty;
    }
    
    /**
     * 現在選択されているitemを取得する.
     * @return selectedItem
     */
    public S getSelectedItem() {
        return this.selectedItemProperty.get();
    }
    
    /**
     * 現在選択されているitemをセットする.
     * @param selectedItem
     */
    public void setSelectedItem(S selectedItem) {
        if (this.columnIds.size() == 0) {
            return;
        }
        if (this.items.indexOf(selectedItem) > -1) {
            this.selectedItemProperty.set(selectedItem);
            if (this.getFocusedControl() != null) {
                Control control = this.getControl(selectedItem, this.getFocusedControl().getId());
                if (control != null) {
                    control.requestFocus();
                }
            }
        }
    }
    
    private SimpleObjectProperty<Control> focusedControlProperty = new SimpleObjectProperty<>();
    
    /**
     * 現在フォーカスされているControlプロパティ.
     * @return focusedControlProperty
     */
    public ReadOnlyProperty<Control> focusedControlProperty() {
        return this.focusedControlProperty;
    }
    
    /**
     * 現在フォーカスされているControlを取得する.
     * @param <T> 
     * @return フォーカスされているコントロール
     * @throws ClassCastException 取得しようとしているclassと内部コントロールのclassが不一致の場合
     */
    @SuppressWarnings("unchecked")
    public <T extends Control> T getFocusedControl() throws ClassCastException {
        return (T) this.focusedControlProperty.get();
    }
    
    /**
     * ColumnIDのList.
     */
    private List<String> columnIds = new ArrayList<>();
    
    /**
     * 追加済みのColumnIDの読み取り専用Listを取得する.
     * @return 読み取り専用のColumnIDリスト
     */
    public List<String> getColumnIds() {
        return Collections.unmodifiableList(this.columnIds);
    }
    
    /**
     * 各Column種類の連想配列（キーはID）.
     */
    private HashMap<String, ColumnType> columnTypes = new HashMap<>();
    
    /**
     * 各Columnのコントロールを生成するFactoryの連想配列（キーはID）.
     */
    private HashMap<String, ControlFactory<S, ?>> controlFactories = new HashMap<>();
    
    /**
     * カラムを追加する.
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param columnType 種類
     * @param controlFactory コントロールを生成し値の受け渡しを行うCallback
     */
    public void appendColumn(String id, String text, ColumnType columnType, ControlFactory<S, ?> controlFactory) {
        if (this.columnIds.contains(id)) {
            return;
        }
        // 内容を表示
        for (Node node: this.getChildren()) {
            node.setVisible(true);
        }
        // カラムの追加処理
        Label label = this.createHeaderLabel(text);
        switch (columnType) {
        case LABEL:
            break;
        case TEXTFIELD:
        case PASSWORD:
        case COMBOBOX:
        case DATEPICKER:
            label.setMinWidth(60);
            break;
        case CHECKBOX:
            label.setMinWidth(30);
            break;
        case BUTTON:
            label.setMinWidth(45);
            break;
        }
        this.columnIds.add(id);
        this.columnHeaderLabels.put(id, label);
        this.controlFactories.put(id, controlFactory);
        this.columnTypes.put(id, columnType);
        if (this.visibleRowsCount > 0) {
            for (int index = 0; index < this.visibleRowsCount; index++) {
                S item = this.items.get(index);
                this.initializeRow(item);
            }
        }
    }
    
    /**
     * Labelを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param readOnlyControlFactory コントロールを生成し値の表示を行うCallback
     */
    public <T extends Label> void appendColumnLabel(String id, String text, ReadOnlyControlFactory<S, T> readOnlyControlFactory) {
        this.appendColumn(id, text, ColumnType.LABEL, readOnlyControlFactory);
    }
    
    /**
     * TextFieldを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成し値の受け渡しを行うCallback
     */
    public <T extends TextField> void appendColumnTextField(String id, String text, ControlFactory<S, T> controlFactory) {
        this.appendColumn(id, text, ColumnType.TEXTFIELD, controlFactory);
    }
    
    /**
     * PasswordFieldを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成し値の受け渡しを行うCallback
     */
    public <T extends PasswordField> void appendColumnPasswordField(String id, String text, ControlFactory<S, T> controlFactory) {
        this.appendColumn(id, text, ColumnType.PASSWORD, controlFactory);
    }
    
    /**
     * ComboBoxを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param <V> コンボボックスの値型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成し値の受け渡しを行うCallback
     */
    public <T extends ComboBox<V>, V> void appendColumnComboBox(String id, String text, ControlFactory<S, T> controlFactory) {
        this.appendColumn(id, text, ColumnType.COMBOBOX, controlFactory);
    }
    
    /**
     * DatePickerを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成し値の受け渡しを行うCallback
     */
    public <T extends DatePicker> void appendColumnDatePicker(String id, String text, ControlFactory<S, T> controlFactory) {
        this.appendColumn(id, text, ColumnType.DATEPICKER, controlFactory);
    }
    
    /**
     * CheckBoxを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param controlFactory コントロールを生成し値の受け渡しを行うCallback
     */
    public <T extends CheckBox> void appendColumnCheckBox(String id, String text, ControlFactory<S, T> controlFactory) {
        this.appendColumn(id, text, ColumnType.CHECKBOX, controlFactory);
    }
    
    /**
     * Buttonを内包するセルを追加する.
     * @param <T> コントロールの型
     * @param id 任意のカラムID
     * @param text ヘッダーテキスト
     * @param fixControlFactory コントロールを生成を行うCallback
     */
    public <T extends Button> void appendColumnButton(String id, String text, FixControlFactory<S, T> fixControlFactory) {
        this.appendColumn(id, text, ColumnType.BUTTON, fixControlFactory);
    }
    
    /**
     * ヘッダーLabelを生成する.
     * @param text ヘッダーテキスト
     * @return Label
     */
    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setPadding(new Insets(0, 5, 0, 5));
        label.setAlignment(Pos.CENTER);
        this.headerHBox.getChildren().add(label);
        Separator separator = this.createHeaderSeparator(label);
        this.headerHBox.getChildren().add(separator);
        return label;
    }

    private double headerResizeStartWidth;
    private double headerResizeStartX;

    /**
     * ヘッダーLabelのりサイズ機能を備えたSeparatorを生成する.
     * @param columnHeaderLabel
     * @return Separator
     */
    private Separator createHeaderSeparator(Label columnHeaderLabel) {
        EditableTable<S> table = this;
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                separator.setCursor(Cursor.E_RESIZE);
            }
        });
        separator.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                separator.setCursor(Cursor.DEFAULT);
            }
        });
        separator.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                table.headerResizeStartWidth = columnHeaderLabel.getWidth();
                table.headerResizeStartX = event.getScreenX();
            }
        });
        separator.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                columnHeaderLabel.setPrefWidth(table.headerResizeStartWidth + event.getScreenX() - table.headerResizeStartX);
                separator.setCursor(Cursor.E_RESIZE);
            }
        });
        separator.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                separator.setCursor(Cursor.DEFAULT);
            }
        });
        return separator;
    }

    /**
     * 最初の行でCtrl+↑が押された場合 または最終行でCtrl+↓が押された場合に発生するイベントクラス.
     * @author hiro
     * @param <S>
     */
    public static abstract class CantMoveCallback<S> {
        
        /**
         * 最初の行でCtrl+↑が押された場合 または最終行でCtrl+↓が押された場合に発生するイベント.
         * @param selectedItem イベントが発生した際に選択されているitem
         */
        public abstract void call(S selectedItem);
        
    }
    
    /**
     * 行のHBoxをクリックした場合にfocusedItemプロパティを選択させるEventHandlerクラス.
     * @author hiro
     */
    private class FocusingItemMouseEventHandler implements EventHandler<MouseEvent> {

        private S item;
        
        /**
         * コンストラクタ.
         * @param item 発生元のitem
         */
        public FocusingItemMouseEventHandler(S item) {
            this.item = item;
        }
        
        @Override
        public void handle(MouseEvent event) {
            EditableTable.this.selectedItemProperty.set(this.item);
        }
        
    }
    
    /**
     * セルに内包するコントロールのフォーカスのChangeListenerクラス.
     * @author hiro
     */
    private class InnerControlFocusChangeListener implements ChangeListener<Boolean> {
        
        private S item;
        
        private Control control;
        
        /**
         * コンストラクタ.
         * @param item 発生元のitem
         * @param control 発生元のcontrol
         */
        public InnerControlFocusChangeListener(S item, Control control) {
            this.item = item;
            this.control = control;
        }
        
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            EditableTable<S> table = EditableTable.this;
            if (newValue) {
                // 現在のitemとコントロールを記録
                table.selectedItemProperty.set(this.item);
                table.focusedControlProperty.set(this.control);
                // 現在のviewportにコントロールが表示されていなければスクロール位置を調整
                table.rowsScrollPane.scroll(this.control);
            } else {
                table.focusedControlProperty.set(null);
            }
        }
        
    }
    
    /**
     * セルに内包するコントロールを生成し値の受け渡しを行うCallbackクラス.
     * @author hiro
     * @param <S> LiveTableViewのitem型
     * @param <T> Control型
     */
    public static abstract class ControlFactory<S, T> {
        
        /**
         * コントロールのインスタンスを生成する処理.
         * @param item 生成する行のitem
         * @return コントロールのインスタンス
         */
        public abstract T newInstance(S item);

        /**
         * itemからコントロールに値をセットする.
         * @param item 行のitem
         * @param control コントロール
         */
        public abstract void setValueForControl(S item, T control);
        
        /**
         * コントロールからitemに値をセットする.
         * @param item 行のitem
         * @param control コントロール
         */
        public abstract void setValueForItem(S item, T control);
        
    }
    
    /**
     * セルに内包するコントロールを生成し値の表示を行うCallbackクラス.
     * @author hiro
     * @param <S> LiveTableViewのitem型
     * @param <T> Control型
     */
    public static abstract class ReadOnlyControlFactory<S, T> extends ControlFactory<S, T> {
        
        @Override @Deprecated
        public final void setValueForItem(S item, T control) {
        }
        
    }
    
    /**
     * セルに内包するコントロール生成を行うCallbackクラス.
     * @author hiro
     * @param <S> LiveTableViewのitem型
     * @param <T> Control型
     */
    public static abstract class FixControlFactory<S, T> extends ReadOnlyControlFactory<S, T> {

        @Override @Deprecated
        public final void setValueForControl(S item, T control) {
        }
        
    }
    
}
