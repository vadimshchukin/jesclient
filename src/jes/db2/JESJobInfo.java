package jes.db2;

public class JESJobInfo {
    private int status;
    public int getStatus() {
        return status;
    }
    public static final int STATUS_JOB_RECEIVED_BUT_NOT_YET_RUN = 1;
    public static final int STATUS_JOB_RUNNING = 2;
    public static final int STATUS_JOB_FINISHED_AND_HAS_OUTPUT = 3;
    public static final int STATUS_JOB_NOT_FOUND = 4;
    public static final int STATUS_JOB_IN_UNKNOWN_PHASE = 5;
    
    private int maxRC;
    public int getMaxRC() {
        return maxRC;
    }
    
    private int completionType;
    public int getCompletionType() {
        return completionType;
    }
    public static final int COMPLETION_TYPE_INFO_NOT_AVAILABLE = 0;
    public static final int COMPLETION_TYPE_JOB_ENDED_NORMALLY = 1;
    public static final int COMPLETION_TYPE_JOB_ENDED_BY_COMPLETION_CODE = 2;
    public static final int COMPLETION_TYPE_JOB_HAD_JCL_ERROR = 3;
    public static final int COMPLETION_TYPE_JOB_WAS_CANCELLED = 4;
    public static final int COMPLETION_TYPE_JOB_TERMINATED_ABNORMALLY = 5;
    public static final int COMPLETION_TYPE_CONVERTED_TERMINATED_ABNORMALLY = 6;
    public static final int COMPLETION_TYPE_JOB_FAILED_SECURITY_CHECKS = 7;
    public static final int COMPLETION_TYPE_JOB_FAILED_IN_END_OF_MEMORY = 8;
    
    private int systemAbendCode;
    public int getSystemAbendCode() {
        return systemAbendCode;
    }
    
    private int userAbendCode;
    public int getUserAbendCode() {
        return userAbendCode;
    }
    
    public JESJobInfo(int status, int maxRC, int completionType, int systemAbendCode, int userAbendCode) {
        this.status = status;
        this.maxRC = maxRC;
        this.completionType = completionType;
        this.systemAbendCode = systemAbendCode;
        this.userAbendCode = userAbendCode;
    }
}
