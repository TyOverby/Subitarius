0.2-alpha
=========
* Rename to Subitarius
* Renamed client module to instance
    * The instance runs a short launcher UI before running an embedded Jetty server
    * Article URLs and tag mappings are fetched from the central server
* Added central module
    * Searcher only stores URLs and is now in the central module
* Added domain module
    * Domain now contains classes designed for distributed use (DistributedEntity)
    * Domain now contains classes digitally signed by central server (SignedEntity)
* Added util module to contain SimpleHttpClient and SLF4J utilities

0.1-alpha
=========
* First numbered version
* Maven project has parent and client modules, more to come
* Legacy design
    * Client runs in servlet container (Tomcat)
    * Depends on full MySQL database running on the same machine
