package edu.kit.anthropomatik.h2t.expertsystem.controller;

import edu.kit.anthropomatik.h2t.expertsystem.GUI;
import edu.kit.anthropomatik.h2t.expertsystem.model.Result;
import edu.kit.anthropomatik.h2t.expertsystem.model.req.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class ResultWindow {

    private static final Logger logger = LogManager.getLogger(ResultWindow.class);

    private static final PDFont FONT = PDType1Font.HELVETICA;
    private static final float TEXT_SIZE = 12;
    private static final int DPI = 100;

    private FormToolkit formToolkit;
    private DecimalFormat df = new DecimalFormat("#.####");

    private Map<String, ResultWindowOption> resultWindowOptions = new HashMap<>();
    private Label imageLabel;
    private boolean isFirstTimeResized = true;

    ResultWindow(FormToolkit formToolkit) {
        this.formToolkit = formToolkit;
        df.setRoundingMode(RoundingMode.CEILING);
    }

    // from http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt
    // .snippets/src/org/eclipse/swt/snippets/Snippet156.java
    private static ImageData convertToSWT(BufferedImage bufferedImage) {
        if (bufferedImage.getColorModel() instanceof DirectColorModel) {
            DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
            PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel
                    .getBlueMask());
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel
                    .getPixelSize(), palette);
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    int rgb = bufferedImage.getRGB(x, y);
                    int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
                    data.setPixel(x, y, pixel);
                    if (colorModel.hasAlpha()) {
                        data.setAlpha(x, y, (rgb >> 24) & 0xFF);
                    }
                }
            }
            return data;
        } else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
            IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
            int size = colorModel.getMapSize();
            byte[] reds = new byte[size];
            byte[] greens = new byte[size];
            byte[] blues = new byte[size];
            colorModel.getReds(reds);
            colorModel.getGreens(greens);
            colorModel.getBlues(blues);
            RGB[] rgbs = new RGB[size];
            for (int i = 0; i < rgbs.length; i++) {
                rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
            }
            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel
                    .getPixelSize(), palette);
            data.transparentPixel = colorModel.getTransparentPixel();
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixelArray);
                    data.setPixel(x, y, pixelArray[0]);
                }
            }
            return data;
        }
        return null;
    }

    private static Image resizeImage(Image image, int width, int height) {
        float ratio = Math.min(1.f * width / image.getBounds().width, 1.f * height / image.getBounds().height);
        int newWidth = Math.round(image.getBounds().width * ratio);
        int newHeight = Math.round(image.getBounds().height * ratio);
        Image scaled = new Image(Display.getCurrent(), newWidth, newHeight);
        GC gc = new GC(scaled);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, newWidth, newHeight);
        gc.dispose();
        return scaled;
    }

    private void parseXMLFiles() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error(e.getMessage(), e);
            return;
        }
        try {
            for (String fileName : getResourceFiles()) {
                if (fileName.startsWith("Structure_") && fileName.endsWith(".xml")) {
                    InputStream stream = getClass().getResourceAsStream("/" + fileName);
                    Document doc;
                    try {
                        doc = builder.parse(stream);
                    } catch (SAXException | IOException e) {
                        logger.error(e.getMessage(), e);
                        continue;
                    }
                    try {
                        parseDocument(fileName, doc);
                    } catch (NullPointerException | NumberFormatException e) {
                        logger.error("Cannot parse " + fileName, e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private List<String> getResourceFiles() throws IOException {
        List<String> filenames = new ArrayList<>();
        try (InputStream in = getClass().getResourceAsStream("/"); BufferedReader br = new BufferedReader(new
                InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }
        return filenames;
    }

    private void parseDocument(String fileName, Document doc) {
        ResultWindowOption resultWindowOption = new ResultWindowOption();
        Element root = doc.getDocumentElement();

        resultWindowOption.setComponentToBeDesigned(root.getElementsByTagName("componentToBeDesigned").item(0)
                .getTextContent());

        resultWindowOption.setOrderPositionToDraw(Integer.parseInt(root.getElementsByTagName("orderPositionToDraw")
                .item(0).getTextContent()));
        resultWindowOption.setPostitionInOverallPictures(Integer.parseInt(root.getElementsByTagName
                ("postitionInOverallPicture").item(0).getTextContent()));

        resultWindowOption.setCenterlinePosition(Float.parseFloat(root.getElementsByTagName("centerlinePosition")
                .item(0).getTextContent()));
        resultWindowOption.setxOffset(Float.parseFloat(root.getElementsByTagName("xOffset").item(0).getTextContent()));

        resultWindowOption.setStructurePosition(root.getElementsByTagName("structurePosition").item(0).getTextContent
                ());
        resultWindowOption.setStructureOption(root.getElementsByTagName("structureOption").item(0).getTextContent());

        NodeList resultElements = root.getElementsByTagName("resultElement");
        for (int i = 0; i < resultElements.getLength(); ++i) {
            ResultWindowOption.ResultElement resultElement = new ResultWindowOption.ResultElement();
            NodeList nestedElement = resultElements.item(i).getChildNodes();
            for (int j = 0; j < nestedElement.getLength(); ++j) {
                Node currentItem = nestedElement.item(j);
                switch (currentItem.getNodeName()) {
                    case "key":
                        resultElement.setKey(currentItem.getTextContent());
                        break;
                    case "x":
                        resultElement.setX(Integer.parseInt(currentItem.getTextContent()));
                        break;
                    case "y":
                        resultElement.setY(Integer.parseInt(currentItem.getTextContent()));
                        break;
                    case "textDirectionDeg":
                        resultElement.setTextDirection(Double.parseDouble(currentItem.getTextContent()));
                        break;
                }
            }
            resultWindowOption.addResultElements(resultElement);
        }
        resultWindowOptions.put(fileName.substring(0, fileName.length() - 4), resultWindowOption);
    }

    public void showWindow(String componentToBeDesigned, Result result) {
        logger.debug("Open new result window");
        isFirstTimeResized = true;
        parseXMLFiles();
        Shell newShell = new Shell();
        newShell.setText("KIT Expert System Humanoid Robot Component Reasoner - Result");
        newShell.setImage(SWTResourceManager.getImage(GUI.class, "/H2T_logo.png"));

        loadAndModifyPDFs(newShell, componentToBeDesigned, result);

        newShell.layout();
        newShell.open();
        // maybe readAndDispatch in thread -> threadPool
    }

    private void loadAndModifyPDFs(Shell newShell, String componentToBeDesigned, Result result) {
        List<MyDocument> myDocuments = handlePDFs(componentToBeDesigned, result);
        myDocuments.sort(Comparator.comparingInt(doc -> doc.resultWindowOption.getOrderPositionToDraw()));
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            concatenateDocuments(myDocuments, document);
            visualizeDocument(newShell, document);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void visualizeDocument(Shell newShell, PDDocument document) {
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage bufferedImage;
        try {
            bufferedImage = renderer.renderImageWithDPI(0, DPI, ImageType.ARGB);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return;
        }
        newShell.addListener(SWT.Resize, e -> {
            Image image = new Image(Display.getCurrent(), Objects.requireNonNull(convertToSWT(bufferedImage)));
            image = resizeImage(image, newShell.getSize().x - 30, newShell.getSize().y - 50);
            if (isFirstTimeResized) {
                isFirstTimeResized = false;
                newShell.setSize(image.getBounds().width + 30, image.getBounds().height + 50);
            }
            if (imageLabel != null) {
                imageLabel.dispose();
            }
            imageLabel = new Label(newShell, SWT.CENTER);
            imageLabel.setBounds(5, 5, image.getBounds().width, image.getBounds().height);
            imageLabel.setImage(image);
            formToolkit.adapt(imageLabel, false, false);
        });
    }

    private List<MyDocument> handlePDFs(String componentToBeDesigned, Result result) {
        List<MyDocument> myDocuments = new ArrayList<>();
        //TODO also check equale reqs -> through bor
        resultWindowOptions.entrySet().stream().filter(entry -> componentToBeDesigned.equals(entry.getValue()
                .getComponentToBeDesigned()) && checkReqContainted(result, entry)).forEach(entry -> {
            try {
                PDDocument document = PDDocument.load(getClass().getResourceAsStream("/" + entry.getKey() + "" + "" +
                        ".pdf"));
                MyDocument myDocument = handlePDF(document, result, entry.getValue());
                myDocuments.add(myDocument);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return myDocuments;
    }

    private boolean checkReqContainted(Result result, Map.Entry<String, ResultWindowOption> entry) {
        return result.components.stream().anyMatch(comp -> comp.nameOfComponent.equals(entry.getValue()
                .getStructurePosition()) && comp.nameOfInstance.equals(entry.getValue().getStructureOption())) ||
                result.requirements.stream().anyMatch(req -> req.displayName.equals(entry.getValue()
                        .getStructurePosition()) && !parseReq(req).equals(entry.getValue().getStructureOption()));
    }

    private void concatenateDocuments(List<MyDocument> documents, PDDocument document) {
        Map<Integer, PDRectangle> positionInOverallPicturesMap = new HashMap<>();

        float totalXmin = (float) Double.MAX_VALUE;
        float totalXmax = 0;
        float totalYmin = (float) Double.MAX_VALUE;
        float totalYmax = 0;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0),
                PDPageContentStream.AppendMode.APPEND, true)) {
            for (MyDocument myDocument : documents) {
                float myDocumentWidth = myDocument.orginalRectangle.getWidth();
                float myDocumentHeight = myDocument.orginalRectangle.getHeight();

                float xToTranslate = getXToTranslate(positionInOverallPicturesMap, myDocument);
                float yToTranslate = -myDocumentHeight / 2.f + myDocumentHeight * (0.5f - myDocument
                        .resultWindowOption.getCenterlinePosition());
                totalYmin = Math.min(totalYmin, yToTranslate);
                totalYmax = Math.max(totalYmax, yToTranslate + myDocumentHeight);
                logger.debug("Height: " + myDocumentHeight + " width: " + myDocumentWidth + " xToTranslate: " +
                        xToTranslate + " yToTranslate: " + yToTranslate);

                PDFRenderer renderer = new PDFRenderer(myDocument.pdDocument);
                BufferedImage bufferedImage = renderer.renderImageWithDPI(0, DPI, ImageType.ARGB);
                PDImageXObject image = LosslessFactory.createFromImage(document, bufferedImage);

                contentStream.drawImage(image, xToTranslate, yToTranslate, myDocumentWidth, myDocumentHeight);
                if (!positionInOverallPicturesMap.containsKey(myDocument.resultWindowOption
                        .getPostitionInOverallPictures())) {
                    positionInOverallPicturesMap.put(myDocument.resultWindowOption.getPostitionInOverallPictures(),
                            new PDRectangle(xToTranslate, yToTranslate, myDocumentWidth, myDocumentHeight));
                }

                totalXmin = Math.min(totalXmin, xToTranslate);
                totalXmax = Math.max(totalXmax, xToTranslate + myDocumentWidth);

                myDocument.pdDocument.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug("totalXmin: " + totalXmin + " totalXmax: " + totalXmax + " totalYmin: " + totalYmin + " " +
                "totalYmax: " + totalYmax);
        document.getPage(0).setMediaBox(new PDRectangle(totalXmin, totalYmin, totalXmax - totalXmin, totalYmax -
                totalYmin));
        // TODO: append values as table at right after last which are not in picture yet
    }

    private float getXToTranslate(Map<Integer, PDRectangle> positionInOverallPicturesMap, MyDocument myDocument) {
        if (positionInOverallPicturesMap.containsKey(myDocument.resultWindowOption.getPostitionInOverallPictures())) {
            PDRectangle pdRectangle = positionInOverallPicturesMap.get(myDocument.resultWindowOption
                    .getPostitionInOverallPictures());
            float xOffset = myDocument.resultWindowOption.getxOffset();
            if (xOffset < 0) {
                return pdRectangle.getUpperRightX() + xOffset;
            } else {
                return pdRectangle.getLowerLeftX() + xOffset;
            }
        } else if (positionInOverallPicturesMap.containsKey(myDocument.resultWindowOption
                .getPostitionInOverallPictures() - 1)) {
            PDRectangle pdRectangle = positionInOverallPicturesMap.get(myDocument.resultWindowOption
                    .getPostitionInOverallPictures() - 1);
            return pdRectangle.getUpperRightX() + myDocument.resultWindowOption.getxOffset();
        } else if (positionInOverallPicturesMap.containsKey(myDocument.resultWindowOption
                .getPostitionInOverallPictures() + 1)) {
            PDRectangle pdRectangle = positionInOverallPicturesMap.get(myDocument.resultWindowOption
                    .getPostitionInOverallPictures() + 1);
            return pdRectangle.getLowerLeftX() + myDocument.resultWindowOption.getxOffset() - myDocument
                    .orginalRectangle.getWidth();
        }
        return 0;
    }

    private MyDocument handlePDF(PDDocument document, Result result, ResultWindowOption resultWindowOption) throws
            IOException {
        PDRectangle orginalRectangle = null;
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            float totalXmin = document.getPage(i).getMediaBox().getLowerLeftX();
            float totalXmax = document.getPage(i).getMediaBox().getUpperRightX();
            float totalYmin = document.getPage(i).getMediaBox().getLowerLeftY();
            float totalYmax = document.getPage(i).getMediaBox().getUpperRightY();
            logger.debug("Begin totalXmin: " + totalXmin + " totalXmax: " + totalXmax + " totalYmin: " + totalYmin +
                    "" + " " + "totalYmax: " + totalYmax);
            orginalRectangle = document.getPage(i).getMediaBox();
            try (PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(i),
                    PDPageContentStream.AppendMode.APPEND, true)) {
                for (ResultWindowOption.ResultElement element : resultWindowOption.getResultElements()) {
                    contentStream.beginText();
                    contentStream.setFont(FONT, TEXT_SIZE);

                    double angleRad = Math.toRadians(element.getTextDirectionDeg());
                    Matrix matrix = new Matrix((float) Math.cos(angleRad), (float) Math.sin(angleRad), -(float) Math
                            .sin(angleRad), (float) Math.cos(angleRad), element.getX(), element.getY());
                    contentStream.setTextMatrix(matrix);

                    String value = result.components.stream().filter(comp -> comp.nameOfComponent.equals(element
                            .getKey())).map(comp -> comp.nameOfInstance).findAny().orElse(null);
                    if (value == null) {
                        value = result.requirements.stream().filter(req -> req.displayName.equals(element.getKey()))
                                .map(this::parseResult).findAny().orElse("");
                    }
                    contentStream.showText(value);
                    contentStream.endText();

                    totalXmin = Math.min(totalXmin, element.getX());
                    totalXmax = Math.max(totalXmax, element.getX());
                    totalYmin = Math.min(totalYmin, element.getY());
                    totalYmax = Math.max(totalYmax, element.getY());
                }
            }
            logger.debug("End totalXmin: " + totalXmin + " totalXmax: " + totalXmax + " totalYmin: " + totalYmin + " " +
                    "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "totalYmax: " + totalYmax + " -> width: " +
                    (totalXmax - totalXmin) + " " + "and " + "height: " + (totalYmax - totalYmin));
            document.getPage(i).setMediaBox(new PDRectangle(totalXmin, totalYmin, totalXmax - totalXmin, totalYmax -
                    totalYmin));
        }
        return new MyDocument(orginalRectangle, document, resultWindowOption);
    }

    private String parseResult(Requirement req) {
        if (req instanceof TextFieldMinMaxRequirement) {
            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else if (req instanceof TextFieldRequirement) {
            TextFieldRequirement realReq = (TextFieldRequirement) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else if (req instanceof CheckboxRequirement) {
            CheckboxRequirement realReq = (CheckboxRequirement) req;
            return String.valueOf(realReq.result);
        } else if (req instanceof DropdownRequirement) {
            DropdownRequirement realReq = (DropdownRequirement) req;
            return realReq.result;
        } else if (req instanceof RequirementOnlyForSolution) {
            RequirementOnlyForSolution realReq = (RequirementOnlyForSolution) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else {
            throw new RuntimeException("Requirement class unknown: " + req.getClass());
        }
    }

    private String parseReq(Requirement req) {
        if (req instanceof TextFieldMinMaxRequirement) {
            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
            return df.format(realReq.defaultMin + realReq.defaultMax);
        } else if (req instanceof TextFieldRequirement) {
            TextFieldRequirement realReq = (TextFieldRequirement) req;
            return df.format(realReq.value);
        } else if (req instanceof CheckboxRequirement) {
            CheckboxRequirement realReq = (CheckboxRequirement) req;
            return String.valueOf(realReq.value);
        } else if (req instanceof DropdownRequirement) {
            DropdownRequirement realReq = (DropdownRequirement) req;
            return realReq.defaultValue;
        } else if (req instanceof RequirementOnlyForSolution) {
            RequirementOnlyForSolution realReq = (RequirementOnlyForSolution) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else {
            throw new RuntimeException("Requirement class unknown: " + req.getClass());
        }
    }

    private static class MyDocument {
        PDRectangle orginalRectangle;
        PDDocument pdDocument;
        ResultWindowOption resultWindowOption;

        MyDocument(PDRectangle orginalRectangle, PDDocument pdDocument, ResultWindowOption resultWindowOption) {
            this.orginalRectangle = orginalRectangle;
            this.pdDocument = pdDocument;
            this.resultWindowOption = resultWindowOption;
        }
    }
}
