package com.hirohiro716.javafx.control;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * 移動やリサイズ機能をつけたLabel。
 *
 * @author hiro
 */
public class MovableLabel extends Label {
	
	/**
	 * コンストラクタ.
	 * @param text
	 * @param graphic
	 */
	public MovableLabel(String text, Node graphic) {
		super(text, graphic);
    	this.setOnMouseMoved(this.mouseMoveEventHandler);
    	this.setOnMousePressed(this.mousePressedEventHandler);
    	this.setOnMouseReleased(this.mouseReleasedEventHandler);
    	this.setOnMouseDragged(this.mouseDraggedEventHandler);
	}

	/**
	 * コンストラクタ.
	 * @param text
	 */
	public MovableLabel(String text) {
		this(text, null);
	}
	
	/**
	 * コンストラクタ.
	 */
	public MovableLabel() {
		this(null, null);
	}
	
    private double edgeSize = 5;
    
    private ArrayList<Operation> operations = new ArrayList<>();
    
    private boolean isMousePressed = false;
    
    private Bounds defaultBounds;
    
    private Point2D startPointOfScene;

	private EventHandler<MouseEvent> mouseMoveEventHandler = new EventHandler<MouseEvent>() {
		
		@Override
		public void handle(MouseEvent event) {
			MovableLabel label = MovableLabel.this;
			if (label.isMousePressed) {
				return;
			}
			double pointX = event.getX();
			double pointY = event.getY();
			double width = label.getWidth();
			double height = label.getHeight();
			label.operations.clear();
			if (pointX < label.edgeSize) {
				label.operations.add(Operation.RESIZE_LEFT);
			}
			if (width - pointX < label.edgeSize) {
				label.operations.add(Operation.RESIZE_RIGHT);
			}
			if (pointY < label.edgeSize) {
				label.operations.add(Operation.RESIZE_TOP);
			}
			if (height - pointY < label.edgeSize) {
				label.operations.add(Operation.RESIZE_BOTTOM);
			}
			if (label.operations.size() == 0) {
				label.operations.add(Operation.MOVE);
			}
			if (label.operations.size() == 1) {
				switch (label.operations.get(0)) {
				case RESIZE_TOP:
				case RESIZE_BOTTOM:
					label.setCursor(Cursor.V_RESIZE);
					break;
				case RESIZE_RIGHT:
				case RESIZE_LEFT:
					label.setCursor(Cursor.H_RESIZE);
					break;
				case MOVE:
					label.setCursor(Cursor.MOVE);
					break;
				}
			} else {
				if (label.operations.contains(Operation.RESIZE_TOP) && label.operations.contains(Operation.RESIZE_LEFT)
						|| label.operations.contains(Operation.RESIZE_BOTTOM) && label.operations.contains(Operation.RESIZE_RIGHT)) {
					label.setCursor(Cursor.NW_RESIZE);
				} else {
					label.setCursor(Cursor.SW_RESIZE);
				}
			}
		}
		
	};
	
	private EventHandler<MouseEvent> mousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			MovableLabel label = MovableLabel.this;
			label.isMousePressed = true;
			label.defaultBounds = label.getBoundsInParent();
			label.startPointOfScene = new Point2D(event.getSceneX(), event.getSceneY());
		}
		
	};
	
	private EventHandler<MouseEvent> mouseReleasedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			MovableLabel label = MovableLabel.this;
			label.isMousePressed = false;
		}
		
	};
	
	private EventHandler<MouseEvent> mouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			MovableLabel label = MovableLabel.this;
			double differenceX = event.getSceneX() - label.startPointOfScene.getX();
			double differenceY = event.getSceneY() - label.startPointOfScene.getY();
			for (Operation operation: label.operations) {
				switch (operation) {
				case RESIZE_TOP:
					double resizeTopLayoutY = label.defaultBounds.getMinY() + differenceY;
					double resizeTopHeight = label.defaultBounds.getHeight() - differenceY;
					if (resizeTopLayoutY >= 0 && resizeTopHeight > label.edgeSize * 3) {
						label.setLayoutY(resizeTopLayoutY);
						label.setPrefHeight(resizeTopHeight);
					}
					break;
				case RESIZE_LEFT:
					double resizeLeftLayoutX = label.defaultBounds.getMinX() + differenceX;
					double resizeLeftWidth = label.defaultBounds.getWidth() - differenceX;
					if (resizeLeftLayoutX >= 0 && resizeLeftWidth > label.edgeSize * 3) {
						label.setLayoutX(resizeLeftLayoutX);
						label.setPrefWidth(resizeLeftWidth);
					}
					break;
				case RESIZE_BOTTOM:
					double resizeMaxHeight = label.getParent().getBoundsInParent().getHeight() - label.getLayoutY();
					double resizeBottomHeight = label.defaultBounds.getHeight() + differenceY;
					if (resizeBottomHeight > label.edgeSize * 3 && resizeBottomHeight < resizeMaxHeight) {
						label.setPrefHeight(resizeBottomHeight);
					}
					break;
				case RESIZE_RIGHT:
					double resizeMaxWidth = label.getParent().getBoundsInParent().getWidth() - label.getLayoutX();
					double resizeRightWidth = label.defaultBounds.getWidth() + differenceX;
					if (resizeRightWidth > label.edgeSize * 3 && resizeRightWidth < resizeMaxWidth) {
						label.setPrefWidth(resizeRightWidth);
					}
					break;
				case MOVE:
					double maxLayoutY = label.getParent().getBoundsInParent().getHeight() - label.getHeight();
					double moveLayoutY = label.defaultBounds.getMinY() + differenceY;
					if (moveLayoutY >= 0 && moveLayoutY < maxLayoutY) {
						label.setLayoutY(moveLayoutY);
					}
					double maxLayoutX = label.getParent().getBoundsInParent().getWidth() - label.getWidth();
					double moveLayoutX = label.defaultBounds.getMinX() + differenceX;
					if (moveLayoutX >= 0 && moveLayoutX < maxLayoutX) {
						label.setLayoutX(moveLayoutX);
					}
					break;
				}
			}
		}
		
	};

	/**
	 * 操作する種類の列挙型.
	 * @author hiro
	 */
    private enum Operation {
    	MOVE,
    	RESIZE_TOP,
    	RESIZE_RIGHT,
    	RESIZE_BOTTOM,
    	RESIZE_LEFT,
    }}
