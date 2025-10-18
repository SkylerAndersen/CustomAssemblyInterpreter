import java.util.ArrayList;

class Terminal {
    private boolean separateCommands;
    private Thread terminalThread;
    private ArrayList<String> commands;
    private String output;
    private int exitCode;
    private ThreadInstructions instructions;
    public Terminal () {
        commands = new ArrayList<>();
        output = "";
        separateCommands = false;
    }

    public Terminal (String command) {
        commands = new ArrayList<>();
        output = "";
        separateCommands = true;
        write(command);
    }

    /**
     * Write commands into the terminal
     * @param command shell command written in the terminal
     * */
    public void write (String command) {
        commands.add(command);
    }

    /**
     * run the commands written in the terminal
     * */
    public void run () {
        instructions = new ThreadInstructions(commands.toArray(new String[0]),separateCommands);
        terminalThread = new Thread(instructions);
        terminalThread.start();
    }

    /**
     * wait for the process to exit
     * @return int exit code of process or 1 if failed to wait for the thread
     * */
    public int waitFor () {
        try {
            terminalThread.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to wait for thread: " + e.getMessage());
            return 1;
        }

        // set output and exit code
        output = instructions.getOutput();
        exitCode = instructions.getExitCode();

        return exitCode;
    }

    /**
     * output of the program that would have been printed to the terminal
     * you must call waitFor before calling get
     * @return String output from terminal
     * */
    public String get () {
        return output;
    }
}