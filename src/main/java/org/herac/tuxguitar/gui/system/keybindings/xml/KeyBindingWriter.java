package org.herac.tuxguitar.gui.system.keybindings.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.herac.tuxguitar.gui.editors.chord.ChordSelector;
import org.herac.tuxguitar.gui.system.keybindings.KeyBindingAction;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class KeyBindingWriter {

  private static final String SHORTCUT_ATTRIBUTE_ACTION = "action";
  private static final String SHORTCUT_ATTRIBUTE_KEY = "key";
  private static final String SHORTCUT_ATTRIBUTE_MASK = "mask";
  private static final String SHORTCUT_ROOT = "shortcuts";
  private static final String SHORTCUT_TAG = "shortcut";

  public static Document createDocument() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.newDocument();
      return document;
    } catch (Throwable throwable) {
      LOG.error(throwable);
    }
    return null;
  }


  /** The Logger for this class. */
  public static final transient Logger LOG = Logger
      .getLogger(KeyBindingWriter.class);
  
  
  public static void saveDocument(Document document, File file) {
    try {
      FileOutputStream fs = new FileOutputStream(file);

      // Write it out again
      TransformerFactory xformFactory = TransformerFactory.newInstance();
      Transformer idTransform = xformFactory.newTransformer();
      Source input = new DOMSource(document);
      Result output = new StreamResult(fs);
      idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
      idTransform.transform(input, output);
    } catch (Throwable throwable) {
      LOG.error(throwable);
    }
  }

  /**
   * Write shortcuts to xml file
   * 
   * @param shortcutsNode
   * @return
   */
  private static void setBindings(List<KeyBindingAction> list, Document document) {
    Node shortcutsNode = document.createElement(SHORTCUT_ROOT);

    for (final KeyBindingAction keyBindingAction : list) {

      Node node = document.createElement(SHORTCUT_TAG);
      shortcutsNode.appendChild(node);

      Attr attrKey = document.createAttribute(SHORTCUT_ATTRIBUTE_KEY);
      Attr attrMask = document.createAttribute(SHORTCUT_ATTRIBUTE_MASK);
      Attr attrAction = document.createAttribute(SHORTCUT_ATTRIBUTE_ACTION);

      attrKey.setNodeValue(Integer.toString(keyBindingAction.getKeyBinding()
          .getKey()));
      attrMask.setNodeValue(Integer.toString(keyBindingAction.getKeyBinding()
          .getMask()));
      attrAction.setNodeValue(keyBindingAction.getAction());

      node.getAttributes().setNamedItem(attrKey);
      node.getAttributes().setNamedItem(attrMask);
      node.getAttributes().setNamedItem(attrAction);
    }
    document.appendChild(shortcutsNode);
  }

  public static void setBindings(List<KeyBindingAction> list, String fileName) {
    try {
      File file = new File(fileName);
      Document doc = createDocument();
      setBindings(list, doc);
      saveDocument(doc, file);
    } catch (Throwable throwable) {
      LOG.error(throwable);
    }
  }

}
