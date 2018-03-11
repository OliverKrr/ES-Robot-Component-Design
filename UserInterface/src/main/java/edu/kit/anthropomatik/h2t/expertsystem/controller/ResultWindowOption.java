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
    private List<ResultDimension> resultDimensions = new ArrayList<>();

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

    public List<ResultDimension> getResultDimensions() {
        return resultDimensions;
    }

    public void addResultDimension(ResultDimension resultDimension) {
        this.resultDimensions.add(resultDimension);
    }

    public static class ResultElement {
        private String key;
        private float xText;
        private float yText;
        private double textDirectionDeg;
        private int colorR;
        private int colorG;
        private int colorB;
        private float xDestination;
        private float yDestination;


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public float getxText() {
            return xText;
        }

        public void setxText(float xText) {
            this.xText = xText;
        }

        public float getyText() {
            return yText;
        }

        public void setyText(float yText) {
            this.yText = yText;
        }

        public double getTextDirectionDeg() {
            return textDirectionDeg;
        }

        public void setTextDirectionDeg(double textDirectionDeg) {
            this.textDirectionDeg = textDirectionDeg;
        }

        public int getColorR() {
            return colorR;
        }

        public void setColorR(int colorR) {
            this.colorR = colorR;
        }

        public int getColorG() {
            return colorG;
        }

        public void setColorG(int colorG) {
            this.colorG = colorG;
        }

        public int getColorB() {
            return colorB;
        }

        public void setColorB(int colorB) {
            this.colorB = colorB;
        }

        public float getxDestination() {
            return xDestination;
        }

        public void setxDestination(float xDestination) {
            this.xDestination = xDestination;
        }

        public float getyDestination() {
            return yDestination;
        }

        public void setyDestination(float yDestination) {
            this.yDestination = yDestination;
        }
    }

    public static class ResultDimension {
        private String key;
        private float offset;
        private double textDirectionDeg;
        private int colorR;
        private int colorG;
        private int colorB;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public float getOffset() {
            return offset;
        }

        public void setOffset(float offset) {
            this.offset = offset;
        }

        public double getTextDirectionDeg() {
            return textDirectionDeg;
        }

        public void setTextDirectionDeg(double textDirectionDeg) {
            this.textDirectionDeg = textDirectionDeg;
        }

        public int getColorR() {
            return colorR;
        }

        public void setColorR(int colorR) {
            this.colorR = colorR;
        }

        public int getColorG() {
            return colorG;
        }

        public void setColorG(int colorG) {
            this.colorG = colorG;
        }

        public int getColorB() {
            return colorB;
        }

        public void setColorB(int colorB) {
            this.colorB = colorB;
        }
    }
}
