class Register {
    private int value;
    public Register () {}
    public Register (Register value) {
        setValue(value);
    }
    public Register (int value) {
        setValue(value);
    }
    public Register (long value) {
        setValue(value);
    }
    public Register (String value) {
        setValue(value);
    }
    public void setValue (Register otherRegister) {
        this.value = otherRegister.getValue();
    }
    public void setValue (int value) {
        this.value = value;
    }
    public void setValue (long value) {
        StringBuilder binary = new StringBuilder();
        long max = 2147483648L;
        while (max > 0) {
            if (value >= max) {
                binary.append('1');
                value -= max;
            } else {
                binary.append('0');
            }
            max /= 2;
        }
        setValue(binary.toString());
    }
    public void setValue (String asciiBinary) {
        StringBuilder binary = new StringBuilder(asciiBinary);
        while (binary.length() != 32)
            binary.insert(0,'0');

        // convert 32-bit two's complement to int
        boolean negative = binary.charAt(0) == '1';
        binary.deleteCharAt(0);
        int value = Integer.parseInt(binary.toString(),2);
        if (negative)
            value += Integer.MIN_VALUE;

        this.value = value;
    }
    public int getValue () {
        return value;
    }
    public String getBinaryValue () {
        StringBuilder binary = new StringBuilder();
        long value = this.value;
        long max = 2147483648L;
        boolean negative = value < 0;
        if (negative) {
            long twoToThe32 = 4294967296L;
            value = twoToThe32 + value;
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
        return binary.toString();
    }
}