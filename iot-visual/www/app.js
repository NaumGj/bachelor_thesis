app = angular.module('IoTVisual', ['ngRoute', 'ngAnimate', 'ngMaterial', 'ngResource', 'ngMessages', 'material.svgAssetsCache', 'IoTVisual.services'])
.config(['$routeProvider', '$locationProvider',
	function($routeProvider, $locationProvider) {

		$routeProvider
		.when('/', {
			templateUrl: 'first.html',
			controller: 'FirstCtrl',
			controllerAs: 'first'
		})
        .when('/counters', {
			templateUrl: 'counters.html',
			controller: 'CountersCtrl',
			controllerAs: 'counters'
		})
		.when('/fire', {
			templateUrl: 'fire.html',
			controller: 'FireCtrl',
			controllerAs: 'fire'
		})
		.when('/shoplifting', {
			templateUrl: 'shoplifting.html',
			controller: 'ShopliftingCtrl',
			controllerAs: 'shoplifting'
		})
		.otherwise({
			redirectTo: '/'
		});

		$locationProvider.html5Mode(false);
		$locationProvider.hashPrefix('!');
	}])
.controller('AppCtrl', function ($scope, $timeout, $mdSidenav, $log, $location) {

	$scope.toggleLeft = buildDelayedToggler('left');
	/*
	 * Supplies a function that will continue to operate until the
	 * time is up.
	 */
	 function debounce(func, wait, context) {
		var timer;
		return function debounced() {
			var context = $scope,
			args = Array.prototype.slice.call(arguments);
			$timeout.cancel(timer);
			timer = $timeout(function() {
				timer = undefined;
				func.apply(context, args);
			}, wait || 10);
		};
	 }
	/**
	 * Build handler to open/close a SideNav; when animation finishes
	 * report completion in console
	 */
	 function buildDelayedToggler(navID) {
		return debounce(function() {$mdSidenav(navID).toggle()}, 200);
	 }
})
.controller('FirstCtrl', ['$routeParams', function($routeParams) {

	this.name = "FirstCtrl";
	this.params = $routeParams;

}])
.controller('CountersCtrl', function($scope, $interval, Counter, Latency) {

	var index = 0;
	$scope.latencyData = [{index: index, value: 0}];
	$scope.countData = [{index: index, value: 0}];

	$interval(function(){
		Counter.query().$promise.then(function(counts) {
			console.log(counts);
			// console.log(count[0]);
			if(counts.length > 0) {
				$scope.count = counts[counts.length - 1].counts;
			} else {
				$scope.count = 0;
			}
			$scope.countData = [];
			index = 0;
			counts.forEach(function(count) {
				// console.log(latency);
				$scope.countData.push({index: index, value: count.counts});
				index = index + 1;
			});
		});
		Latency.query().$promise.then(function(avgLatencies) {
			console.log(avgLatencies);
			// console.log(latency[0]);
			if(avgLatencies.length > 0) {
				$scope.latency = parseFloat(Math.round(avgLatencies[avgLatencies.length - 1].ten * 100) / 100).toFixed(3);
			} else {
				$scope.latency = 0;
			}
			$scope.latencyData = [];
			index = 0;
			avgLatencies.forEach(function(latency) {
				// console.log(latency);
				$scope.latencyData.push({index: index, value: latency.ten});
				index = index + 1;
			});
		});
	}, 2000, 0);

})
.controller('FireCtrl', function($scope, $interval, $mdToast, TempEvent, SmokeEvent, Fire) {

	this.name = "FireCtrl";

    $scope.warningTemp = "Warning";
	$scope.warningSmoke = "Warning";
	$scope.toastRoomIds = [];
	$scope.fires = [];
    
    $scope.sendTempEventCheck = function(){
        if($scope.temperature == null | $scope.temperature == "") {
            return false;
        }
        if ($scope.temperature.room_number == null | $scope.temperature.room_number == "") {
            $scope.warningTemp = "Specify the room number (1 to 10).";
            return false;
        }
        if ($scope.temperature.celsius_temperature == null | $scope.temperature.celsius_temperature == "") {
            $scope.warningTemp = "Specify the temperature in Celsius degrees.";
            return false;
        }
        return true;
    }
    $scope.sendTemperatureEvent = function(temp) {
        var data = JSON.parse(JSON.stringify(temp));
		data["@type"] = "temperature";
		data["type"] = "temperature";
		data["timestamp"] = 0;
		data["serial_num"] = 0;
		data["timestamp_consumed"] = 0;
		data["sensor_id"] = 1;
		console.log(data);
        TempEvent.save(data, function(gla) {
            console.log("Sent");
        }); 
    }
	
	$scope.sendSmokeEventCheck = function(){
        if($scope.smoke == null | $scope.smoke == "") {
            return false;
        }
        if ($scope.smoke.room_number == null | $scope.smoke.room_number == "") {
            $scope.warningSmoke = "Specify the room number (1 to 10).";
            return false;
        }
        if ($scope.smoke.obscuration == null | $scope.smoke.obscuration == "") {
            $scope.warningSmoke = "Specify the obscuration in percentage of obscuration per meter.";
            return false;
        }
        return true;
    }
    $scope.sendSmokeEvent = function(smoke) {
        var data = JSON.parse(JSON.stringify(smoke));
		data["@type"] = "smoke";
		data["type"] = "smoke";
		data["timestamp"] = 0;
		data["serial_num"] = 0;
		data["timestamp_consumed"] = 0;
		data["sensor_id"] = 1;
		console.log(data);
        SmokeEvent.save(data, function() {
            console.log("Sent");
        }); 
    }
	
	$interval(function(){
		Fire.query().$promise.then(function(fires) {
			console.log(fires);
			var roomIds = [];
			var uniqueFires = [];
			for (var i=fires.length-1; i>=0; i--) {
				console.log(roomIds);
				console.log(uniqueFires);
				if(roomIds.indexOf(fires[i].room_number) == -1) {
					roomIds.push(fires[i].room_number);
					uniqueFires.push(fires[i]);
				}
			}
			$scope.fires = uniqueFires;
		});
	}, 500, 0);
	
})
.controller('ShopliftingCtrl', function($scope, $interval, $mdToast, StolenProducts, ShelfEvent, PaidEvent, ExitEvent) {

	this.name = "ShopliftingCtrl";
	$scope.stolen = [];

    $scope.sendShelfEventCheck = function(){
        if($scope.shelf == null | $scope.shelf == "") {
            return false;
        }
        if ($scope.shelf.product_id == null | $scope.shelf.product_id == "") {
            return false;
        }
        return true;
    }
    $scope.sendShelfEvent = function(shelf) {
        var data = JSON.parse(JSON.stringify(shelf));
		data["@type"] = "shelf";
		data["type"] = "shelf";
		data["timestamp"] = 0;
		data["serial_num"] = 0;
		data["timestamp_consumed"] = 0;
		console.log(data);
        ShelfEvent.save(data, function() {
            console.log("Sent");
        }); 
    }
	
	$scope.sendPaidEventCheck = function(){
        if($scope.paid == null | $scope.paid == "") {
            return false;
        }
        if ($scope.paid.product_id == null | $scope.paid.product_id == "") {
            return false;
        }
        return true;
    }
    $scope.sendPaidEvent = function(paid) {
        var data = JSON.parse(JSON.stringify(paid));
		data["@type"] = "paid";
		data["type"] = "paid";
		data["timestamp"] = 0;
		data["serial_num"] = 0;
		data["timestamp_consumed"] = 0;
		console.log(data);
        PaidEvent.save(data, function() {
            console.log("Sent");
        }); 
    }
	
	$scope.sendExitEventCheck = function(){
        if($scope.exit == null | $scope.exit == "") {
            return false;
        }
        if ($scope.exit.product_id == null | $scope.exit.product_id == "") {
            return false;
        }
        return true;
    }
    $scope.sendExitEvent = function(exit) {
        var data = JSON.parse(JSON.stringify(exit));
		data["@type"] = "exit";
		data["type"] = "exit";
		data["timestamp"] = 0;
		data["serial_num"] = 0;
		data["timestamp_consumed"] = 0;
		console.log(data);
        ExitEvent.save(data, function() {
            console.log("Sent");
        }); 
    }
	
	$interval(function(){
		StolenProducts.query().$promise.then(function(stolen) {
			console.log(stolen);
			/* var roomIds = [];
			var uniqueStolen = [];
			for (var i=stolen.length-1; i>=0; i--) {
				console.log(roomIds);
				console.log(uniqueFires);
				if(roomIds.indexOf(fires[i].room_number) == -1) {
					roomIds.push(fires[i].room_number);
					uniqueFires.push(fires[i]);
				}
			} */
			$scope.stolen = stolen;
		});
	}, 500, 0);
	
})
.directive('linearChart', function($parse, $window){
   return{
	  restrict:'EA',
	  template:"<svg width='850' height='200'></svg>",
	   link: function(scope, elem, attrs){
		   var exp = $parse(attrs.chartData);

           var dataToPlot=exp(scope);
           var padding = 20;
           var pathClass="path";
           var xScale, yScale, xAxisGen, yAxisGen, lineFun;

           var d3 = $window.d3;
           var rawSvg=elem.find('svg');
           var svg = d3.select(rawSvg[0]);

           scope.$watchCollection(exp, function(newVal, oldVal){
               dataToPlot=newVal;
               redrawLineChart();
           });

           function setChartParameters(){

               xScale = d3.scale.linear()
                   .domain([0, dataToPlot.length-1])
                   .range([padding + 50, rawSvg.attr("width") - padding]);

               yScale = d3.scale.linear()
                   .domain([0, d3.max(dataToPlot, function (d) {
                       return d.value;
                   })])
                   .range([rawSvg.attr("height") - padding, 0]);

               xAxisGen = d3.svg.axis()
                   .scale(xScale)
                   .orient("bottom")
                   .ticks(0);

               yAxisGen = d3.svg.axis()
                   .scale(yScale)
                   .orient("left")
                   .ticks(5);

               lineFun = d3.svg.line()
                   .x(function (d) {
                       return xScale(d.index);
                   })
                   .y(function (d) {
                       return yScale(d.value);
                   })
                   .interpolate("basis");
           }
		 
		 function drawLineChart() {

			   setChartParameters();

			   svg.append("svg:g")
				   .attr("class", "x axis")
				   .attr("transform", "translate(0,180)")
				   .call(xAxisGen);

			   svg.append("svg:g")
				   .attr("class", "y axis")
				   .attr("transform", "translate(65,0)")
				   .call(yAxisGen);

			   svg.append("svg:path")
				   .attr({
					   d: lineFun(dataToPlot),
					   "stroke": "blue",
					   "stroke-width": 2,
					   "fill": "none",
					   "class": pathClass
				   });
		   }

		   function redrawLineChart() {

			   setChartParameters();

			   svg.selectAll("g.y.axis").call(yAxisGen);

			   svg.selectAll("g.x.axis").call(xAxisGen);

			   svg.selectAll("."+pathClass)
				   .attr({
					   d: lineFun(dataToPlot)
				   });
		   }

		   drawLineChart();
	   }
   };
});

var INTEGER_REGEXP = /^\-?\d+$/;
app.directive('integer', function() {
  return {
    require: 'ngModel',
    link: function(scope, elm, attrs, ctrl) {
      ctrl.$validators.integer = function(modelValue, viewValue) {
        if (ctrl.$isEmpty(modelValue)) {
          return true;
        }

        if (INTEGER_REGEXP.test(viewValue)) {
          return true;
        }

        return false;
      };
    }
  };
});
