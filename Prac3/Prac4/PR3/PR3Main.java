public class PR3Main {
    public static void main(String[] args) {
        LightImageNode fileImage = new LightImageNode("images/photo.png");
        LightImageNode webImage = new LightImageNode("https://example.com/picture.jpg");

        System.out.println("First image:");
        System.out.println(fileImage.outerHTML());
        fileImage.loadImage();

        System.out.println();

        System.out.println("Second image:");
        System.out.println(webImage.outerHTML());
        webImage.loadImage();
    }
}

abstract class LightNode {
    public abstract String outerHTML();
    public abstract String innerHTML();
}

interface ImageLoadStrategy {
    void load(String href);
}

class FileImageLoadStrategy implements ImageLoadStrategy {
    public void load(String href) {
        System.out.println("Loading image from file system: " + href);
    }
}

class NetworkImageLoadStrategy implements ImageLoadStrategy {
    public void load(String href) {
        System.out.println("Loading image from network: " + href);
    }
}

class LightImageNode extends LightNode {
    private final String href;
    private final ImageLoadStrategy strategy;

    public LightImageNode(String href) {
        this.href = href;

        if (href.startsWith("http://") || href.startsWith("https://")) {
            this.strategy = new NetworkImageLoadStrategy();
        } else {
            this.strategy = new FileImageLoadStrategy();
        }
    }

    public void loadImage() {
        strategy.load(href);
    }

    public String outerHTML() {
        return "<img src=\"" + href + "\" />";
    }

    public String innerHTML() {
        return "";
    }
}