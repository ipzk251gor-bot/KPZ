package lab3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Lab3StructuralPatterns {

    public static void main(String[] args) {

        System.out.println("===== ADAPTER =====");
        Logger consoleLogger = new Logger();
        FileWriterCustom fileWriter = new FileWriterCustom();
        Logger fileLogger = new FileLoggerAdapter(fileWriter);

        consoleLogger.Log("Console message");
        consoleLogger.Warn("Warning message");
        consoleLogger.Error("Error message");

        fileLogger.Log("File log message");
        fileLogger.Warn("File warning message");
        fileLogger.Error("File error message");


        System.out.println("\n===== DECORATOR =====");

        Hero warrior = new Warrior();
        warrior = new SwordDecorator(warrior);
        warrior = new ArmorDecorator(warrior);
        warrior = new ArtifactDecorator(warrior);
        warrior.show();

        System.out.println();

        Hero mage = new Mage();
        mage = new ArtifactDecorator(mage);
        mage = new StaffDecorator(mage);
        mage.show();

        System.out.println();

        Hero paladin = new Paladin();
        paladin = new ArmorDecorator(paladin);
        paladin = new SwordDecorator(paladin);
        paladin.show();


        System.out.println("\n===== BRIDGE =====");

        Shape circleVector = new Circle(new VectorRenderer());
        Shape squareRaster = new Square(new RasterRenderer());
        Shape triangleVector = new Triangle(new VectorRenderer());

        circleVector.draw();
        squareRaster.draw();
        triangleVector.draw();


        System.out.println("\n===== PROXY =====");

        SmartTextReader textReader = new SmartTextReader();
        SmartTextChecker checker = new SmartTextChecker(textReader);
        checker.read("test.txt");

        System.out.println();

        SmartTextReaderLocker locker = new SmartTextReaderLocker(textReader, "secret.*");
        locker.read("secret.txt");
        locker.read("test.txt");


        System.out.println("\n===== COMPOSITE =====");

        LightElementNode div = new LightElementNode("div", DisplayType.BLOCK, ClosingType.PAIR);
        div.addCssClass("container");

        LightElementNode h2 = new LightElementNode("h2", DisplayType.BLOCK, ClosingType.PAIR);
        h2.addChild(new LightTextNode("Shopping List"));

        LightElementNode ul = new LightElementNode("ul", DisplayType.BLOCK, ClosingType.PAIR);

        LightElementNode li1 = new LightElementNode("li", DisplayType.BLOCK, ClosingType.PAIR);
        li1.addChild(new LightTextNode("Milk"));

        LightElementNode li2 = new LightElementNode("li", DisplayType.BLOCK, ClosingType.PAIR);
        li2.addChild(new LightTextNode("Bread"));

        LightElementNode li3 = new LightElementNode("li", DisplayType.BLOCK, ClosingType.PAIR);
        li3.addChild(new LightTextNode("Apples"));

        ul.addChild(li1);
        ul.addChild(li2);
        ul.addChild(li3);

        div.addChild(h2);
        div.addChild(ul);

        System.out.println("innerHTML:");
        System.out.println(div.innerHTML());

        System.out.println("\nouterHTML:");
        System.out.println(div.outerHTML());


        System.out.println("\n===== FLYWEIGHT =====");

        try {
            List<String> bookLines = readLinesFromFile("книга.txt");

            List<LightNode> htmlNodesWithoutFlyweight = buildHtmlFromBook(bookLines, false);
            List<LightNode> htmlNodesWithFlyweight = buildHtmlFromBook(bookLines, true);

            System.out.println("First generated elements:");
            int previewCount = Math.min(10, htmlNodesWithFlyweight.size());
            for (int i = 0; i < previewCount; i++) {
                System.out.println(htmlNodesWithFlyweight.get(i).outerHTML());
            }

            long memoryWithoutFlyweight = estimateMemoryWithoutFlyweight(bookLines);
            long memoryWithFlyweight = estimateMemoryWithFlyweight(bookLines);

            System.out.println();
            System.out.println("Estimated tree size without Flyweight: " + memoryWithoutFlyweight + " bytes");
            System.out.println("Estimated tree size with Flyweight: " + memoryWithFlyweight + " bytes");
            System.out.println("Saved memory: " + (memoryWithoutFlyweight - memoryWithFlyweight) + " bytes");

        } catch (IOException e) {
            System.out.println("Cannot read книга.txt");
        }
    }

    private static List<String> readLinesFromFile(String fileName) throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        reader.close();
        return lines;
    }

    private static List<LightNode> buildHtmlFromBook(List<String> lines, boolean useFlyweight) {
        List<LightNode> nodes = new ArrayList<LightNode>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.trim().isEmpty()) {
                continue;
            }

            String tagName;
            DisplayType displayType = DisplayType.BLOCK;
            ClosingType closingType = ClosingType.PAIR;

            if (i == 0) {
                tagName = "h1";
            } else if (line.startsWith(" ")) {
                tagName = "blockquote";
            } else if (line.length() < 20) {
                tagName = "h2";
            } else {
                tagName = "p";
            }

            LightElementNode node;

            if (useFlyweight) {
                node = new FlyweightLightElementNode(tagName, displayType, closingType);
            } else {
                node = new LightElementNode(tagName, displayType, closingType);
            }

            node.addChild(new LightTextNode(line));
            nodes.add(node);
        }

        return nodes;
    }

    private static long estimateMemoryWithoutFlyweight(List<String> lines) {
        long total = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.trim().isEmpty()) {
                continue;
            }

            String tagName;

            if (i == 0) {
                tagName = "h1";
            } else if (line.startsWith(" ")) {
                tagName = "blockquote";
            } else if (line.length() < 20) {
                tagName = "h2";
            } else {
                tagName = "p";
            }

            total += estimateStringMemory(tagName);
            total += 16;
            total += estimateStringMemory(line);
        }

        return total;
    }

    private static long estimateMemoryWithFlyweight(List<String> lines) {
        long total = 0;
        List<String> uniqueTags = new ArrayList<String>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.trim().isEmpty()) {
                continue;
            }

            String tagName;

            if (i == 0) {
                tagName = "h1";
            } else if (line.startsWith(" ")) {
                tagName = "blockquote";
            } else if (line.length() < 20) {
                tagName = "h2";
            } else {
                tagName = "p";
            }

            if (!uniqueTags.contains(tagName)) {
                uniqueTags.add(tagName);
            }

            total += 16;
            total += estimateStringMemory(line);
        }

        for (String tag : uniqueTags) {
            total += estimateStringMemory(tag);
        }

        return total;
    }

    private static long estimateStringMemory(String text) {
        if (text == null) {
            return 0;
        }
        return 40 + (long) text.length() * 2;
    }
}



