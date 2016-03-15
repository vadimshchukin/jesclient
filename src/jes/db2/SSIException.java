package jes.db2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSIException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private String jobId;
    
    private String nameCSECT;
    public String getNameCSECT() {
        return nameCSECT;
    }
    
    private int functionCodeSSI;
    public int getFunctionCodeSSI() {
        return functionCodeSSI;
    }
    
    private int returnCodeSSI;
    public int getReturnCodeSSI() {
        return returnCodeSSI;
    }
    
    private int processSYSOUTReturnCode;
    public int getProcessSYSOUTReturnCode() {
        return processSYSOUTReturnCode;
    }
    
    private final static Pattern pattern = Pattern.compile(
            "DSNA619I +(?<nameCSECT>\\w+) SUBSYSTEM INTERFACE ERROR, FUNCTION CODE=(?<functionCode>\\d+), "
            + "RETURN CODE=(?<returnCode>\\d+), SSOBRETN=(?<SSOBRETN>\\d+)");
    
    public final static int INVALID_SEARCH_ARGUMENTS = 8;
    
    public SSIException(String message, String jobId) {
        super(message);
        this.jobId = jobId;
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            nameCSECT = matcher.group("nameCSECT");
            functionCodeSSI = Integer.parseInt(matcher.group("functionCode"));
            returnCodeSSI = Integer.parseInt(matcher.group("returnCode"));
            processSYSOUTReturnCode = Integer.parseInt(matcher.group("SSOBRETN"));
        }
    }
    
    @Override
    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(super.getMessage());
        if (processSYSOUTReturnCode == INVALID_SEARCH_ARGUMENTS) {
            messageBuilder.append(String.format("%nInvalid search arguments (possibly job with ID '%s' was not found).", jobId));
        }
        return messageBuilder.toString();
    }
}