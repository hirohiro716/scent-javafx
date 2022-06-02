package com.hirohiro716.javafx.barcode;

import com.hirohiro716.number.CalculationHelper;

import javafx.scene.canvas.GraphicsContext;

/**
 * JavaFXでバーコードを生成するクラス。
 *
 * @author hiro
 */
public class NW7Helper extends com.hirohiro716.barcode.NW7Helper {

    /**
     * NW7バーコードを描画します。
     *
     * @param barcode バーコード
     * @param width 描画幅
     * @param height 描画高さ
     * @param x 描画位置x
     * @param y 描画位置y
     * @param graphicsContext GraphicsContextインスタンス
     */
    public static void drawBarcode(String barcode, double width, double height, double x, double y, GraphicsContext graphicsContext) {
        // キャラクタ間ギャップ
        double gap = 4;
        // すべてのキャラクタ総数を計算する
        double allCharactersWidth = 0;
        for (int i = 0; i < barcode.length(); i++) {
            if (i != 0) {
                allCharactersWidth += gap;
            }
            int[] characters = getDataCharacters().get(barcode.substring(i, i + 1));
            allCharactersWidth += CalculationHelper.calculateAddingAllIntegers(characters);
        }
        double characterBase = width / allCharactersWidth;
        // 描画
        double printX = x;
        for (int i = 0; i < barcode.length(); i++) {
            int[] characters = getDataCharacters().get(barcode.substring(i, i + 1));
            boolean isPause = false;
            for (int characterWidth: characters) {
                if (isPause == false) {
                    graphicsContext.fillRect(printX, y, characterBase * characterWidth, height);
                    isPause = true;
                } else {
                    isPause = false;
                }
                printX += characterBase * characterWidth;
            }
            printX += characterBase * gap;
        }
    }

}
