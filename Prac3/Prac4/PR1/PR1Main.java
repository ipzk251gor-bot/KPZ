import java.util.*;

public class PR1Main {
    public static void main(String[] args) {
        System.out.println("===== TASK 1: CHAIN OF RESPONSIBILITY =====");
        runTask1();

        System.out.println("\n===== TASK 2: MEDIATOR =====");
        runTask2();

        System.out.println("\n===== TASK 5: MEMENTO =====");
        runTask5();
    }

    // ---------------- TASK 1 ----------------
    private static void runTask1() {
        Scanner scanner = new Scanner(System.in);

        SupportHandler general = new GeneralSupportHandler();
        SupportHandler billing = new BillingSupportHandler();
        SupportHandler technical = new TechnicalSupportHandler();
        SupportHandler vip = new VIPSupportHandler();

        general.setNext(billing);
        billing.setNext(technical);
        technical.setNext(vip);

        while (true) {
            System.out.println("\n--- User Support Menu ---");
            boolean handled = general.handle(scanner);

            if (handled) {
                System.out.println("Support level found. Menu finished.");
                break;
            }

            System.out.print("No support level found. Repeat menu? (yes/no): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (!answer.equals("yes")) {
                System.out.println("Support menu closed.");
                break;
            }
        }
    }

    // ---------------- TASK 2 ----------------
    private static void runTask2() {
        Runway runway1 = new Runway();
        Runway runway2 = new Runway();

        Aircraft aircraft1 = new Aircraft("Boeing-737");
        Aircraft aircraft2 = new Aircraft("Airbus-A320");
        Aircraft aircraft3 = new Aircraft("AN-225");

        new CommandCentre(
                new Runway[]{runway1, runway2},
                new Aircraft[]{aircraft1, aircraft2, aircraft3}
        );

        aircraft1.land();
        aircraft2.land();
        aircraft3.land();

        aircraft1.takeOff();
        aircraft3.land();
    }

    // ---------------- TASK 5 ----------------
    private static void runTask5() {
        TextDocument document = new TextDocument();
        TextEditor editor = new TextEditor(document);
        History history = new History();

        editor.write("First version of document");
        history.push(editor.save());
        editor.show();

        editor.write("Second version of document");
        history.push(editor.save());
        editor.show();

        editor.write("Third version of document");
        editor.show();

        DocumentMemento previous = history.pop();
        if (previous != null) {
            editor.restore(previous);
            editor.show();
        }

        previous = history.pop();
        if (previous != null) {
            editor.restore(previous);
            editor.show();
        }
    }
}

// ================= TASK 1 CLASSES =================
interface SupportHandler {
    void setNext(SupportHandler next);
    boolean handle(Scanner scanner);
}

abstract class AbstractSupportHandler implements SupportHandler {
    protected SupportHandler next;

    public void setNext(SupportHandler next) {
        this.next = next;
    }

    public boolean handle(Scanner scanner) {
        if (canHandle(scanner)) {
            return true;
        }
        if (next != null) {
            return next.handle(scanner);
        }
        return false;
    }

    protected abstract boolean canHandle(Scanner scanner);
}

class GeneralSupportHandler extends AbstractSupportHandler {
    protected boolean canHandle(Scanner scanner) {
        System.out.print("1. Do you need general information or consultation? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (answer.equals("yes")) {
            System.out.println("Redirecting to Level 1: General Support.");
            return true;
        }
        return false;
    }
}

class BillingSupportHandler extends AbstractSupportHandler {
    protected boolean canHandle(Scanner scanner) {
        System.out.print("2. Do you have a billing or payment issue? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (answer.equals("yes")) {
            System.out.println("Redirecting to Level 2: Billing Support.");
            return true;
        }
        return false;
    }
}

class TechnicalSupportHandler extends AbstractSupportHandler {
    protected boolean canHandle(Scanner scanner) {
        System.out.print("3. Do you have a technical issue with system or device? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (answer.equals("yes")) {
            System.out.println("Redirecting to Level 3: Technical Support.");
            return true;
        }
        return false;
    }
}

