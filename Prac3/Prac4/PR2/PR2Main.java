import java.util.*;

public class PR2Main {
    public static void main(String[] args) {
        LightElementNode button = new LightElementNode("button", "inline", false);
        button.addClass("btn");
        button.addChild(new LightTextNode("Click me"));

        button.addEventListener("click", (eventType, element) ->
                System.out.println("Listener 1: button clicked. Tag = " + element.getTagName()));

        button.addEventListener("click", (eventType, element) ->
                System.out.println("Listener 2: second click handler worked."));

        button.addEventListener("mouseover", (eventType, element) ->
                System.out.println("Mouse over event fired."));

        System.out.println("OuterHTML:");
        System.out.println(button.outerHTML());

        System.out.println("\nTrigger click:");
        button.triggerEvent("click");

        System.out.println("\nTrigger mouseover:");
        button.triggerEvent("mouseover");

        System.out.println("\nTrigger dblclick:");
        button.triggerEvent("dblclick");
    }
}

abstract class LightNode {
    public abstract String outerHTML();
    public abstract String innerHTML();
}

class LightTextNode extends LightNode {
    private final String text;

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

interface EventListener {
    void handle(String eventType, LightElementNode element);
}

class LightElementNode extends LightNode {
    private final String tagName;
    private final String displayType;
    private final boolean selfClosing;
    private final List<String> cssClasses = new ArrayList<>();
    private final List<LightNode> children = new ArrayList<>();
    private final Map<String, List<EventListener>> listeners = new HashMap<>();

    public LightElementNode(String tagName, String displayType, boolean selfClosing) {
        this.tagName = tagName;
        this.displayType = displayType;
        this.selfClosing = selfClosing;
    }

    public String getTagName() {
        return tagName;
    }

    public void addClass(String className) {
        cssClasses.add(className);
    }

    public void addChild(LightNode node) {
        if (!selfClosing) {
            children.add(node);
        }
    }

    public void addEventListener(String eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void triggerEvent(String eventType) {
        System.out.println("Event triggered: " + eventType + " on <" + tagName + ">");
        List<EventListener> eventListeners = listeners.get(eventType);

        if (eventListeners == null || eventListeners.isEmpty()) {
            System.out.println("No listeners for this event.");
            return;
        }

        for (EventListener listener : eventListeners) {
            listener.handle(eventType, this);
        }
    }

    public String innerHTML() {
        StringBuilder sb = new StringBuilder();
        for (LightNode child : children) {
            sb.append(child.outerHTML());
        }
        return sb.toString();
    }

    public String outerHTML() {
        String classPart = cssClasses.isEmpty() ? "" : " class=\"" + String.join(" ", cssClasses) + "\"";

        if (selfClosing) {
            return "<" + tagName + classPart + "/>";
        }

        return "<" + tagName + classPart + ">" + innerHTML() + "</" + tagName + ">";
    }
}