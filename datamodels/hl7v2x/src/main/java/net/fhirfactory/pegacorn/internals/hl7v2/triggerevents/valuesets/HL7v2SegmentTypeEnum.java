/*
 * Copyright (c) 2022 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.hl7v2.triggerevents.valuesets;

public enum HL7v2SegmentTypeEnum {
    ABS("ABS", "Abstract", "This segment was created to communicate patient abstract information used for billing and reimbursement purposes. Abstract is a condensed form of medical history created for analysis, care planning, etc", 2.4, 2.8),
    ACC("ACC", "Accident", "The ACC segment contains patient information relative to an accident in which the patient has been involved.", 2.1, 2.8 ),
    ADD("ADD", "Addendum", "The ADD segment is used to define the continuation of the prior segment in a continuation message.", 2.1 , 2.8),
    ADJ("ADJ", "Adjustment", "This segment describes Provider and/or Payer adjustments to a Product/Service Line Item or Response Summary.  These include surcharges such as tax, dispensing fees and mark ups.", 2.6 , 2.8),
    AFF("AFF", "Professional Affiliation", "The AFF segment adds detailed information regarding professional affiliations with which the staff member identified by the STF segment is/was associated.", 2.4,2.8 ),
    AIG("AIG", "Appointment Information - General Resource", "The AIG segment contains information about various kinds of resources that can be scheduled.  Resources described by this segment are general kinds of resources, such as equipment, that are identified with a simple identification code.", 2.3, 2.8 ),
    AIL("AIL", "Appointment Information - Location", "The AIL segment contains information about location resources (meeting rooms, operating rooms, examination rooms, or other locations) that can be scheduled.  Location resources are identified with this specific segment because of the specific encoding of locations used by the HL7 specification.", 2.3, 2.8),
    AIP("AIP", "Appointment Information - Personnel Resource", "The AIP segment contains information about the personnel types that can be scheduled.  The kinds of personnel described on this segment include any healthcare provider in the institution controlled by a schedule (for example: technicians, physicians, nurses, surgeons, anesthesiologists, or CRNAs).", 2.3, 2.8 ),
    AIS("AIS", "Appointment Information - Service", "The AIS segment contains information about various kinds of services that can be scheduled.", 2.3, 2.8),
    AL1("AL1", "Patient Allergy Information", "The AL1 segment contains patient allergy information of various types.  Most of this information will be derived from user-defined tables.  Each AL1 segment describes a single patient allergy.",  2.2, 2.8),
    APR("APR", "Appointment Preferences","The APR segment contains parameters and preference specifications used for requesting appointments in the SRM message.", 2.3, 2.8),
    ARQ("ARQ", "Appointment Request", "", 2.3, 2.8),
    AUT("AUT", "Authorisation Information", "", 2.3, 2.8),
    BHS("BHS", "Batch Trailer", "The BHS segment defines the start of a batch.", 2.1,  2.8),
    BLC("BLC", "Blood Code", "", 2.4, 2.8),
    BLG("BLG", "Billing", "The BLG segment is used to provide billing information, on the ordered service, to the filling application.", 2.1, 2.8),
    BPO("BPO", "Blood Product Order", "", 2.5, 2.8),
    BPX ("BPX", "Blood Product Dispense Status", "", 2.5, 2.8),
    BTS("BTS", "Batch Trailer", "The BTS segment defines the end of a batch.", 2.1, 2.8),
    BTX("BTX", "Blood Product Transfusion/Disposition", "", 2.5, 2.8),
    CDM("CDM", "Charge Description Master", "", 2.3, 2.8),
    CER("CER", "Certificate Detail", "", 2.5, 2.8),
    CM0("CM0", "Clinical Study Master", "", 2.3, 2.8),
    CM1("CM1", "Clinical Study Phase Master", "", 2.3, 2.8),
    CM2("CM2", "Clinical Study Schedule Master", "", 2.3, 2.8),
    CNS("CNS", "Clear Notification", "", 2.5, 2.8),
    CON("CON", "Consent", "", 2.5, 2.8),
    CSP("CSP", "Clinical Study Phase", "", 2.3, 2.8),
    CSR("CSR", "Clinical Study Registration", "", 2.3, 2.8),
    CSS("CSS", "Clinical Study Data Schedule", "", 2.3, 2.8),
    CTD("CTD", "Contact Data", "", 2.3, 2.8),
    CTI("CTI", "Clinical Trial Information", "", 2.3, 2.8),
    DB1("DB1", "Disability Segment", "", 2.3, 2.8),
    DG1("DG1", "Diagnosis", "The DG1 segment contains patient diagnosis information of various types.  For example: Admitting, Current, Primary, Final, etc.  Coding methodologies are also defined", 2.1, 2.8),
    DMI("DMI", "DRG Master File Information", "", 2.6, 2.8),
    DRG("DRG", "Diagnosis Related Group", "The DRG segment contains diagnoses-related grouping information of various types. The DRG segment is used to send the DRG information, for example, for billing and medical records encoding.", 2.31, 2.8),
    DSC("DSC", "Continuation Pointer", "The DSC segment is used in the continuation protocol.", 2.1, 2.8),
    DSP("DSP", "Display Data", "The DSP segment is used to contain data that has been preformatted by the sender for display.  The semantic content of the data is lost; the data is simply treated as lines of text.", 2.1, 2.8),
    ECD("ECD", "Equipment Command", "", 2.4, 2.8),
    ECR("ECR", "Equipment Command Response", "", 2.4, 2.8),
    EDU("EDU", "Education Detail", "",  2.6, 2.8),
    EQL("EQL", "Embedded Query Language", "", 2.3, 2.8),
    EQP("EQP", "Equipment/Log Service", "", 2.4, 2.8),
    EQU("EQU", "Equipment Detail", "", 2.4, 2.8),
    ERQ("ERQ", "Event Replay Query Segment", "", 2.3, 2.8),
    ERR("ERR", "Error", "The ERR Segment is used to add error comments to acknowledgement messages", 2.1, 2.8),
    EVN("EVN", "Event Type", "The EVN segment is used to communicate necessary trigger event information to receiving applications.", 2.1, 2.8),
    FAC("FAC", "Facility", "", 2.3, 2.8),
    FHS("FHS", "File Header", "The FHS segment is used to head a file (group of batches).", 2.1, 2.8),
    FT1("FT1", "Financial Transaction", "The FT1 segment contains detail data necessary to post changes, payments, adjustments, etc. to patient accounting records.", 2.1, 2.8 ),
    FTS("FTS", "File Transfer", "", 2.1, 2.8),
    GOL("GOL", "Goal Detail", "", 2.3, 2.8),
    GP1("GP1", "Grouping/Reimbursement - Visit", "", 2.4, 2.8),
    GP2("GP2", "Grouping/Reimbursement - Procedure Line Item", "", 2.4, 2.8),
    GT1("GT1", "Guarantor", "", 2.1, 2.8),
    IAM("IAM", "Patient Adverse Reaction Information - Unique Identifier", "", 2.4, 2.8),
    IIM("IIM", "Inventory Item Master", "", 2.6, 2.8),
    ILT("ILT", "Material Lot Segment", "", 2.6, 2.8),
    IN1("IN1", "Insurance", "", 2.1, 2.8),
    IN2("IN2", "Insurance Additional Info", "The IN2 segment contains additional insurance policy coverage and benefit information necessary for proper billing and reimbursement.", 2.2, 2.8),
    IN3("IN3", "Insurance Additional Info - Certification", "The IN3 segment contains additional insurance information for certifying the need for patient care.", 2.2, 2.8),
    INV("INV", "Inventory Detail", "", 2.4, 2.8),
    IPC("IPC", "Imaging Procedure Control Segment", "", 2.6, 2.8),
    IPR("IPR", "Invoice Processing Results", "", 2.6, 2.8),
    ISD("ISD", "Interaction Status Detail", "", 2.4, 2.8),
    ITM("ITM", "Material Item", "", 2.6, 2.8),
    IVC("IVC", "Invoice", "", 2.6, 2.8),
    IVT("IVT", "Material Location", "", 2.6, 2.8),
    LAN("LAN", "Language Detail", "", 2.4, 2.8),
    LCC("LCC", "Location Charge Code", "", 2.3, 2.8),
    LCH("LCH", "Location Characteristic", "", 2.3, 2.8),
    LDP("LDP", "Location Department", "", 2.3, 2.8),
    LOC("LOC", "Location Identification", "", 2.3, 2.8),
    LRL("LRL", "Location Relationship", "", 2.3, 2.8),
    MFA("MFA", "Master File Acknowledgement", "", 2.2, 2.8),
    MFE("MFE", "Master File Entry", "", 2.2, 2.8),
    MFI("MFI", "Master File Identification", "", 2.2, 2.8),
    MRG("MRG", "Merge Patient Information", "", 2.1, 2.8 ),
    MSA("MSA", "Message Acknowledgement", "", 2.1, 2.8),
    MSH("MSH", "Message Header", "", 2.1, 2.8),
    NCK("NCK", "System Clock", "",2.1, 2.8),
    NDS("NDS", "Notification Detail", "The equipment notification detail segment is the data necessary to maintain an adequate audit trail as well as notifications of events.", 2.6, 2.8 ),
    NK1("NK1", "Next Of Kin", "", 2.1, 2.8),
    NPU("NPU", "Best Status Update", "The NPU segment allows the updating of census (bed status) data without sending patient specific data.", 2.1, 2.8),
    NSC("NSC", "Status Change", "The NSC segment can be used to request the start-up, shut-down, and/or migration (to a different cpu or file-server/file-system) of a particular application. It can also be used in an unsolicited update from one system to another to announce the start-up, shut-down, or migration of an application.", 2.1, 2.8),
    NST("NST", "Statistics", "The NST segment allows network statistical information to be passed between the various systems on the network. Some fields in this segment refer to portions of lower level protocols; they contain information that can be used by network management applications monitoring the state of various network links.", 2.1, 2.8),
    NTE("NTE", "Notes and Comments", "The NTE segment is a common format for sending notes and comments", 2.1, 2.8),
    OBR("OBR", "Observation Request", "The Observation Request (OBR) segment is used to transmit information specific to an order for a diagnostic study or observation, physical exam, or assessment.", 2.1, 2.8),
    OBX("OBX", "Observation Result", "", 2.1, 2.8),
    ODS("ODS", "Dietary Orders, Supplements, and Preferences", "", 2.2, 2.8),
    ODT("ODT", "Diet Tray Instruction", "", 2.2, 2.8),
    OM1("OM1", "General - Fields That Apply To Most Observations", "The OM1 segment contains attributes that apply to the definition of most observations.  This segment also contains the field attributes that specify what additional segments might also be defined for this observation.", 2.2, 2.8),
    OM2("OM2", "Numeric Observation", "This segment contains attributes of observations with continuous values (including those with data types of numeric, date, or time stamp).", 2.2, 2.8),
    OM3("OM3", "Categorical Test/Observation", "This segment applies to free text and other non-numeric data types.", 2.2, 2.8),
    OM4("OM4", "Observation That Require Specimens", "", 2.2, 2.8),
    OM5("OM5", "Observation Batteries", "", 2.2, 2.8),
    OM6("OM6", "Observations That Are Calculated From Other Observations", "", 2.2, 2.8),
    OM7("OM7", "Additional Basic Attributes", "", 2.4, 2.8),
    ORC("ORC", "Common Order", "The Common Order segment (ORC) is used to transmit data elements that are common to all orders (all types of services that are requested).  The ORC segment is required in both the Order (ORM) and Order Acknowledgement (ORR) messages.", 2.1, 2.8),
    ORG("ORG", "Practitioner Organization Unit", "", 2.4, 2.8),
    OVR("OVR", "Override Segment", "", 2.6, 2.8),
    ORO("ORO", "Order Other", "", 2.1, 2.1),
    PCE("PCE", "Patient Charge Cost Centre Exception", "", 2.6, 2.8),
    PCR("PCR", "Possible Causal Relationship", "", 2.3, 2.8),
    PD1("PD1", "Patient Demographics", "", 2.1, 2.8),
    PDA("PDA", "Patient Death and Autopsy", "", 2.4, 2.8),
    PDC("PDC", "Product Detail Country", "", 2.1, 2.8),
    PEO("PEO", "Product Experience Observation", "", 2.3, 2.8),
    PES("PES", "Product Experience Sender", "", 2.3, 2.8),
    PID("PID", "Patient Identification", "", 2.1, 2.8),
    PKG("PKG", "Packaging Segment", "", 2.6, 2.8),
    PMT("PMT", "Payment Information", "", 2.6, 2.8),
    PR1("PR1", "Procedures", "The PR1 segment contains information relative to various types of procedures that can be performed on a patient.  For example: Surgical, Nuclear Medicine, X-Ray with contrast, etc.", 2.1, 2.8),
    PRA("PRA", "Practitioner Detail", "", 2.2, 2.8),
    PRB("PRB", "Problem Detail", "", 2.3, 2.8),
    PRC("PRC", "Pricing", "", 2.3, 2.8),
    PRD("PRD", "Provider Data", "", 2.3, 2.8),
    PSG("PSG", "Product/Service Group", "", 2.6, 2.8),
    PSH("PSH", "Product Summary Header", "", 2.3, 2.8),
    PSL("PSL", "Product/Service Line Item", "", 2.6, 2.8),
    PSS("PSS", "Product/Service Section", "", 2.6, 2.8),
    PTH("PTH", "Pathway", "", 2.3, 2.8),
    PV1("PV1", "Patient Visit", "", 2.1, 2.8),
    PV2("PV2", "Patient Visit - Additional Information", "The PV2 segment is a contiuation of viist specific information contained in the PV1 segment.", 2.2, 2.8),
    PYE("PYE", "Payee Information", "", 2.6, 2.8),
    QAK("QAK", "Query Acknowledgement", "", 2.3, 2.8),
    QID("QID", "Query Identification", "", 2.4, 2.8),
    QPD("QPD", "Query Parameter Definition", "", 2.4, 2.8),
    QRD("QRD", "Query Definition", "", 2.1, 2.8),
    QRF("QRF", "Query Filter", "", 2.1, 2.8),
    QRI("QRI", "Query Response Instance", "", 2.6, 2.8),
    RCP("RCP", "Response Control Parameter", "", 2.6, 2.8),
    RDF("RDF", "Table Row Definition", "", 2.3, 2.8),
    RDT("RDT", "Table Row Data", "", 2.3, 2.8),
    REL("REL", "Clinical Relationship", "", 2.6, 2.8),
    RF1("RF1", "Referral Information Segment", "", 2.3, 2.8),
    RFI("RFI", "Request for Information", "", 2.6, 2.8),
    RGS("RGS", "Resource Group", "", 2.3, 2.8),
    RMI("RMI", "Risk Management Incident", "", 2.4, 2.8),
    ROL("ROL", "Role", "", 2.3, 2.8),
    RQ1("RQ1", "Requisition Detail 1", "RQ1 contains additional detail for each nonstock requisitioned item. This segment definition is paired with a preceeding RQD segment", 2.2, 2.8),
    RQD("RQD", "Requisition Detail", "RQD contains the detail for each requisitioned item.", 2.2, 2.8),
    RX1("RX1", "Pharmacy Order", "", 2.1, 2.1),
    RXA("RXA", "Pharmacy/Treatment Administration", "", 2.2, 2.8),
    RXC("RXC", "Pharmacy Component Order", "", 2.2, 2.8),
    RXD("RXD", "Pharmacy Dispense", "", 2.2, 2.8),
    RXE("RXE", "Pharmacy Encoded Order", "", 2.2, 2.8),
    RXG("RXG", "Pharmacy Give", "", 2.2,2.8),
    RXO("RXO", "Pharmacy Prescription Order", "", 2.2, 2.8),
    RXR("RXR", "Pharmacy Route", "", 2.2, 2.8),
    SAC("SAC", "Specimen and Container Detail", "", 2.4, 2.8),
    SCD("SCD", "Anti-Microbial Cycle Data", "", 2.6, 2.8),
    SCH("SCH", "Schedule Activity Information", "", 2.3, 2.8),
    SCP("SCP", "Steralizer Configuration", "", 2.6, 2.8),
    SDD("SDD", "Sterilization Device Data", "", 2.6, 2.8),
    SFT("SFT", "Software Segment", "", 2.6, 2.8),
    SID("SID", "Substance Identifier", "", 2.3, 2.8),
    SLT("SLT", "Sterilization Lot Segment", "", 2.6, 2.8),
    SPM("SPM", "Specimen", "", 2.6, 2.8),
    SPR("SPR", "Stored Procedure Request Definition", "", 2.3, 2.8),
    STF("STF", "Staff Identification Segment", "",2.2, 2.8),
    STZ("STZ", "Sterilization Parameter", "", 2.6, 2.8),
    TCC("TCC", "Test Code Configuration", "", 2.4, 2.8),
    TCD("TCD", "Test Code Detail", "", 2.4, 2.8),
    TQ1("TQ1", "Timing/Quantity", "", 2.6, 2.8),
    TQ2("TQ2", "Timing/Quantity Relationship", "", 2.6, 2.8),
    TXA("TXA", "Document Notification Segment", "", 2.3, 2.8),
    UB1("UB1", "Ub82 Data", "",2.1, 2.8),
    URD("URD", "Results/Update Definition", "", 2.1, 2.8),
    URS("URS", "Unsolicited Selection", "", 2.1, 2.8),
    VAR("VAR", "Variance", "", 2.3, 2.8),
    VND("VND", "Purchasing Vendor", "", 2.6, 2.8),
    VTQ("VTQ", "Virtual Table Query Request", "The VTQ segment is used to define queries that are responded to with the Tabular Data Message (TBR). The VTQ query message is an alternate method to the EQQ query message that some systems may find easier to implement, due to its use of HL7 delimiters that separate components of the selection definition, and its limited selection criteria.  Queries involving complex selection criteria (nested operators, etc.) may need to be formatted as an EQL segment.", 2.3, 2.6  ),
    ZXX("ZXX", "Any Z Segment", "", 2.2, 2.8);

    private String key;
    private String name;
    private String description;
    private Double since;
    private Double until;

    private HL7v2SegmentTypeEnum(String key, String name, String description, Double since, Double until){
        this.key = key;
        this.name = name;
        this.since = since;
        this.until = until;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getSince() {
        return since;
    }

    public Double getUntil() {
        return until;
    }
}
