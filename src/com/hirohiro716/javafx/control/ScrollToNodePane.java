package com.hirohiro716.javafx.control;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;

/**
 * 指定領域へスクロールすることができるScrollPaneコントロールクラス.
 * @author hiro
 */
public class ScrollToNodePane extends ScrollPane {
    
    /**
     * Viewport内での指定領域が現在表示されているかを判定する.
     * @param layoutX 指定領域の開始横位置
     * @param layoutY 指定領域の開始縦位置
     * @param width 指定領域の幅
     * @param height 指定領域の高さ
     * @return 結果
     */
    public boolean isSeen(double layoutX, double layoutY, double width, double height) {
        double layoutEndX = layoutX + width;
        double viewportStartX = (this.getContent().getBoundsInParent().getWidth() - this.getViewportBounds().getWidth()) * this.getHvalue();
        double viewportEndX = viewportStartX + this.getViewportBounds().getWidth();
        if (viewportStartX > layoutX || viewportEndX < layoutEndX) {
            return false;
        }
        double layoutEndY = layoutY + height;
        double viewportStartY = (this.getContent().getBoundsInParent().getHeight() - this.getViewportBounds().getHeight()) * this.getVvalue();
        double viewportEndY = viewportStartY + this.getViewportBounds().getHeight();
        if (viewportStartY > layoutY || viewportEndY < layoutEndY) {
            return false;
        }
        return true;
    }

    /**
     * 指定位置へのスクロール.
     * @param x
     * @param y
     */
    public void scroll(double x, double y) {
        this.setHvalue(x / this.getContent().getBoundsInParent().getWidth());
        this.setVvalue(y / this.getContent().getBoundsInParent().getHeight());
    }

    /**
     * 内包している指定Nodeまでスクロールする. すでに指定Nodeが表示されていれば処理しない.
     * @param node 対象
     */
    public void scroll(Node node) {
        if (node == null || node.getBoundsInParent().getMaxX() == 0 || node.getBoundsInParent().getMaxY() == 0) {
            return;
        }
        double contentLayoutX = node.getBoundsInParent().getMinX();
        double contentLayoutY = node.getBoundsInParent().getMinY();
        Parent parent = node.getParent();
        while (this.getContent() != parent) {
            contentLayoutX += parent.getBoundsInParent().getMinX();
            contentLayoutY += parent.getBoundsInParent().getMinY();
            parent = parent.getParent();
        }
        if (this.isSeen(contentLayoutX, contentLayoutY, node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight()) == false) {
            this.scroll(contentLayoutX, contentLayoutY);
        }
    }
    
}
