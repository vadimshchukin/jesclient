package jes.ftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPFile;

public class JESJob {
    public JESClient clientJES;
    public String name;
    public String handle;
    public String owner;
    public String status;
    public String type;
    public Integer conditionCode = null;
    public Integer abendCode = null;
    public Integer spoolFileCount = null;
    private String spool = null;

    public JESJob(JESClient clientJES) {
        this.clientJES = clientJES;
    }

    public boolean parseDetails(String details) {
        Pattern pattern = Pattern
                .compile("(?<name>[^ ]+) +(?<handle>[^ ]+) +(?<owner>[^ ]+) +(?<status>[^ ]+) +(?<class>[^ ]+)"
                        + "(?<completion> +(?<result>[^=]+)=(?<code>\\d+)(?<spool> +(?<files>\\d+) +spool files)?)? +");
        Matcher matcher = pattern.matcher(details);
        if (!matcher.matches()) {
            return false;
        }

        name = matcher.group("name");
        handle = matcher.group("handle");
        owner = matcher.group("owner");
        status = matcher.group("status");
        type = matcher.group("class");
        if (matcher.group("completion") != null) {
            String result = matcher.group("result");
            if (result.equals("RC")) {
                conditionCode = Integer.parseInt(matcher.group("code"));
            } else if (result.equals("ABEND")) {
                abendCode = Integer.parseInt(matcher.group("code"));
            }
            if (matcher.group("spool") != null) {
                spoolFileCount = Integer.parseInt(matcher.group("files"));
            }
        }

        return true;
    }

    public List<JESSpoolFile> getSpoolFiles() throws IOException {
        List<JESSpoolFile> spool = new ArrayList<JESSpoolFile>();
        for (FTPFile file : this.clientJES.listFiles(this.handle)) {
            String rawListing = file.getRawListing();

            Pattern pattern = Pattern.compile("^ +\\d+");
            Matcher matcher = pattern.matcher(rawListing);
            if (matcher.find()) {
                JESSpoolFile spoolFile = new JESSpoolFile(this);
                spoolFile.handle = Integer.parseInt(rawListing.substring(9, 12).trim());
                spoolFile.step = rawListing.substring(13, 21).trim();
                spoolFile.procedure = rawListing.substring(22, 30).trim();
                spoolFile.type = rawListing.substring(31, 32).trim();
                spoolFile.nameDD = rawListing.substring(33, 41).trim();
                spoolFile.byteCount = Integer.parseInt(rawListing.substring(42).trim());
                spool.add(spoolFile);
                continue;
            }

            parseDetails(rawListing);
        }
        return spool;
    }

    public void setSpool(String spool) {
        this.spool = spool;
    }

    public String readSpool() throws IOException {
        if (spool == null) {
            spool = this.clientJES.readSpool(handle);
        }
        return spool;
    }

    public void purge() throws IOException {
        this.clientJES.deleteFile(this.handle);
    }
}
