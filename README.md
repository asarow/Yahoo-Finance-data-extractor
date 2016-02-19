# Data Extractor
The Yahoo! Finance data extractor is a tool which allows quick retrieval of basic stock information, financial statements, and the ability to export financial statements in an excel-friendly format.

## Features
* An easy to use Graphical User Interface which displays basic financial information
* An option to quickly build and view the company's income statement, balance sheet, and statement of cash flows
* An option to view annual or quarterly financial statements
* The ability to export annual or quarterly financial statements in an excel-friendly format

## How to run (for now)
1.  Clone the repository from github.  

    `git clone git@github.com:asarow/Yahoo-Finance-data-extractor.git`

2.  Navigate to the Yahoo-Finance-data-extractor directory. Then the src directory. Compile.  

    `cd Yahoo-Finance-data-extractor/src`  

    `javac -cp ../lib/YahooFinanceYQLWrapper.jar:../lib/poi-3.12/poi-3.12-20150511.jar:. dev/stockanalyzer/*/*.java`  


3.  Run.  

    `java -cp ../lib/YahooFinanceYQLWrapper.jar:../lib/poi-3.12/poi-3.12-20150511.jar:. dev/stockanalyzer/main.StockMain`  

## Known issues
- GUI does not appear on certain Mac OS X versions.
- Current Assets/Assets is not appearing for balance sheet.

## Quick Notes
The data extractor is not affiliated nor endorsed by Yahoo!. The data extractor relies on the Yahoo Query Language platform (YQL) and uses the YQL Console to fetch data from the Yahoo! Finance database. The YQL terms of use can be found [here](https://policies.yahoo.com/us/en/yahoo/terms/product-atos/yql/index.htm). YQL usage limits can be found [here](https://developer.yahoo.com/yql/guide/usage_info_limits.html).

The data extractor does not work with any other platforms (i.e. Google Finance, Reuters). Because the data extractor relies on the YQL platform, the data extractor may fail to work if any changes are made to the YQL platform, Yahoo! Finance database, or if there are any unforeseen issues on Yahoo!'s end. 

## Version notices:

###1.1
- Used YQLWrapper as found [here](https://github.com/asarow/Yahoo-Query-Language-Wrapper) to replace data extraction code.

### 1.0
- First completely functioning build. 




