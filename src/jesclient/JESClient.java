package jesclient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

public class JESClient extends FTPClient {
	public JESClient() {
		FTPClientConfig config = new FTPClientConfig();
		config.setUnparseableEntries(true);
		configure(config);
	}

	public boolean login(String username, String password) throws IOException {
		boolean result = super.login(username, password);
		if (result) {
			site("FILE=JES JESJOBNAME=*");
		}
		return result;
	}

	public int setOwnerFilter(String owner) throws IOException {
		return site(String.format("JESOWNER=%s", owner));
	}
	
	public int setNameFilter(String name) throws IOException {
		return site(String.format("JESJOBNAME=%s", name));
	}

	public List<JESJob> listJobs() throws IOException {
		return listJobs(false);
	}

	public List<JESJob> listJobs(boolean detailed) throws IOException {
		if (detailed) {
			return listJobsDetailed();
		} else {
			return listJobsSummary();
		}
	}

	public List<JESJob> listJobsSummary() throws IOException {
		List<JESJob> jobs = new ArrayList<JESJob>();
		String[] names = listNames();
		if (names == null) {
			return jobs;
		}
		for (String name : names) {
			JESJob job = new JESJob(this);
			job.handle = name;
			jobs.add(job);
		}
		return jobs;
	}

	public List<JESJob> listJobsDetailed() throws IOException {
		List<JESJob> jobs = new ArrayList<JESJob>();
		for (FTPFile file : listFiles()) {
			JESJob job = new JESJob(this);
			if (job.parseDetails(file.getRawListing())) {
				jobs.add(job);
			}
		}
		return jobs;
	}

	public String readSpool(String handle) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		retrieveFile(handle, outputStream);
		return outputStream.toString();
	}

	public JESJob submit(String sourceJCL) throws IOException {
		OutputStream outputStream = storeFileStream("job");
		outputStream.write(sourceJCL.getBytes());
		outputStream.close();
		completePendingCommand();

		Pattern pattern = Pattern.compile("It is known to JES as (?<handle>.+)");
		Matcher matcher = pattern.matcher(getReplyString());
		JESJob job = null;
		if (matcher.find()) {
			job = new JESJob(this);
			job.handle = matcher.group("handle");
		}
		return job;
	}

	public JESJob execute(String datasetName) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		retrieveFile(String.format("'%s'", datasetName), outputStream);
		JESJob job = new JESJob(this);
		job.setSpool(outputStream.toString());
		return job;
	}
}
