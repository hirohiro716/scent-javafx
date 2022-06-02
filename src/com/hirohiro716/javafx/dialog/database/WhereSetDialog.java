package com.hirohiro716.javafx.dialog.database;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.hirohiro716.RegexHelper.RegexPattern;
import com.hirohiro716.StringConverter;
import com.hirohiro716.database.WhereSet;
import com.hirohiro716.database.WhereSet.Comparison;
import com.hirohiro716.database.WhereSet.Where;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.IMEHelper;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.PaneNodeFinder;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.HashMapComboBox;
import com.hirohiro716.javafx.control.LimitTextField;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.confirm.Confirm;
import com.hirohiro716.javafx.dialog.question.Question;
import com.hirohiro716.javafx.dialog.select.HashMapComboBoxDialog;
import com.hirohiro716.robot.InterfaceTypingRobotJapanese.IMEMode;
import com.hirohiro716.javafx.dialog.datetime.DatetimePickerDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * WhereSet配列を作成するダイアログを表示するクラス。
 *
 * @author hiro
 */
public class WhereSetDialog extends AbstractDialog<WhereSet[]> {
    
    /**
     * コンストラクタ。
     */
    public WhereSetDialog() {
        this.setTitle("検索条件の指定");
        this.setMessage("検索条件を指定してください。");
    }

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    @FXML
    private EnterFireButton buttonAddWhere;

    @FXML
    private EnterFireButton buttonAddWhereSet;

    @FXML
    private VBox vboxWhereSet;

