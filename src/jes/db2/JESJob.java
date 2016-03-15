package jes.db2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JESJob implements AutoCloseable {
    private Connection connection;
    
    private String id;
    public String getId() {
        return id;
    }
    
    private boolean autoPurge = true;
    public void setAutoPurge(boolean autoPurge) {
        this.autoPurge = autoPurge;
    }
    
    public JESJob(JESClient clientJES, String id) {
        this.connection = clientJES.getConnection();
        this.id = id;
    }
    
    public JESJobInfo retrieveInfo(String userID, String password) throws Exception {
        try (CallableStatement callStatement = connection.prepareCall("CALL SYSPROC.ADMIN_JOB_QUERY (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            callStatement.setString(1, userID); // user-ID
            callStatement.setString(2, password); // password
            callStatement.setString(3, id); // job-ID
            callStatement.registerOutParameter(4, java.sql.Types.INTEGER); // status
            callStatement.registerOutParameter(5, java.sql.Types.INTEGER); // max-RC
            callStatement.registerOutParameter(6, java.sql.Types.INTEGER); // completion-type
            callStatement.registerOutParameter(7, java.sql.Types.INTEGER); // system-abend-code
            callStatement.registerOutParameter(8, java.sql.Types.INTEGER); // user-abend-code
            callStatement.registerOutParameter(9, java.sql.Types.INTEGER); // return-code
            callStatement.registerOutParameter(10, java.sql.Types.VARCHAR); // message
            callStatement.execute();

            int returnCode = callStatement.getInt("RETURN_CODE");
            String message = callStatement.getString("MSG");
            if (returnCode == 4) {
                throw new Exception("The job was not found, or the job status is unknown.");
            } else if (returnCode == 12) {
                throw new Exception(message);
            }
            
            int status = callStatement.getInt("STATUS");
            int maxRC = callStatement.getInt("MAXRC");
            int completionType = callStatement.getInt("COMPLETION_TYPE");
            int systemAbendCode = callStatement.getInt("SYSTEM_ABENDCD");
            int userAbendCode = callStatement.getInt("USER_ABENDCD");
            return new JESJobInfo(status, maxRC, completionType, systemAbendCode, userAbendCode);
            
        }
    }
    
    public JESJobInfo retrieveStatus() throws Exception {
        return retrieveInfo(null, null);
    }
    
    public final static int PROCESSING_OPTION_CANCEL = 1;
    public final static int PROCESSING_OPTION_PURGE = 2;
    public void cancel(String userID, String password, int processingOption) throws Exception {
        try (CallableStatement callStatement = connection.prepareCall("CALL SYSPROC.ADMIN_JOB_CANCEL (?, ?, ?, ?, ?, ?)")) {
            callStatement.setString(1, userID); // user-ID
            callStatement.setString(2, password); // password
            callStatement.setInt(3, processingOption); // processing-option
            callStatement.setString(4, id); // job-ID
            callStatement.registerOutParameter(5, java.sql.Types.INTEGER); // return-code
            callStatement.registerOutParameter(6, java.sql.Types.VARCHAR); // message
            callStatement.execute();

            int returnCode = callStatement.getInt("RETURN_CODE");
            String message = callStatement.getString("MSG");
            if (returnCode != 0) {
                throw new Exception(message);
            }
            
        }
    }
    
    public void cancel(int processingOption) throws Exception {
        cancel(null, null, processingOption);
    }
    
    public void cancel(String userID, String password) throws Exception {
        cancel(userID, password, PROCESSING_OPTION_CANCEL);
    }
    
    public void cancel() throws Exception {
        cancel(null, null);
    }
    
    public void purge(String userID, String password) throws Exception {
        cancel(userID, password, PROCESSING_OPTION_PURGE);
    }
    
    public void purge() throws Exception {
        purge(null, null);
    }
    
    public List<String> retrieveSpool(String userID, String password) throws Exception {
        try (CallableStatement callStatement = connection.prepareCall("CALL SYSPROC.ADMIN_JOB_FETCH (?, ?, ?, ?, ?)")) {
            callStatement.setString(1, userID);
            callStatement.setString(2, password);
            callStatement.setString(3, id);
            callStatement.registerOutParameter(4, java.sql.Types.INTEGER);
            callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
            callStatement.execute();

            int returnCode = callStatement.getInt("RETURN_CODE");
            String message = callStatement.getString("MSG");
            if (returnCode != 0) {
                if (message.startsWith("DSNA619I")) {
                    throw new SSIException(message, id);
                } else {
                    throw new Exception(message);                    
                }
            }
            
            ResultSet resultSet = callStatement.getResultSet();
            List<String> spoolLines = new ArrayList<String>();
            while (resultSet.next()) {
                spoolLines.add(resultSet.getString("TEXT"));
            }
            resultSet = callStatement.getResultSet();
            return spoolLines;
            
        }
    }
    
    public List<String> retrieveSpool() throws Exception {
        return retrieveSpool(null, null);
    }
    
    public void waitUntilFinished(String userID, String password, long milliseconds) throws Exception {
        while (true) {
            JESJobInfo jobInfo = retrieveInfo(userID, password);
            int jobStatus = jobInfo.getStatus();
            if (jobStatus == JESJobInfo.STATUS_JOB_RECEIVED_BUT_NOT_YET_RUN || jobStatus == JESJobInfo.STATUS_JOB_RUNNING) {
                Thread.sleep(milliseconds);
            } else if (jobStatus == JESJobInfo.STATUS_JOB_FINISHED_AND_HAS_OUTPUT) {
                break;
            } else if (jobStatus == JESJobInfo.STATUS_JOB_NOT_FOUND) {
                throw new Exception("Job not found.");
            } else if (jobStatus == JESJobInfo.STATUS_JOB_IN_UNKNOWN_PHASE) {
                throw new Exception("Job in an unknown phase.");
            }
        }
    }
    
    public void waitUntilFinished(long milliseconds) throws Exception {
        waitUntilFinished(null, null, milliseconds);
    }
    
    public void waitUntilFinished() throws Exception {
        waitUntilFinished(1000);
    }
    
    public void close() throws Exception {
        if (autoPurge) {
            purge();            
        }
    }
}
