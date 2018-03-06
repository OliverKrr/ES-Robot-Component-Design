package edu.kit.anthropomatik.h2t.expertsystem.controller;

import edu.kit.anthropomatik.h2t.expertsystem.model.Result;
import edu.kit.anthropomatik.h2t.expertsystem.model.req.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
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

    private static final PDFont font = PDType1Font.HELVETICA;
    private static final float textSize = 12;

    private FormToolkit formToolkit;
    private DecimalFormat df = new DecimalFormat("#.####");

    private Map<String, ResultWindowOption> resultWindowOptions = new HashMap<>();

    ResultWindow(FormToolkit formToolkit) {
        this.formToolkit = formToolkit;
        df.setRoundingMode(RoundingMode.CEILING);
        parseXMLFiles();
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
        resultWindowOption.setAlignmentInPictureX(Integer.parseInt(root.getElementsByTagName("alignmentInPictureX")
                .item(0).getTextContent()));
        resultWindowOption.setAlignmentInPictureY(Integer.parseInt(root.getElementsByTagName("alignmentInPictureY")
                .item(0).getTextContent()));
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
        Shell newShell = new Shell();
        newShell.setText("KIT ES Component Reasoner - Result");

        loadAndModifyPDFs(newShell, componentToBeDesigned, result);

        newShell.layout();
        newShell.open();
        // maybe readAndDispatch in thread -> threadPool
    }

    private void loadAndModifyPDFs(Shell newShell, String componentToBeDesigned, Result result) {
        resultWindowOptions.entrySet().stream().filter(entry -> componentToBeDesigned.equals(entry.getValue()
                .getComponentToBeDesigned()) && result.components.stream().anyMatch(comp -> comp.nameOfComponent
                .equals(entry.getValue().getStructurePosition()) && comp.nameOfInstance.equals(entry.getValue()
                .getStructureOption()))).forEach(entry -> {
            try (PDDocument document = PDDocument.load(getClass().getResourceAsStream("/" + entry.getKey() + "" + ""
                    + ".pdf"))) {
                handlePDF(document, result, entry.getValue());
                PDFRenderer renderer = new PDFRenderer(document);
                for (int page = 0; page < document.getNumberOfPages(); ++page) {
                    BufferedImage bufferedImage = renderer.renderImageWithDPI(page, 300, ImageType.RGB);
                    Image image = new Image(Display.getCurrent(), Objects.requireNonNull(convertToSWT(bufferedImage)));
                    image = resizeImage(image, newShell.getSize().x - 10, newShell.getSize().y - 50);
                    Label imageLabel = new Label(newShell, SWT.CENTER);
                    imageLabel.setBounds(5, 5, image.getBounds().width, image.getBounds().height);
                    imageLabel.setImage(image);
                    formToolkit.adapt(imageLabel, false, false);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    private void handlePDF(PDDocument document, Result result, ResultWindowOption resultWindowOption) throws
            IOException {
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            try (PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(i),
                    PDPageContentStream.AppendMode.APPEND, true, true)) {
                for (ResultWindowOption.ResultElement element : resultWindowOption.getResultElements()) {
                    contentStream.beginText();
                    contentStream.setFont(font, textSize);

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
                }
            }
        }
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
}
