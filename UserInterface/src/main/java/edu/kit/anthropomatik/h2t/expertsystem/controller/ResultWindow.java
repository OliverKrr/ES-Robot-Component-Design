package edu.kit.anthropomatik.h2t.expertsystem.controller;

import edu.kit.anthropomatik.h2t.expertsystem.GuiHelper;
import edu.kit.anthropomatik.h2t.expertsystem.model.Component;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ResultWindow {

    private static final float ARROW_HEIGHT = 5;
    private static final float ARROW_WIDTH = 5;
    private static final Logger logger = LogManager.getLogger(ResultWindow.class);
    private static final PDFont FONT = PDType1Font.HELVETICA;
    private static final float TEXT_SIZE = 13;
    private static final int DPI = 200;
    private FormToolkit formToolkit;
    private DecimalFormat df = new DecimalFormat("#.####");
    private Map<String, ResultWindowOption> resultWindowOptions = new HashMap<>();
    private Map<Shell, Label> imageLabels = new HashMap<>();
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

    private static List<String> getResourceFolderFiles() throws URISyntaxException, IOException {
        List<String> filenames = new ArrayList<>();
        URI uri = ResultWindow.class.getResource("/structures/").toURI();
        Path myPath;
        FileSystem fileSystem = null;
        if (uri.getScheme().equals("jar")) {
            fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            myPath = fileSystem.getPath("/structures/");
        } else {
            myPath = Paths.get(uri);
        }
        Files.walk(myPath, 1).forEach(file -> filenames.add(file.getName(file.getNameCount() - 1).toString()));
        if (fileSystem != null) {
            fileSystem.close();
        }
        return filenames;
    }

    private void parseXMLFiles() throws IOException, URISyntaxException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error(e.getMessage(), e);
            return;
        }
        for (String fileName : getResourceFolderFiles()) {
            logger.debug("FileName: " + fileName);
            if (fileName.startsWith("Structure_") && fileName.endsWith(".xml")) {
                InputStream stream = ResultWindow.class.getResourceAsStream("/structures/" + fileName);
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
                    case "xText":
                        resultElement.setxText(Float.parseFloat(currentItem.getTextContent()));
                        break;
                    case "yText":
                        resultElement.setyText(Float.parseFloat(currentItem.getTextContent()));
                        break;
                    case "colorR":
                        resultElement.setColorR(Integer.parseInt(currentItem.getTextContent()));
                        break;
                    case "colorG":
                        resultElement.setColorG(Integer.parseInt(currentItem.getTextContent()));
                        break;
                    case "colorB":
                        resultElement.setColorB(Integer.parseInt(currentItem.getTextContent()));
                        break;
                    case "xDestination":
                        resultElement.setxDestination(Float.parseFloat(currentItem.getTextContent()));
                        break;
                    case "yDestination":
                        resultElement.setyDestination(Float.parseFloat(currentItem.getTextContent()));
                        break;
                    case "textDirectionDeg":
                        resultElement.setTextDirectionDeg(Double.parseDouble(currentItem.getTextContent()));
                        break;
                }
            }
            resultWindowOption.addResultElements(resultElement);
        }

        resultElements = root.getElementsByTagName("resultDimension");
        for (int i = 0; i < resultElements.getLength(); ++i) {
            ResultWindowOption.ResultDimension resultDimension = new ResultWindowOption.ResultDimension();
            NodeList nestedElement = resultElements.item(i).getChildNodes();
            for (int j = 0; j < nestedElement.getLength(); ++j) {
                Node currentItem = nestedElement.item(j);
                switch (currentItem.getNodeName()) {
                    case "key":
                        resultDimension.setKey(currentItem.getTextContent());
                        break;
                    case "offset":
                        resultDimension.setOffset(Float.parseFloat(currentItem.getTextContent()));
                        break;
                    case "colorR":
                        resultDimension.setColorR(Integer.parseInt(currentItem.getTextContent()));
                        break;
                    case "colorG":
                        resultDimension.setColorG(Integer.parseInt(currentItem.getTextContent()));
                        break;
                    case "colorB":
                        resultDimension.setColorB(Integer.parseInt(currentItem.getTextContent()));
                        break;
                    case "textDirectionDeg":
                        resultDimension.setTextDirectionDeg(Double.parseDouble(currentItem.getTextContent()));
                        break;
                }
            }
            resultWindowOption.addResultDimension(resultDimension);
        }
        resultWindowOptions.put(fileName.substring(0, fileName.length() - 4), resultWindowOption);
    }

    public void showWindow(String componentToBeDesigned, Result result) {
        //TODO move back to constructor
        try {
            parseXMLFiles();
        } catch (IOException | URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }

        logger.debug("Open new result window");
        isFirstTimeResized = true;
        Shell newShell = new Shell();
        newShell.setText("KIT Expert System Humanoid Robot Component Reasoner - Result");
        newShell.setImage(SWTResourceManager.getImage(ResultWindow.class, "/logos/H2T_logo.png"));

        PDDocument document = loadAndModifyPDFs(componentToBeDesigned, result);
        newShell.addDisposeListener(event -> {
            try {
                document.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });

        Button exportButton = new Button(newShell, SWT.PUSH);
        exportButton.setText("Export as PDF");
        Point size = GuiHelper.getSizeOfControl(exportButton);
        exportButton.setBounds(5, 5, size.x, size.y);
        formToolkit.adapt(exportButton, true, true);
        exportButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                FileDialog fileDialog = new FileDialog(newShell, SWT.SAVE);
                fileDialog.setOverwrite(true);
                fileDialog.setFilterExtensions(new String[]{"*.pdf"});

                String absolutePath = new File(".").getAbsolutePath();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
                String fileNameFilter = sdf.format(new Date()) + "_" + componentToBeDesigned + ".pdf";
                fileDialog.setFileName(absolutePath.substring(0, absolutePath.length() - 1) + fileNameFilter);

                String fileName = fileDialog.open();
                if (fileName != null) {
                    try {
                        document.save(fileName);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        visualizeDocument(newShell, document, size);

        newShell.open();
        newShell.layout();
        // maybe readAndDispatch in thread -> threadPool
    }

    private PDDocument loadAndModifyPDFs(String componentToBeDesigned, Result result) {
        List<MyDocument> myDocuments = handlePDFs(componentToBeDesigned, result);
        myDocuments.sort(Comparator.comparingInt(doc -> doc.resultWindowOption.getOrderPositionToDraw()));

        PDDocument document = new PDDocument();
        document.addPage(new PDPage());
        concatenateDocuments(myDocuments, document, componentToBeDesigned);
        return document;
    }

    private void visualizeDocument(Shell newShell, PDDocument document, Point size) {
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
            image = resizeImage(image, newShell.getSize().x - 30, newShell.getSize().y - 50 - size.y - 5);
            if (isFirstTimeResized) {
                isFirstTimeResized = false;
                newShell.setSize(Math.max(image.getBounds().width, size.x) + 30, image.getBounds().height + 50 + size
                        .y + 5);
            }
            Label imageLabel = imageLabels.get(newShell);
            if (imageLabel != null) {
                imageLabel.dispose();
            }
            imageLabel = new Label(newShell, SWT.CENTER);
            imageLabel.setBounds(5, 5 + size.y + 5, image.getBounds().width, image.getBounds().height);
            imageLabel.setImage(image);
            formToolkit.adapt(imageLabel, false, false);
            imageLabels.put(newShell, imageLabel);
        });
    }

    private List<MyDocument> handlePDFs(String componentToBeDesigned, Result result) {
        List<MyDocument> myDocuments = new ArrayList<>();
        resultWindowOptions.entrySet().stream().filter(entry -> componentToBeDesigned.equals(entry.getValue()
                .getComponentToBeDesigned()) && checkReqContainted(result, entry)).forEach(entry -> {
            try {
                PDDocument document = PDDocument.load(ResultWindow.class.getResourceAsStream("/structures/" + entry
                        .getKey() + ".pdf"));
                MyDocument myDocument = handlePDF(document, result, entry.getValue());
                myDocuments.add(myDocument);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });

        try {
            myDocuments.add(makeTable(result));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return myDocuments;
    }

    private boolean checkReqContainted(Result result, Map.Entry<String, ResultWindowOption> entry) {
        return result.components.stream().anyMatch(comp -> comp.nameOfComponent.equals(entry.getValue()
                .getStructurePosition()) && comp.nameOfInstance.equals(entry.getValue().getStructureOption())) ||
                result.requirements.stream().anyMatch(req -> req.displayName.equals(entry.getValue()
                        .getStructurePosition()) && !parseReq(req).equals(entry.getValue().getStructureOption()));
    }

    private void concatenateDocuments(List<MyDocument> documents, PDDocument document, String componentToBeDesigned) {
        Map<Integer, PDRectangle> positionInOverallPicturesMap = new HashMap<>();

        float totalXmin = (float) Double.MAX_VALUE;
        float totalXmax = 0;
        float totalYmin = (float) Double.MAX_VALUE;
        float totalYmax = 0;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0),
                PDPageContentStream.AppendMode.APPEND, true)) {
            for (MyDocument myDocument : documents) {
                float orginalWidth = myDocument.orginalRectangle.getWidth();
                float orginalHeight = myDocument.orginalRectangle.getHeight();

                float myDocumentWidth = myDocument.pdDocument.getPage(0).getMediaBox().getWidth();
                float myDocumentHeight = myDocument.pdDocument.getPage(0).getMediaBox().getHeight();

                float xToTranslate = getXToTranslate(positionInOverallPicturesMap, myDocument);
                float yToTranslate = -orginalHeight / 2.f + orginalHeight * (0.5f - myDocument.resultWindowOption
                        .getCenterlinePosition()) + myDocument.pdDocument.getPage(0).getMediaBox().getLowerLeftY();
                totalYmin = Math.min(totalYmin, yToTranslate);
                totalYmax = Math.max(totalYmax, yToTranslate + myDocumentHeight);
                logger.debug("myDocumentHeight: " + myDocumentHeight + " myDocumentWidth: " + myDocumentWidth + " " +
                        "xToTranslate: " + xToTranslate + " yToTranslate: " + yToTranslate);

                PDFRenderer renderer = new PDFRenderer(myDocument.pdDocument);
                BufferedImage bufferedImage = renderer.renderImageWithDPI(0, DPI, ImageType.ARGB);
                PDImageXObject image = LosslessFactory.createFromImage(document, bufferedImage);

                contentStream.drawImage(image, xToTranslate, yToTranslate, myDocumentWidth, myDocumentHeight);
                if (!positionInOverallPicturesMap.containsKey(myDocument.resultWindowOption
                        .getPostitionInOverallPictures())) {
                    positionInOverallPicturesMap.put(myDocument.resultWindowOption.getPostitionInOverallPictures(),
                            new PDRectangle(xToTranslate, yToTranslate, orginalWidth, myDocumentHeight));
                }

                totalXmin = Math.min(totalXmin, xToTranslate);
                totalXmax = Math.max(totalXmax, xToTranslate + myDocumentWidth);

                myDocument.pdDocument.close();
            }

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 3);
            contentStream.newLineAtOffset(totalXmin, totalYmax);
            contentStream.showText(componentToBeDesigned);
            contentStream.endText();

            totalXmin = Math.min(totalXmin, totalXmin - 5);
            totalXmax = Math.max(totalXmax, totalXmin + getTextWidth(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 3,
                    componentToBeDesigned) + 5);
            totalYmin = Math.min(totalYmin, totalYmax - 5);
            totalYmax = Math.max(totalYmax, totalYmax + getTextHeight(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 3));

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug("totalXmin: " + totalXmin + " totalXmax: " + totalXmax + " totalYmin: " + totalYmin + " " +
                "totalYmax: " + totalYmax);
        document.getPage(0).setMediaBox(new PDRectangle(totalXmin, totalYmin, totalXmax - totalXmin, totalYmax -
                totalYmin));
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
            orginalRectangle = document.getPage(i).getMediaBox();
            try (PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(i),
                    PDPageContentStream.AppendMode.APPEND, true)) {
                float offset = 7;
                contentStream.setLineWidth(2);
                for (ResultWindowOption.ResultDimension element : resultWindowOption.getResultDimensions()) {
                    contentStream.setStrokingColor(java.awt.Color.BLACK);
                    if (element.getTextDirectionDeg() % 360 == 0) {
                        contentStream.moveTo(0, element.getOffset());
                        contentStream.lineTo(0, orginalRectangle.getLowerLeftY());
                        contentStream.stroke();

                        contentStream.moveTo(orginalRectangle.getUpperRightX(), element.getOffset());
                        contentStream.lineTo(orginalRectangle.getUpperRightX(), orginalRectangle.getLowerLeftY());
                        contentStream.stroke();

                        contentStream.setStrokingColor(element.getColorR(), element.getColorG(), element.getColorB());
                        contentStream.moveTo(orginalRectangle.getLowerLeftX(), element.getOffset() + offset);
                        contentStream.lineTo(orginalRectangle.getUpperRightX(), element.getOffset() + offset);
                        contentStream.stroke();
                        drawTriangle(contentStream, orginalRectangle.getLowerLeftX() + ARROW_HEIGHT, element
                                .getOffset() + offset - ARROW_WIDTH / 2.f, 90);
                        drawTriangle(contentStream, orginalRectangle.getUpperRightX() - ARROW_HEIGHT, element
                                .getOffset() + offset - ARROW_WIDTH / 2.f, -90);
                        contentStream.setStrokingColor(java.awt.Color.BLACK);

                        contentStream.beginText();
                        contentStream.setFont(FONT, TEXT_SIZE);

                        String value = result.components.stream().filter(comp -> comp.nameOfComponent.equals(element
                                .getKey())).map(comp -> comp.nameOfInstance).findAny().orElse(null);
                        if (value == null) {
                            value = result.requirements.stream().filter(req -> req.displayName.equals(element.getKey
                                    ())).map(this::parseResult).findAny().orElse("");
                        }
                        float width = orginalRectangle.getUpperRightX() - orginalRectangle.getLowerLeftX();
                        float x = orginalRectangle.getLowerLeftX() + width / 2f - getTextWidth(value) / 2f;
                        float y = element.getOffset() - 6;
                        double angleRad = Math.toRadians(element.getTextDirectionDeg());
                        Matrix matrix = new Matrix((float) Math.cos(angleRad), (float) Math.sin(angleRad), -(float)
                                Math.sin(angleRad), (float) Math.cos(angleRad), x, y);
                        contentStream.setTextMatrix(matrix);

                        contentStream.showText(value);
                        contentStream.endText();

                        totalXmin = Math.min(totalXmin, orginalRectangle.getLowerLeftX() - 2);
                        totalXmax = Math.max(totalXmax, orginalRectangle.getUpperRightX() + 2);
                        totalYmin = Math.min(totalYmin, orginalRectangle.getLowerLeftY() + element.getOffset() +
                                offset - getTextHeight());
                    } else if (element.getTextDirectionDeg() % 360 == 90) {
                        contentStream.moveTo(element.getOffset(), orginalRectangle.getUpperRightY());
                        contentStream.lineTo(orginalRectangle.getLowerLeftX(), orginalRectangle.getUpperRightY());
                        contentStream.stroke();

                        contentStream.moveTo(element.getOffset(), orginalRectangle.getLowerLeftY());
                        contentStream.lineTo(orginalRectangle.getLowerLeftX(), orginalRectangle.getLowerLeftY());
                        contentStream.stroke();

                        contentStream.setStrokingColor(element.getColorR(), element.getColorG(), element.getColorB());
                        contentStream.moveTo(element.getOffset() + offset, orginalRectangle.getUpperRightY());
                        contentStream.lineTo(element.getOffset() + offset, orginalRectangle.getLowerLeftY());
                        contentStream.stroke();
                        drawTriangle(contentStream, element.getOffset() + offset - ARROW_WIDTH / 2.f,
                                orginalRectangle.getUpperRightY() - ARROW_HEIGHT, 0);
                        drawTriangle(contentStream, element.getOffset() + offset - ARROW_WIDTH / 2.f,
                                orginalRectangle.getLowerLeftY() + ARROW_HEIGHT, 180);
                        contentStream.setStrokingColor(java.awt.Color.BLACK);

                        contentStream.beginText();
                        contentStream.setFont(FONT, TEXT_SIZE);

                        String value = result.components.stream().filter(comp -> comp.nameOfComponent.equals(element
                                .getKey())).map(comp -> comp.nameOfInstance).findAny().orElse(null);
                        if (value == null) {
                            value = result.requirements.stream().filter(req -> req.displayName.equals(element.getKey
                                    ())).map(this::parseResult).findAny().orElse("");
                        }
                        float height = orginalRectangle.getUpperRightY() - orginalRectangle.getLowerLeftY();
                        float x = orginalRectangle.getLowerLeftX() + element.getOffset() + 2;
                        float y = orginalRectangle.getLowerLeftY() + height / 2f - getTextWidth(value) / 2f + 50;
                        double angleRad = Math.toRadians(element.getTextDirectionDeg());
                        Matrix matrix = new Matrix((float) Math.cos(angleRad), (float) Math.sin(angleRad), -(float)
                                Math.sin(angleRad), (float) Math.cos(angleRad), x, y);
                        contentStream.setTextMatrix(matrix);

                        contentStream.showText(value);
                        contentStream.endText();

                        totalXmin = Math.min(totalXmin, orginalRectangle.getLowerLeftX() + element.getOffset() +
                                offset - getTextHeight());
                        totalYmin = Math.min(totalYmin, orginalRectangle.getLowerLeftY() - 2);
                        totalYmax = Math.max(totalYmax, orginalRectangle.getUpperRightY() + 2);
                    } else if (element.getTextDirectionDeg() % 360 == -90) {
                        contentStream.moveTo(element.getOffset() + orginalRectangle.getUpperRightX(),
                                orginalRectangle.getUpperRightY());
                        contentStream.lineTo(orginalRectangle.getUpperRightX(), orginalRectangle.getUpperRightY());
                        contentStream.stroke();

                        contentStream.moveTo(element.getOffset() + orginalRectangle.getUpperRightX(),
                                orginalRectangle.getLowerLeftY());
                        contentStream.lineTo(orginalRectangle.getUpperRightX(), orginalRectangle.getLowerLeftY());
                        contentStream.stroke();

                        contentStream.setStrokingColor(element.getColorR(), element.getColorG(), element.getColorB());
                        contentStream.moveTo(element.getOffset() + orginalRectangle.getUpperRightX() - offset,
                                orginalRectangle.getUpperRightY());
                        contentStream.lineTo(element.getOffset() + orginalRectangle.getUpperRightX() - offset,
                                orginalRectangle.getLowerLeftY());
                        contentStream.stroke();
                        drawTriangle(contentStream, element.getOffset() + orginalRectangle.getUpperRightX() - offset
                                - ARROW_WIDTH / 2.f, orginalRectangle.getUpperRightY() - ARROW_HEIGHT, 0);
                        drawTriangle(contentStream, element.getOffset() + orginalRectangle.getUpperRightX() - offset
                                - ARROW_WIDTH / 2.f, orginalRectangle.getLowerLeftY() + ARROW_HEIGHT, 180);
                        contentStream.setStrokingColor(java.awt.Color.BLACK);

                        contentStream.beginText();
                        contentStream.setFont(FONT, TEXT_SIZE);

                        String value = result.components.stream().filter(comp -> comp.nameOfComponent.equals(element
                                .getKey())).map(comp -> comp.nameOfInstance).findAny().orElse(null);
                        if (value == null) {
                            value = result.requirements.stream().filter(req -> req.displayName.equals(element.getKey
                                    ())).map(this::parseResult).findAny().orElse("");
                        }
                        float height = orginalRectangle.getUpperRightY() - orginalRectangle.getLowerLeftY();
                        float x = orginalRectangle.getUpperRightX() + element.getOffset() - 2;
                        float y = orginalRectangle.getLowerLeftY() + height / 2f + getTextWidth(value) / 2f;
                        double angleRad = Math.toRadians(element.getTextDirectionDeg());
                        Matrix matrix = new Matrix((float) Math.cos(angleRad), (float) Math.sin(angleRad), -(float)
                                Math.sin(angleRad), (float) Math.cos(angleRad), x, y);
                        contentStream.setTextMatrix(matrix);

                        contentStream.showText(value);
                        contentStream.endText();

                        totalXmax = Math.max(totalXmax, orginalRectangle.getUpperRightX() + element.getOffset() -
                                offset + getTextHeight());
                        totalYmin = Math.min(totalYmin, orginalRectangle.getLowerLeftY() - 2);
                        totalYmax = Math.max(totalYmax, orginalRectangle.getUpperRightY() + 2);
                    }
                }

                contentStream.setLineWidth(3);
                for (ResultWindowOption.ResultElement element : resultWindowOption.getResultElements()) {
                    contentStream.beginText();
                    contentStream.setFont(FONT, TEXT_SIZE);

                    double angleRad = Math.toRadians(element.getTextDirectionDeg());
                    Matrix matrix = new Matrix((float) Math.cos(angleRad), (float) Math.sin(angleRad), -(float) Math
                            .sin(angleRad), (float) Math.cos(angleRad), element.getxText(), element.getyText());
                    contentStream.setTextMatrix(matrix);

                    String value = result.components.stream().filter(comp -> comp.nameOfComponent.equals(element
                            .getKey())).map(comp -> comp.nameOfInstance).findAny().orElse(null);
                    if (value == null) {
                        value = result.requirements.stream().filter(req -> req.displayName.equals(element.getKey()))
                                .map(this::parseResult).findAny().orElse("");
                    }
                    contentStream.showText(value);
                    contentStream.endText();

                    contentStream.setStrokingColor(element.getColorR(), element.getColorG(), element.getColorB());

                    float x = element.getxText() - 3;
                    float y = element.getyText() - 4;
                    float width = getTextWidth(value);
                    float height = getTextHeight();

                    Matrix matrixRotation = new Matrix((float) Math.cos(angleRad), (float) Math.sin(angleRad), -
                            (float) Math.sin(angleRad), (float) Math.cos(angleRad), 0, 0);

                    //TODO fix that also work with rotation
                    if (element.getTextDirectionDeg() == 0) {
                        Point2D.Float lowLeft = matrixRotation.transformPoint(x, y);
                        Point2D.Float upRight = matrixRotation.transformPoint(x + width, y + height);
                        logger.debug("value: " + value + " lowLeft: " + lowLeft + " upRight: " + upRight);
                        width = Math.abs(upRight.x - lowLeft.x);
                        height = Math.abs(upRight.y - lowLeft.y);

                        contentStream.addRect(lowLeft.x, lowLeft.y, width, height);
                        contentStream.stroke();

                        totalXmin = Math.min(totalXmin, lowLeft.x - 5);
                        totalXmax = Math.max(totalXmax, upRight.x + 5);
                        totalYmin = Math.min(totalYmin, lowLeft.y - 5);
                        totalYmax = Math.max(totalYmax, upRight.y + 5);

                        float arrowToX = element.getxDestination();
                        float arrowToY = element.getyDestination();
                        if (arrowToX != 0 && arrowToY != 0) {
                            float startLineX = Math.abs(upRight.x - lowLeft.x) / 2.f + Math.min(upRight.x, lowLeft.x);
                            float startLineY = Math.abs(lowLeft.y - arrowToY) < Math.abs(upRight.y - arrowToY) ?
                                    lowLeft.y : upRight.y;
                            contentStream.setStrokingColor(java.awt.Color.BLACK);
                            contentStream.moveTo(startLineX, startLineY);
                            contentStream.lineTo(arrowToX, arrowToY);
                            contentStream.stroke();
                            //TODO fix with direction
                            //drawTriangle(contentStream, arrowToX, arrowToY);
                        }
                    }
                }
            }
            document.getPage(i).setMediaBox(new PDRectangle(totalXmin, totalYmin, totalXmax - totalXmin, totalYmax -
                    totalYmin));
        }
        return new MyDocument(orginalRectangle, document, resultWindowOption);
    }

    private float getTextHeight() throws IOException {
        return FONT.getBoundingBox().getHeight() / 1000 * TEXT_SIZE + 4;
    }

    private float getTextWidth(String value) throws IOException {
        return FONT.getStringWidth(value) / 1000 * TEXT_SIZE + 6;
    }

    private float getTextHeight(PDFont font, float textSize) throws IOException {
        return font.getBoundingBox().getHeight() / 1000 * textSize + 4;
    }

    private float getTextWidth(PDFont font, float textSize, String value) throws IOException {
        return font.getStringWidth(value) / 1000 * textSize + 6;
    }

    private MyDocument makeTable(Result result) throws IOException {
        PDDocument document = new PDDocument();
        document.addPage(new PDPage());

        float textWidth1 = 0;
        float textWidth2 = 0;
        for (Component component : result.components) {
            textWidth1 = Math.max(textWidth1, getTextWidth(component.nameOfComponent));
            textWidth2 = Math.max(textWidth2, getTextWidth(component.nameOfInstance));
        }
        for (Requirement req : result.requirements) {
            if (req.resultIRI == null) {
                continue;
            }
            textWidth1 = Math.max(textWidth1, getTextWidth(req.displayName));
            String unit = req.unit == null ? "" : req.unit;
            textWidth2 = Math.max(textWidth2, getTextWidth(parseResult(req) + " " + unit));
        }
        logger.debug("textWidth1: " + textWidth1 + " textWidth2: " + textWidth2);
        textWidth1 += 3;
        textWidth2 += 3;
        float textHeight = getTextHeight() + 2;

        float totalXmin = (float) Double.MAX_VALUE;
        float totalXmax = 0;
        float totalYmin = (float) Double.MAX_VALUE;
        float totalYmax = 0;

        float x = 0;
        float y = 0;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0),
                PDPageContentStream.AppendMode.APPEND, true)) {

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 1);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText("Subcomponents");
            contentStream.endText();

            totalXmin = Math.min(totalXmin, x - 5);
            totalXmax = Math.max(totalXmax, x + getTextWidth(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 1,
                    "Subcomponents") + 5);
            totalYmin = Math.min(totalYmin, y - 5);
            totalYmax = Math.max(totalYmax, y + getTextHeight(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 1));

            y -= 1.5f * getTextHeight(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 1);

            for (Component component : result.components) {
                contentStream.beginText();
                contentStream.setFont(FONT, TEXT_SIZE);
                contentStream.newLineAtOffset(x + 3, y + 6);
                contentStream.showText(component.nameOfComponent);
                contentStream.endText();

                if (addColorIfAvailable(contentStream, component.nameOfComponent)) {
                    contentStream.setLineWidth(4);
                } else {
                    contentStream.setLineWidth(1.5f);
                }
                contentStream.addRect(x, y, textWidth1, textHeight);
                contentStream.stroke();
                contentStream.setStrokingColor(java.awt.Color.BLACK);

                totalXmin = Math.min(totalXmin, x - 5);
                totalXmax = Math.max(totalXmax, x + textWidth1 + 5);
                totalYmin = Math.min(totalYmin, y - 5);
                totalYmax = Math.max(totalYmax, y + textHeight + 5);

                contentStream.beginText();
                contentStream.setFont(FONT, TEXT_SIZE);
                contentStream.newLineAtOffset(x + textWidth1 + 6, y + 6);
                contentStream.showText(component.nameOfInstance);
                contentStream.endText();
                contentStream.setStrokingColor(java.awt.Color.BLACK);

                if (addColorIfAvailable(contentStream, component.nameOfComponent)) {
                    contentStream.setLineWidth(4);
                } else {
                    contentStream.setLineWidth(1.5f);
                }
                contentStream.addRect(x + textWidth1, y, textWidth2, textHeight);
                contentStream.stroke();
                contentStream.setStrokingColor(java.awt.Color.BLACK);

                totalXmin = Math.min(totalXmin, x + textWidth1 - 5);
                totalXmax = Math.max(totalXmax, x + textWidth1 + textWidth2 + 10);
                y -= textHeight;
            }

            y -= 3;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 1);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText("Properties");
            contentStream.endText();

            totalXmin = Math.min(totalXmin, x - 5);
            totalXmax = Math.max(totalXmax, x + getTextWidth(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 1, "Properties")
                    + 5);
            totalYmin = Math.min(totalYmin, y - 5);
            totalYmax = Math.max(totalYmax, y + getTextHeight(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 1));

            y -= 1.5f * getTextHeight(PDType1Font.HELVETICA_BOLD, TEXT_SIZE + 1);

            for (Requirement req : result.requirements) {
                if (req.resultIRI == null) {
                    continue;
                }
                contentStream.beginText();
                contentStream.setFont(FONT, TEXT_SIZE);
                contentStream.newLineAtOffset(x + 3, y + 6);
                contentStream.showText(req.displayName);
                contentStream.endText();

                if (addColorIfAvailable(contentStream, req.displayName)) {
                    contentStream.setLineWidth(4);
                } else {
                    contentStream.setLineWidth(1.5f);
                }
                contentStream.addRect(x, y, textWidth1, textHeight);
                contentStream.stroke();
                contentStream.setStrokingColor(java.awt.Color.BLACK);

                totalXmin = Math.min(totalXmin, x - 5);
                totalXmax = Math.max(totalXmax, x + textWidth1 + 5);
                totalYmin = Math.min(totalYmin, y - 5);
                totalYmax = Math.max(totalYmax, y + textHeight + 5);

                contentStream.beginText();
                contentStream.setFont(FONT, TEXT_SIZE);
                contentStream.newLineAtOffset(x + textWidth1 + 6, y + 6);
                String unit = req.unit == null ? "" : req.unit;
                contentStream.showText(parseResult(req) + " " + unit);
                contentStream.endText();

                if (addColorIfAvailable(contentStream, req.displayName)) {
                    contentStream.setLineWidth(4);
                } else {
                    contentStream.setLineWidth(1.5f);
                }
                contentStream.addRect(x + textWidth1, y, textWidth2, textHeight);
                contentStream.stroke();
                contentStream.setStrokingColor(java.awt.Color.BLACK);

                totalXmin = Math.min(totalXmin, x + textWidth1 - 5);
                totalXmax = Math.max(totalXmax, x + textWidth1 + textWidth2 + 10);
                y -= textHeight;
            }
        }
        logger.debug("table: totalXmin: " + totalXmin + " totalXmax: " + totalXmax + " totalYmin: " + totalYmin + " "
                + "totalYmax: " + totalYmax);
        PDRectangle pdRectangle = new PDRectangle(totalXmin, totalYmin, totalXmax - totalXmin, totalYmax - totalYmin);
        document.getPage(0).setMediaBox(pdRectangle);

        ResultWindowOption resultWindowOption = new ResultWindowOption();
        resultWindowOption.setPostitionInOverallPictures(getMaxPostitionInOverallPictures());
        resultWindowOption.setOrderPositionToDraw(getMaxOrderPositionToDraw());
        resultWindowOption.setCenterlinePosition(-0.5f);
        resultWindowOption.setxOffset(60);
        return new MyDocument(pdRectangle, document, resultWindowOption);
    }

    private boolean addColorIfAvailable(PDPageContentStream contentStream, String key) {
        return resultWindowOptions.values().stream().map(res -> {
            res.getResultElements().stream().filter(resEle -> key.equals(resEle.getKey())).forEach(ele -> {
                try {
                    contentStream.setStrokingColor(ele.getColorR(), ele.getColorG(), ele.getColorB());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
            res.getResultDimensions().stream().filter(ele -> key.equals(ele.getKey())).forEach(resEle -> {
                try {
                    contentStream.setStrokingColor(resEle.getColorR(), resEle.getColorG(), resEle.getColorB());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
            return res.getResultElements().stream().anyMatch(ele -> key.equals(ele.getKey())) || res
                    .getResultDimensions().stream().anyMatch(ele -> key.equals(ele.getKey()));
        }).reduce((right, left) -> right || left).orElse(false);
    }

    private void drawTriangle(PDPageContentStream contentStream, float x, float y, int degree) throws IOException {
        if (degree % 360 == 0) {
            contentStream.moveTo(x, y);
            contentStream.lineTo(x + ARROW_WIDTH, y);
            contentStream.stroke();

            contentStream.moveTo(x + ARROW_WIDTH, y);
            contentStream.lineTo(x + ARROW_WIDTH / 2.f, y + ARROW_HEIGHT);
            contentStream.stroke();

            contentStream.moveTo(x + ARROW_WIDTH / 2.f, y + ARROW_HEIGHT);
            contentStream.lineTo(x, y);
            contentStream.stroke();
        } else if (degree % 360 == 90) {
            contentStream.moveTo(x, y);
            contentStream.lineTo(x - ARROW_HEIGHT, y + ARROW_WIDTH / 2.f);
            contentStream.stroke();

            contentStream.moveTo(x - ARROW_HEIGHT, y + ARROW_WIDTH / 2.f);
            contentStream.lineTo(x, y + ARROW_WIDTH);
            contentStream.stroke();

            contentStream.moveTo(x, y + ARROW_WIDTH);
            contentStream.lineTo(x, y);
            contentStream.stroke();
        } else if (degree % 360 == -90) {
            contentStream.moveTo(x, y);
            contentStream.lineTo(x, y + ARROW_WIDTH);
            contentStream.stroke();

            contentStream.moveTo(x, y + ARROW_WIDTH);
            contentStream.lineTo(x + ARROW_WIDTH, y + ARROW_WIDTH / 2.f);
            contentStream.stroke();

            contentStream.moveTo(x + ARROW_WIDTH, y + ARROW_WIDTH / 2.f);
            contentStream.lineTo(x, y);
            contentStream.stroke();
        } else if (degree % 360 == 180) {
            contentStream.moveTo(x, y);
            contentStream.lineTo(x + ARROW_WIDTH, y);
            contentStream.stroke();

            contentStream.moveTo(x + ARROW_WIDTH, y);
            contentStream.lineTo(x + ARROW_WIDTH / 2.f, y - ARROW_HEIGHT);
            contentStream.stroke();

            contentStream.moveTo(x + ARROW_WIDTH / 2.f, y - ARROW_HEIGHT);
            contentStream.lineTo(x, y);
            contentStream.stroke();
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

    private int getMaxOrderPositionToDraw() {
        return resultWindowOptions.values().stream().max(Comparator.comparingInt
                (ResultWindowOption::getOrderPositionToDraw)).map(ResultWindowOption::getOrderPositionToDraw).orElse
                (0) + 1;
    }

    private int getMaxPostitionInOverallPictures() {
        return resultWindowOptions.values().stream().max(Comparator.comparingInt
                (ResultWindowOption::getPostitionInOverallPictures)).map
                (ResultWindowOption::getPostitionInOverallPictures).orElse(0) + 1;
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
