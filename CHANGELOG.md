0.2.1-alpha
===========
* Add buttons to the main page to fetch entities and parse articles
* Fix a number of article parser bugs and fetch/parse performance issues

0.2-alpha
=========
* Rename to com.subitarius
* Renamed client module to instance
    * The instance is the WAR for the client, containing the GWT app
    * Instance is responsible for running the local database
    * No communication with central server, yet
* Added launcher module, which just launches instances in an embedded Jetty server
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