class VIPSupportHandler extends AbstractSupportHandler {
    protected boolean canHandle(Scanner scanner) {
        System.out.print("4. Are you a VIP client or do you have a critical issue? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (answer.equals("yes")) {
            System.out.println("Redirecting to Level 4: VIP Support.");
            return true;
        }
        return false;
    }
}

// ================= TASK 2 CLASSES =================
class CommandCentre {
    private final List<Runway> runways = new ArrayList<>();

    public CommandCentre(Runway[] runways, Aircraft[] aircrafts) {
        for (Runway runway : runways) {
            this.runways.add(runway);
            runway.setCommandCentre(this);
        }

        for (Aircraft aircraft : aircrafts) {
            aircraft.setCommandCentre(this);
        }
    }

    public void requestLanding(Aircraft aircraft) {
        System.out.println("Aircraft " + aircraft.getName() + " requests landing.");
        for (Runway runway : runways) {
            if (!runway.isBusy()) {
                runway.assignAircraft(aircraft);
                aircraft.setCurrentRunway(runway);
                aircraft.setTakingOff(false);
                System.out.println("Aircraft " + aircraft.getName() + " has landed.");
                return;
            }
        }
        System.out.println("No free runway for landing.");
    }

    public void requestTakeOff(Aircraft aircraft) {
        System.out.println("Aircraft " + aircraft.getName() + " requests takeoff.");
        Runway runway = aircraft.getCurrentRunway();

        if (runway == null) {
            System.out.println("Aircraft " + aircraft.getName() + " is not on any runway.");
            return;
        }

        aircraft.setTakingOff(true);
        runway.releaseAircraft();
        aircraft.setCurrentRunway(null);
        System.out.println("Aircraft " + aircraft.getName() + " has taken off.");
    }
}

class Aircraft {
    private final String name;
    private CommandCentre commandCentre;
    private Runway currentRunway;
    private boolean isTakingOff;

    public Aircraft(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCommandCentre(CommandCentre commandCentre) {
        this.commandCentre = commandCentre;
    }

    public Runway getCurrentRunway() {
        return currentRunway;
    }

    public void setCurrentRunway(Runway currentRunway) {
        this.currentRunway = currentRunway;
    }

    public void setTakingOff(boolean takingOff) {
        isTakingOff = takingOff;
    }

    public void land() {
        System.out.println("Aircraft " + name + " is landing.");
        commandCentre.requestLanding(this);
    }

    public void takeOff() {
        System.out.println("Aircraft " + name + " is taking off.");
        commandCentre.requestTakeOff(this);
    }
}

class Runway {
    private final UUID id = UUID.randomUUID();
    private Aircraft busyWithAircraft;
    private CommandCentre commandCentre;

    public void setCommandCentre(CommandCentre commandCentre) {
        this.commandCentre = commandCentre;
    }

    public boolean isBusy() {
        return busyWithAircraft != null;
    }

    public void assignAircraft(Aircraft aircraft) {
        this.busyWithAircraft = aircraft;
        highlightRed();
    }

    public void releaseAircraft() {
        this.busyWithAircraft = null;
        highlightGreen();
    }

    public void highlightRed() {
        System.out.println("Runway " + id + " is busy!");
    }

    public void highlightGreen() {
        System.out.println("Runway " + id + " is free!");
    }
}

// ================= TASK 5 CLASSES =================
class TextDocument {
    private String content = "";

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

class DocumentMemento {
    private final String content;

    public DocumentMemento(String content) {
        this.content = content;
    }

    public String getSavedContent() {
        return content;
    }
}

class TextEditor {
    private final TextDocument document;

    public TextEditor(TextDocument document) {
        this.document = document;
    }

    public void write(String text) {
        document.setContent(text);
    }

    public void show() {
        System.out.println("Document content: " + document.getContent());
    }

    public DocumentMemento save() {
        System.out.println("Saving state...");
        return new DocumentMemento(document.getContent());
    }

    public void restore(DocumentMemento memento) {
        System.out.println("Restoring state...");
        document.setContent(memento.getSavedContent());
    }
}

class History {
    private final Stack<DocumentMemento> history = new Stack<>();

    public void push(DocumentMemento memento) {
        history.push(memento);
    }

    public DocumentMemento pop() {
        if (history.isEmpty()) {
            return null;
        }
        return history.pop();
    }
}