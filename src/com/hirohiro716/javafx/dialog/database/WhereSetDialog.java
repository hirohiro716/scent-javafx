package com.hirohiro716.javafx.dialog.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.hirohiro716.StringConverter;
import com.hirohiro716.database.WhereSet;
import com.hirohiro716.database.WhereSet.Comparison;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.question.QuestionPane;
import com.hirohiro716.javafx.dialog.select.HashMapComboBoxPaneDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * WhereSet配列を作成するダイアログを表示するクラス.
 * @author hiro
 */
public class WhereSetDialog extends AbstractDialog<WhereSet[]> {

    /**
     * カラムのデータ型
     * @author hiro
     */
    public enum ColumnType {
        /**
         * 文字列
         */
        STRING,
        /**
         * 文字列（数字だけ）
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
         * 日付と時刻
         */
        DATETIME,
        /**
         * 真偽
         */
        BOOLEAN,
        ;
    }

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
    private VBox vboxWhereSetGroup;

    @FXML
    private VBox vboxWhereSet;

    /**
     * 共通の処理が膨大なので別インスタンスへ
     */
    private WhereSetDialogCore common = new WhereSetDialogCore();

    /**
     * 検索できるカラムを追加する.
     * @param columnName カラム名
     * @param description カラム説明
     * @param columnType 種類
     */
    public void addSearchableColumn(String columnName, String description, ColumnType columnType) {
        this.common.addSearchableColumn(columnName, description, columnType);
    }

    /**
     * 検索できる選択可能なカラムを追加する.
     * @param columnName カラム名
     * @param description カラム説明
     * @param columnType 種類
     * @param hashMap 連想配列
     */
    public void addSearchableColumn(String columnName, String description, ColumnType columnType, HashMap<?, ?> hashMap) {
        this.common.addSearchableColumn(columnName, description, columnType, hashMap);
    }

    /**
     * 文字列の検索方法リストに新しい比較演算子を追加する.
     * @param columnType 対象カラムタイプ
     * @param comparison 比較演算子
     * @param description 説明
     */
    public void addStringComparison(ColumnType columnType, Comparison comparison, String description) {
        this.common.addStringComparison(columnType, comparison, description);
    }

    /**
     * コンストラクタ
     * @param parentStage
     */
    public WhereSetDialog(Stage parentStage) {
        super(parentStage);
    }

    @Override
    protected void preparationCallback() {
        // タイトルのセット
        this.getStage().setTitle(this.title);
        this.labelTitle.setText(this.title);
        // メッセージのセット
        if (this.message != null) {
            Label label = new Label(this.message);
            label.setWrapText(true);
            this.paneMessage.getChildren().add(label);
            LayoutHelper.setAnchor(label, 0, 0, 0, 0);
        }
        // メッセージNodeのセット
        if (this.messageNode != null) {
            this.paneMessage.getChildren().add(this.messageNode);
        }
        // Commonにコントロールを渡す
        this.common.setDialogPane(this.getDialogPane());
        this.common.setVBoxWhereSet(this.vboxWhereSet);
        this.common.setVBoxWhereSetGroup(this.vboxWhereSetGroup);
        // 初期値のセット
        if (this.defaultValue == null) {
            this.common.addWhereSet(false);
        } else {
            for (WhereSet whereSet: this.defaultValue) {
                this.common.whereSetGroup.add(whereSet);
                RadioButton radioButton = this.common.addWhereSetRadioButton(whereSet);
                if (this.common.focusWhereSet == null) {
                    this.common.focusWhereSet = whereSet;
                    this.common.updateVBoxFromWhereSet();
                    radioButton.setSelected(true);
                }
            }
        }
        // 検索カラム追加ボタン
        this.buttonAddWhere.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WhereSetDialog whereSetDialog = WhereSetDialog.this;
                HashMapComboBoxPaneDialog<String> dialog = new HashMapComboBoxPaneDialog<>(whereSetDialog.getDialogPane());
                dialog.setTitle("検索項目の追加");
                dialog.setMessage("追加する検索項目を選択してください。");
                dialog.setHashMap(whereSetDialog.common.searchTableRowsableColumnDescriptions);
                Iterator<String> iterator = whereSetDialog.common.searchTableRowsableColumnDescriptions.keySet().iterator();
                if (iterator.hasNext()) {
                    dialog.setDefaultValue(iterator.next());
                }
                dialog.setCloseEvent(new CloseEventHandler<String>() {
                    @Override
                    public void handle(String resultValue) {
                        if (whereSetDialog.common.searchTableRowsableColumnDescriptions.containsKey(resultValue)) {
                            whereSetDialog.common.addWhere(resultValue);
                        }
                    }
                });
                dialog.show();
            }
        });
        // 検索セット追加ボタン
        this.buttonAddWhereSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WhereSetDialog dialog = WhereSetDialog.this;
                dialog.common.updateWhereSetFromVBox();
                for (WhereSet whereSet: dialog.common.whereSetGroup) {
                    if (whereSet.size() == 0) {
                        return;
                    }
                }
                QuestionPane.show("コピーの確認", StringConverter.join("現在の検索条件セットをコピーして条件セットを追加することができます。コピーしますか？",
                        StringConverter.LINE_SEPARATOR, "空の条件セットを追加する場合は「いいえ」を選択してください。"), dialog.getDialogPane(), new CloseEventHandler<DialogResult>() {
                    @Override
                    public void handle(DialogResult resultValue) {
                        if (resultValue == DialogResult.YES) {
                            dialog.common.addWhereSet(true);
                        } else {
                            dialog.common.addWhereSet(false);
                        }
                    }
                });
            }
        });
        // 確定、キャンセルボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WhereSetDialog dialog = WhereSetDialog.this;
                dialog.common.updateWhereSetFromVBox();
                ArrayList<WhereSet> result = new ArrayList<>();
                for (WhereSet whereSet: dialog.common.whereSetGroup) {
                    if (whereSet.size() > 0) {
                        result.add(whereSet);
                    }
                }
                dialog.setResult(result.toArray(new WhereSet[]{}));
                dialog.close();
                event.consume();
            }
        });
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    WhereSetDialog.this.setResult(null);
                    WhereSetDialog.this.close();
                    event.consume();
                }
            });
        } else {
            this.buttonCancel.setVisible(false);
            LayoutHelper.setAnchor(this.buttonOk, null, 20d, 20d, null);
        }
    }

    @Override
    public void show() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("WhereSetDialog.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
        }
    }

    @Override
    public WhereSet[] showAndWait() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("WhereSetDialog.fxml"), this);
            return this.showAndWait(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
            return null;
        }
    }

    private String title = "検索条件の指定";

    /**
     * タイトルをセットする.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    private String message = "検索条件を指定してください。";

    /**
     * メッセージ内容をセットする.
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    private Node messageNode;

    /**
     * メッセージに代わるNodeをセットする.
     * @param node
     */
    public void setMessageNode(Node node) {
        this.messageNode = node;
    }

    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する.
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    /**
     * キャンセル可能かを取得する.
     * @return キャンセル可能か
     */
    public boolean isCancelable() {
        return this.isCancelable;
    }

    private WhereSet[] defaultValue;

    /**
     * ダイアログに初期値を表示する.
     * @param whereSets
     */
    public void setDefaultValue(WhereSet[] whereSets) {
        this.defaultValue = whereSets;
    }

    @Override @Deprecated
    public void setWidth(double width) {
    }

    @Override @Deprecated
    public void setHeight(double height) {
    }

}
