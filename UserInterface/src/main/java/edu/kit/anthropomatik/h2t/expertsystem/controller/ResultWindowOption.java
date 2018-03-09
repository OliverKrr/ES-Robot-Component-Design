package edu.kit.anthropomatik.h2t.expertsystem.controller;

import java.util.ArrayList;
import java.util.List;

public class ResultWindowOption {

    private String componentToBeDesigned;
    private int postitionInOverallPictures;
    private int orderPositionToDraw;
    private float centerlinePosition;
    private float xOffset;
    private String structurePosition;
    private String structureOption;
    private List<ResultElement> resultElements = new ArrayList<>();

    public float getxOffset() {
        return xOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public int getOrderPositionToDraw() {
        return orderPositionToDraw;
    }

    public void setOrderPositionToDraw(int orderPositionToDraw) {
        this.orderPositionToDraw = orderPositionToDraw;
    }

    public String getComponentToBeDesigned() {
        return componentToBeDesigned;
    }

    public void setComponentToBeDesigned(String componentToBeDesigned) {
        this.componentToBeDesigned = componentToBeDesigned;
    }

    public int getPostitionInOverallPictures() {
        return postitionInOverallPictures;
    }

    public void setPostitionInOverallPictures(int postitionInOverallPictures) {
        this.postitionInOverallPictures = postitionInOverallPictures;
    }

    public float getCenterlinePosition() {
        return centerlinePosition;
    }

    public void setCenterlinePosition(float centerlinePosition) {
        this.centerlinePosition = centerlinePosition;
    }

    public String getStructurePosition() {
        return structurePosition;
    }

    public void setStructurePosition(String structurePosition) {
        this.structurePosition = structurePosition;
    }

    public String getStructureOption() {
        return structureOption;
    }

    public void setStructureOption(String structureOption) {
        this.structureOption = structureOption;
    }

    public List<ResultElement> getResultElements() {
        return resultElements;
    }

    public void addResultElements(ResultElement resultElement) {
        this.resultElements.add(resultElement);
    }

    public static class ResultElement {
        private String key;
        private int x;
        private int y;
        private double textDirectionDeg;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public double getTextDirectionDeg() {
            return textDirectionDeg;
        }

        public void setTextDirection(double textDirectionDeg) {
            this.textDirectionDeg = textDirectionDeg;
        }
    }
}
