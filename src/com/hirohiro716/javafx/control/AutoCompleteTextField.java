package com.hirohiro716.javafx.control;

import com.hirohiro716.StringConverter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Screen;

/**
 * 自動補完機能付きのLimitTextField.
 * @author hiro
 */
public class AutoCompleteTextField extends LimitTextField {

    /**
     * コンストラクタ.
     */
    public AutoCompleteTextField() {
        this("", FXCollections.observableArrayList());
    }

    /**
     * コンストラクタ.
     * @param text
     */
    public AutoCompleteTextField(String text) {
        this(text, FXCollections.observableArrayList());
    }

    /**
     * コンストラクタ.
     * @param items 自動補完候補リスト
     */
    public AutoCompleteTextField(ObservableList<String> items) {
        this("", items);
    }

    /**
     * コンストラクタ.
     * @param text
     * @param items 自動補完候補リスト
     */
    public AutoCompleteTextField(String text, ObservableList<String> items) {
        super(text);
        this.items = items;
        // フォーカスされたらリスト表示して外れたら非表示に
        this.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                AutoCompleteTextField field = AutoCompleteTextField.this;
                if (newValue) {
                    field.createOnlyFirstItems();
                    field.itemAddService.cancel();
                    field.itemAddService.restart();
                } else {
                    field.hideItems();
                }
            }
        });
        // 入力値が変更されたらフィルター
        this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                AutoCompleteTextField field = AutoCompleteTextField.this;
                if (newValue == null || oldValue == null) {
                    return;
                }
                field.createOnlyFirstItems();
                field.itemAddService.cancel();
                field.itemAddService.restart();
            }
        });
        // TextFieldのキーイベントでオートコンプリートアイテムを操作
        this.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                AutoCompleteTextField field = AutoCompleteTextField.this;
                if (field.isVisibleItems() == false) {
                    return;
                }
                switch (event.getCode()) {
                case DOWN:
                    if (field.listView.getSelectionModel().getSelectedIndex() == -1) {
                        field.listView.getSelectionModel().selectFirst();
                    } else {
                        field.listView.getSelectionModel().selectNext();
                    }
                    field.listView.scrollTo(field.listView.getSelectionModel().getSelectedIndex() -  (int)field.listItemHeightRate + 1);
                    event.consume();
                    break;
                case UP:
                    if (field.listView.getSelectionModel().getSelectedIndex() == -1) {
                        field.listView.getSelectionModel().selectFirst();
                    } else {
                        field.listView.getSelectionModel().selectPrevious();
                    }
                    field.listView.scrollTo(field.listView.getSelectionModel().getSelectedIndex() -  (int)field.listItemHeightRate + 1);
                    event.consume();
                    break;
                case ENTER:
                    if (field.listView.getSelectionModel().getSelectedIndex() == -1) {
                        return;
                    }
                    field.setText(field.listView.getSelectionModel().getSelectedItem());
                    field.end();
                    field.hideItems();
                    event.consume();
                    break;
                default:
                    break;
                }
            }
        });
    }

    private double listItemHeightRate = 8;

    /**
     * リストアイテムの高さをTextFieldの高さ何個分かで指定する.(初期値は8個分)
     * @param rate TextFieldの高さ何個分にするか
     */
    public void setListItemHeightRate(double rate) {
        this.listItemHeightRate = rate;
    }

    /**
     * リストアイテムの高さをTextFieldの高さ何個分かを取得する.
     * @return TextFieldの高さ何個分か
     */
    public double getListItemHeightRate() {
        return this.listItemHeightRate;
    }

    private int maxItemsCount = 100;

    /**
     * リストアイテムの最大表示数を指定する.(初期値は100行)
     * @param maxCount 最大表示数
     */
    public void setMaxItemsCount(int maxCount) {
        this.maxItemsCount = maxCount;
    }

    /**
     * リストアイテムの最大表示数を取得する.
     * @return 最大表示数
     */
    public int getMaxItemsCount() {
        return this.maxItemsCount;
    }

    private ObservableList<String> items;

    /**
     * 内部のリストアイテムを取得する.
     * @return リストアイテム
     */
    public ObservableList<String> getItems() {
        return this.items;
    }

    /**
     * リストアイテムをセットする.
     * @param items
     */
    public void setItems(ObservableList<String> items) {
        this.items = items;
        if (this.listView != null) {
            this.listView.setItems(items);
        }
    }

    /**
     * リストアイテムを非表示にする.
     */
    public void hideItems() {
        if (this.popup == null) {
            return;
        }
        this.popup.hide();
    }

    /**
     * リストアイテムを表示する.
     */
    public void showItems() {
        if (this.popup == null || this.isFocused() == false) {
            return;
        }
        Bounds screenBounds = this.localToScreen(this.getBoundsInLocal());
        ObservableList<Screen> screens = Screen.getScreensForRectangle(screenBounds.getMinX(), screenBounds.getMinY(), screenBounds.getWidth(), screenBounds.getHeight());
        if (screens.size() == 0) {
            return;
        }
        double screenMaxY = screens.get(0).getBounds().getMaxY();
        double popupLayoutY = screenBounds.getMinY() + this.getHeight() + 1;
        if (screenMaxY < popupLayoutY + this.listView.getHeight()) {
            popupLayoutY = screenBounds.getMinY() - this.listView.getHeight() - 1;
        }
        this.popup.show(this.getScene().getWindow(), screenBounds.getMinX(), popupLayoutY);
    }

    /**
     * リストアイテムの表示状況を取得する.
     * @return 表示されていればTrue
     */
    public boolean isVisibleItems() {
        if (this.popup == null || this.popup.isShowing() == false) {
            return false;
        }
        return true;
    }

    /*
     * 文字入力時のフィルタをバックグラウンドで行うサービス
     */
    Service<Void> itemAddService = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            AutoCompleteTextField field = AutoCompleteTextField.this;
            return new Task<Void>() {

                private ObservableList<String> items = FXCollections.observableArrayList();

                @Override
                protected void succeeded() {
                    super.succeeded();
                    if (field.popup == null) {
                        return;
                    }
                    field.listView.setItems(this.items);
                    field.listView.getSelectionModel().clearSelection();
                    field.listView.getSelectionModel().select(field.getText());
                    if (field.listView.getItems().size() == 0 || field.listView.getItems().size() == 1 && field.listView.getItems().contains(field.getText())) {
                        field.hideItems();
                    } else {
                        field.showItems();
                    }
                }

                @Override
                protected Void call() throws Exception {
                    this.items.clear();
                    StringConverter converter = new StringConverter();
                    converter.addLowerToUpper();
                    converter.addNarrowToWide();
                    converter.addKatakanaToHiragana();
                    converter.addReplaceWideSpace("");
                    int addedCount = 0;
                    for (String item: field.items) {
                        if (this.isCancelled()) {
                            break;
                        }
                        if (field.getText().length() == 0 || converter.execute(item).indexOf(converter.execute(field.getText())) > -1) {
                            this.items.add(item);
                            addedCount++;
                            if (addedCount == field.maxItemsCount) {
                                break;
                            }
                        }
                    }
                    return null;
                }
            };
        }
    };
    
    private Popup popup;
    private ListView<String> listView;

    /**
     * 初回のみリストアイテムを作成する
     */
    private void createOnlyFirstItems() {
        AutoCompleteTextField field = AutoCompleteTextField.this;
        if (this.popup != null || this.getWidth() == 0 || this.getHeight() == 0 || this.getParent() == null || this.getParent().getScene() == null) {
            return;
        }
        // リストビューを作成して設定
        this.listView = new ListView<>();
        this.listView.setFocusTraversable(false);
        this.listView.setPrefSize(this.getWidth(), this.getHeight() * this.listItemHeightRate);
        this.listView.setStyle("-fx-font-size: " + this.getFont().getSize() + "px;");
        // リストアイテムクリック時の引用処理
        this.listView.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                field.setText(field.listView.getFocusModel().getFocusedItem());
                field.end();
            }
        });
        // TextFieldのWindowの移動に同期させる
        this.getParent().getScene().getWindow().xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (oldValue == null || newValue == null) {
                    return;
                }
                field.popup.setX(field.popup.getX() + newValue.doubleValue() - oldValue.doubleValue());
            }
        });
        this.getParent().getScene().getWindow().yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (oldValue == null || newValue == null) {
                    return;
                }
                field.popup.setY(field.popup.getY() + newValue.doubleValue() - oldValue.doubleValue());
            }
        });
        // 幅をコントロールに同期
        this.listView.prefWidthProperty().bind(this.widthProperty());
        // Popupを作成して表示
        this.popup = new Popup();
        this.popup.getContent().add(this.listView);
    }
    
}
