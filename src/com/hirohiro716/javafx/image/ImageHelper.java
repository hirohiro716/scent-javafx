package com.hirohiro716.javafx.image;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Imageの扱いを補助するクラス。
 *
 * @author hiro
 */
public class ImageHelper {
    
    /**
     * Imageをファイルに保存する。
     *
     * @param image 保存対象
     * @param formatName jpgなどの画像フォーマット名
     * @param file 保存先
     * @throws IOException 
     */
    public static void saveAs(Image image, String formatName, File file) throws IOException {
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), formatName, file);
    }}