    @FXML
    private VBox vboxWhereSetGroup;

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        WhereSetDialog dialog = this;
        // Paneの生成
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        // 検索カラム追加ボタン
        this.buttonAddWhere.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HashMapComboBoxDialog<String, String> comboBoxDialog = new HashMapComboBoxDialog<>();
                comboBoxDialog.setTitle("検索項目の追加");
                comboBoxDialog.setMessage("追加する検索項目を選択してください。");
                comboBoxDialog.setHashMap(dialog.searchableColumnDescriptions);
                Iterator<String> iterator = dialog.searchableColumnDescriptions.keySet().iterator();
                if (iterator.hasNext()) {
                    comboBoxDialog.setDefaultValue(iterator.next());
                }
                comboBoxDialog.setCloseEvent(new CloseEventHandler<String>() {
                    @Override
                    public void handle(String resultValue) {
                        if (dialog.searchableColumnDescriptions.containsKey(resultValue)) {
                            dialog.addWhere(resultValue);
                        }
                    }
                });
                comboBoxDialog.showOnPane(dialog.getContentPane());
            }
        });
        // 検索セット追加ボタン
        this.buttonAddWhereSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.updateWhereSetFromVBox();
                for (WhereSet whereSet: dialog.getWhereSetGroup()) {
                    if (whereSet.size() == 0) {
                        return;
                    }
                }
                Question.showOnPane("コピーの確認", StringConverter.join("現在の検索条件セットをコピーして条件セットを追加することができます。コピーしますか？",
                        StringConverter.LINE_SEPARATOR, "空の条件セットを追加する場合は「いいえ」を選択してください。"), dialog.getContentPane(), new CloseEventHandler<DialogResult>() {
                    @Override
                    public void handle(DialogResult resultValue) {
                        if (resultValue == DialogResult.YES) {
                            dialog.addWhereSet(true);
                        } else {
                            dialog.addWhereSet(false);
                        }
                    }
                });
            }
        });
        // 確定、キャンセルボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.updateWhereSetFromVBox();
                ArrayList<WhereSet> result = new ArrayList<>();
                for (WhereSet whereSet: dialog.getWhereSetGroup()) {
                    if (whereSet.size() > 0) {
                        result.add(whereSet);
                    }
                }
                dialog.setResult(result.toArray(new WhereSet[]{}));
                dialog.close();
            }
        });
        return fxmlLoader.getPaneRoot();
    }

    @Override
    public void breforeShowPrepare() {
        WhereSetDialog dialog = this;
        // 初期値のセット
        if (this.defaultValue == null || this.defaultValue.length == 0) {
            this.addWhereSet(false);
        } else {
            for (WhereSet whereSet: this.defaultValue) {
                this.getWhereSetGroup().add(whereSet);
                RadioButton radioButton = this.addWhereSetRadioButton(whereSet);
                if (this.getDisplayedWhereSet() == null) {
                    this.setDisplayWhereSet(whereSet);
                    this.updateVBoxFromWhereSet();
                    radioButton.setSelected(true);
                }
            }
        }
        // キャンセル可能かどうか
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.setResult(null);
                    dialog.close();
                }
            });
        } else {
            HBox hboxButton = (HBox) this.buttonCancel.getParent();
            hboxButton.getChildren().remove(this.buttonCancel);
        }
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return this.isCancelable;
    }
    
    /**
     * メッセージ内容をセットする。
     *
     * @param message
     */
    public void setMessage(String message) {
        this.paneMessage.getChildren().clear();
        Label label = new Label(message);
        label.setWrapText(true);
        this.paneMessage.getChildren().add(label);
        LayoutHelper.setAnchor(label, 0, 0, 0, 0);
    }

    /**
     * メッセージに代わるNodeをセットする。
     *
     * @param node
     */
    public void setMessageNode(Node node) {
        this.paneMessage.getChildren().clear();
        this.paneMessage.getChildren().add(node);
    }
    
    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する。初期値はtrue。
     *
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    /**
     * キャンセル可能かを取得する。
     *
     * @return キャンセル可能か
     */
    public boolean isCancelable() {
        return this.isCancelable;
    }

    private WhereSet[] defaultValue;

    /**
     * ダイアログに初期値を表示する。
     *
     * @param whereSets
     */
    public void setDefaultValue(WhereSet[] whereSets) {
        this.defaultValue = whereSets;
    }

    private ToggleGroup toggleGroup = new ToggleGroup();

    private ArrayList<WhereSet> whereSetGroup = new ArrayList<>();

    /**
     * すべてのWhereSetを取得する。
     *
     * @return すべてのWhereSet
     */
    private ArrayList<WhereSet> getWhereSetGroup() {
        return this.whereSetGroup;
    }

    private WhereSet displayedWhereSet;

    /**
     * 現在VBoxに表示されているWhereSetを取得する。
     *
     * @return WhereSet
     */
    private WhereSet getDisplayedWhereSet() {
        return this.displayedWhereSet;
    }
    
    /**
     * VBoxにWhereSetを表示する。
     *
     * @param whereSet
     */
    private void setDisplayWhereSet(WhereSet whereSet) {
        this.displayedWhereSet = whereSet;
    }

    /**
     * HBoxからカラム名を取得するための関連付け
     */
    private HashMap<HBox, String> hboxToColumnNames = new HashMap<>();

    // HBox内のコントロールにつけるID
    private static final String NAME = "name";
    private static final String COMPARISON = "comparison";
    private static final String VALUE = "value";
    private static final String VALUE_LABEL = "value_label";
    private static final String NOT = "not";

    /**
     * 新しいWhereSetを追加する。
     *
     * @param isCopy 
     */
    private void addWhereSet(boolean isCopy) {
        // 現在のWhereSetを保存して初期化
        this.updateWhereSetFromVBox();
        if (this.displayedWhereSet != null && this.displayedWhereSet.size() == 0) {
            return;
        }
        // コピーを作成する場合は削除しない
        if (isCopy == false) {
            this.removeVBoxRowAll();
        }
        // 新しくWhereSetを作成する
        WhereSet whereSet = new WhereSet();
        this.displayedWhereSet = whereSet;
        this.whereSetGroup.add(whereSet);
        // コピーを作成する場合はこの段階でVBoxから値を取り込む
        if (isCopy) {
            this.updateWhereSetFromVBox();
        }
        // ラジオボタンを作成する
        this.addWhereSetRadioButton(whereSet).setSelected(true);
    }

    private int whereSetId = 1;

    /**
     * WhereSetGroupに追加済みのWhereSetに対応したラジオボタンを作成する。
     *
     * @param whereSet
     * @return 作成したラジオボタン
     */
    private RadioButton addWhereSetRadioButton(WhereSet whereSet) {
        WhereSetDialog dialog = this;
        RadioButton radioButton = new RadioButton("条件セット" + (this.whereSetId++));
        this.toggleGroup.getToggles().add(radioButton);
        radioButton.setUserData(whereSet);
        radioButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                RadioButton radioButton = (RadioButton) event.getSource();
                if (event.getButton() == MouseButton.PRIMARY) {
                    // 現在のWhereSetを保存
                    dialog.updateWhereSetFromVBox();
                    // 新しいWhereSetをVBoxに表示
                    dialog.displayedWhereSet = (WhereSet) radioButton.getUserData();
                    dialog.updateVBoxFromWhereSet();
                } else {
                    if (dialog.whereSetGroup.size() == 1) {
                        return;
                    }
                    Confirm.showOnPane("削除の確認", "この検索条件セットを削除します。", dialog.getContentPane(), new CloseEventHandler<DialogResult>() {
                        @Override
                        public void handle(DialogResult resultValue) {
                            if (resultValue == DialogResult.OK) {
                                dialog.whereSetGroup.remove(radioButton.getUserData());
                                dialog.vboxWhereSetGroup.getChildren().remove(radioButton);
                                for (Node node: dialog.vboxWhereSetGroup.getChildren()) {
                                    RadioButton tempButton = (RadioButton) node;
                                    // 新しいWhereSetをVBoxに表示
                                    dialog.displayedWhereSet = (WhereSet) tempButton.getUserData();
                                    tempButton.setSelected(true);
                                    dialog.updateVBoxFromWhereSet();
                                    break;
                                }
                            }
                        }
                    });
                }
            }
        });
        this.vboxWhereSetGroup.getChildren().add(radioButton);
        return radioButton;
    }

    /**
     * VBoxの内容を取得してWhereSetを再構成する。
     */
    private void updateWhereSetFromVBox() {
        if (this.displayedWhereSet == null) {
            return;
        }
        this.displayedWhereSet.clear();
        for (Node node: this.vboxWhereSet.getChildren()) {
            HBox hbox = (HBox) node;
            // カラム名
            String columnName = this.hboxToColumnNames.get(hbox);
            // カラムタイプ
            ColumnType columnType = this.searchableColumnTypes.get(columnName);
            // 比較タイプ
            HashMapComboBox<?, ?> comparisonComboBox = (HashMapComboBox<?, ?>) hbox.lookup("#" + COMPARISON);
            Comparison comparison = (Comparison) comparisonComboBox.getKey();
            // 逆転
            CheckBox isNotCheckBox = (CheckBox) hbox.lookup("#" + NOT);
            boolean isNot;
            if (isNotCheckBox != null) {
                isNot = isNotCheckBox.isSelected();
            } else {
                isNot = false;
            }
            // 値
            Object value1;
            Object value2;
            Control[] nodes = hbox.lookupAll("#" + VALUE).toArray(new Control[]{});
            if (nodes.length > 0) {
                switch (comparison) {
                case BETWEEN:
                    value1 = getRowValue(nodes[0], columnType);
                    value2 = getRowValue(nodes[1], columnType);
                    if (value1 != null && value2 != null) {
                        this.displayedWhereSet.addBetween(columnName, isNot, value1, value2);
                    }
                    break;
                case LIKE:
                    value1 = getRowValue(nodes[0], columnType);
                    if (value1 != null) {
                        this.displayedWhereSet.add(columnName, comparison, isNot, StringConverter.join("%", value1, "%"));
                    }
                    break;
                case EQUAL:
                default:
                    value1 = getRowValue(nodes[0], columnType);
                    if (value1 != null) {
                        this.displayedWhereSet.add(columnName, comparison, isNot, value1);
                    }
                    break;
                }
            }
        }
    }

    /**
     * WhereSetの内容をVBoxに表示する。
     */
    private void updateVBoxFromWhereSet() {
        if (this.displayedWhereSet == null) {
            return;
        }
        this.removeVBoxRowAll();
        for (Where where: this.displayedWhereSet.getWheres()) {
            if (this.searchableColumnDescriptions.get(where.getColumn()) == null) {
                break;
            }
            // カラムタイプを取得
            ColumnType columnType = this.searchableColumnTypes.get(where.getColumn());
            // 検索カラムを追加
            HBox hbox = this.addWhere(where.getColumn());
            // Helper使う
            PaneNodeFinder finder = new PaneNodeFinder(hbox);
            // 検索方法を入力
            HashMapComboBox<Comparison, String> comparisonComboBox = finder.findHashMapComboBox("#" + COMPARISON);
            comparisonComboBox.setKey(where.getComparison());
            if (hbox.lookupAll("#" + VALUE).size() == 0) {
                // なぜか最初の方はOnActionが動かない
                Control[] controls = this.createValueControl(where.getColumn(), where.getComparison());
                hbox.getChildren().addAll(controls);
            }
            // 値を入力
            Control[] nodes = hbox.lookupAll("#" + VALUE).toArray(new Control[]{});
            switch (where.getComparison()) {
            case BETWEEN:
                setRowValue(nodes[0], where.getValue(), columnType);
                setRowValue(nodes[1], where.getValue2(), columnType);
                break;
            case LIKE:
                String stringValue = (String) where.getValue();
                setRowValue(nodes[0], stringValue.substring(1, stringValue.length() - 1), columnType);
                break;
            case EQUAL:
            default:
                setRowValue(nodes[0], where.getValue(), columnType);
                break;
            }
            // 逆転を入力
            CheckBox isNotCheckBox = finder.findCheckBox("#" + NOT);
            if (isNotCheckBox != null) {
                isNotCheckBox.setSelected(where.isNot());
            }
        }
    }

    /**
     * 検索値から値を取得する。(コントロール毎の値の取得方法の違いをカバーする)
     * @param control
     * @param columnType
     * @return 検索値
     */
    private static Object getRowValue(Control control, ColumnType columnType) {
        // テキストフィールドの場合はかなり特殊
        if (control instanceof TextField) {
            TextField textField = (TextField) control;
            switch (columnType) {
            case STRING:
            case NUMBER_STRING:
            case DATE_STRING:
            case DATETIME_STRING:
                return textField.getText(); // 文字列はそのまま
            case NUMBER:
                return StringConverter.stringToDouble(textField.getText()); // 数値は変換
            case DATE:
                return Datetime.stringToDate(textField.getText()); // 日時は変換
            case DATETIME:
                return new Timestamp(Datetime.stringToDate(textField.getText()).getTime()); // 日時は変換
            case BOOLEAN:
                break;
            }
        }
        if (control instanceof HashMapComboBox<?, ?>) {
            HashMapComboBox<?, ?> hashMapComboBox = (HashMapComboBox<?, ?>) control;
            return hashMapComboBox.getKey();
        }
        if (control instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) control;
            return checkBox.isSelected();
        }
        return null;
    }

    /**
     * 値を検索コントロールにセットする。(コントロール毎の値のセット方法の違いをカバーする)
     * @param control
     * @param value
     * @param columnType
     */
    private static void setRowValue(Control control, Object value, ColumnType columnType) {
        // テキストフィールドの場合はかなり特殊
        if (control instanceof TextField) {
            TextField textField = (TextField) control;
            switch (columnType) {
            case STRING:
            case NUMBER_STRING:
            case DATE_STRING:
            case DATETIME_STRING:
                textField.setText((String) value);
                break;
            case NUMBER:
                textField.setText(StringConverter.tryNonFraction(String.valueOf(value))); // 数値は丸めて文字列へ
                break;
            case DATE:
                textField.setText(Datetime.dateToString((Date) value, "yyyy-MM-dd"));
                break;
            case DATETIME:
                textField.setText(Datetime.dateToString((Date) value, "yyyy-MM-dd HH:mm:ss"));
                break;
            case BOOLEAN:
            }
        }
        if (control instanceof HashMapComboBox<?, ?>) {
            @SuppressWarnings("unchecked")
            HashMapComboBox<Object, ?> hashMapComboBox = (HashMapComboBox<Object, ?>) control;
            hashMapComboBox.setKey(value);
        }
        if (control instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) control;
            checkBox.setSelected((boolean) value);
        }
    }

    /**
     * WhereSetVBoxを空にする。
     */
    private void removeVBoxRowAll() {
        this.vboxWhereSet.getChildren().clear();
        this.hboxToColumnNames.clear();
    }

    // ダイアログ作成時にユーザーから指定された検索に使えるカラムを保存
    private LinkedHashMap<String, String> searchableColumnDescriptions = new LinkedHashMap<>();
    private LinkedHashMap<String, ColumnType> searchableColumnTypes = new LinkedHashMap<>();
    private LinkedHashMap<String, HashMap<?, ?>> searchableColumnHashMaps = new LinkedHashMap<>();
    private LinkedHashMap<String, StringConverter> searchableColumnStringConverters = new LinkedHashMap<>();

    /**
     * 検索できるカラムを追加する。
     *
     * @param columnName カラム名
     * @param description カラム説明
     * @param columnType 種類
     */
    public void addSearchableColumn(String columnName, String description, ColumnType columnType) {
        this.searchableColumnDescriptions.put(columnName, description);
        this.searchableColumnTypes.put(columnName, columnType);
    }

    /**
     * 検索できるカラムを追加する。
     *
     * @param columnName カラム名
     * @param description カラム説明
     * @param columnType 種類
     * @param stringConverter 文字列のコンバーター
     */
    public void addSearchableColumn(String columnName, String description, ColumnType columnType, StringConverter stringConverter) {
        this.searchableColumnDescriptions.put(columnName, description);
        this.searchableColumnTypes.put(columnName, columnType);
        this.searchableColumnStringConverters.put(columnName, stringConverter);
    }

    /**
     * 検索できる選択可能なカラムを追加する。
     *
     * @param columnName カラム名
     * @param description カラム説明
     * @param columnType 種類
     * @param hashMap 連想配列
     */
    public void addSearchableColumn(String columnName, String description, ColumnType columnType, HashMap<?, ?> hashMap) {
        this.searchableColumnDescriptions.put(columnName, description);
        this.searchableColumnTypes.put(columnName, columnType);
        this.searchableColumnHashMaps.put(columnName, hashMap);
    }

    private LinkedHashMap<String, ColumnType> userComparisonColumnTypes = new LinkedHashMap<>();
    private LinkedHashMap<String, Comparison> userComparisons = new LinkedHashMap<>();

    /**
     * 文字列の検索方法リストに新しい比較演算子を追加する。
     *
     * @param columnType 対象カラムタイプ
     * @param comparison 比較演算子
     * @param description 説明
     */
    public void addStringComparison(ColumnType columnType, Comparison comparison, String description) {
        switch (columnType) {
        case STRING:
        case NUMBER_STRING:
            this.userComparisonColumnTypes.put(description, columnType);
            this.userComparisons.put(description, comparison);
            break;
        default:
            break;
        }
    }

    /**
     * カラムタイプに応じた検索方法リストを取得する。
     *
     * @param columnType
     * @return 選択可能な検索方法リスト
     */
    private LinkedHashMap<Comparison, String> getComparisonHashMap(ColumnType columnType) {
        LinkedHashMap<Comparison, String> hashMap = new LinkedHashMap<>();
        switch (columnType) {
        case STRING:
            hashMap.put(Comparison.EQUAL, "検索値と等しい");
            hashMap.put(Comparison.LIKE, "検索値を含む");
            break;
        case NUMBER_STRING:
            hashMap.put(Comparison.EQUAL, "検索値と等しい");
            hashMap.put(Comparison.BETWEEN, "検索値１～検索値２の間");
            hashMap.put(Comparison.LIKE, "検索値を含む");
            break;
        case NUMBER:
            hashMap.put(Comparison.EQUAL, "検索値と等しい");
            hashMap.put(Comparison.BETWEEN, "検索値１～検索値２の間");
            break;
        case DATE:
        case DATE_STRING:
            hashMap.put(Comparison.EQUAL, "検索値と等しい");
            hashMap.put(Comparison.BETWEEN, "検索値１～検索値２の間");
            break;
        case DATETIME:
        case DATETIME_STRING:
            hashMap.put(Comparison.BETWEEN, "検索値１～検索値２の間");
            break;
        case BOOLEAN:
            hashMap.put(Comparison.EQUAL, "検索値と等しい");
            break;
        }
        // ユーザー定義比較演算子を追加する
        Iterator<String> iterator = this.userComparisons.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (columnType == this.userComparisonColumnTypes.get(key)) {
                hashMap.put(this.userComparisons.get(key), key);
            }
        }
        return hashMap;
    }

    /**
     * カラム名を指定して検索値入力行を追加する。
     *
     * @param columnName
     * @return HBox
     */
    private HBox addWhere(String columnName) {
        WhereSetDialog dialog = this;
        // 検索行を作成していく
        HBox hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER_LEFT);
        // 削除ボタンを配置
        EnterFireButton button = new EnterFireButton("削除");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnterFireButton button = (EnterFireButton) event.getSource();
                HBox hbox = (HBox) button.getParent();
                dialog.vboxWhereSet.getChildren().remove(hbox);
            }
        });
        hbox.getChildren().add(button);
        // 検索カラムの説明ラベルを配置
        String descriptionString = this.searchableColumnDescriptions.get(columnName);
        Label descriptionLabel = new Label(descriptionString);
        descriptionLabel.setId(NAME);
        hbox.getChildren().add(descriptionLabel);
        // 検索方法コンボボックスを配置
        ColumnType columnType = this.searchableColumnTypes.get(columnName);
        HashMap<Comparison, String> comparisonItems  = this.getComparisonHashMap(columnType);
        HashMapComboBox<Comparison, String> comparisonComboBox = new HashMapComboBox<>(comparisonItems);
        comparisonComboBox.setId(COMPARISON);
        comparisonComboBox.valueProperty().addListener(new ComparisonComboBoxChangeListener(columnName, comparisonComboBox));
        hbox.getChildren().add(comparisonComboBox);
        // 検索条件VBoxに追加
        this.vboxWhereSet.getChildren().add(hbox);
        this.hboxToColumnNames.put(hbox, columnName);
        return hbox;
    }

    /**
     * 比較演算子のChangeListener。
     *
     * @author hiro
     */
    private class ComparisonComboBoxChangeListener implements ChangeListener<String> {

        private String columnName;
        private HashMapComboBox<?, ?> hashMapComboBox;

        /**
         * コンストラクタ。
         *
         * @param columnName
         * @param hashMapComboBox
         */
        public ComparisonComboBoxChangeListener(String columnName, HashMapComboBox<?, ?> hashMapComboBox) {
            this.columnName = columnName;
            this.hashMapComboBox = hashMapComboBox;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            WhereSetDialog dialog = WhereSetDialog.this;
            // インスタンス取得
            HashMapComboBox<?, ?> comparisonComboBox = this.hashMapComboBox;
            HBox hbox = (HBox) comparisonComboBox.getParent();
            if (comparisonComboBox.getKey() == null) {
                return;
            }
            // 検索値用コントロール作成
            hbox.getChildren().removeAll(hbox.lookupAll("#" + VALUE));
            hbox.getChildren().removeAll(hbox.lookupAll("#" + VALUE_LABEL));
            hbox.getChildren().removeAll(hbox.lookupAll("#" + NOT));
            Comparison comparison = (Comparison) comparisonComboBox.getKey();
            Control[] controls = dialog.createValueControl(this.columnName, comparison);
            hbox.getChildren().addAll(controls);
        }
    }

    /**
     * 日時検索値の入力ダイアログを表示して日時を入力させる。
     */
    private class DatetimeInputEventHander implements EventHandler<MouseEvent> {

        private boolean isTimeInput;
        
        private StringConverter stringConverter;

        /**
         * コンストラクタ。
         *
         * @param isTimeInput 時刻を入力させるかどうか
         * @param stringConverter Dateを文字列に変換するStringConverter
         */
        public DatetimeInputEventHander(boolean isTimeInput, StringConverter stringConverter) {
                this.isTimeInput = isTimeInput;
                this.stringConverter = stringConverter;
        }

        @Override
        public void handle(MouseEvent event) {
            DatetimeInputEventHander handler = this;
            WhereSetDialog dialog = WhereSetDialog.this;
            if (event.getButton() == MouseButton.PRIMARY) {
                TextField field = (TextField) event.getSource();
                DatetimePickerDialog datetimePickerDialog = new DatetimePickerDialog();
                datetimePickerDialog.setTimeInput(this.isTimeInput);
                datetimePickerDialog.setTitle("検索値の入力");
                if (this.isTimeInput) {
                    datetimePickerDialog.setMessage("検索に使用する日時を入力してください。");
                } else {
                    datetimePickerDialog.setMessage("検索に使用する日付を入力してください。");
                }
                String text = StringConverter.nullReplace(field.getText(), "");
                datetimePickerDialog.setDefaultValue(Datetime.stringToDate(text));
                datetimePickerDialog.setCloseEvent(new CloseEventHandler<Date>() {
                    @Override
                    public void handle(Date resultValue) {
                        if (resultValue != null) {
                            if (handler.stringConverter == null) {
                                if (handler.isTimeInput) {
                                    field.setText(Datetime.dateToString(resultValue));
                                } else {
                                    field.setText(Datetime.dateToString(resultValue, "yyyy-MM-dd"));
                                }
                            } else {
                                field.setText(handler.stringConverter.execute(resultValue));
                            }
                        }
                    }
                });
                datetimePickerDialog.showOnPane(dialog.getContentPane());
            }
        }
    }

    /**
     * カラム名と検索タイプから検索値入力用のコントロールを生成する。
     *
     * @param columnName カラム名
     * @param comparison 検索タイプ
     * @return 作成されたコントロール配列
     */
    private Control[] createValueControl(String columnName, Comparison comparison) {
        // 必要な値を取得
        ColumnType columnType = this.searchableColumnTypes.get(columnName);
        HashMap<?, ?> hashMap = this.searchableColumnHashMaps.get(columnName);
        StringConverter stringConverter = this.searchableColumnStringConverters.get(columnName);
        // コントロール配列を作成
        ArrayList<Control> controls = new ArrayList<>();
        LimitTextField limitTextField;
        HashMapComboBox<?, ?> hashMapComboBox;
        CheckBox checkBox;
        if (hashMap == null) {
            switch (columnType) {
            case STRING:
                switch (comparison) {
                case BETWEEN:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setStringConverter(stringConverter);
                    IMEHelper.apply(limitTextField, IMEMode.HIRAGANA);
                    controls.add(limitTextField);
                    controls.add(new Label("～"));
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setStringConverter(stringConverter);
                    IMEHelper.apply(limitTextField, IMEMode.HIRAGANA);
                    controls.add(limitTextField);
                    break;
                case EQUAL:
                case LIKE:
                default:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setStringConverter(stringConverter);
                    IMEHelper.apply(limitTextField, IMEMode.HIRAGANA);
                    controls.add(limitTextField);
                    break;
                }
                break;
            case NUMBER_STRING:
                switch (comparison) {
                case EQUAL:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setStringConverter(stringConverter);
                    limitTextField.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
                    IMEHelper.apply(limitTextField, IMEMode.OFF);
                    controls.add(limitTextField);
                    break;
                case BETWEEN:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setStringConverter(stringConverter);
                    limitTextField.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
                    IMEHelper.apply(limitTextField, IMEMode.OFF);
                    controls.add(limitTextField);
                    controls.add(new Label("～"));
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setStringConverter(stringConverter);
                    limitTextField.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
                    IMEHelper.apply(limitTextField, IMEMode.OFF);
                    controls.add(limitTextField);
                    break;
                case LIKE:
                default:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setStringConverter(stringConverter);
                    IMEHelper.apply(limitTextField, IMEMode.OFF);
                    controls.add(limitTextField);
                    break;
                }
                break;
            case NUMBER:
                switch (comparison) {
                case EQUAL:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(75);
                    limitTextField.setStringConverter(stringConverter);
                    limitTextField.addPermitRegex(RegexPattern.DECIMAL_NEGATIVE.getPattern(), false);
                    IMEHelper.apply(limitTextField, IMEMode.OFF);
                    controls.add(limitTextField);
                    break;
                case BETWEEN:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(75);
                    limitTextField.setStringConverter(stringConverter);
                    limitTextField.addPermitRegex(RegexPattern.DECIMAL_NEGATIVE.getPattern(), false);
                    IMEHelper.apply(limitTextField, IMEMode.OFF);
                    controls.add(limitTextField);
                    controls.add(new Label("～"));
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(75);
                    limitTextField.setStringConverter(stringConverter);
                    limitTextField.addPermitRegex(RegexPattern.DECIMAL_NEGATIVE.getPattern(), false);
                    IMEHelper.apply(limitTextField, IMEMode.OFF);
                    controls.add(limitTextField);
                    break;
                default:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setStringConverter(stringConverter);
                    IMEHelper.apply(limitTextField, IMEMode.OFF);
                    controls.add(limitTextField);
                    break;
                }
                break;
            case DATE:
            case DATE_STRING:
                switch (comparison) {
                case EQUAL:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setEditable(false);
                    limitTextField.setOnMouseClicked(new DatetimeInputEventHander(false, stringConverter));
                    controls.add(limitTextField);
                    break;
                case BETWEEN:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setEditable(false);
                    limitTextField.setOnMouseClicked(new DatetimeInputEventHander(false, stringConverter));
                    controls.add(limitTextField);
                    controls.add(new Label("～"));
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(100);
                    limitTextField.setEditable(false);
                    limitTextField.setOnMouseClicked(new DatetimeInputEventHander(false, stringConverter));
                    controls.add(limitTextField);
                    break;
                default:
                    break;
                }
                break;
            case DATETIME:
            case DATETIME_STRING:
                switch (comparison) {
                case EQUAL:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(160);
                    limitTextField.setEditable(false);
                    limitTextField.setOnMouseClicked(new DatetimeInputEventHander(true, stringConverter));
                    controls.add(limitTextField);
                    break;
                case BETWEEN:
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(160);
                    limitTextField.setEditable(false);
                    limitTextField.setOnMouseClicked(new DatetimeInputEventHander(true, stringConverter));
                    controls.add(limitTextField);
                    controls.add(new Label("～"));
                    limitTextField = new LimitTextField();
                    limitTextField.setPrefWidth(160);
                    limitTextField.setEditable(false);
                    limitTextField.setOnMouseClicked(new DatetimeInputEventHander(true, stringConverter));
                    controls.add(limitTextField);
                    break;
                default:
                    break;
                }
                break;
            case BOOLEAN:
                switch (comparison) {
                case EQUAL:
                    checkBox = new CheckBox();
                    controls.add(checkBox);
                    break;
                default:
                    break;
                }
                break;
            }
        } else {
            switch (comparison) {
            case EQUAL:
            case LIKE:
                hashMapComboBox = new HashMapComboBox<>(hashMap);
                controls.add(hashMapComboBox);
                break;
            case BETWEEN:
                hashMapComboBox = new HashMapComboBox<>(hashMap);
                controls.add(hashMapComboBox);
                controls.add(new Label("～"));
                hashMapComboBox = new HashMapComboBox<>(hashMap);
                controls.add(hashMapComboBox);
                break;
            default:
                break;
            }
        }
        // すべてのコントロールにVALUEをつける
        for (Control control: controls) {
            if (control instanceof Label) {
                control.setId(VALUE_LABEL);
            } else {
                control.setId(VALUE);
            }
        }
        // 逆転チェックをつける
        if (columnType != ColumnType.BOOLEAN) {
            checkBox = new CheckBox("逆転");
            checkBox.setId(NOT);
            controls.add(checkBox);
        }
        return controls.toArray(new Control[]{});
    }
    
    /**
     * カラムのデータ型。
     *
     * @author hiro
     */
    public enum ColumnType {
        /**
         * 文字列
         */
        STRING,
        /**
         * 文字列(数字だけ)
         */
        NUMBER_STRING,
        /**
         * 数値
         */
        NUMBER,
        /**
         * 日付
         */
        DATE,
        /**
         * 日付(文字列)
         */
        DATE_STRING,
        /**
         * 日付と時刻
         */
        DATETIME,
        /**
         * 日付と時刻(文字列)
         */
        DATETIME_STRING,
        /**
         * 真偽
         */
        BOOLEAN,
        ;
    }}
