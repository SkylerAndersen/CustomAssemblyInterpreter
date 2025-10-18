import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class CustomInterpreter {
    private IDE app;
    private Listener listener;
    private int delay;
    private boolean executingCompiled;

    public CustomInterpreter () {
        app = new IDE();
        listener = new Listener();
        app.addListener(listener);
        delay = 0;
    }

    private void disclose () {
//        System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName() + " ");

    }

    private void SET (String RD, int imm) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        app.setRegister(RD,imm);
    }
    private void INC (String RD, int imm) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        app.setRegister(RD,app.getRegister(RD)+imm);
    }
    private void LTI (String RS, int imm) {
        disclose();
        int value = (app.getRegister(RS) < imm) ? 1 : 0;
        app.setRegister("CN",value);
    }
    private void GTI (String RS, int imm) {
        disclose();
        int value = (app.getRegister(RS) > imm) ? 1 : 0;
        app.setRegister("CN",value);
    }
    private void EQI (String RS, int imm) {
        disclose();
        int value = (app.getRegister(RS) == imm) ? 1 : 0;
        app.setRegister("CN",value);
    }
    private void ADD (String RS, String RT, String RD) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        int sum = app.getRegister(RS) + app.getRegister(RT);
        app.setRegister(RD,sum);
    }
    private void LES (String RS, String RT) {
        disclose();
        int value = (app.getRegister(RS) < app.getRegister(RT)) ? 1 : 0;
        app.setRegister("CN",value);
    }
    private void GRT (String RS, String RT) {
        disclose();
        int value = (app.getRegister(RS) > app.getRegister(RT)) ? 1 : 0;
        app.setRegister("CN",value);
    }
    private void EQV (String RS, String RT) {
        disclose();
        int value = (app.getRegister(RS) == app.getRegister(RT)) ? 1 : 0;
        app.setRegister("CN",value);
    }
    private void ORR (String RS, String RT) {
        disclose();
        int value = (app.getRegister(RS) == 1 || app.getRegister(RT) == 1) ? 1 : 0;
        app.setRegister("CN",value);
    }
    private void AND (String RS, String RT) {
        disclose();
        int value = (app.getRegister(RS) == 1 && app.getRegister(RT) == 1) ? 1 : 0;
        app.setRegister("CN",value);
    }
    private void RTN () {
        disclose();
        app.setRegister("CL",app.getRegister("PS"));
    }
    private void JNC (int imm) {
        disclose();
        app.setRegister("CL",imm-1);
    }
    private void JWC (int imm) {
        disclose();
        if (app.getRegister("CN") == 1)
            app.setRegister("CL",imm-1);
    }
    private void CPT (int imm) {
        disclose();
        int value = app.getRegister("CL") + 1 + imm;
        app.setRegister("PS",value);
    }
    private void SAV (String RS, int offset, String RT) {
        disclose();
        int value = app.getRegister(RS);
        int address = app.getRegister(RT) + 4*offset;
        app.setStackValue(address,value);
    }
    private void LOD (String RD, int offset, String RS) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        int address = app.getRegister(RS) + 4*offset;
        app.setRegister(RD,app.getStackValue(address));
    }
    private void SWAP (String RS, String RT) {
        disclose();
        if (RS.equalsIgnoreCase("PS") || RT.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RS.equalsIgnoreCase("PK") || RT.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        app.setRegister("TP",app.getRegister(RS));
        app.setRegister(RS,app.getRegister(RT));
        app.setRegister(RT,app.getRegister("TP"));
    }
    private void CAST (String RD) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        // get ascii values to write the number stored in RD, plus a null
        String decimal = ""+app.getRegister(RD);
        int[] ascii = new int[decimal.length()+1];
        for (int i = 0; i < decimal.length(); i++)
            ascii[i] = decimal.charAt(i);
        ascii[ascii.length-1] = 0;

        // allocate the memory
        app.allocateMemory(ascii.length);

        // add values to the stack
        int ST = app.getRegister("ST");
        for (int i = ascii.length-1; i >= 0; i--) {
            int address = ST+4*i;
            int value = ascii[i];
            app.setStackValue(address,value);
        }

        // set RD to be the address
        app.setRegister(RD,ST);
    }
    private void PARS (String RD) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }
        int address = app.getRegister(RD);
        if (address % 4 != 0 || address >= 0 || address < app.getRegister("ST")) {
            app.println("Invalid stack address");
            return;
        }

        // get values until null is reached
        int[] stackValues = getNullTerminated(address);
        if (stackValues.length == 1 && stackValues[0] == -1) { // if null not reached
            app.println("Data is not null terminated.");
            return;
        }

        // create the String
        StringBuilder string = new StringBuilder();
        for (int character : stackValues)
            string.append((char)character);

        // cast the string and set RD to the value
        int value = Integer.parseInt(string.toString());
        app.setRegister(RD,value);
    }
    private void PRNT (String RS) {
        disclose();
        int address = app.getRegister(RS);

        // get values until null is reached
        int[] stackValues = getNullTerminated(address);
        if (stackValues.length == 1 && stackValues[0] == -1) { // if null not reached
            app.println("Data is not null terminated.");
            return;
        }

        // create the String
        StringBuilder string = new StringBuilder();
        for (int character : stackValues)
            string.append((char)character);

        // print
        app.print(string.toString());
    }
    private void PLAY (String RS) {
        disclose();
        int address = app.getRegister(RS);

        // get values until null is reached
        int[] stackValues = getNullTerminated(address);
        if (stackValues.length == 1 && stackValues[0] == -1) { // if null not reached
            app.println("Data is not null terminated.");
            return;
        }

        // create soundtrack
        boolean invalid = false;
        int[] soundTrack = new int[stackValues.length*2];
        for (int i = 0; i < stackValues.length; i++) {
            int tone = stackValues[i] >> 16;
            int duration = stackValues[i] & 0xFFFF;
            soundTrack[i*2] = tone;
            soundTrack[i*2+1] = duration;
            if (tone < 0 || duration < 0)
                invalid = true;
        }

        // play sound
        if (invalid) {
            app.println("Invalid sound track.");
            return;
        }
        app.playSoundTrack(soundTrack);
    }
    private void DISP (String RS) {
        disclose();
        int address = app.getRegister(RS);

        // get values until null is reached
        int[] stackValues = getNullTerminated(address);
        if (stackValues.length == 1 && stackValues[0] == -1) { // if null not reached
            app.println("Data is not null terminated.");
            return;
        }

        // if there are not enough pixels
        if (stackValues.length != app.screenWidth()*app.screenHeight()) {
            app.println("Not enough pixels in the buffer.");
            return;
        }

        // draw the pixels on the screen
        app.drawRaster(stackValues);
    }
    private void MFPK (String RD) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        app.setRegister(RD,app.getRegister("PK"));
    }
    private void TIME (String RD) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        int seconds = (int) (System.nanoTime() / 1000000000);
        app.setRegister(RD,seconds);
    }
    private void CCAT (String RS, String RT, String RD) {
        disclose();
        if (RD.equalsIgnoreCase("PS")) {
            app.println("PS is read-only");
            return;
        } else if (RD.equalsIgnoreCase("PK")) {
            app.println("PK is read-only");
            return;
        }

        // get values until null is reached
        int[] rsValues = getNullTerminated(app.getRegister(RS));
        int[] rtValues = getNullTerminated(app.getRegister(RT));

        // if null not reached
        if ((rsValues.length == 1 && rsValues[0] == -1) || (rtValues.length == 1 && rtValues[0] == -1)) {
            app.println("Data is not null terminated.");
            return;
        }

        // create the Strings
        int max = Math.max(rsValues.length,rtValues.length);
        StringBuilder rsString = new StringBuilder();
        StringBuilder rtString = new StringBuilder();
        for (int i = 0; i < max; i++) {
            if (i < rsValues.length)
                rsString.append((char)rsValues[i]);
            if (i < rtValues.length)
                rtString.append((char)rtValues[i]);
        }

        // concatenate them
        String combined = rsString.toString() + rtString;

        // add them to the stack
        int[] ascii = new int[combined.length()+1];
        for (int i = 0; i < combined.length(); i++)
            ascii[i] = combined.charAt(i);
        ascii[ascii.length-1] = 0;

        // allocate the memory
        app.allocateMemory(ascii.length);

        // add values to the stack
        int ST = app.getRegister("ST");
        for (int i = ascii.length-1; i >= 0; i--) {
            int address = ST+4*i;
            int value = ascii[i];
            app.setStackValue(address,value);
        }

        // set RD to be the address
        app.setRegister(RD,ST);
    }
    private void ALOC (int imm) {
        disclose();
        if (imm >= 0)
            app.allocateMemory(imm);
        else
            app.deallocateMemory(-1*imm);
    }
    private void PACK (String RS, String RT, int offset) {
        disclose();
        if (offset > 16 || offset < 0) {
            app.println("Invalid offset to pack. Either > 16 or < 0.");
            return;
        }

        // pack values
        long value1 = ((long) app.getRegister(RS)) << offset;
        int mask = 0;
        for (int i = 0; i < offset; i++)
            mask = (mask << 1) | 1;
        long value2 = app.getRegister(RT) & mask;
        long value = value1 | value2;

        // update register
        app.setRegister("PK",new Register(value).getValue());
    }

    public void run () {
        app.openGUI();
        while (!listener.guiClosed()) {
            update();
        }
    }

    private void update () {
        if (!listener.hasUpdates())
            return;

        // check if user ran code in ide
        if (listener.pressedRun()) {
            listener.markRecieved();
            String[] instructions = CustomAssembler.assembleInstructions(app.getCode());
            for (String line : instructions)
                System.out.println(line);
            if (!executingCompiled)
                executeInstructions(instructions);
            else
                compileAndRun(instructions);
        }
    }

    private void executeInstructions (String[] instructions) {
        int i = 0;
        boolean running = true;
        app.systemPrintln("Program beginning execution.");

        while (running) {
            // check if program is terminated
            if (listener.hasUpdates() && listener.pressedTerminate()) {
                app.systemPrintln("Terminated: Program executed unsuccessfully.");
                listener.markRecieved();
                break;
            }

            // execute the instruction at the current line or end program execution
            i = app.getRegister("CL");
            if (i >= instructions.length) {
                app.systemPrintln("Program executed successfully.");
                break;
            }
            app.setRegister("CL",i+1);
            executeInstruction(instructions[i]);

            // add user specified delay
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to add user specified delay.");
                }
            }
        }
    }

    private void executeInstruction (String instruction) {
        int opcode = parseUnsigned(instruction.substring(0,4));
        if (opcode == 3) {
            int rNum = parseUnsigned(instruction.substring(4,8));
            int imm = parseSigned(instruction.substring(8));
            String rd = SimulatorData.getRegisterName(rNum);
            SET(rd,imm);
        } else if (opcode == 4) {
            int rNum = parseUnsigned(instruction.substring(4,8));
            int imm = parseSigned(instruction.substring(8));
            String rd = SimulatorData.getRegisterName(rNum);
            INC(rd,imm);
        } else if (opcode == 5) {
            int rNum = parseUnsigned(instruction.substring(4,8));
            int imm = parseSigned(instruction.substring(8));
            String rs = SimulatorData.getRegisterName(rNum);
            LTI(rs,imm);
        } else if (opcode == 6) {
            int rNum = parseUnsigned(instruction.substring(4,8));
            int imm = parseSigned(instruction.substring(8));
            String rs = SimulatorData.getRegisterName(rNum);
            GTI(rs,imm);
        } else if (opcode == 7) {
            int rNum = parseUnsigned(instruction.substring(4,8));
            int imm = parseSigned(instruction.substring(8));
            String rs = SimulatorData.getRegisterName(rNum);
            EQI(rs,imm);
        } else if (opcode == 8) {
            int rsNum = parseUnsigned(instruction.substring(4,8));
            int rtNum = parseUnsigned(instruction.substring(8,12));
            int offset = parseOffset(instruction.substring(12));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            SAV(rs,offset,rt);
        } else if (opcode == 9) {
            int rsNum = parseUnsigned(instruction.substring(4,8));
            int rdNum = parseUnsigned(instruction.substring(8,12));
            int offset = parseOffset(instruction.substring(12));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rd = SimulatorData.getRegisterName(rdNum);
            LOD(rd,offset,rs);
        } else if (opcode == 10) {
            int rsNum = parseUnsigned(instruction.substring(4,8));
            int rtNum = parseUnsigned(instruction.substring(8,12));
            int offset = parseSigned(instruction.substring(12));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            PACK(rs,rt,offset);
        } else {
            executeInstructionFromSpecial(instruction);
        }
    }

    private void executeInstructionFromSpecial (String instruction) {
        int opcode = parseUnsigned(instruction.substring(0,4));
        int special = parseUnsigned(instruction.substring(4,8));
        if (opcode == 1) {
            if (special == 0) {
                int rdNum = parseUnsigned(instruction.substring(16,20));
                String rd = SimulatorData.getRegisterName(rdNum);
                MFPK(rd);
            } else if (special == 1) {
                int rdNum = parseUnsigned(instruction.substring(16,20));
                String rd = SimulatorData.getRegisterName(rdNum);
                TIME(rd);
            } else if (special == 2) {
                int rsNum = parseUnsigned(instruction.substring(8,12));
                int rtNum = parseUnsigned(instruction.substring(12,16));
                int rdNum = parseUnsigned(instruction.substring(16,20));
                String rs = SimulatorData.getRegisterName(rsNum);
                String rt = SimulatorData.getRegisterName(rtNum);
                String rd = SimulatorData.getRegisterName(rdNum);
                CCAT(rs,rt,rd);
            } else {
                int imm = parseSigned(instruction.substring(8,24));
                ALOC(imm);
            }
            return;
        }
        if (special == 0) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            int rtNum = parseUnsigned(instruction.substring(12,16));
            int rdNum = parseUnsigned(instruction.substring(16,20));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            String rd = SimulatorData.getRegisterName(rdNum);
            ADD(rs,rt,rd);
        } else if (special == 1) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            int rtNum = parseUnsigned(instruction.substring(12,16));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            LES(rs,rt);
        } else if (special == 2) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            int rtNum = parseUnsigned(instruction.substring(12,16));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            GRT(rs,rt);
        } else if (special == 3) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            int rtNum = parseUnsigned(instruction.substring(12,16));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            EQV(rs,rt);
        } else if (special == 4) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            int rtNum = parseUnsigned(instruction.substring(12,16));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            ORR(rs,rt);
        } else if (special == 5) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            int rtNum = parseUnsigned(instruction.substring(12,16));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            AND(rs,rt);
        } else if (special == 6) {
            RTN();
        } else if (special == 7) {
            int imm = parseSigned(instruction.substring(8));
            JNC(imm);
        } else if (special == 8) {
            int imm = parseSigned(instruction.substring(8));
            JWC(imm);
        } else if (special == 9) {
            int imm = parseSigned(instruction.substring(8));
            CPT(imm);
        } else if (special == 10) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            int rtNum = parseUnsigned(instruction.substring(12,16));
            String rs = SimulatorData.getRegisterName(rsNum);
            String rt = SimulatorData.getRegisterName(rtNum);
            SWAP(rs,rt);
        } else if (special == 11) {
            int rdNum = parseUnsigned(instruction.substring(16,20));
            String rd = SimulatorData.getRegisterName(rdNum);
            CAST(rd);
        } else if (special == 12) {
            int rdNum = parseUnsigned(instruction.substring(16,20));
            String rd = SimulatorData.getRegisterName(rdNum);
            PARS(rd);
        } else if (special == 13) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            String rs = SimulatorData.getRegisterName(rsNum);
            PRNT(rs);
        } else if (special == 14) {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            String rs = SimulatorData.getRegisterName(rsNum);
            PLAY(rs);
        } else {
            int rsNum = parseUnsigned(instruction.substring(8,12));
            String rs = SimulatorData.getRegisterName(rsNum);
            DISP(rs);
        }
    }

    private int parseSigned (String asciiBin) {
        StringBuilder binary = new StringBuilder(asciiBin);

        // convert two's complement to int
        boolean negative = binary.charAt(0) == '1';
        binary.deleteCharAt(0);
        int value = Integer.parseInt(binary.toString(),2);
        if (negative)
            value -= 32768;

        return value;
    }

    private int parseOffset (String asciiBin) {
        StringBuilder binary = new StringBuilder(asciiBin);

        // convert two's complement to int
        boolean negative = binary.charAt(0) == '1';
        binary.deleteCharAt(0);
        int value = Integer.parseInt(binary.toString(),2);
        if (negative)
            value -= 2048;

        return value;
    }

    private int parseUnsigned (String value) {
        return Integer.parseInt(value,2);
    }

    private int[] getNullTerminated (int address) {
        boolean notReached = true;
        ArrayList<Integer> stackValues = new ArrayList<>();
        for (int i = address; i < 0 && notReached; i+=4) {
            int value = app.getStackValue(i);
            if (value == 0) {
                notReached = false;
                break;
            }
            stackValues.add(value);
        }

        // if not reached
        int[] output = {-1};
        if (notReached)
            return output;

        // create output
        output = new int[stackValues.size()];
        int c = 0;
        for (int value : stackValues) {
            output[c++] = value;
        }
        return output;
    }

    private static String intToAsciiBin (int value) {
        StringBuilder output = new StringBuilder();
        int max = 32768; // value represented by a 1 in the highest bit
        while (max > 0) {
            if (value >= max) {
                value -= max;
                output.append('1');
            } else {
                output.append('0');
            }
            max /= 2;
        }
        return output.toString();
    }

    public void setInstructionDelay (int delay) {
        this.delay = delay;
    }

    private void executeCompiled (boolean executingCompiled) {
        // this function is not yet available, hence it is private
        this.executingCompiled = executingCompiled;
    }

    private void compileAndRun (String[] instructions) {
        // does not exist yet
    }

    private void runC (String code) {
        // write code in a file
        File file = new File("/private/tmp/tempProgram.cpp");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(code);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed");
        }

        // compile code
        Terminal terminal = new Terminal();
        terminal.write("cd /private/tmp");
        terminal.write("clang++ tempProgram.cpp -o tempProgram.exe");
        terminal.run();
        terminal.waitFor();

        // execute compiled code
        terminal = new Terminal();
        terminal.write("cd /private/tmp");
        terminal.write("./tempProgram.exe");
        terminal.run();

        // return output
        terminal.waitFor();
        System.out.println(terminal.get().strip());

        // clean up
        (new File("/private/tmp/tempProgram.exe")).delete();
        file.delete();
    }
}
