package edu.kit.anthropomatik.h2t.expertsystem.controller;

import edu.kit.anthropomatik.h2t.expertsystem.model.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResultWindow {

    private static final Logger logger = LogManager.getLogger(ResultWindow.class);

    private FormToolkit formToolkit;

    private List<ResultWindowOption> resultWindowOptions = new ArrayList<>();

    ResultWindow(FormToolkit formToolkit) {
        this.formToolkit = formToolkit;
        parseXMLFiles();
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
                        parseDocument(doc);
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

    private void parseDocument(Document doc) {
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
                    case "textDirection":
                        resultElement.setTextDirection(currentItem.getTextContent());
                        break;
                }
            }
            resultWindowOption.addResultElements(resultElement);
        }
        resultWindowOptions.add(resultWindowOption);
    }

    public void showWindow(Result result) {
        Shell newShell = new Shell();
        newShell.setText("KIT ES Component Reasoner - Result");

        Text text = new Text(newShell, SWT.BORDER | SWT.WRAP);
        text.setText(resultWindowOptions.get(0).getComponentToBeDesigned());
        formToolkit.adapt(text, true, true);
        text.setBounds(0, 0, newShell.getSize().x, newShell.getSize().y);
        newShell.open();
        //TODO make right
        // maybe readAndDispatch in thread -> threadPool
    }
}
