# jesclient
Overview
-----------
z/OS JCL job submit utility. It uses FTP to communicate with JES. The JAR file includes all dependencies.

Features:
  - Lists jobs using JOBNAME and/or OWNER filters.
  - Lists job spool files.
  - Reads and prints job spool files.
  - Submits both local and remote JCL jobs.
  - Cancels/purges jobs.

Command-line options:
```text
 -b,--submit           submit job
 -e,--detailed         detailed jobs list
 -f,--filename <arg>   filename
 -g,--purge            purge
 -h,--help             print this help and exit
 -i,--list-spool       list spool files
 -j,--jobname <arg>    job name filter
 -l,--list-jobs        list jobs
 -o,--owner <arg>      job owner filter
 -p,--password <arg>   FTP password
 -r,--read-spool       read spool files
 -s,--hostname <arg>   FTP hostname
 -u,--username <arg>   FTP username
```

Source code
-----------
Source code contains 2 implementations of a JES client - one uses FTP to communicate with JES (jes.ftp package) and another uses DB2 stored procedures (jes.db2 package).

Dependencies
-----------

jesclient depends on the following libraries:

* [Apache Commons CLI].
* [Apache Commons Net].

[Apache Commons CLI]:https://commons.apache.org/cli/
[Apache Commons Net]:https://commons.apache.org/net/