package edu.kit.anthropomatik.h2t.expertsystem.controller;

import java.util.ArrayList;
import java.util.List;

public class ResultWindowOption {

    private String componentToBeDesigned;
    private int alignmentInPictureX;
    private int alignmentInPictureY;
    private String structurePosition;
    private String structureOption;
    private List<ResultElement> resultElements = new ArrayList<>();

    public String getComponentToBeDesigned() {
        return componentToBeDesigned;
    }

    public void setComponentToBeDesigned(String componentToBeDesigned) {
        this.componentToBeDesigned = componentToBeDesigned;
    }

    public int getAlignmentInPictureX() {
        return alignmentInPictureX;
    }

    public void setAlignmentInPictureX(int alignmentInPictureX) {
        this.alignmentInPictureX = alignmentInPictureX;
    }

    public int getAlignmentInPictureY() {
        return alignmentInPictureY;
    }

    public void setAlignmentInPictureY(int alignmentInPictureY) {
        this.alignmentInPictureY = alignmentInPictureY;
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
        private String textDirection;

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

        public String getTextDirection() {
            return textDirection;
        }

        public void setTextDirection(String textDirection) {
            this.textDirection = textDirection;
        }
    }
}
