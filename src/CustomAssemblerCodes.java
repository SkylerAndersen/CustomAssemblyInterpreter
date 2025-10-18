import javax.crypto.spec.PSource;

enum CustomAssemblerCodes {
    SET("RD imm","0011rdimm"),
    INC("RD imm","0100rdimm"),
    LTI("RS imm","0101rsimm"),
    GTI("RS imm","0110rsimm"),
    EQI("RS imm","0111rsimm"),
    ADD("RS RT RD","00000000rsrtrd0000"),
    LES("RS RT","00000001rsrt00000000"),
    GRT("RS RT","00000010rsrt00000000"),
    EQV("RS RT","00000011rsrt00000000"),
    ORR("RS RT","00000100rsrt00000000"),
    AND("RS RT","00000101rsrt00000000"),
    RTN("","000001100000000000000000"),
    JNC("imm","00000111imm"),
    JWC("imm","00001000imm"),
    CPT("imm","00001001imm"),
    SAV("RS offset(RT)","1000rsrtoffset"),
    LOD("RD offset(RS)","1001rsrdoffset"),
    SWAP("RS RT","00001010rsrt00000000"),
    CAST("RD","0000101100000000rd0000"),
    PARS("RD","0000110000000000rd0000"),
    PRNT("RS","00001101rs000000000000"),
    PLAY("RS","00001110rs000000000000"),
    DISP("RS","00001111rs000000000000"),
    MFPK("RD","0001000000000000rd0000"),
    TIME("RD","0001000100000000rd0000"),
    CCAT("RS RT RD","00010010rsrtrd0000"),
    ALOC("imm","00010011imm"),
    PACK("RS RT offset","1010rsrtoffset"),
    R0("","0000"),
    R1("","0001"),
    R2("","0010"),
    R3("","0011"),
    R4("","0100"),
    R5("","0101"),
    R6("","0110"),
    R7("","0111"),
    R8("","1000"),
    R9("","1001"),
    TP("","1010"),
    CL("","1011"),
    ST("","1100"),
    PS("","1101"),
    CN("","1110"),
    PK("","1111");
    private final String binary;
    private final String order;
    CustomAssemblerCodes(String order, String binary) {
        this.order = order;
        this.binary = binary;
    }

    public String getBinary () {
        return binary;
    }

    public String getOrder () {
        return order;
    }
}