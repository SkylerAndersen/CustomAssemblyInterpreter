public class Listener implements ButtonListener {
    private boolean hasUpdates;
    private boolean pressedRun;
    private boolean pressedTerminate;
    private boolean guiClosed;

    public Listener () {
        hasUpdates = false;
        pressedRun = false;
        pressedTerminate = false;
        guiClosed = false;
    }

    @Override
    public synchronized boolean hasUpdates() {
        return hasUpdates;
    }

    @Override
    public synchronized boolean pressedRun() {
        return pressedRun;
    }

    @Override
    public synchronized boolean pressedTerminate() {
        return pressedTerminate;
    }

    @Override
    public synchronized boolean guiClosed() {
        return guiClosed;
    }

    @Override
    public synchronized void markRecieved() {
        hasUpdates = false;
        pressedRun = false;
        pressedTerminate = false;
    }

    @Override
    public synchronized void declareUpdates() {
        hasUpdates = true;
    }

    @Override
    public synchronized void notifyOfRun() {
        pressedRun = true;
        declareUpdates();
    }

    @Override
    public synchronized void notifyOfTerminate() {
        pressedTerminate = true;
        declareUpdates();
    }

    @Override
    public synchronized void notifyOfGUIClosed() {
        guiClosed = true;
        notifyOfTerminate();
    }
}