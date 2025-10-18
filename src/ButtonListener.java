interface ButtonListener {
    boolean hasUpdates ();
    boolean pressedRun ();
    boolean pressedTerminate ();
    boolean guiClosed ();
    void markRecieved ();
    void declareUpdates ();
    void notifyOfRun ();
    void notifyOfTerminate ();
    void notifyOfGUIClosed ();
}