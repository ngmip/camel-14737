### Introduction
This is a simple springboot + camel project that illustrate the camel issue [14737](https://issues.apache.org/jira/browse/CAMEL-14737)

In brief, when the application is gracefully shutdown while it's still processing a file the datasource is closed too early which causes multiple error for inflight messages.

### How it works
The application simply reads gziped text file line by line from a directory and insert them in a h2 database.

### How to use
- start the springboot application with the parameter `-DfileComponentUri=...` configured with a valid directory containing at least one "*big**" gziped text file

### How to produce the issue
- when you see the log `processing file: xxx` then shutdown the application *gracefully* before the file is fully processed
- you'll see thousands of "datasource already closed" errors  

**big gzip file*: I use an application log file of 100Mb ~20 Mb compressed.
It just has to be big enough for you to have the time to call the graceful shutdown before the file it completely processed
Due to the file size, I preferred not to upload such file on github.

### Application parameter
- `fileComponentUri`:  valid URI of a file endpoint. Something like `file://C:/tmp/14737`. It must be `file://` as it's used to build a `org.apache.camel.component.file.FileEndpoint`
