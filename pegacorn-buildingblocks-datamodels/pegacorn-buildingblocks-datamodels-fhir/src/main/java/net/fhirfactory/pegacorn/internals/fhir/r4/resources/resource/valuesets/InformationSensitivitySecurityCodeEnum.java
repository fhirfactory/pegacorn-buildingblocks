package net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.valuesets;

public enum InformationSensitivitySecurityCodeEnum {
    ETH("substance abuse information sensitivity"),
    GDIS("genetic disease information sensitivity"),
    HIV("HIV/AIDS information sensitivity"),
    MST("military sexual trauma information sensitivity"),
    SCA("sickle cell anemia information sensitivity"),
    SDV("sexual assault, abuse or domestic violence information sensitivity"),
    SEX("sexuality and reproductive health information sensitivity"),
    SPI("specially protected information sensitivity"),
    BH("behavioural health information sensitivity"),
    COGN("cognitive disability information sensitivity"),
    DVD("developmental disability information sensitivity"),
    EMOTDIS("emotional disturbance information sensitivity"),
    MH("mental health information sensitivity"),
    PSY("psychiatry disorder information sensitivity"),
    PSYTHPN("psychotherapy note information sensitivity"),
    SUD("substance use disorder information sensitivity"),
    ETHUD("alcohol use disorder information sensitivity"),
    OPIOIDUD("opioid use disorder information sensitivity"),
    STD("sexually transmitted disease information sensitivity"),
    TBOO("taboo"),
    VIO	("violence information sensitivity"),
    SICKLE("sickle cell"),
    DEMO("all demographic information sensitivity"),
    DOB("date of birth information sensitivity"),
    GENDER("gender and sexual orientation information sensitivity"),
    LIVARG("living arrangement information sensitivity"),
    MARST("marital status information sensitivity"),
    RACE("race information sensitivity"),
    REL("religion information sensitivity"),
    B("business information sensitivity"),
    EMPL("employer information sensitivity"),
    LOCIS("location information sensitivity"),
    SSP("sensitive service provider information sensitivity"),
    ADOL("adolescent information sensitivity"),
    CEL("celebrity information sensitivity"),
    DIA("diagnosis information sensitivity"),
    DRGIS("drug information sensitivity"),
    EMP("employee information sensitivity"),
    PDS("patient default information sensitivity"),
    PHY("physician requested information sensitivity"),
    PRS("patient requested information sensitivity");

    private String displayText;

    private InformationSensitivitySecurityCodeEnum(String displayText){
        this.displayText = displayText;
    }

    public String getDisplayText(){
        return(this.displayText);
    }
}
