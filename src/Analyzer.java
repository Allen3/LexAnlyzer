import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Allen on 2016/3/20.
 */
public class Analyzer {
    private final static String FILE_NAME = "input.cpp";
    private final static String[] KEY_WORDS = {"void", "int", "double", "float", "true", "false"};

    private String rawString;

    private List<Unit> resultList;
    private List<String> symbolList;
    private List<String> constantList;
    private Map<Integer, String> signCodeMap;

    public Analyzer() {
        rawString = null;
        resultList = new ArrayList<>();
        symbolList = new ArrayList<>();
        constantList = new ArrayList<>();
        signCodeMap = new HashMap<>();
    }

    private String readFile(String fileName) {
        try {
            byte[] encodedBytes = Files.readAllBytes(Paths.get(fileName));
            rawString = new String(encodedBytes, Charset.defaultCharset());
            return rawString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Analyzer execute() {

        int index = 0, status = 0, newLine = 1;
        StringBuilder token = new StringBuilder();

        while (index < rawString.length()) {
            char ch = rawString.charAt(index);
            int chType = Util.checkCharType(ch);

            switch (status) {
                case 0:
                    if (chType == Util.SEPARATOR) {
                        if (ch == '\n' && System.getProperty("line.separator").length() == 2) {
                            newLine++;
                        } else if (ch != ' ' && System.getProperty("line.separator").length() == 1) {
                            newLine++;
                        }

                    } else if (chType == Util.LETTER) {
                        status = 1;
                        token.append(ch);
                    } else if (chType == Util.NUMBER) {
                        status = 3;
                        token.append(ch);
                    } else if (chType == Util.LESS) {
                        status = 5;
                    } else if (chType == Util.GREATER) {
                        status = 8;
                    } else if (chType == Util.EQUAL) {
                        status = 11;
                    } else if (chType == Util.NOT) {
                        status = 14;
                    } else if (chType == Util.ADD) {
                        status = 17;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "+");
                        }
                        status = 0;
                    } else if (chType == Util.SUB) {
                        status = 18;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "-");
                        }
                        status = 0;
                    } else if (chType == Util.MUL) {
                        status = 19;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "*");
                        }
                        status = 0;
                    } else if (chType == Util.DIV) {
                        status = 20;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "/");
                        }
                        status = 0;
                    } else if (chType == Util.LPAR) {
                        status = 21;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "(");
                        }
                        status = 0;
                    } else if (chType == Util.RPAR) {
                        status = 22;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, ")");
                        }
                        status = 0;
                    } else if (chType == Util.COM) {
                        status = 23;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, ",");
                        }
                        status = 0;
                    } else if (chType == Util.SEM) {
                        status = 24;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, ";");
                        }
                        status = 0;
                    } else if (chType == Util.LBR) {
                        status = 25;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "{");
                        }
                        status = 0;
                    } else if (chType == Util.RBR) {
                        status = 26;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "}");
                        }
                        status = 0;
                    } else if (chType == Util.ILLEGAL) {
                        status = 27;
                        //Handle
                        errorReport(newLine);
                        status = 0;
                    }

                    break;

                case 1:
                    if (chType == Util.LETTER || chType == Util.NUMBER) {
                        token.append(ch);
                    } else {
                        //Handle
                        int keyWordMatch = matchKeyWord(token.toString());
                        if (keyWordMatch == -1) {
                            int id = symbolList.indexOf(token.toString());
                            if (id == -1) {
                                symbolList.add(token.toString());
                                resultList.add(new Unit(keyWordMatch, symbolList.size() - 1));
                            } else {
                                resultList.add(new Unit(keyWordMatch, id));
                            }
                        } else {
                            resultList.add(new Unit(keyWordMatch));
                        }

                        status = 0;
                        token = new StringBuilder();
                        index--;
                    }
                    break;

                case 3:
                    if (chType == Util.NUMBER) {
                        token.append(ch);
                    } else {
                        //Handle
                        int id = constantList.indexOf(token.toString());
                        if (id == -1) {
                            constantList.add(token.toString());
                            resultList.add(new Unit(-2, constantList.size() - 1));
                        } else {
                            resultList.add(new Unit(-2, id));
                        }

                        status = 0;
                        token = new StringBuilder();
                        index--;
                    }
                    break;

                case 5:
                    if (chType == Util.EQUAL) {
                        status = 6;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "<=");
                        }
                        status = 0;
                    } else {
                        status = 7;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "<");
                        }
                        status = 0;
                        index--;
                    }
                    break;

                case 8:
                    if (chType == Util.EQUAL) {
                        status = 9;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, ">=");
                        }
                        status = 0;
                    } else {
                        status = 10;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, ">");
                        }
                        status = 0;
                        index--;
                    }
                    break;

                case 11:
                    if (chType == Util.EQUAL) {
                        status = 12;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "==");
                        }
                        status = 0;
                    } else {
                        status = 13;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        if (signCodeMap.get(status + KEY_WORDS.length) == null) {
                            signCodeMap.put(status + KEY_WORDS.length, "=");
                        }
                        status = 0;
                        index--;
                    }
                    break;

                case 14:
                    if (chType == Util.EQUAL) {
                        status = 15;
                        //Handle
                        resultList.add(new Unit(status + KEY_WORDS.length));
                        status = 0;
                    } else {
                        status = 16;
                        //Handle
                        errorReport(newLine);
                        status = 0;
                    }
                    break;

            }

            index++;

        }

        return this;
    }

    private void errorReport(int lineNumber) {
        System.out.println("Line: " + lineNumber + ", Error Detected!");
    }

    private int matchKeyWord(String token) {
        for (int i = 0; i < KEY_WORDS.length; i++) {
            if (KEY_WORDS[i].equals(token)) {
                return i;
            }
        }
        return -1;
    }

    private void generateXML() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = docFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();
            Element rootElement = doc.createElement("lex");
            doc.appendChild(rootElement);

            // Add stream node.
            Element stream = doc.createElement("stream");
            rootElement.appendChild(stream);

            for (int i = 0; i < resultList.size(); i++) {
                Unit unit = resultList.get(i);
                Element word = doc.createElement("word");

                Attr raw = doc.createAttribute("raw");
                if (unit.code == -1) {
                    raw.setValue(symbolList.get(unit.pos));
                } else if (unit.code == -2) {
                    raw.setValue(constantList.get(unit.pos));
                } else {
                    raw.setValue(signCodeMap.get(unit.code));
                }
                word.setAttributeNode(raw);

                Element code = doc.createElement("code");
                code.appendChild(doc.createTextNode(Integer.toHexString(unit.code)));
                word.appendChild(code);

                Element pos = doc.createElement("pos");
                pos.appendChild(doc.createTextNode(Integer.toHexString(unit.pos)));
                word.appendChild(pos);

                stream.appendChild(word);
            }

            // Add Symbols node
            Element symbols = doc.createElement("symbols");
            rootElement.appendChild(symbols);

            for (String symbolString : symbolList) {
                Element symbol = doc.createElement("symbol");
                symbol.appendChild(doc.createTextNode(symbolString));
                symbols.appendChild(symbol);
            }

            // Add Constants node
            Element constants = doc.createElement("constants");
            rootElement.appendChild(constants);

            for (String constantString : constantList) {
                Element constant = doc.createElement("constant");
                constant.appendChild(doc.createTextNode(constantString));
                constants.appendChild(constant);
            }

            // Output to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File("lex.xml"));

            transformer.transform(source, streamResult);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        Analyzer analyzer = new Analyzer();
        analyzer.readFile(args[0]);
        analyzer.execute().generateXML();
    }

}