class Logger {

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    public void Log(String message) {
        System.out.println(GREEN + "[LOG] " + message + RESET);
    }

    public void Error(String message) {
        System.out.println(RED + "[ERROR] " + message + RESET);
    }

    public void Warn(String message) {
        System.out.println(YELLOW + "[WARN] " + message + RESET);
    }
}



class FileWriterCustom {

    public void Write(String text) {
        System.out.print(text);
    }

    public void WriteLine(String text) {
        System.out.println(text);
    }
}



class FileLoggerAdapter extends Logger {

    private FileWriterCustom writer;

    public FileLoggerAdapter(FileWriterCustom writer) {
        this.writer = writer;
    }

    @Override
    public void Log(String message) {
        writer.WriteLine("[FILE LOG] " + message);
    }

    @Override
    public void Error(String message) {
        writer.WriteLine("[FILE ERROR] " + message);
    }

    @Override
    public void Warn(String message) {
        writer.WriteLine("[FILE WARN] " + message);
    }
}



interface Hero {
    void show();
}



class Warrior implements Hero {
    public void show() {
        System.out.println("Hero: Warrior");
    }
}



class Mage implements Hero {
    public void show() {
        System.out.println("Hero: Mage");
    }
}



class Paladin implements Hero {
    public void show() {
        System.out.println("Hero: Paladin");
    }
}



abstract class HeroDecorator implements Hero {

    protected Hero hero;

    public HeroDecorator(Hero hero) {
        this.hero = hero;
    }

    public void show() {
        hero.show();
    }
}



class SwordDecorator extends HeroDecorator {

    public SwordDecorator(Hero hero) {
        super(hero);
    }

    public void show() {
        super.show();
        System.out.println("- Sword");
    }
}



class ArmorDecorator extends HeroDecorator {

    public ArmorDecorator(Hero hero) {
        super(hero);
    }

    public void show() {
        super.show();
        System.out.println("- Armor");
    }
}



class ArtifactDecorator extends HeroDecorator {

    public ArtifactDecorator(Hero hero) {
        super(hero);
    }

    public void show() {
        super.show();
        System.out.println("- Artifact");
    }
}



class StaffDecorator extends HeroDecorator {

    public StaffDecorator(Hero hero) {
        super(hero);
    }

    public void show() {
        super.show();
        System.out.println("- Magic Staff");
    }
}



interface Renderer {
    void render(String shapeName);
}



class VectorRenderer implements Renderer {
    public void render(String shapeName) {
        System.out.println("Drawing " + shapeName + " as vector");
    }
}



class RasterRenderer implements Renderer {
    public void render(String shapeName) {
        System.out.println("Drawing " + shapeName + " as pixels");
    }
}



abstract class Shape {

    protected Renderer renderer;

    public Shape(Renderer renderer) {
        this.renderer = renderer;
    }

    public abstract void draw();
}



class Circle extends Shape {

    public Circle(Renderer renderer) {
        super(renderer);
    }

    public void draw() {
        renderer.render("Circle");
    }
}



class Square extends Shape {

    public Square(Renderer renderer) {
        super(renderer);
    }

    public void draw() {
        renderer.render("Square");
    }
}



class Triangle extends Shape {

    public Triangle(Renderer renderer) {
        super(renderer);
    }

    public void draw() {
        renderer.render("Triangle");
    }
}



class SmartTextReader {

