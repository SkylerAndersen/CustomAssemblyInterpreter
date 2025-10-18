import java.util.HashMap;
import java.util.Stack;

public class SimulatorData {
    public Register R0;
    public Register R1;
    public Register R2;
    public Register R3;
    public Register R4;
    public Register R5;
    public Register R6;
    public Register R7;
    public Register R8;
    public Register R9;
    public Register TP;
    public Register CL;
    public Register ST;
    public Register CN;
    public Register PK;
    public Stack<Integer> PS;
    public HashMap<Integer,Integer> stack;

    public SimulatorData () {
        R0 = new Register();
        R1 = new Register();
        R2 = new Register();
        R3 = new Register();
        R4 = new Register();
        R5 = new Register();
        R6 = new Register();
        R7 = new Register();
        R8 = new Register();
        R9 = new Register();
        TP = new Register();
        CL = new Register();
        ST = new Register();
        CN = new Register();
        PK = new Register();
        PS = new Stack<>();
        stack = new HashMap<>(); // ironic, but makes more sense for asm
    }

    public static String getRegisterName (int number) {
        if (number == 10) {
            return "TP";
        } else if (number == 11) {
            return "CL";
        } else if (number == 12) {
            return "ST";
        } else if (number == 13) {
            return "PS";
        } else if (number == 14) {
            return "CN";
        } else if (number == 15) {
            return "PK";
        } else {
            return "R" + number;
        }
    }

    public static int getRegisterNumber (String name) {
        int number;
        if (name.equalsIgnoreCase("TP"))
            number = 10;
        else if (name.equalsIgnoreCase("CL"))
            number = 11;
        else if (name.equalsIgnoreCase("ST"))
            number = 12;
        else if (name.equalsIgnoreCase("PS"))
            number = 13;
        else if (name.equalsIgnoreCase("CN"))
            number = 14;
        else if (name.equalsIgnoreCase("PK"))
            number = 15;
        else
            number = Integer.parseInt(""+name.charAt(1));

        return number;
    }
}
