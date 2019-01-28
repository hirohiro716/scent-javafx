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
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.database.WhereSetDialog.ColumnType;
import com.hirohiro716.javafx.dialog.question.QuestionPane;
import com.hirohiro716.javafx.dialog.select.HashMapComboBoxPaneDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * WhereSet配列を作成するダイアログを表示するクラス.
 * @author hiro
 */
public class WhereSetPaneDialog extends AbstractPaneDialog<WhereSet[]> {

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
    private WhereSetDialogCore core = new WhereSetDialogCore();

    /**
     * 検索できるカラムを追加する.
     * @param columnName カラム名
     * @param description カラム説明
     * @param columnType 種類
     */
    public void addSearchableColumn(String columnName, String description, ColumnType columnType) {
        this.core.addSearchableColumn(columnName, description, columnType);
    }

    /**
     * 検索できる選択可能なカラムを追加する.
     * @param columnName カラム名
     * @param description カラム説明
     * @param columnType 種類
     * @param hashMap 連想配列
     */
    public void addSearchableColumn(String columnName, String description, ColumnType columnType, HashMap<?, ?> hashMap) {
        this.core.addSearchableColumn(columnName, description, columnType, hashMap);
    }

    /**
     * 文字列の検索方法リストに新しい比較演算子を追加する.
     * @param columnType 対象カラムタイプ
     * @param comparison 比較演算子
     * @param description 説明
     */
    public void addStringComparison(ColumnType columnType, Comparison comparison, String description) {
        this.core.addStringComparison(columnType, comparison, description);
    }

    /**
     * コンストラクタ.
     * @param parentPane
     */
    public WhereSetPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public void show() {
        // ダイアログ表示
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WhereSetDialog.class.getResource(WhereSetDialog.class.getSimpleName() + ".fxml"), this);
            this.show(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        // タイトルのセット
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
        this.core.setDialogPane(this.getDialogPane());
        this.core.setVBoxWhereSet(this.vboxWhereSet);
        this.core.setVBoxWhereSetGroup(this.vboxWhereSetGroup);
        // 初期値のセット
        if (this.defaultValue == null) {
            this.core.addWhereSet(false);
        } else {
            for (WhereSet whereSet: this.defaultValue) {
                this.core.whereSetGroup.add(whereSet);
                RadioButton radioButton = this.core.addWhereSetRadioButton(whereSet);
                if (this.core.focusWhereSet == null) {
                    this.core.focusWhereSet = whereSet;
                    this.core.updateVBoxFromWhereSet();
                    radioButton.setSelected(true);
                }
            }
        }
        // 検索カラム追加ボタン
        this.buttonAddWhere.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WhereSetPaneDialog whereSetDialog = WhereSetPaneDialog.this;
                HashMapComboBoxPaneDialog<String, String> dialog = new HashMapComboBoxPaneDialog<>(whereSetDialog.getDialogPane());
                dialog.setTitle("検索項目の追加");
                dialog.setMessage("追加する検索項目を選択してください。");
                dialog.setHashMap(whereSetDialog.core.searchTableRowsableColumnDescriptions);
                Iterator<String> iterator = whereSetDialog.core.searchTableRowsableColumnDescriptions.keySet().iterator();
                if (iterator.hasNext()) {
                    dialog.setDefaultValue(iterator.next());
                }
                dialog.setCloseEvent(new CloseEventHandler<String>() {
                    @Override
                    public void handle(String resultValue) {
                        if (whereSetDialog.core.searchTableRowsableColumnDescriptions.containsKey(resultValue)) {
                            whereSetDialog.core.addWhere(resultValue);
                        }
                    }
                });
                dialog.show();
            }
        });
        // 検索セット追加ボタン
        WhereSetPaneDialog dialog = WhereSetPaneDialog.this;
        this.buttonAddWhereSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.core.updateWhereSetFromVBox();
                for (WhereSet whereSet: dialog.core.whereSetGroup) {
                    if (whereSet.size() == 0) {
                        return;
                    }
                }
                QuestionPane.show("コピーの確認", StringConverter.join("現在の検索条件セットをコピーして条件セットを追加することができます。コピーしますか？",
                        StringConverter.LINE_SEPARATOR, "空の条件セットを追加する場合は「いいえ」を選択してください。"), dialog.getDialogPane(), new CloseEventHandler<DialogResult>() {
                    @Override
                    public void handle(DialogResult resultValue) {
                        if (resultValue == DialogResult.YES) {
                            dialog.core.addWhereSet(true);
                        } else {
                            dialog.core.addWhereSet(false);
                        }
                    }
                });
            }
        });
        // 確定、キャンセルボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.core.updateWhereSetFromVBox();
                ArrayList<WhereSet> result = new ArrayList<>();
                for (WhereSet whereSet: dialog.core.whereSetGroup) {
                    if (whereSet.size() > 0) {
                        result.add(whereSet);
                    }
                }
                dialog.setResult(result.toArray(new WhereSet[]{}));
                dialog.close();
            }
        });
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.setResult(null);
                    dialog.close();
                }
            });
        } else {
            this.buttonCancel.setVisible(false);
            LayoutHelper.setAnchor(this.buttonOk, null, 20d, 20d, null);
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

}