    public char[][] read(String path) {
        try {
            List<char[]> lines = new ArrayList<char[]>();
            BufferedReader reader = new BufferedReader(new FileReader(path));

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.toCharArray());
            }

            reader.close();
            return lines.toArray(new char[0][]);

        } catch (IOException e) {
            System.out.println("File read error: " + path);
            return null;
        }
    }
}



class SmartTextChecker {

    private SmartTextReader reader;

    public SmartTextChecker(SmartTextReader reader) {
        this.reader = reader;
    }

    public char[][] read(String path) {
        System.out.println("Opening file: " + path);

        char[][] text = reader.read(path);

        if (text == null) {
            System.out.println("Read failed.");
            System.out.println("Closing file: " + path);
            return null;
        }

        int lineCount = text.length;
        int charCount = 0;

        for (int i = 0; i < text.length; i++) {
            charCount += text[i].length;
        }

        System.out.println("Successfully read file: " + path);
        System.out.println("Lines: " + lineCount);
        System.out.println("Characters: " + charCount);
        System.out.println("Closing file: " + path);

        return text;
    }
}



class SmartTextReaderLocker {

    private SmartTextReader reader;
    private String restrictedPattern;

    public SmartTextReaderLocker(SmartTextReader reader, String restrictedPattern) {
        this.reader = reader;
        this.restrictedPattern = restrictedPattern;
    }

    public char[][] read(String path) {
        if (path.matches(restrictedPattern)) {
            System.out.println("Access denied!");
            return null;
        }

        System.out.println("Access allowed: " + path);
        return reader.read(path);
    }
}



abstract class LightNode {
    public abstract String outerHTML();
    public abstract String innerHTML();
}



class LightTextNode extends LightNode {

    private String text;

    public LightTextNode(String text) {
        this.text = text;
    }

    public String outerHTML() {
        return text;
    }

    public String innerHTML() {
        return text;
    }
}



enum DisplayType {
    BLOCK,
    INLINE
}



enum ClosingType {
    SINGLE,
    PAIR
}



class LightElementNode extends LightNode {

    protected String tagName;
    protected DisplayType displayType;
    protected ClosingType closingType;
    protected List<String> cssClasses;
    protected List<LightNode> children;

    public LightElementNode(String tagName, DisplayType displayType, ClosingType closingType) {
        this.tagName = tagName;
        this.displayType = displayType;
        this.closingType = closingType;
        this.cssClasses = new ArrayList<String>();
        this.children = new ArrayList<LightNode>();
    }

    public void addChild(LightNode node) {
        children.add(node);
    }

    public void addCssClass(String cssClass) {
        cssClasses.add(cssClass);
    }

    public int getChildrenCount() {
        return children.size();
    }

    protected String renderClasses() {
        if (cssClasses.isEmpty()) {
            return "";
        }

        StringBuilder classes = new StringBuilder();
        classes.append(" class=\"");

        for (int i = 0; i < cssClasses.size(); i++) {
            classes.append(cssClasses.get(i));
            if (i < cssClasses.size() - 1) {
                classes.append(" ");
            }
        }

        classes.append("\"");
        return classes.toString();
    }

    public String innerHTML() {
        StringBuilder html = new StringBuilder();

        for (int i = 0; i < children.size(); i++) {
            html.append(children.get(i).outerHTML());
        }

        return html.toString();
    }

    public String outerHTML() {
        if (closingType == ClosingType.SINGLE) {
            return "<" + tagName + renderClasses() + "/>";
        }

        return "<" + tagName + renderClasses() + ">" +
                innerHTML() +
                "</" + tagName + ">";
    }
}



class LightElementFlyweightState {

    private String tagName;
    private DisplayType displayType;
    private ClosingType closingType;

    public LightElementFlyweightState(String tagName, DisplayType displayType, ClosingType closingType) {
        this.tagName = tagName;
        this.displayType = displayType;
        this.closingType = closingType;
    }

    public String getTagName() {
        return tagName;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public ClosingType getClosingType() {
        return closingType;
    }
}



class LightElementFlyweightFactory {

    private static final Map<String, LightElementFlyweightState> states =
            new HashMap<String, LightElementFlyweightState>();

    public static LightElementFlyweightState getState(String tagName, DisplayType displayType, ClosingType closingType) {
        String key = tagName + "|" + displayType + "|" + closingType;

        if (!states.containsKey(key)) {
            states.put(key, new LightElementFlyweightState(tagName, displayType, closingType));
        }

        return states.get(key);
    }
}



class FlyweightLightElementNode extends LightElementNode {

    private LightElementFlyweightState state;

    public FlyweightLightElementNode(String tagName, DisplayType displayType, ClosingType closingType) {
        super(tagName, displayType, closingType);
        this.state = LightElementFlyweightFactory.getState(tagName, displayType, closingType);
    }

    @Override
    public String outerHTML() {
        if (state.getClosingType() == ClosingType.SINGLE) {
            return "<" + state.getTagName() + renderClasses() + "/>";
        }

        return "<" + state.getTagName() + renderClasses() + ">" +
                innerHTML() +
                "</" + state.getTagName() + ">";
    }
}