WebCrawling-XPath
=================

A topic-specific crawler looks for documents or data matching a particular category here, just like RSS Feeds and the category will be specified as an XPath expression.

1. Implemented the mercator design for crawling. 
When the crawling starts, we maintain a urlfrontier called urlList
We maintain the contentSeen by using an ArrayList called alreadyVisited
The component which retrieves urls is a function getHrefs()

2. Subscriptions have been added. Inside user home page, we have a button to list channels and subscribe. 

3. Admin web interface is also implemented. 
Find "Login as Admin" in http://localhost:8008/homepage
Enter username as: admin and password also as: admin


Any special instructions for building and running your solution?
commandline to run the Server
8008
./testfiles
./web.xml

commandline to run the crawler (We have enabled the number of max files to crawl) 
http://crawltest.cis.upenn.edu/
./BDBStore
50
100


Please empty BDBStore and run two tests in XPathCrawlerTest/PutInitialData.java before crawling.
1. testXpaths
2. testrssagg

If you want to test from other places:
String args[] = {"http://crawltest.cis.upenn.edu/","./BDBStore","100","100"};
startCrawl(args);

Please make sure to open the BerkeleyDB from only a single source. 
Multiple connections will result in an error.

To test the web interface:
Home page: 
http://localhost:8008/homepage

The user home page has options to logout, add a channel, view a list and subscribe. 
If there are any problems while browsing, please try logging in again. 
As the application runs on my own server, there might be some session problems.

I have given two options to view channels, 
one is to view as an xml file 
and another using as an xsl file. 

RSS and warandpeace.xp files have been added to rss folder. 
I have also added rssaggregator as a channel

The BDBStore already has existing data using a test case, XPathCrawlerTest/PutInitialData.java:
We add users and channels and xpaths as follows to test our application:

bdb.putUser("a", "a");
bdb.putUser("b", "b");
bdb.putChannel("a", "channel1");
bdb.putChannel("a", "channel2");
bdb.putChannel("a", "channel3");
bdb.putChannel("b", "channel4");
bdb.putChannel("b", "channel5");
bdb.putChannelandXpaths("channel1", "/rss/channel/description[contains(text(),\"news\")]");
bdb.putChannelandXpaths("channel1","/rss/channel/language[text()=\"en-us\"]");
bdb.putChannelandXpaths("channel1","/rss/channel/language[text()=\"en-gb\"]");
bdb.putChannelandXpaths("channel2","/rss/channel/language[text()=\"en-us\"]");
bdb.putChannelandXpaths("channel2","/rss/channel/language[text()=\"it-IT\"]");
bdb.putChannelandXpaths("channel3","/rss/channel/item/link[contains(text(),\"corriere\")]");
bdb.putChannelandXpaths("channel4","/dwml/data/parameters/temperature[@units=\"Fahrenheit\"]");
bdb.putChannelandXpaths("channel5","/table/T/C_MKTSEGMENT[text()=\"AUTOMOBILE\"]");

In case the data is lost, please run two tests in XPathCrawlerTest/PutInitialData.java
1. testXpaths
2. testrssagg

