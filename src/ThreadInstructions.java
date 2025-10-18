import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class ThreadInstructions implements Runnable {
    private String[] commands;
    private StringBuilder output;
    private int exitCode;
    private boolean runSeparate;
    private String oneCommand;

    public ThreadInstructions (String[] commands, boolean separateCommands) {
        this.commands = commands;
        this.runSeparate = separateCommands;
        StringBuilder commandBuilder = new StringBuilder(commands[0]);
        for (int i = 1; i < commands.length; i++) {
            commandBuilder.append(" && ");
            commandBuilder.append(commands[i]);
        }
        oneCommand = commandBuilder.toString();
    }

    @Override
    public void run () {
        ProcessBuilder builder = new ProcessBuilder();
        String[] builderCommand = {"/bin/bash","-c",oneCommand};
        if (runSeparate)
            builderCommand[2] = commands[0];
        builder.command(builderCommand);
        exitCode = 1;
        output = new StringBuilder();

        // run process
        try {
            // run command in new thread
            Process process = builder.start();

            // write additional commands separately if necessary
            if (runSeparate) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                for (int i = 1; i < commands.length; i++) {
                    writer.write(commands[i] + "\n");
                    writer.flush();
                    System.out.println("running: " + commands[i]);
                }
                writer.close();
            }

            // have this thread wait for output and exit code
            exitCode = process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String temp;
            while ((temp = reader.readLine()) != null) {
                output.append(temp);
                output.append('\n');
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("User entered process failed to run: " + e.getMessage());
        }
    }

    public String getOutput () {
        return output.toString();
    }

    public int getExitCode () {
        return exitCode;
    }
}