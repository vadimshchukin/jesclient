# jesclient
IBM mainframe JCL job submitter. It uses FTP to communicate with JES. The JAR file includes all dependencies.

Features:
  - List jobs using JOBNAME and/or OWNER filters.
  - List jobs spool files.
  - Read and print jobs spool files.
  - Submit both local and remote JCL jobs.
  - Cancel/purge jobs.

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

Dependencies
-----------

jesclient depends on the following libraries:

* [Apache Commons CLI].
* [Apache Commons Net].

[Apache Commons CLI]:https://commons.apache.org/cli/
[Apache Commons Net]:https://commons.apache.org/net/
