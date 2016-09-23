package kawaiiklash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import static kawaiiklash.Utility.fail;
import org.newdawn.slick.SlickException;

/**
 * StaXParser XML reader.
 *
 * @author Jeff Niu
 * @version 28 February 2015
 */
public class Parser {

    /**
     * The {@code Parser} is a <b>singleton</b>, so only one instance of
     * this class exists. This is so that a centralized controller of
     * {@code List<SpriteConfiguration>} may be. In other words, there is
     * only one {@link #cache cache}.
     */
    private static Parser parser = null;

    /**
     * Get the single instance of the {@code Parser}.
     *
     * @return the single instance
     */
    public static Parser get() {
        if (parser == null) {
            parser = new Parser();
        }
        return parser;
    }

    /**
     * This method determines the length of a {@code xml} file in terms of
     * the number of lines. This is so that a preset length to the
     * {@code List<SpriteConfiguration}s may be set to increase
     * performance. If any error occurs, it is not a big deal, so return
     * {@code 0}.
     *
     * @param url the {@code URL} link to the {@code xml} file
     * @return
     */
    private static int fileLengthOf(URL url) {
        int length = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(url.toURI())));
            while (reader.readLine() != null) {
                length++;
            }
        } catch (FileNotFoundException | URISyntaxException ex) {
            return 0;
        } catch (IOException ex) {
            return 0;
        }
        return length;
    }

    /**
     * The following is a {@code HashMap} cache of the {@code List}
     * collections of {@code SpriteConfiguration} objects obtained from
     * reading a {@code data.xml} for those {@code Object}s that
     * necessitate {@code SpriteSheet}s. This cache is so that the file
     * does not have to be read each time. It is initialized the size of
     * the {@code Package} because it is anticipated of no more than one
     * {@code data.xml} per class.
     */
    private final HashMap<String, List<SpriteConfiguration>> cache = new HashMap<>(Package.getPackages().length);

    private final ScriptEngineManager mgr;
    private final ScriptEngine engine;

    /**
     * Private constructor to prevent any other class from instantiating
     * this class.
     */
    private Parser() {
        mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");
    }

    /**
     * XML parser for the data.xml file of the entities. Reads an XML file
     * for all the configuration parameters of a particular Sprite of a
     * particular SpriteSheet. The standard format of the data.xml requires
     * that each element name be "i". Dynamically creates the
     * configurations.
     *
     * @param dataFile the filepath for the data.xml
     * @return a List of the SpriteConfiguration objects
     */
    public List<SpriteConfiguration> readDataXML(String dataFile) {
        if (cache.get(dataFile) != null) {
            return cache.get(dataFile);
        }
        URL url = getClass().getClassLoader().getResource(dataFile);
        List<SpriteConfiguration> configs = new ArrayList<>(fileLengthOf(url));
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = url.openStream();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            SpriteConfiguration config = null;
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    @SuppressWarnings("unchecked")
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    config = new SpriteConfiguration();
                    while (attributes.hasNext()) {
                        Attribute attribute = attributes.next();
                        Method method = null;
                        try {
                            method = SpriteConfiguration.class.getMethod(attribute.getName().toString(), String.class);
                        } catch (NoSuchMethodException ex) {
                            fail("Could not find method: " + attribute.getName().toString() + ", (String s)", ex);
                        }
                        if (method != null) {
                            String value = attribute.getValue();
                            try {
                                method.invoke(config, engine.eval(value).toString());
                            } catch (InvocationTargetException ex) {
                                fail(String.format("Could not invoke SpriteConfiguration method: %s, %s", method, dataFile), ex);
                            } catch (ScriptException ex) {
                                try {
                                    method.invoke(config, value);
                                } catch (InvocationTargetException exc) {
                                    fail(String.format("Could not invoke method: &s, &s", method, dataFile), exc);
                                }
                            }
                        }
                    }
                }
                if (event.isEndElement()) {
                    configs.add(config);
                }
            }
        } catch (FileNotFoundException ex) {
            fail("Could not find the Sprite data file: " + dataFile, ex);
        } catch (IOException ex) {
            fail("Could not open the stream from the data file:: " + dataFile, ex);
        } catch (XMLStreamException ex) {
            fail("Something went wrong when reading the XML events", ex);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            fail("Could not access the method", ex);
        }

        // Due to the way the data.xml files are formatted
        // <i>
        // <i ...attributes... />
        // <i/>
        // The last end element is causing the last SpriteConfiguration to 
        // be duplicated, so remove that
        configs.remove(configs.size() - 1);
        cache.put(dataFile, configs);
        return configs;
    }

    /**
     * This method will open and read the level.xml files. Upon reading
     * each event, it will create the appropriate Entity object. By
     * default, an Entity will have its direction set to some value and may
     * be overrode by a subclass constructor. Then, each subclass
     * constructor will initialize the Entity to some state. However, the
     * file may specifically ask for the method to override all that to
     * specify specifically a direction and state. Finally, all speed and
     * acceleration values are dealt with in the Entity subclass. The
     * primary function of this method is to dynamically create the
     * instances of the Entities and set their positions. Dynamically read
     * a level.xml and return all specified Objects.
     *
     * @param game
     * @param levelFile
     * @return
     * @throws SlickException
     */
    public List<Object> loadLevel(Game game, String levelFile) throws SlickException {
        URL url = getClass().getClassLoader().getResource(levelFile);
        if (url == null) {
            throw new SlickException("Cannot find file: " + levelFile);
        }
        int fileLength = fileLengthOf(url);
        List<Object> objects = new ArrayList<>(fileLength);
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream inputStream = url.openStream();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
            Package[] packages = Package.getPackages();
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                Object object = null;
                String objectName;
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    objectName = startElement.getName().toString();
                    Class<?> cls = null;
                    Constructor<?> ctor = null;
                    for (Package p : packages) {
                        String packageName = p.getName();
                        String tentative = packageName + "." + objectName;
                        try {
                            cls = Class.forName(tentative);
                            ctor = cls.getConstructor(Game.class);
                        } catch (ClassNotFoundException | NoSuchMethodException | NoClassDefFoundError ex) {
                            continue;
                        }
                        break;
                    }
                    if (cls != null && ctor != null) {
                        try {
                            object = ctor.newInstance(game);
                        } catch (InvocationTargetException | InstantiationException ex) {
                            throw new SlickException("Could not invoke Object constructor: " + ctor, ex);
                        }
                        if (object != null) {
                            @SuppressWarnings("unchecked")
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String methodName = attribute.getName().toString();
                                String param = attribute.getValue();
                                Method method = null;
                                try {
                                    method = object.getClass().getMethod(methodName, String.class);
                                    if (method == null) {
                                        throw new NoSuchMethodException("Object method not found");
                                    }
                                } catch (NoSuchMethodException ex) {
                                    throw new SlickException("Failed to find method: " + method, ex);
                                }
                                try {
                                    method.invoke(object, engine.eval(param).toString());
                                } catch (InvocationTargetException ex) {
                                    throw new SlickException("Could not invoke method: " + method + ", param: " + param, ex);
                                } catch (ScriptException ex) {
                                    try {
                                        method.invoke(object, param);
                                    } catch (IllegalArgumentException | InvocationTargetException exc) {
                                        throw new SlickException("Could not invoke method: " + method + ", param: " + param, exc);
                                    }
                                }
                            }
                            objects.add(object);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new SlickException("Level file not found: " + levelFile, ex);
        } catch (SecurityException ex) {
            throw new SlickException("Cannot access constructor", ex);
        } catch (IllegalAccessException ex) {
            throw new SlickException("Access to constructor restricted", ex);
        } catch (XMLStreamException ex) {
            throw new SlickException("Error occured while reading XML events from xml file", ex);
        }
        return objects;
    }

    /**
     * Load audio files in the same sort of manner.
     *
     * @param ref
     * @return
     */
    public List<String> getAudioFiles(String ref) {
        URL url = getClass().getClassLoader().getResource(ref);
        int fileLength = fileLengthOf(url);
        List<String> files = new ArrayList<>(fileLength);
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            InputStream in = url.openStream();
            XMLEventReader eventReader = factory.createXMLEventReader(in);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    @SuppressWarnings("unchecked")
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    while (attributes.hasNext()) {
                        Attribute attribute = attributes.next();
                        files.add(attribute.getValue());
                    }
                }
            }
        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return files;
    }
}
