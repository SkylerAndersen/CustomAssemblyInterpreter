import java.util.ArrayList;
import java.util.HashMap;

public class CustomAssembler {
    public static String[] assembleInstructions (String[] text) {
        if (text.length == 0)
            return text;

        text = cleanup(text);
        String[] instructions = new String[text.length];

        // convert
        for (int i = 0; i < instructions.length; i++) {
            instructions[i] = convertLine(text[i]);
        }

        return instructions;
    }

    private static String convertLine (String line) {
        // get the parts and types for the instruction + bitfield
        String[] parts = removeParenthesis(line).split(" ");
        CustomAssemblerCodes codes = CustomAssemblerCodes.valueOf(parts[0]);
        String[] types = removeParenthesis(codes.getOrder().toLowerCase()).split(" ");
        StringBuilder bitfield = new StringBuilder(codes.getBinary());
        if (parts[0].equalsIgnoreCase("RTN")) {
            String[] temp = {"RTN",""};
            parts = temp;
        }


        // create a conversion table from types to binary
        HashMap<String,String> conversions = new HashMap<>();
        for (int i = 0; i < types.length; i++)
            conversions.put(types[i],convertItem(parts[i+1],types[i]));

        // fill in bitfield parts for each possible type
        tryReplacing(conversions,bitfield,"offset");
        tryReplacing(conversions,bitfield,"imm");
        tryReplacing(conversions,bitfield,"rs");
        tryReplacing(conversions,bitfield,"rt");
        tryReplacing(conversions,bitfield,"rd");

        return bitfield.toString();
    }

    private static void tryReplacing (HashMap<String,String> conversions, StringBuilder bitfield, String type) {
        int i = bitfield.indexOf(type);
        if (i != -1) {
            bitfield.replace(i,i+type.length(),conversions.get(type));
        }
    }

    private static String convertItem (String item, String type) {
        if (type.isBlank())
            return "";

        if (type.charAt(0) == 'r') { // register
            return CustomAssemblerCodes.valueOf(item).getBinary();
        } else if (type.charAt(0) == 'o') { // offset
            int decimal = Integer.parseInt(item);

            // get binary for magnitude
            StringBuilder binary = convert16BitToAsciiBin(decimal);

            // offsets can only be as big as 12 bits, so remove empty upper bits
            binary.delete(0,4);

            // add sign
            if (decimal < 0)
                binary.setCharAt(0,'1');

            return binary.toString();
        } else { // immediate
            int decimal = Integer.parseInt(item);

            // get binary for magnitude
            StringBuilder binary = convert16BitToAsciiBin(decimal);

            return binary.toString();
        }
    }

    private static StringBuilder convert16BitToAsciiBin (int value) {
        StringBuilder binary = new StringBuilder();
        int max = 32768;
        boolean negative = value < 0;
        if (negative) {
            int twoToThe16 = 65536;
            value = twoToThe16 + value;
        }
        while (max > 0) {
            if (value >= max) {
                binary.append('1');
                value -= max;
            } else {
                binary.append('0');
            }
            max /= 2;
        }
        return binary;
    }

    private static String removeParenthesis (String line) {
        StringBuilder output = new StringBuilder();
        line = line.strip();

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '(') {
                output.append(' ');
            } else if (line.charAt(i) != ')') {
                output.append(line.charAt(i));
            }
        }

        return output.toString();
    }

    private static String[] cleanup (String[] instructions) {
        // strip all lines
        for (int i = 0; i < instructions.length; i++) {
            instructions[i] = instructions[i].strip();
        }

        // remove all comments
        ArrayList<String> reduced = new ArrayList<>();
        for (String line : instructions) {
            boolean comment = line.length() < 2 || (line.charAt(0) == '/' && line.charAt(1) == '/');
            int end = (line.indexOf('/') != -1) ? line.indexOf('/') : line.length();

            if (!comment)
                reduced.add(line.substring(0,end).strip());
        }
        instructions = reduced.toArray(new String[0]);

        // create a hashmap with the line numbers of the labeled sections of code
        HashMap<String,Integer> lineNumbers = getLineNumbers(instructions);

        // find all jumps, and replace destinations with line numbers
        for (int i = 0; i < instructions.length; i++) {
            // check if it is a jump
            boolean isJump = (instructions[i].length() > 3 && (instructions[i].charAt(0) == 'j' ||
                    instructions[i].charAt(0) == 'J') && instructions[i].charAt(3) == ' ');

            // replace word with line number of destination
            if (isJump) {
                int targetPos = instructions[i].lastIndexOf(' ')+1;
                String label = instructions[i].substring(targetPos);
                instructions[i] = instructions[i].substring(0,targetPos) + lineNumbers.get(label);
            }
        }

        // remove labels
        reduced = new ArrayList<>();
        for (String line : instructions) {
            boolean isLabel = line.isEmpty() || line.charAt(line.length()-1) == ':';

            if (!isLabel)
                reduced.add(line);
        }
        instructions = reduced.toArray(new String[0]);

        return instructions;
    }

    private static HashMap<String,Integer> getLineNumbers (String[] fileLines) {
        HashMap<String,Integer> lineNumbers = new HashMap<>();

        int currLine = 0;
        boolean labelPlaced = false;
        String label = "";
        for (String line : fileLines) {
            line = line.strip();

            // update current line number
            boolean isInstruction = line.length() > 1 &&
                    line.charAt(0) != '/' &&
                    line.charAt(line.length()-1) != ':';
            if (isInstruction)
                currLine++;

            // if a label has been placed, store the line number of the label, reset label placed
            if (isInstruction && labelPlaced) {
                lineNumbers.put(label,currLine);
                labelPlaced = false;
            }

            // if this is a label, say label placed, store label
            if (line.length() > 1 && line.charAt(line.length()-1) == ':') {
                label = line.substring(0,line.indexOf(':'));
                labelPlaced = true;
            }
        }

        // if a label was placed at the end, store the label at the last line
        if (labelPlaced)
            lineNumbers.put(label,currLine+1);

        return lineNumbers;
    }
}
