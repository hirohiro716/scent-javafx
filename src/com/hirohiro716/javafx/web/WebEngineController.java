package com.hirohiro716.javafx.web;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.web.WebEngine;

import static com.hirohiro716.StringConverter.*;

/**
 * WebEngineの操作を補助するクラス.
 * @author hiro
 */
public class WebEngineController {

    /**
     * コンストラクタ.
     * @param webEngine
     */
    public WebEngineController(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    private WebEngine webEngine;
    
    /**
     * WebEngineインスタンスを取得する.
     * @return WebEngine
     */
    public WebEngine getWebEngine() {
        return this.webEngine;
    }
    
    private ArrayList<Element> selectedElementsList = new ArrayList<>();
    
    /**
     * Elementが選択されているかを確認する.
     * @return boolean
     */
    public boolean isSelectedElement() {
        return this.selectedElementsList.size() > 0;
    }
    
    /**
     * 選択済みのElementsを取得する.
     * @return Element[]
     */
    public Element[] getSelectedElements() {
        return this.selectedElementsList.toArray(new Element[] {});
    }
    
    /**
     * 選択済みのElementを取得する.
     * @return Element
     */
    public Element getSelectedElement() {
        if (this.isSelectedElement() == false) {
            return null;
        }
        return this.getSelectedElements()[0];
    }
    
    /**
     * ElementをID属性を元に検索して選択する.
     * @param id
     */
    public void selectElementById(String id) {
        this.selectedElementsList.clear();
        Element element = this.webEngine.getDocument().getElementById(id);
        if (element != null) {
            this.selectedElementsList.add(element);
        }
    }
    
    /**
     * Elementをタグ名を元に検索して選択する.
     * @param tagName
     */
    public void selectElementsByTagName(String tagName) {
        this.selectedElementsList.clear();
        NodeList nodeList = this.webEngine.getDocument().getElementsByTagName(tagName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            this.selectedElementsList.add((Element) nodeList.item(i));
        }
    }

    /**
     * Elementのタグ名とテキストを元に正規表現で検索して選択する.
     * @param tagName
     * @param textCompareRegex テキストと比較する正規表現
     */
    public void selectElementsByTagName(String tagName, String textCompareRegex) {
        this.selectedElementsList.clear();
        NodeList nodeList = this.webEngine.getDocument().getElementsByTagName(tagName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String elementText = element.getTextContent();
            if (elementText != null && elementText.matches(textCompareRegex)) {
                this.selectedElementsList.add(element);
            }
        }
    }
    
    /**
     * Elementを属性値を元に正規表現で検索して選択する.
     * @param attributeName 属性名
     * @param valueCompareRegex 属性値と比較する正規表現
     */
    public void selectElementsByAttribute(String attributeName, String valueCompareRegex) {
        this.selectedElementsList.clear();
        for (Node node: this.getChildNodeList(this.webEngine.getDocument())) {
            if (node instanceof Element) {
                Element element = (Element) node;
                String value = element.getAttribute(attributeName);
                if (value != null && value.matches(valueCompareRegex)) {
                    this.selectedElementsList.add(element);
                }
            }
        }
    }

    /**
     * 再帰的に子要素を取得する.
     * @param targetNode
     * @return ArrayList<Node>
     */
    private ArrayList<Node> getChildNodeList(Node targetNode) {
        ArrayList<Node> result = new ArrayList<>();
        NodeList nodeList = targetNode.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node temporaryNode = nodeList.item(i);
                result.add(temporaryNode);
                ArrayList<Node> temporaryNodes = this.getChildNodeList(temporaryNode);
                for (Node node: temporaryNodes) {
                    result.add(node);
                }
            }
        }
        return result;
    }
    
    /**
     * JavaScriptを利用して選択されている最初のElementをクリックする.
     */
    public void click() {
        if (this.isSelectedElement()) {
            clickElement(this.webEngine, this.selectedElementsList.get(0));
        }
    }
    
    private final static String TARGET_CLASS_NAME = "com-hirohiro716-javafx-web-webenginecontroller-target";
    
    /**
     * JavaScriptを利用してElementをクリックする.
     * @param webEngine
     * @param element
     */
    public static void clickElement(WebEngine webEngine, Element element) {
        if (element == null) {
            return;
        }
        String originalClass = nullReplace(element.getAttribute("class"), "");
        element.setAttribute("class", join(originalClass, " ", TARGET_CLASS_NAME));
        webEngine.executeScript(join("document.getElementsByClassName('", TARGET_CLASS_NAME, "')[0].click();"));
        element.setAttribute("class", originalClass);
    }
    
}
