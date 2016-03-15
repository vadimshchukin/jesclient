package jes.db2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class JESClient {

    private Connection connection;
    public Connection getConnection() {
        return connection;
    }

    public JESClient(Connection connection) {
        this.connection = connection;
    }

    public JESJob submit(Iterable<String> linesJCL, String userID, String password) throws Exception {

        try (
            Statement deleteStatement = connection.createStatement();
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO SYSIBM.JOB_JCL VALUES (?, ?)");
            CallableStatement callStatement = connection.prepareCall("CALL SYSPROC.ADMIN_JOB_SUBMIT (?, ?, ?, ?, ?)");
        ) {
            deleteStatement.executeUpdate("DELETE FROM SYSIBM.JOB_JCL");

            int lineNumber = 1;
            for (String lineJCL : linesJCL) {
                insertStatement.setInt(1, lineNumber++);
                insertStatement.setString(2, lineJCL);
                insertStatement.executeUpdate();
            }

            callStatement.setString(1, userID);
            callStatement.setString(2, password);
            callStatement.registerOutParameter(3, java.sql.Types.CHAR);
            callStatement.registerOutParameter(4, java.sql.Types.INTEGER);
            callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
            callStatement.executeUpdate();
            connection.commit();

            int returnCode = callStatement.getInt("RETURN_CODE");
            String message = callStatement.getString("MSG");
            if (returnCode != 0) {
                throw new Exception(message);
            }
            
            String jobID = callStatement.getString("JOB_ID");
            return new JESJob(this, jobID);
            
        }
    }
    
    public JESJob getExistingJob(String jobId) {
        JESJob jobJES = new JESJob(this, jobId);
        jobJES.setAutoPurge(false);
        return jobJES;
    }
    
    public JESJob submit(Iterable<String> linesJCL) throws Exception {
        return submit(linesJCL, null, null);
    }
}
