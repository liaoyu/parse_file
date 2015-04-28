package com.liaoyu.process;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.liaoyu.po.FileItem;
import com.liaoyu.po.RecordField;

/**
 * @author  liaoyu
 * @created Apr 28, 2015
 */
public class ResourceFactory {

    private static final String CONFIG_DIR_PATH = "./config/";
    private static final String CONFIG_EXTFILE_LIST_NAME = "file_list_config.properties";
    private Map<String, Map<String, List<RecordField>>> CONFIG_CACHE = new HashMap<String, Map<String, List<RecordField>>>();
    private List<FileItem> fileList = new ArrayList<FileItem>();
    private Document dom = null;
    private static ResourceFactory factory = null;

    private ResourceFactory() {
        initResources();
    }

    public static ResourceFactory getSington() {
        if (factory == null) {
            factory = new ResourceFactory();
        }
        return factory;
    }

    private void initResources() {

        try {
            InputStream inStream = new FileInputStream(CONFIG_DIR_PATH + CONFIG_EXTFILE_LIST_NAME);
            PropertyResourceBundle bundle = new PropertyResourceBundle(inStream);
            inStream.close();

            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                FileItem fileItem = new FileItem();
                String key = keys.nextElement();
                fileItem.setKey(key);
                fileItem.setValue(bundle.getString(key));
                fileList.add(fileItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseTargetExtfile(String targetFileName) {
        try {
            InputStream inStream = new FileInputStream(CONFIG_DIR_PATH + targetFileName);
            PropertyResourceBundle bundle = new PropertyResourceBundle(inStream);
            inStream.close();

            String indicator = "";
            String defName = "";
            Enumeration<String> keys = bundle.getKeys();
            Map<String, List<RecordField>> records = new HashMap<String, List<RecordField>>();

            while (keys.hasMoreElements()) {
                indicator = keys.nextElement();
                defName = bundle.getString(indicator);
                List<RecordField> fields = new ArrayList<RecordField>();

                InputStream tmpInStream = new FileInputStream(CONFIG_DIR_PATH + defName);
                parseXmlFile(tmpInStream);
                tmpInStream.close();

                NodeList nodeList = dom.getElementsByTagName("field");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    RecordField field = new RecordField();

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        field.setTitle(element.getTextContent());
                        field.setLength(Integer.valueOf(element.getAttribute("length")));
                    }

                    fields.add(field);
                }

                records.put(indicator, fields);

            }

            CONFIG_CACHE.put(targetFileName, records);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseXmlFile(InputStream inStream) {
        // get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = db.parse(inStream);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<RecordField>> getConfigCache(String targetFileName) {
        if (CONFIG_CACHE.get(targetFileName) == null) {
            parseTargetExtfile(targetFileName);
        }
        return CONFIG_CACHE.get(targetFileName);
    }

    public List<FileItem> getFileItemList() {
        return fileList;
    }
}
