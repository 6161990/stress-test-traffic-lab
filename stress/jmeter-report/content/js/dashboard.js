/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 97.40259740259741, "KoPercent": 2.5974025974025974};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9506493506493506, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "POST /api/users"], "isController": false}, {"data": [1.0, 500, 1500, "GET /api/products/search?name=laptop"], "isController": false}, {"data": [0.95, 500, 1500, "GET /api/products"], "isController": false}, {"data": [0.9, 500, 1500, "GET /api/users"], "isController": false}, {"data": [1.0, 500, 1500, "POST /api/products"], "isController": false}, {"data": [1.0, 500, 1500, "GET /api/products/in-stock"], "isController": false}, {"data": [1.0, 500, 1500, "GET /api/products/1"], "isController": false}, {"data": [1.0, 500, 1500, "GET /api/users/1"], "isController": false}, {"data": [0.85, 500, 1500, "GET /api/orders/user/1"], "isController": false}, {"data": [0.5, 500, 1500, "GET /actuator/health"], "isController": false}, {"data": [0.0, 500, 1500, "POST /api/orders"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 385, 10, 2.5974025974025974, 63.91168831168827, 3, 810, 16.0, 123.0, 396.8999999999999, 802.14, 103.52245227211617, 217.19863370529714, 16.966296383436408], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["POST /api/users", 20, 0, 0.0, 70.15, 7, 234, 23.5, 184.5, 231.54999999999995, 234.0, 18.570102135561743, 5.000689124883937, 4.548224233983287], "isController": false}, {"data": ["GET /api/products/search?name=laptop", 60, 0, 0.0, 14.933333333333328, 3, 81, 9.0, 37.699999999999996, 46.79999999999998, 81.0, 21.27659574468085, 3.615359042553192, 3.0543550531914896], "isController": false}, {"data": ["GET /api/products", 60, 0, 0.0, 102.35000000000002, 4, 786, 17.5, 609.7999999999997, 781.0, 786.0, 16.242555495397944, 95.00599578708717, 2.030319436924743], "isController": false}, {"data": ["GET /api/users", 20, 0, 0.0, 198.50000000000003, 9, 785, 43.0, 779.8, 784.75, 785.0, 10.26167265264238, 18.610304964084143, 1.2526455874807594], "isController": false}, {"data": ["POST /api/products", 60, 0, 0.0, 30.666666666666664, 5, 166, 11.0, 121.99999999999999, 145.29999999999995, 166.0, 21.505376344086024, 6.668626792114695, 6.126092069892473], "isController": false}, {"data": ["GET /api/products/in-stock", 60, 0, 0.0, 16.333333333333343, 3, 53, 12.5, 38.8, 44.84999999999999, 53.0, 21.0896309314587, 125.41671243409489, 2.821561950790861], "isController": false}, {"data": ["GET /api/products/1", 60, 0, 0.0, 17.066666666666666, 3, 78, 11.0, 44.0, 48.74999999999998, 78.0, 20.847810979847115, 6.2502714558721335, 2.646694753300903], "isController": false}, {"data": ["GET /api/users/1", 20, 0, 0.0, 34.199999999999996, 6, 79, 27.5, 73.50000000000001, 78.75, 79.0, 17.605633802816904, 4.57333846830986, 2.1835112235915495], "isController": false}, {"data": ["GET /api/orders/user/1", 10, 0, 0.0, 289.1, 16, 784, 42.0, 783.8, 784.0, 784.0, 9.149130832570906, 1.5546374656907593, 1.188314844464776], "isController": false}, {"data": ["GET /actuator/health", 5, 0, 0.0, 799.6, 776, 810, 803.0, 810.0, 810.0, 810.0, 6.172839506172839, 3.267264660493827, 0.7896894290123456], "isController": false}, {"data": ["POST /api/orders", 10, 10, 100.0, 77.89999999999999, 9, 148, 76.5, 148.0, 148.0, 148.0, 41.84100418410041, 8.417233263598327, 10.415304654811717], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["500", 10, 100.0, 2.5974025974025974], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 385, 10, "500", 10, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /api/orders", 10, 10, "500", 10, "", "", "", "", "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
