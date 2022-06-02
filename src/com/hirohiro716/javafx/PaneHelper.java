package com.hirohiro716.javafx;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * Paneを便利に扱う静的メソッドクラス。
 *
 * @author hiro
 */
public class PaneHelper {

    /**
     * Pane内の指定された型のjavafxオブジェクトを取得する。
     *
     * @param <T> 検索するクラス
     * @param pane 検索対象
     * @param type 検索する型
     * @return 検索結果(該当するものがなければnull)
     */
    public static <T> T findNode(Pane pane, Class<T> type) {
        applyCss(pane);
        return findNode(pane, type, "*");
    }

    /**
     * Pane内の指定された型のjavafxオブジェクトを取得する。
     *
     * @param <T> 検索するクラス
     * @param pane 検索対象
     * @param type 検索する型
     * @param selector セレクター
     * @return 検索結果(該当するものがなければnull)
     */
    @SuppressWarnings("unchecked")
    public static <T> T findNode(Pane pane, Class<T> type, String selector) {
        for (Node node: pane.lookupAll(selector)) {
            if (type.isInstance(node)) {
                return (T) node;
            }
        }
        for (Node parentNode: pane.getChildren()) {
            for (Node node: parentNode.lookupAll(selector)) {
                if (type.isInstance(node)) {
                    return (T) node;
                }
            }
        }
        return null;
    }

    /**
     * Pane上のフォーカス移動をEnterキーで行えるようにする。
     *
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param pane 対象Pane
     */
    public static <T extends Pane> void applyFocusTraversalBindEnterKey(T pane) {
        applyCss(pane);
        ArrayList<Node> focusables = new ArrayList<>();
        for (Node node: pane.lookupAll("*")) {
            if (node instanceof Control) {
                focusables.add(node);
            }
        }
        for (int i = 0; i < focusables.size(); i++) {
            Node focusable = focusables.get(i);
            EventHandler<KeyEvent> eventHandler = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                    case ENTER:
                        int focusableIndex = focusables.indexOf(focusable);
                        if (focusableIndex == -1) {
                            return;
                        }
                        int nextIndex;
                        if (event.isShiftDown() == false) {
                            nextIndex = focusableIndex + 1;
                        } else {
                            nextIndex = focusableIndex - 1;
                        }
                        while (nextIndex != focusableIndex) {
                            try {
                                Node node = focusables.get(nextIndex);
                                if (node.isFocusTraversable() && node.isDisable() == false && node.isVisible()) {
                                    node.requestFocus();
                                    break;
                                }
                                if (event.isShiftDown() == false) {
                                    nextIndex++;
                                } else {
                                    nextIndex--;
                                }
                            } catch (IndexOutOfBoundsException exception) {
                                if (event.isShiftDown() == false) {
                                    nextIndex = 0;
                                } else {
                                    nextIndex = focusables.size() - 1;
                                }
                            }
                        }
                        event.consume();
                        break;
                    default:
                        break;
                    }
                }
            };
            if (focusable.getParent() instanceof DatePicker) {
                focusable.addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
            } else if (focusable instanceof TextArea) {
                TextArea textArea = (TextArea) focusable;
                // 編集ができない場合以外は改行できなくなるので対象外
                if (textArea.isEditable() == false) {
                    focusable.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
                }
            } else {
                focusable.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
            }
        }
    }

    /**
     * ScrollPaneやTabPaneなどの中身をlookupAllする場合はこれをしないとできない。
     *
     * @param pane 対象Pane
     */
    private static void applyCss(Pane pane) {
        String id = PaneHelper.class.getName().replaceAll("\\.", "_").toUpperCase() + "_FLAG_FOR_APPLIED_CSS";
        if (pane.getStyleClass().contains(id) == false) {
            pane.applyCss();
            pane.getStyleClass().add(id);
        }
    }
}
