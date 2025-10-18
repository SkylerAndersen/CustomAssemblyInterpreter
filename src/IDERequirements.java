interface IDERequirements {
    void openGUI ();
    void resetPeripherals ();
    void print (String message);
    void println (String message);
    void allocateMemory (int numBytes);
    void deallocateMemory (int numBytes);
    void setStackValue (int address, int value);
    int getStackValue (int address);
    void setRegister (String name, int value);
    int getRegister (String name);
    void drawRaster (int[] buffer);
    void playSoundTrack (int[] buffer);
    String[] getCode ();
    void addListener (ButtonListener listener);
    void runPressed ();
    void terminatePressed ();
}