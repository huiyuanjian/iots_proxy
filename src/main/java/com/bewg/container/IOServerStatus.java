package com.bewg.container;

public class IOServerStatus {

    private IOServerStatus() {
        this.iOStatus = IOStatus.OTHER;
    }

    private static IOServerStatus instance = new IOServerStatus();

    public static IOServerStatus getInstance() {
        return instance;
    }

    public IOStatus iOStatus;

    public IOStatus getiOStatus() {
        return iOStatus;
    }

    public void setiOStatus(IOStatus iOStatus) {
        this.iOStatus = iOStatus;
    }

    public enum IOStatus {
        RUNNING(1), STOPPED(10), OTHER(99);

        private int code;

        IOStatus(int code) {
            this.code = code;
        }

        public String toString() {
            return String.valueOf(this.code);
        }
    }

}
