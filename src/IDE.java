import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class IDE implements IDERequirements {
    private ButtonListener listener;
    private DynamicLookManager manager;
    private JFrame frame;
    private SimulatorData sim;

    @Override
    public void runPressed () {
        resetPeripherals();
        listener.notifyOfRun();
    }

    @Override
    public void terminatePressed () {
        listener.notifyOfTerminate();
    }

    @Override
    public void resetPeripherals () {
        // reset screen
        Dimension screenSize = manager.getScreenSize();
        manager.drawOnScreen(new BufferedImage((int)screenSize.getWidth(),(int)screenSize.getHeight(),
                BufferedImage.TYPE_INT_ARGB));

        // reset special registers in display (others can stay)
        manager.changeRegisterValue(SimulatorData.getRegisterNumber("TP"),0);
        manager.changeRegisterValue(SimulatorData.getRegisterNumber("CL"),0);
        manager.changeRegisterValue(SimulatorData.getRegisterNumber("ST"),0);
        manager.changeRegisterValue(SimulatorData.getRegisterNumber("PS"),0);
        manager.changeRegisterValue(SimulatorData.getRegisterNumber("CN"),0);
        manager.changeRegisterValue(SimulatorData.getRegisterNumber("PK"),0);

        // reset terminal
        manager.changeTerminal("");

        // reset stack
        manager.changeStack("Stack Memory:\n\nAddress\tDecimal\n--- Frame Pointer ---");

        // reset data and pull from general purpose registers
        sim = new SimulatorData();
        sim.R0.setValue(manager.getRegisterValue(0));
        sim.R1.setValue(manager.getRegisterValue(1));
        sim.R2.setValue(manager.getRegisterValue(2));
        sim.R3.setValue(manager.getRegisterValue(3));
        sim.R4.setValue(manager.getRegisterValue(4));
        sim.R5.setValue(manager.getRegisterValue(5));
        sim.R6.setValue(manager.getRegisterValue(6));
        sim.R7.setValue(manager.getRegisterValue(7));
        sim.R8.setValue(manager.getRegisterValue(8));
        sim.R9.setValue(manager.getRegisterValue(9));
    }

    @Override
    public void openGUI() {
        frame = new JFrame();
        frame.setSize(new Dimension(1166, 760));
        frame.setMinimumSize(new Dimension(400,300));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setTitle("Custom Assembly IDE - Skyler Andersen");
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                listener.notifyOfGUIClosed();
                super.windowClosed(e);
            }
        });
        manager = new DynamicLookManager(this,frame);
        frame.addComponentListener(manager);
    }

    @Override
    public void print(String message) {
        boolean newLineEnd = (message.charAt(message.length()-1) == '\n');
        String dialog = manager.getTerminalText();
        if (dialog.isEmpty() || dialog.charAt(dialog.length()-1) == '\n')
            dialog += "> ";
        StringBuilder addition = new StringBuilder();
        Arrays.stream(message.split("\n")).forEach((String n) -> {addition.append("> "); addition.append(n);
            addition.append("\n");});
        addition.delete(0,2);
        if (addition.length() > 0)
            addition.deleteCharAt(addition.length()-1);
        if (newLineEnd)
            addition.append('\n');
        manager.changeTerminal(dialog + addition);
    }

    @Override
    public void println(String message) {
        boolean newLineEnd = (message.charAt(message.length()-1) == '\n');
        String dialog = manager.getTerminalText();
        if (dialog.isEmpty() || dialog.charAt(dialog.length()-1) == '\n')
            dialog += "> ";
        StringBuilder addition = new StringBuilder();
        Arrays.stream(message.split("\n")).forEach((String n) -> {addition.append("> "); addition.append(n);
            addition.append("\n");});
        addition.delete(0,2);
        if (addition.length() > 0)
            addition.deleteCharAt(addition.length()-1);
        if (newLineEnd)
            addition.append('\n');
        manager.changeTerminal(dialog + addition + "\n");
    }

    public void systemPrint(String message) {
        String terminalText = manager.getTerminalText();
        if (terminalText.isEmpty() || terminalText.charAt(terminalText.length()-1) != '\n')
            terminalText = terminalText + '\n';
        manager.changeTerminal(terminalText + "[CONSOLE]: " + message);
    }

    public void systemPrintln(String message) {
        String terminalText = manager.getTerminalText();
        if (terminalText.isEmpty() || terminalText.charAt(terminalText.length()-1) != '\n')
            terminalText = terminalText + '\n';
        manager.changeTerminal(terminalText + "[CONSOLE]: " + message + "\n");
    }

    private void updateStackDisplay () {
        String preamble = "Stack Memory:\n\nAddress\tDecimal\n";
        String end = "--- Frame Pointer ---";
        StringBuilder data = new StringBuilder();
        for (int i = sim.ST.getValue(); i < 0; i+=4) {
            data.append(i);
            data.append('\t');
            data.append(sim.stack.get(i));
            data.append('\n');
        }
        data.insert(0,preamble);
        data.append(end);
        manager.changeStack(data.toString());
    }

    @Override
    public void allocateMemory(int numWords) {
        // update stack in simulator
        for (int i = 0; i < numWords; i++)
            sim.stack.put(sim.ST.getValue()-4*(i+1),(int)(256*Math.random()));

        // update stack pointer + stack pointer display
        sim.ST.setValue(sim.ST.getValue()-4*numWords);
        manager.changeRegisterValue(SimulatorData.getRegisterNumber("ST"),sim.ST.getValue());

        // write to display
        updateStackDisplay();
    }

    @Override
    public void deallocateMemory(int numWords) {
        // update stack in simulator
        for (int i = sim.ST.getValue(); i < sim.ST.getValue()+4*numWords; i+=4)
            sim.stack.remove(i);

        // update stack pointer + stack pointer display
        sim.ST.setValue(sim.ST.getValue()+4*numWords);
        manager.changeRegisterValue(SimulatorData.getRegisterNumber("ST"),sim.ST.getValue());

        // write to display
        updateStackDisplay();
    }

    @Override
    public void setStackValue(int address, int value) {
        if (address >= 0 || address < sim.ST.getValue() || Math.abs(address) % 4 != 0)
            return;

        sim.stack.replace(address,value);
        updateStackDisplay();
    }

    @Override
    public int getStackValue(int address) {
        if (address >= 0 || address < sim.ST.getValue() || Math.abs(address) % 4 != 0)
            return -1;

        return sim.stack.get(address);
    }

    @Override
    public void setRegister(String name, int value) {
        // handle special cases like stack and return pos
        if (name.equalsIgnoreCase("ST")) { // handle moving the stack pointer
            if (Math.abs(value) % 4 != 0 || value >= 0) {
                System.out.println("Invalid Stack Address");
                return;
            }

            // allocate and deallocate memory to update stack pointer
            int offset = sim.ST.getValue() - value;
            if (offset == 0)
                return;
            else if (offset > 0)
                allocateMemory(offset/4);
            else
                deallocateMemory(-1*offset/4);

            // exit
            return;
        } else if (name.equalsIgnoreCase("PS")) { // handle the PS stack
            sim.PS.push(value);
            manager.changeRegisterValue(SimulatorData.getRegisterNumber("PS"),value);
            return;
        }

        // all other cases, update register value in simulator
        try {
            ((Register) SimulatorData.class.getField(name).get(sim)).setValue(value);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to fetch register");
        }

        // update the register display
        manager.changeRegisterValue(SimulatorData.getRegisterNumber(name),value);
    }

    @Override
    public int getRegister(String name) {
        // get register value
        int registerValue = SimulatorData.getRegisterNumber(name);

        // handle PS, return pos stack
        if (name.equalsIgnoreCase("PS")) {
            if (sim.PS.isEmpty()) {
                System.out.println("RETURNED INVALIDLY!!!");
                return Integer.MAX_VALUE;
            }
            if (sim.PS.size() == 1) {
                int returnValue = sim.PS.pop();
                manager.changeRegisterValue(registerValue,0);
                return returnValue;
            }
            int returnValue = sim.PS.pop();
            manager.changeRegisterValue(registerValue,sim.PS.peek());
            return returnValue;
        }

        // return value
        int returnValue = -1;
        try {
            returnValue = ((Register) SimulatorData.class.getField(name).get(sim)).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to fetch register");
        }
        return returnValue;
    }

    @Override
    public void drawRaster(int[] buffer) {
        int width = (int) manager.getScreenSize().getWidth();
        int height = (int) manager.getScreenSize().getHeight();
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);

        // copy pixels from buffer to image
        for (int i = 0; i < width*height; i++) {
            int x = i % width;
            int y = i / width;
            if (i < buffer.length)
                image.setRGB(x,y,buffer[i]);
            else
                image.setRGB(x,y,0);
        }

        // update raster
        manager.drawOnScreen(image);
    }

    @Override
    public void playSoundTrack(int[] buffer) {
        Thread dispatchSounds = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<byte[]> waveforms = new ArrayList<>();
                for (int i = 0; i < buffer.length; i+=2) {
                    System.out.println("i: " + i + ", buffer[i]: " + buffer[i] + ", buffer[i+1]: " + buffer[i+1]);
                    waveforms.add(generateWaveform(buffer[i], buffer[i + 1]));
                }
                int length = 0;
                for (byte[] waveform : waveforms)
                    length += waveform.length;
                byte[] combinedWaveform = new byte[length];
                int c = 0;
                for (byte[] waveform : waveforms) {
                    for (byte datapoint : waveform) {
                        combinedWaveform[c++] = datapoint;
                    }
                }
                playAudio(combinedWaveform);
            }
        });
        dispatchSounds.start();
    }

    private byte[] generateWaveform (int frequency,int duration) {
        // generate waveform
        int samples = duration * 44100 / 1000;
        byte[] waveform = new byte[samples];
        for (int i = 0; i < waveform.length; i++) {
            double theta = 2 * Math.PI * (frequency/10.0) * i / 44100;
            waveform[i] = (byte) (127 * Math.sin(theta));
        }
        return waveform;
    }

    private void playAudio (byte[] waveform) {
        AudioFormat format = new AudioFormat(44100,8,1,true,false);
        SourceDataLine audio;
        try {
            audio = AudioSystem.getSourceDataLine(format);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to get source data line");
            return;
        }

        // play audio
        try {
            audio.open(format);
            audio.start();
            audio.write(waveform,0,waveform.length);
            audio.flush();
            audio.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to play");
        }
    }

    @Override
    public String[] getCode() {
        String code = manager.getCodeText();

        // remove excessive newlines at the end
        while (code.length() > 0 && code.charAt(code.length()-1) == '\n')
            code = code.substring(0,code.length()-1);

        // if there is nothing
        if (code.length() == 0) {
            String[] empty = {""};
            return empty;
        }

        return code.split("\n");
    }

    @Override
    public void addListener(ButtonListener listener) {
        this.listener = listener;
    }

    public int screenWidth () {
        return (int) manager.getScreenSize().getWidth();
    }

    public int screenHeight () {
        return (int) manager.getScreenSize().getHeight();
    }
}